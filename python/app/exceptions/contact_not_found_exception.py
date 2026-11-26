from app.exceptions.app_exception import AppException
from app.models.error_model import ErrorCode


class ContactNotFoundException(AppException):
    """없는 연락처 id."""

    def __init__(self, contact_id: int):
        """오류 코드와 메시지를 정한다."""
        super().__init__(ErrorCode.CONTACT_NOT_FOUND, f"연락처를 찾을 수 없습니다. id={contact_id}")
