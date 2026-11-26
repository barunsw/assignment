select_group_list = """
    SELECT id, name FROM contact_group ORDER BY id
"""

select_group_by_id = """
    SELECT id, name FROM contact_group WHERE id = :group_id
"""

select_group_summary_list = """
    SELECT g.id, g.name, COUNT(c.id) AS contact_count
    FROM contact_group g
    LEFT JOIN contact c ON c.group_id = g.id
    GROUP BY g.id, g.name
    ORDER BY g.id
"""
