-- 테스트에서 스프링 컨텍스트가 다시 뜰 때도 같은 메모리 DB를 쓰므로 매번 새로 만든다.
DROP TABLE IF EXISTS contact;
DROP TABLE IF EXISTS contact_group;

CREATE TABLE contact_group (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE contact (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(50)  NOT NULL,
    phone    VARCHAR(11)  NOT NULL UNIQUE,
    email    VARCHAR(100),
    group_id BIGINT,
    CONSTRAINT fk_contact_group FOREIGN KEY (group_id) REFERENCES contact_group (id)
);
