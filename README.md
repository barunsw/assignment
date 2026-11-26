# 주소록 REST API 과제

바른개발연구소 신입 개발자 채용 사전 과제입니다.

연락처를 등록·조회하고 CSV 파일로 한꺼번에 가져오는 작은 REST API입니다.
같은 사양의 코드가 Java판(`java/`)과 Python판(`python/`)으로 준비되어 있습니다. **지원할 때 알려 주신 언어 하나만** 풀면 됩니다.

- 예상 소요 시간: **2~3시간**
- AI 도구 사용: **허용**합니다. 대신 사용 기록을 `AI_LOG.md`에 남겨 주세요.
- 면접에서는 제출한 코드를 직접 설명하고 질문에 답합니다(면접에서는 AI를 쓰지 않습니다). AI가 만든 코드도 본인이 이해하고 설명할 수 있어야 합니다.

## 해야 할 일

1. **기능 구현**: 아래 사양의 `GET /api/contacts/search` (전화번호 부분 일치 검색)를 구현합니다. 지금은 `501 Not Implemented`를 돌려줍니다.
2. **사양과 다르게 동작하는 부분 고치기**: 이미 구현된 API 중 아래 사양과 다르게 동작하는 곳이 있으면 찾아서 고칩니다.
3. **테스트**
   - 구현한 검색 기능의 테스트
   - 고친 문제마다 **재현 테스트** 1개 이상: 고치기 전 코드에서는 실패하고, 고친 뒤에는 통과해야 합니다.
   - 처음부터 있던 테스트도 계속 통과해야 합니다.
4. **문서**
   - `REPORT.md`: 무엇을, 왜, 어떻게 고쳤는지. 양식은 저장소 루트의 `REPORT.md`에 있습니다.
   - `AI_LOG.md`: 어떤 AI 도구를 어디에 썼고, 받은 답을 그대로 썼는지 고쳐 썼는지 버렸는지. 양식은 저장소 루트의 `AI_LOG.md`에 있습니다.

시간 안에 다 못 했다면 한 곳까지를 제출하고, 남은 부분은 `REPORT.md`에 적어 주세요.

## API 사양

모든 요청·응답 본문은 JSON(UTF-8)이고, 필드 이름은 snake_case입니다.

### 공통 오류 응답

```json
{ "code": "CONTACT_NOT_FOUND", "message": "연락처를 찾을 수 없습니다. id=99" }
```

| HTTP 상태 | code | 언제 |
|---|---|---|
| 400 | `INVALID_INPUT` | 입력값이 사양에 맞지 않을 때 |
| 404 | `CONTACT_NOT_FOUND` | 없는 연락처 id |
| 409 | `DUPLICATE_CONTACT` | 이미 등록된 전화번호 |
| 500 | `INTERNAL_ERROR` | 그 밖의 서버 오류 |

### 전화번호 규칙

- 숫자 외 문자(하이픈, 공백 등)는 무시합니다. `010-1234-5678`과 `01012345678`은 같은 번호입니다.
- 숫자만 남겼을 때 `01`로 시작하는 10~11자리여야 합니다.
- DB에는 숫자만 저장하고, 응답에서는 하이픈을 넣어 보여 줍니다(`010-1234-5678`, 10자리는 `011-123-4567`).

### 연락처 등록 `POST /api/contacts`

요청:

```json
{ "name": "홍길동", "phone": "010-1234-5678", "email": "hong@example.com", "group_id": 1 }
```

| 필드 | 필수 | 규칙 |
|---|---|---|
| `name` | 예 | 앞뒤 공백 제외 1~50자 |
| `phone` | 예 | 위 전화번호 규칙 |
| `email` | 아니요 | 있으면 `아이디@도메인` 형식 |
| `group_id` | 아니요 | 있으면 존재하는 그룹 id |

- 성공: `201 Created`, 등록된 연락처(아래 "연락처 응답")
- 규칙 위반: `400`, 이미 등록된 전화번호: `409`

연락처 응답:

```json
{ "id": 1, "name": "홍길동", "phone": "010-1234-5678", "email": "hong@example.com", "group_id": 1, "group_name": "가족" }
```

그룹이 없으면 `group_id`, `group_name`은 `null`입니다.

### 연락처 조회 `GET /api/contacts/{id}`

- 성공: `200`, 연락처 응답
- 없는 id: `404 CONTACT_NOT_FOUND`

### 연락처 목록 `GET /api/contacts?page=1&size=20&sort=id`

| 파라미터 | 기본값 | 규칙 |
|---|---|---|
| `page` | 1 | 1부터 시작 |
| `size` | 20 | 1~100 |
| `sort` | `id` | `id`, `name`, `phone` 중 하나(오름차순). 그 밖의 값은 `400` |

- 그룹이 없는 연락처도 목록에 나옵니다.
- 응답 `200`:

```json
{ "page": 1, "size": 20, "total_count": 42, "items": [ { "id": 1, "name": "홍길동", "...": "..." } ] }
```

### 전화번호 검색 `GET /api/contacts/search?phone=1234` (구현할 기능)

- `phone`의 숫자 부분이 전화번호에 **포함된** 연락처를 모두 돌려줍니다. 하이픈 유무와 관계없이 찾아야 합니다.
  - 예: `phone=1234`, `phone=010-1234`, `phone=0101234`는 모두 `010-1234-5678`을 찾습니다.
- `phone`이 없거나 숫자가 하나도 없으면 `400 INVALID_INPUT`
- 결과는 id 오름차순 배열입니다. 없으면 빈 배열 `[]`, `200`.
- 배열의 각 항목은 "연락처 응답" 형식입니다. 그룹이 없는 연락처도 결과에 나옵니다(`group_id`, `group_name`은 `null`).

### CSV 가져오기 `POST /api/contacts/import`

- `multipart/form-data`, 파일 필드 이름은 `file`, UTF-8(BOM 있어도 됨)
- 첫 줄은 헤더 `name,phone,email,group`입니다. 둘째 줄부터 연락처 한 명씩입니다. 빈 줄은 건너뜁니다.
- `email`, `group`은 비어 있을 수 있습니다. `group`은 그룹 **이름**입니다.
- 한 줄이 규칙에 맞지 않으면(필수값 누락, 형식 오류, 없는 그룹 이름, 이미 등록된 번호, 파일 안에서 중복된 번호) **그 줄만 실패**로 기록하고 나머지 줄은 계속 가져옵니다.
- 응답 `200`:

```json
{ "total": 3, "imported": 2, "failed": 1, "failures": [ { "line": 4, "reason": "이미 등록된 전화번호입니다." } ] }
```

`line`은 헤더를 1번째 줄로 센 파일의 줄 번호입니다. 예시 파일: `java/sample/contacts.csv`, `python/sample/contacts.csv`

### 그룹 목록 `GET /api/groups`

그룹마다 속한 연락처 수를 돌려줍니다. 연락처가 없는 그룹도 `0`으로 나옵니다. id 오름차순.

```json
[ { "id": 1, "name": "가족", "contact_count": 2 }, { "id": 3, "name": "회사", "contact_count": 0 } ]
```

그룹은 시작할 때 미리 들어가 있는 `가족`, `친구`, `회사` 세 개입니다(그룹 등록 API는 없습니다).

## 코드 둘러보기

Java판과 Python판은 구조가 같습니다. 요청은 **API → 업무 처리 → DB 접근** 순서로 흐릅니다.

| 역할 | Java (`java/src/main/java/com/barun/contact/`) | Python (`python/app/`) |
|---|---|---|
| API(요청 받기, 응답 돌려주기) | `controller/` | `routers/` |
| 업무 처리 | `service/` | `services/` |
| DB 접근 | `mapper/` (SQL은 `src/main/resources/mapper/*.xml`) | `access/` (SQL은 `queries/`) |
| 요청·응답 데이터 | `vo/` | `models/` |
| 전화번호 | `domain/PhoneNumber` | `domain/phone_number.py` |
| 입력 검증 규칙 | `validator/` | `validators/` |
| 업무 오류 | `exception/` | `exceptions/` |
| 객체 조립 | 스프링이 자동으로 한다 | `app_factory.py` |

- **전화번호** `PhoneNumber`: 입력에서 숫자만 남겨 보관합니다. 같은 번호인지 비교(Java `equals`/`hashCode`, Python `__eq__`/`__hash__`)하고, 하이픈을 넣은 형식으로 바꿉니다(Java `toString`, Python `__str__`).
- **입력 검증**: 규칙 하나가 `ContactValidator` 인터페이스(Python은 추상 클래스)의 구현 하나입니다(이름, 전화번호, 이메일). 서비스는 구현들을 목록으로 받아 차례로 실행만 합니다.
- **업무 오류**: 서비스는 `AppException`을 상속한 예외를 던지고, 공통 예외 처리기(Java `GlobalExceptionHandler`, Python `exception_handler_registry.py`)가 위 "공통 오류 응답"으로 바꿉니다.
- **테스트**: Java `java/src/test/`, Python `python/tests/`. 코드를 읽기 전에 테스트부터 보면 각 부분이 어떻게 동작해야 하는지 알기 쉽습니다.

## 실행 방법

### Java판

- 필요: JDK 17, Maven 3.9 이상
- DB: H2 메모리 DB(MariaDB 호환 모드). 별도 설치가 필요 없고, 재시작하면 데이터가 초기화됩니다.

```bash
cd java
mvn test               # 테스트
mvn spring-boot:run    # 서버 실행 (http://localhost:8080)
```

### Python판

- 필요: Python 3.11 이상
- DB: 표준 라이브러리 `sqlite3`. 파일 `contacts.db`가 `python/` 아래에 생깁니다(지우면 초기화).

```bash
cd python
python -m venv .venv
# Windows: .venv\Scripts\activate    macOS/Linux: source .venv/bin/activate
pip install -r requirements.txt
pytest                                  # 테스트
uvicorn app.main:app --reload           # 서버 실행 (http://localhost:8000, 문서: /docs)
```

### 호출 예시

`java/` 또는 `python/` 폴더에서 실행합니다. 한글이 깨지지 않도록 요청 본문은 UTF-8 파일(`sample/contact.json`)로 보냅니다.

```bash
curl -X POST http://localhost:8080/api/contacts -H "Content-Type: application/json" --data-binary @sample/contact.json
curl http://localhost:8080/api/contacts/1
curl "http://localhost:8080/api/contacts?page=1&size=10&sort=name"
curl -F "file=@sample/contacts.csv" http://localhost:8080/api/contacts/import
curl http://localhost:8080/api/groups
```

- Python판은 포트만 `8000`으로 바꾸면 됩니다. 브라우저에서 `http://localhost:8000/docs`를 열어 호출해 볼 수도 있습니다.
- Windows PowerShell에서는 `curl` 대신 `curl.exe`라고 입력하세요(`curl`은 PowerShell의 다른 명령으로 연결되어 있습니다).

### 문제 해결

- Windows에서 Java 서버가 `Unable to establish loopback connection` 오류로 뜨지 않으면, 임시 폴더 권한 문제일 수 있습니다. 빈 폴더(예: `C:\tmp`)를 만들고 아래처럼 실행해 보세요.

  ```bash
  mvn spring-boot:run "-Dspring-boot.run.jvmArguments=-Djdk.net.unixdomain.tmpdir=C:/tmp"
  ```

## 제출 방법

> **이 저장소를 GitHub에서 포크(Fork)하지 마세요.** 포크는 공개되어 다른 지원자가 볼 수 있습니다.

아래 둘 중 하나로 **비공개로** 제출해 주세요.

1. **zip 이메일**: 저장소 전체를 zip으로 묶어 안내받은 이메일 주소로 보냅니다. `target/`, `.venv/`, `__pycache__/`, `*.db`는 빼 주세요.
2. **비공개 저장소**: 본인 계정에 **Private** 저장소를 만들어 올리고, 안내받은 GitHub 계정을 협업자(Collaborator)로 초대합니다.

제출물:

- 수정한 코드(풀었던 언어의 폴더)
- 추가한 테스트
- `REPORT.md`
- `AI_LOG.md`

커밋 기록도 그대로 보내 주시면 작업 과정을 이해하는 데 도움이 됩니다(필수는 아닙니다).
