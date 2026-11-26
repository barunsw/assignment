from app.models.error_model import ErrorCode


class AppException(Exception):
    """비즈니스 오류의 공통 부모. 서비스는 HTTP를 모르고 오류 코드만 정한다."""

    def __init__(self, error_code: ErrorCode, message: str):
        super().__init__(message)
        self.error_code = error_code
        self.message = message
