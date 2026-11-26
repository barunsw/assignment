import sqlite3
from logging import Logger

from app.access.group_access import GroupAccess
from app.models.group_model import GroupResponse


class GroupService:
    """그룹 목록 조회."""

    def __init__(self, logger: Logger, group_access: GroupAccess):
        self.logger = logger
        self.group_access = group_access

    def get_group_list(self, connection: sqlite3.Connection) -> list[GroupResponse]:
        """그룹마다 속한 연락처 수를 함께 돌려준다."""
        return [GroupResponse(**one_group) for one_group in self.group_access.select_group_summary_list(connection)]
