import sqlite3
from logging import Logger

from app.models.contact_model import Contact
from app.queries import contact_query


class ContactAccess:
    """contact 테이블 조회/저장. SQL 실행과 결과 반환만 한다."""

    def __init__(self, logger: Logger):
        self.logger = logger

    def select_contact_by_id(self, connection: sqlite3.Connection, contact_id: int) -> dict | None:
        """id로 연락처 한 건을 조회한다. 없으면 None."""
        contact_row = connection.execute(contact_query.select_contact_by_id, {"contact_id": contact_id}).fetchone()
        if contact_row is None:
            return None
        return dict(contact_row)

    def select_contact_list(self, connection: sqlite3.Connection, sort: str, size: int, offset: int) -> list[dict]:
        """정렬 기준에 따라 연락처 한 페이지를 조회한다."""
        sql = f"{contact_query.select_contact_list} ORDER BY c.{sort} LIMIT :size OFFSET :offset"
        cursor = connection.execute(sql, {"size": size, "offset": offset})
        return [dict(one_row) for one_row in cursor.fetchall()]

    def count_contact(self, connection: sqlite3.Connection) -> int:
        """전체 연락처 수."""
        return connection.execute(contact_query.count_contact).fetchone()[0]

    def count_contact_by_phone(self, connection: sqlite3.Connection, phone: str) -> int:
        """숫자만 남긴 전화번호로 등록된 연락처 수."""
        return connection.execute(contact_query.count_contact_by_phone, {"phone": phone}).fetchone()[0]

    def insert_contact(self, connection: sqlite3.Connection, contact: Contact) -> int:
        """연락처를 저장하고 새 id를 돌려준다."""
        cursor = connection.execute(contact_query.insert_contact, {
            "name": contact.name,
            "phone": contact.phone,
            "email": contact.email,
            "group_id": contact.group_id,
        })
        return cursor.lastrowid
