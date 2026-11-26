import sqlite3

from fastapi import APIRouter, Depends, File, UploadFile

from app.database import Database
from app.models.contact_model import ContactCreateRequest, ContactPageResponse, ContactResponse, ImportResult
from app.services.contact_service import ContactService


def build_contact_router(contact_service: ContactService, database: Database) -> APIRouter:
    """연락처 라우터를 만든다."""
    router = APIRouter(prefix="/api/contacts", tags=["contacts"])

    @router.post("", status_code=201)
    def register_contact(request: ContactCreateRequest, connection: sqlite3.Connection = Depends(database.get_connection)) -> ContactResponse:
        """연락처를 등록한다. 성공하면 201과 등록된 연락처를 돌려준다."""
        return contact_service.register_contact(connection, request)

    # /{contact_id}보다 먼저 선언해야 "search"가 id로 해석되지 않는다.
    @router.get("/search")
    def search_contacts(phone: str | None = None, connection: sqlite3.Connection = Depends(database.get_connection)) -> list[ContactResponse]:
        """전화번호 일부로 연락처를 검색한다. phone이 빠진 요청도 서비스가 같은 규칙으로 판단하도록 선택 파라미터로 받는다."""
        return contact_service.search_contacts_by_phone(connection, phone)

    @router.get("/{contact_id}")
    def get_contact(contact_id: int, connection: sqlite3.Connection = Depends(database.get_connection)) -> ContactResponse:
        """id로 연락처를 조회한다."""
        return contact_service.get_contact(connection, contact_id)

    @router.get("")
    def get_contact_page(page: int = 1, size: int = 20, sort: str = "id", connection: sqlite3.Connection = Depends(database.get_connection)) -> ContactPageResponse:
        """연락처 목록 한 페이지를 조회한다."""
        return contact_service.get_contact_page(connection, page, size, sort)

    @router.post("/import")
    def import_contacts(file: UploadFile = File(...), connection: sqlite3.Connection = Depends(database.get_connection)) -> ImportResult:
        """CSV 파일로 연락처를 가져온다. 실패한 줄이 있어도 200과 결과 요약을 돌려준다."""
        return contact_service.import_contacts(connection, file.file.read())

    return router
