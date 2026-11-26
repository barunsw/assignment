# sqlite는 외래 키 검사가 기본으로 꺼져 있고, 연결마다 켜야 한다.
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

# 그룹 등록 API가 없으므로 기본 그룹을 넣는다. 파일 DB라 재시작해도 남아 있으므로 OR IGNORE로 중복을 피한다.
# 테스트와 README 예시는 1=가족, 2=친구, 3=회사를 전제로 한다.
insert_default_contact_group_list = """
    INSERT OR IGNORE INTO contact_group (id, name)
    VALUES (1, '가족'), (2, '친구'), (3, '회사')
"""
