select_group_list = """
    SELECT id, name FROM contact_group ORDER BY id
"""

select_group_by_id = """
    SELECT id, name FROM contact_group WHERE id = :group_id
"""

# 연락처가 0명인 그룹도 나오도록 그룹 쪽을 기준으로 LEFT JOIN한다.
# COUNT(*)는 짝이 없어 c 열이 모두 NULL인 행도 1로 세므로, COUNT(c.id)로 실제 연락처가 있는 행만 센다.
select_group_summary_list = """
    SELECT g.id, g.name, COUNT(c.id) AS contact_count
    FROM contact_group g
    LEFT JOIN contact c ON c.group_id = g.id
    GROUP BY g.id, g.name
    ORDER BY g.id
"""
