from pydantic import BaseModel


class GroupResponse(BaseModel):
    """그룹과 그 그룹에 속한 연락처 수."""

    id: int
    name: str
    contact_count: int
