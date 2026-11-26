from fastapi.testclient import TestClient


def test_post_contacts_정상_입력이면_201(client: TestClient):
    response = client.post("/api/contacts", json={"name": "홍길동", "phone": "01012345678", "group_id": 1})

    assert response.status_code == 201
    assert response.json()["phone"] == "010-1234-5678"
    assert response.json()["group_name"] == "가족"


def test_post_contacts_전화번호_형식이_틀리면_400(client: TestClient):
    response = client.post("/api/contacts", json={"name": "홍길동", "phone": "02-123"})

    assert response.status_code == 400
    assert response.json()["code"] == "INVALID_INPUT"


def test_post_contacts_같은_번호를_다시_등록하면_409(client: TestClient):
    client.post("/api/contacts", json={"name": "홍길동", "phone": "010-1234-5678"})

    response = client.post("/api/contacts", json={"name": "고길동", "phone": "01012345678"})

    assert response.status_code == 409
    assert response.json()["code"] == "DUPLICATE_CONTACT"


def test_post_contacts_import_예시_파일이면_200(client: TestClient):
    csv_content = "name,phone,email,group\n홍길동,010-1234-5678,hong@example.com,가족\n"

    response = client.post("/api/contacts/import", files={"file": ("contacts.csv", csv_content.encode("utf-8"), "text/csv")})

    assert response.status_code == 200
    assert response.json()["imported"] == 1


def test_get_groups_그룹_세_개를_돌려준다(client: TestClient):
    response = client.get("/api/groups")

    assert response.status_code == 200
    assert len(response.json()) == 3
