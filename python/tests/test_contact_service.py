import sqlite3

import pytest

from app.exceptions.duplicate_contact_exception import DuplicateContactException
from app.exceptions.invalid_input_exception import InvalidInputException
from app.models.contact_model import ContactCreateRequest
from app.services.contact_service import ContactService

SAMPLE_CSV = """name,phone,email,group
홍길동,010-1234-5678,hong@example.com,가족
김철수,010-2222-3333,,친구
"""


def test_register_contact_정상_입력이면_하이픈_넣은_번호로_돌려준다(contact_service: ContactService, connection: sqlite3.Connection):
    request = ContactCreateRequest(name="홍길동", phone="01012345678", email="hong@example.com", group_id=1)

    response = contact_service.register_contact(connection, request)

    assert response.name == "홍길동"
    assert response.phone == "010-1234-5678"
    assert response.group_name == "가족"


def test_register_contact_전화번호_형식이_틀리면_예외발생(contact_service: ContactService, connection: sqlite3.Connection):
    with pytest.raises(InvalidInputException):
        contact_service.register_contact(connection, ContactCreateRequest(name="홍길동", phone="02-123"))


def test_register_contact_하이픈만_다른_같은_번호면_예외발생(contact_service: ContactService, connection: sqlite3.Connection):
    contact_service.register_contact(connection, ContactCreateRequest(name="홍길동", phone="010-1234-5678"))

    with pytest.raises(DuplicateContactException):
        contact_service.register_contact(connection, ContactCreateRequest(name="고길동", phone="01012345678"))


def test_get_contact_등록한_id면_연락처를_돌려준다(contact_service: ContactService, connection: sqlite3.Connection):
    registered = contact_service.register_contact(connection, ContactCreateRequest(name="홍길동", phone="010-1234-5678"))

    response = contact_service.get_contact(connection, registered.id)

    assert response.id == registered.id
    assert response.group_id is None


def test_get_contact_page_이름순이면_가나다순으로_돌려준다(contact_service: ContactService, connection: sqlite3.Connection):
    contact_service.register_contact(connection, ContactCreateRequest(name="나영", phone="010-1111-1111"))
    contact_service.register_contact(connection, ContactCreateRequest(name="가영", phone="010-2222-2222"))

    page = contact_service.get_contact_page(connection, page=1, size=20, sort="name")

    assert page.total_count == 2
    assert [one_item.name for one_item in page.items] == ["가영", "나영"]


def test_import_contacts_예시_파일이면_모두_가져온다(contact_service: ContactService, connection: sqlite3.Connection):
    result = contact_service.import_contacts(connection, SAMPLE_CSV.encode("utf-8"))

    assert result.total == 2
    assert result.imported == 2
    assert result.failed == 0
