enable_foreign_keys = "PRAGMA foreign_keys = ON"

create_contact_group_table = """
    CREATE TABLE IF NOT EXISTS contact_group (
        id   INTEGER PRIMARY KEY AUTOINCREMENT,
        name VARCHAR(50) NOT NULL UNIQUE
    )
"""

create_contact_table = """
    CREATE TABLE IF NOT EXISTS contact (
        id       INTEGER PRIMARY KEY AUTOINCREMENT,
        name     VARCHAR(50)  NOT NULL,
        phone    VARCHAR(11)  NOT NULL UNIQUE,
        email    VARCHAR(100),
        group_id INTEGER REFERENCES contact_group (id)
    )
"""

insert_default_contact_group_list = """
    INSERT OR IGNORE INTO contact_group (id, name)
    VALUES (1, '가족'), (2, '친구'), (3, '회사')
"""
