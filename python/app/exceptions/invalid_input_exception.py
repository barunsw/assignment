from app.exceptions.app_exception import AppException
from app.models.error_model import ErrorCode


class InvalidInputException(AppException):
    """입력값이 사양에 맞지 않을 때."""

    def __init__(self, message: str):
        """오류 코드와 메시지를 정한다."""
        super().__init__(ErrorCode.INVALID_INPUT, message)
