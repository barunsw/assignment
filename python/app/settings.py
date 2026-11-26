import os
from dataclasses import dataclass
from pathlib import Path

DEFAULT_DATABASE_PATH = "contacts.db"
DEFAULT_LOG_LEVEL = "INFO"


@dataclass(frozen=True)
class Settings:
    """애플리케이션 설정. 시작할 때 한 번 읽어서 주입한다."""

    database_path: Path
    log_level: str

    @classmethod
    def from_environment(cls) -> "Settings":
        """환경변수에서 설정을 읽는다. 없으면 기본값을 쓴다."""
        return cls(
            database_path=Path(os.environ.get("CONTACT_DATABASE_PATH", DEFAULT_DATABASE_PATH)),
            log_level=os.environ.get("CONTACT_LOG_LEVEL", DEFAULT_LOG_LEVEL),
        )
