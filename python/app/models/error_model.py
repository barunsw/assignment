from enum import Enum

from pydantic import BaseModel


class ErrorCode(Enum):
    """오류 응답의 code 값. HTTP 상태 코드로 바꾸는 일은 라우터 계층이 맡는다."""

    INVALID_INPUT = "INVALID_INPUT"
    CONTACT_NOT_FOUND = "CONTACT_NOT_FOUND"
    DUPLICATE_CONTACT = "DUPLICATE_CONTACT"
    NOT_IMPLEMENTED = "NOT_IMPLEMENTED"
    INTERNAL_ERROR = "INTERNAL_ERROR"


class ErrorResponse(BaseModel):
    """공통 오류 응답."""

    code: str
    message: str
