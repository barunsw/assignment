import sqlite3
from collections.abc import Iterator
from contextlib import contextmanager
from logging import Logger
from pathlib import Path

from app.queries import schema_query


class Database:
    """sqlite3 연결을 발급한다. 요청 하나 = 연결 하나 = 트랜잭션 하나."""

    def __init__(self, logger: Logger, database_path: Path):
        """의존 객체를 주입받는다."""
        self.logger = logger
        self._database_path = database_path

    def get_connection(self) -> Iterator[sqlite3.Connection]:
        """FastAPI 의존성. 정상 종료 시 commit, 예외 시 rollback, 항상 close."""
        with self.connection_scope() as connection:
            yield connection

    @contextmanager
    def connection_scope(self) -> Iterator[sqlite3.Connection]:
        """요청 밖(시작 시 스키마 생성, 테스트)에서 쓰는 연결 컨텍스트 매니저."""
        connection = self._connect()
        try:
            yield connection
            connection.commit()
        except Exception:
            connection.rollback()
            raise
        finally:
            connection.close()

    def initialize_schema(self) -> None:
        """테이블과 기본 그룹을 만든다. 이미 있으면 그대로 둔다."""
        with self.connection_scope() as connection:
            connection.execute(schema_query.create_contact_group_table)
            connection.execute(schema_query.create_contact_table)
            connection.execute(schema_query.insert_default_contact_group_list)
        self.logger.info(f"DB 준비 완료: {self._database_path}")

    def _connect(self) -> sqlite3.Connection:
        """연결을 열고 행을 이름으로 읽을 수 있게 설정한다."""
        # FastAPI는 동기 의존성과 핸들러를 서로 다른 스레드에서 실행할 수 있다.
        connection = sqlite3.connect(self._database_path, check_same_thread=False)
        connection.row_factory = sqlite3.Row
        connection.execute(schema_query.enable_foreign_keys)
        return connection
