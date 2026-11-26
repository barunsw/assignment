from app.exceptions.app_exception import AppException
from app.models.error_model import ErrorCode


class DuplicateContactException(AppException):
    """이미 등록된 전화번호."""

    def __init__(self, message: str):
        super().__init__(ErrorCode.DUPLICATE_CONTACT, message)
