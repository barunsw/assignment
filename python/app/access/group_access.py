import sqlite3
from logging import Logger

from app.queries import group_query


class GroupAccess:
    """contact_group 테이블 조회. SQL 실행과 결과 반환만 한다."""

    def __init__(self, logger: Logger):
        self.logger = logger

    def select_group_list(self, connection: sqlite3.Connection) -> list[dict]:
        """전체 그룹 목록."""
        return [dict(one_row) for one_row in connection.execute(group_query.select_group_list).fetchall()]

    def select_group_by_id(self, connection: sqlite3.Connection, group_id: int) -> dict | None:
        """id로 그룹 한 건을 조회한다. 없으면 None."""
        group_row = connection.execute(group_query.select_group_by_id, {"group_id": group_id}).fetchone()
        if group_row is None:
            return None
        return dict(group_row)

    def select_group_summary_list(self, connection: sqlite3.Connection) -> list[dict]:
        """그룹별 연락처 수. 연락처가 없는 그룹도 포함한다."""
        return [dict(one_row) for one_row in connection.execute(group_query.select_group_summary_list).fetchall()]
