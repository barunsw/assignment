select_contact_by_id = """
    SELECT c.id, c.name, c.phone, c.email, c.group_id, g.name AS group_name
    FROM contact c
    LEFT JOIN contact_group g ON g.id = c.group_id
    WHERE c.id = :contact_id
"""

select_contact_list = """
    SELECT c.id, c.name, c.phone, c.email, c.group_id, g.name AS group_name
    FROM contact c
    LEFT JOIN contact_group g ON g.id = c.group_id
"""

count_contact = """
    SELECT COUNT(*) FROM contact
"""

count_contact_by_phone = """
    SELECT COUNT(*) FROM contact WHERE phone = :phone
"""

insert_contact = """
    INSERT INTO contact (name, phone, email, group_id)
    VALUES (:name, :phone, :email, :group_id)
"""
