from logging import Logger

from fastapi import FastAPI

from app.access.contact_access import ContactAccess
from app.access.group_access import GroupAccess
from app.database import Database
from app.routers.contact_router import build_contact_router
from app.routers.exception_handler_registry import ExceptionHandlerRegistry
from app.routers.group_router import build_group_router
from app.services.contact_csv_parser import ContactCsvParser
from app.services.contact_service import ContactService
from app.services.group_service import GroupService
from app.settings import Settings
from app.validators.email_contact_validator import EmailContactValidator
from app.validators.name_contact_validator import NameContactValidator
from app.validators.phone_contact_validator import PhoneContactValidator


def create_app(logger: Logger, settings: Settings) -> FastAPI:
    """컴포지션 루트. 객체를 한곳에서 조립하고 라우터에 주입한다."""
    database = Database(logger, settings.database_path)
    database.initialize_schema()

    contact_access = ContactAccess(logger)
    group_access = GroupAccess(logger)
    # 목록 순서대로 검사한다. 규칙을 추가할 때는 구현 객체를 여기에 넣기만 하면 된다.
    contact_validator_list = [
        NameContactValidator(logger),
        PhoneContactValidator(logger),
        EmailContactValidator(logger),
    ]
    contact_service = ContactService(logger, contact_access, group_access, contact_validator_list, ContactCsvParser(logger))
    group_service = GroupService(logger, group_access)

    app = FastAPI(title="주소록 API")
    ExceptionHandlerRegistry(logger).register(app)
    app.include_router(build_contact_router(contact_service, database))
    app.include_router(build_group_router(group_service, database))
    return app
