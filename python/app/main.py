"""실행 파일. uvicorn app.main:app 으로 실행한다."""

import logging

from app.app_factory import create_app
from app.settings import Settings

settings = Settings.from_environment()
logging.basicConfig(level=settings.log_level, format="%(asctime)s %(levelname)s %(name)s - %(message)s")
logger = logging.getLogger("contact")

app = create_app(logger, settings)
