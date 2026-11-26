import sqlite3

from fastapi import APIRouter, Depends

from app.database import Database
from app.models.group_model import GroupResponse
from app.services.group_service import GroupService


def build_group_router(group_service: GroupService, database: Database) -> APIRouter:
    """그룹 라우터를 만든다."""
    router = APIRouter(prefix="/api/groups", tags=["groups"])

    @router.get("")
    def get_group_list(connection: sqlite3.Connection = Depends(database.get_connection)) -> list[GroupResponse]:
        """그룹 목록과 그룹별 연락처 수."""
        return group_service.get_group_list(connection)

    return router
