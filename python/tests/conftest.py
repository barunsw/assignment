import logging
import sqlite3
from collections.abc import Iterator
from pathlib import Path

import pytest
from fastapi.testclient import TestClient

from app.access.contact_access import ContactAccess
from app.access.group_access import GroupAccess
from app.app_factory import create_app
from app.database import Database
from app.services.contact_csv_parser import ContactCsvParser
from app.services.contact_service import ContactService
from app.services.group_service import GroupService
from app.settings import Settings
from app.validators.email_contact_validator import EmailContactValidator
from app.validators.name_contact_validator import NameContactValidator
from app.validators.phone_contact_validator import PhoneContactValidator


@pytest.fixture
def logger() -> logging.Logger:
    """테스트용 logger. 테스트 실행 파일 역할이므로 여기서 생성한다."""
    return logging.getLogger("contact-test")


@pytest.fixture
def settings(tmp_path: Path) -> Settings:
    """테스트마다 새 DB 파일을 쓴다."""
    return Settings(database_path=tmp_path / "test.db", log_level="DEBUG")


@pytest.fixture
def client(logger: logging.Logger, settings: Settings) -> Iterator[TestClient]:
    """API 테스트용 클라이언트."""
    with TestClient(create_app(logger, settings)) as test_client:
        yield test_client


@pytest.fixture
def database(logger: logging.Logger, settings: Settings) -> Database:
    """스키마를 만든 DB."""
    database = Database(logger, settings.database_path)
    database.initialize_schema()
    return database


@pytest.fixture
def connection(database: Database) -> Iterator[sqlite3.Connection]:
    """서비스 테스트용 연결. 테스트가 끝나면 commit하고 닫는다."""
    with database.connection_scope() as connection:
        yield connection


@pytest.fixture
def group_access(logger: logging.Logger) -> GroupAccess:
    """그룹 access."""
    return GroupAccess(logger)


@pytest.fixture
def contact_service(logger: logging.Logger, group_access: GroupAccess) -> ContactService:
    """실제 access와 검증 규칙으로 조립한 연락처 서비스."""
    contact_validator_list = [NameContactValidator(logger), PhoneContactValidator(logger), EmailContactValidator(logger)]
    return ContactService(logger, ContactAccess(logger), group_access, contact_validator_list, ContactCsvParser(logger))


@pytest.fixture
def group_service(logger: logging.Logger, group_access: GroupAccess) -> GroupService:
    """그룹 서비스."""
    return GroupService(logger, group_access)
