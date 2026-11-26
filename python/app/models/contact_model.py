from dataclasses import dataclass

from pydantic import BaseModel

from app.domain.phone_number import PhoneNumber


class ContactCreateRequest(BaseModel):
    """연락처 등록 요청. 필수 여부와 형식은 ContactValidator들이 검사한다."""

    name: str | None = None
    phone: str | None = None
    email: str | None = None
    group_id: int | None = None


class ContactResponse(BaseModel):
    """연락처 응답. phone은 하이픈을 넣은 형식."""

    id: int
    name: str
    phone: str
    email: str | None
    group_id: int | None
    group_name: str | None

    @classmethod
    def from_row(cls, contact_row: dict) -> "ContactResponse":
        """DB 조회 결과 한 행을 응답으로 바꾼다."""
        return cls(
            id=contact_row["id"],
            name=contact_row["name"],
            phone=str(PhoneNumber(contact_row["phone"])),
            email=contact_row["email"],
            group_id=contact_row["group_id"],
            group_name=contact_row["group_name"],
        )


class ContactPageResponse(BaseModel):
    """연락처 목록 한 페이지."""

    page: int
    size: int
    total_count: int
    items: list[ContactResponse]


class ImportFailure(BaseModel):
    """CSV 가져오기에서 실패한 줄."""

    line: int
    reason: str


class ImportResult(BaseModel):
    """CSV 가져오기 결과."""

    total: int
    imported: int
    failed: int
    failures: list[ImportFailure]


@dataclass(frozen=True)
class ContactCsvRow:
    """CSV 한 줄. line_number는 헤더를 1번째 줄로 센 파일의 줄 번호."""

    line_number: int
    name: str
    phone: str
    email: str
    group_name: str


@dataclass
class Contact:
    """저장할 연락처. phone에는 숫자만 들어 있다."""

    name: str
    phone: str
    email: str | None
    group_id: int | None
    id: int | None = None

    # 이 메서드가 없으면 @dataclass가 자동으로 만든 __repr__이 대신 쓰여 전화번호와 이메일이 로그에 그대로 남는다.
    def __str__(self) -> str:
        """로그에 개인정보가 그대로 남지 않도록 전화번호를 가리고 이메일은 뺀다."""
        return f"Contact(id={self.id}, name='{self.name}', phone='{PhoneNumber(self.phone).to_masked_string()}', group_id={self.group_id})"
