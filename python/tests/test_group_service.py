import sqlite3

from app.services.group_service import GroupService


def test_get_group_list_연락처가_없는_그룹도_0으로_돌려준다(group_service: GroupService, connection: sqlite3.Connection):
    group_list = group_service.get_group_list(connection)

    assert [one_group.name for one_group in group_list] == ["가족", "친구", "회사"]
    assert group_list[0].contact_count == 0
