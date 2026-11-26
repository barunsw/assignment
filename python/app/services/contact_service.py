import sqlite3
from logging import Logger

from app.access.contact_access import ContactAccess
from app.access.group_access import GroupAccess
from app.domain.phone_number import PhoneNumber
from app.exceptions.app_exception import AppException
from app.exceptions.duplicate_contact_exception import DuplicateContactException
from app.exceptions.invalid_input_exception import InvalidInputException
from app.models.contact_model import Contact, ContactCreateRequest, ContactCsvRow, ContactPageResponse, ContactResponse, ImportFailure, ImportResult
from app.services.contact_csv_parser import ContactCsvParser
from app.validators.contact_validator import ContactValidator

MAX_PAGE_SIZE = 100


class ContactService:
    """연락처 등록·조회·목록·CSV 가져오기."""

    def __init__(self, logger: Logger, contact_access: ContactAccess, group_access: GroupAccess, contact_validator_list: list[ContactValidator], contact_csv_parser: ContactCsvParser):
        self.logger = logger
        self.contact_access = contact_access
        self.group_access = group_access
        self.contact_validator_list = contact_validator_list
        self.contact_csv_parser = contact_csv_parser

    def register_contact(self, connection: sqlite3.Connection, request: ContactCreateRequest) -> ContactResponse:
        """연락처를 등록하고 등록된 연락처를 돌려준다."""
        self._validate(request)
        if request.group_id is not None and self.group_access.select_group_by_id(connection, request.group_id) is None:
            raise InvalidInputException(f"존재하지 않는 그룹입니다. group_id={request.group_id}")
        contact_id = self._insert_new_contact(connection, request)
        return ContactResponse.from_row(self.contact_access.select_contact_by_id(connection, contact_id))

    def get_contact(self, connection: sqlite3.Connection, contact_id: int) -> ContactResponse:
        """id로 연락처를 조회한다."""
        contact_row = self.contact_access.select_contact_by_id(connection, contact_id)
        return ContactResponse.from_row(contact_row)

    def get_contact_page(self, connection: sqlite3.Connection, page: int, size: int, sort: str) -> ContactPageResponse:
        """연락처 목록 한 페이지를 조회한다."""
        if page < 1:
            raise InvalidInputException("page는 1 이상이어야 합니다.")
        if size < 1 or size > MAX_PAGE_SIZE:
            raise InvalidInputException(f"size는 1~{MAX_PAGE_SIZE} 사이여야 합니다.")
        offset = (page - 1) * size
        contact_row_list = self.contact_access.select_contact_list(connection, sort, size, offset)
        return ContactPageResponse(
            page=page,
            size=size,
            total_count=self.contact_access.count_contact(connection),
            items=[ContactResponse.from_row(one_row) for one_row in contact_row_list],
        )

    def search_contacts_by_phone(self, connection: sqlite3.Connection, phone: str | None) -> list[ContactResponse]:
        """전화번호 부분 일치 검색."""
        # TODO: 전화번호 부분 일치 검색 구현 (README의 API 사양 참고)
        raise NotImplementedError("전화번호 검색은 아직 구현되지 않았습니다.")

    def import_contacts(self, connection: sqlite3.Connection, content: bytes) -> ImportResult:
        """CSV 내용을 가져온다. 실패한 줄은 기록만 하고 나머지는 계속 가져온다."""
        row_list = self.contact_csv_parser.parse(content)
        group_id_repo = self._load_group_id_repo(connection)
        imported_phone_set: set[PhoneNumber] = set()
        failure_list = []

        for one_row in row_list:
            try:
                self._import_row(connection, one_row, group_id_repo, imported_phone_set)
            except AppException as exception:
                failure_list.append(ImportFailure(line=one_row.line_number, reason=exception.message))

        imported_count = len(row_list) - len(failure_list)
        self.logger.info(f"CSV 가져오기 완료: 전체 {len(row_list)}건, 성공 {imported_count}건, 실패 {len(failure_list)}건")
        return ImportResult(total=len(row_list), imported=imported_count, failed=len(failure_list), failures=failure_list)

    def _import_row(self, connection: sqlite3.Connection, row: ContactCsvRow, group_id_repo: dict[str, int], imported_phone_set: set[PhoneNumber]) -> None:
        """CSV 한 줄을 검증하고 저장한다."""
        group_id = self._resolve_group_id(row.group_name, group_id_repo)
        request = ContactCreateRequest(name=row.name, phone=row.phone, email=row.email, group_id=group_id)
        self._validate(request)
        # 같은 파일 안의 중복은 DB에 넣기 전에 걸러서 사유를 따로 알려 준다.
        phone_number = PhoneNumber(request.phone)
        if phone_number in imported_phone_set:
            raise DuplicateContactException("파일 안에서 중복된 전화번호입니다.")
        imported_phone_set.add(phone_number)
        self._insert_new_contact(connection, request)

    def _resolve_group_id(self, group_name: str | None, group_id_repo: dict[str, int]) -> int | None:
        """그룹 이름을 id로 바꾼다. 비어 있으면 그룹 없음."""
        if group_name is None or not group_name.strip():
            return None
        group_id = group_id_repo.get(group_name)
        if group_id is None:
            raise InvalidInputException(f"존재하지 않는 그룹입니다: {group_name}")
        return group_id

    def _load_group_id_repo(self, connection: sqlite3.Connection) -> dict[str, int]:
        """그룹 이름 → id."""
        return {one_group["name"]: one_group["id"] for one_group in self.group_access.select_group_list(connection)}

    def _insert_new_contact(self, connection: sqlite3.Connection, request: ContactCreateRequest) -> int:
        """중복을 확인하고 저장한 뒤 새 id를 돌려준다."""
        phone_number = PhoneNumber(request.phone)
        if self.contact_access.count_contact_by_phone(connection, phone_number.digits) > 0:
            raise DuplicateContactException("이미 등록된 전화번호입니다.")

        contact = Contact(name=request.name.strip(), phone=phone_number.digits, email=self._blank_to_none(request.email), group_id=request.group_id)
        contact.id = self.contact_access.insert_contact(connection, contact)
        self.logger.info(f"연락처 등록: {contact}")
        return contact.id

    def _validate(self, request: ContactCreateRequest) -> None:
        """등록된 검증 규칙을 차례로 적용한다."""
        for one_validator in self.contact_validator_list:
            one_validator.validate(request)

    def _blank_to_none(self, value: str | None) -> str | None:
        """빈 문자열은 None으로 바꾼다."""
        if value is None or not value.strip():
            return None
        return value.strip()
