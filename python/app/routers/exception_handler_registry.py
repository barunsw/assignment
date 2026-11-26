from logging import Logger

from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from starlette.exceptions import HTTPException as StarletteHTTPException

from app.exceptions.app_exception import AppException
from app.models.error_model import ErrorCode, ErrorResponse

# 오류 코드 → HTTP 상태. 서비스는 이 표를 몰라야 HTTP가 아닌 곳(배치, 테스트)에서도 그대로 쓸 수 있다.
HTTP_STATUS_BY_ERROR_CODE = {
    ErrorCode.INVALID_INPUT: 400,
    ErrorCode.CONTACT_NOT_FOUND: 404,
    ErrorCode.DUPLICATE_CONTACT: 409,
    ErrorCode.NOT_IMPLEMENTED: 501,
    ErrorCode.INTERNAL_ERROR: 500,
}


class ExceptionHandlerRegistry:
    """서비스가 던진 오류 코드를 HTTP 상태 코드로 바꾸는 핸들러를 등록한다."""

    def __init__(self, logger: Logger):
        """의존 객체를 주입받는다."""
        self.logger = logger

    def register(self, app: FastAPI) -> None:
        """앱에 예외 핸들러를 등록한다."""
        app.add_exception_handler(AppException, self._handle_app_exception)
        app.add_exception_handler(RequestValidationError, self._handle_request_validation_error)
        app.add_exception_handler(StarletteHTTPException, self._handle_http_exception)
        app.add_exception_handler(NotImplementedError, self._handle_not_implemented)
        app.add_exception_handler(Exception, self._handle_unexpected_exception)

    async def _handle_app_exception(self, request: Request, exception: AppException) -> JSONResponse:
        """업무 규칙 위반. 예상한 오류이므로 debug로만 남긴다."""
        self.logger.debug(f"요청 처리 실패: {exception.error_code.value} {exception.message}")
        return self._to_response(HTTP_STATUS_BY_ERROR_CODE[exception.error_code], exception.error_code, exception.message)

    async def _handle_request_validation_error(self, request: Request, exception: RequestValidationError) -> JSONResponse:
        """요청 형식 오류(JSON 문법, 타입 불일치, 파일 누락). Java판과 맞추려고 422 대신 400으로 돌려준다."""
        return self._to_response(400, ErrorCode.INVALID_INPUT, "요청 형식이 올바르지 않습니다.")

    async def _handle_http_exception(self, request: Request, exception: StarletteHTTPException) -> JSONResponse:
        """프레임워크가 낸 HTTP 오류(본문 파싱 실패, 없는 경로 등)도 공통 오류 형식으로 돌려준다."""
        if exception.status_code < 500:
            return self._to_response(exception.status_code, ErrorCode.INVALID_INPUT, str(exception.detail))
        return self._to_response(exception.status_code, ErrorCode.INTERNAL_ERROR, str(exception.detail))

    async def _handle_not_implemented(self, request: Request, exception: NotImplementedError) -> JSONResponse:
        """아직 구현하지 않은 기능."""
        return self._to_response(501, ErrorCode.NOT_IMPLEMENTED, str(exception))

    async def _handle_unexpected_exception(self, request: Request, exception: Exception) -> JSONResponse:
        """처리하지 못한 오류. 내부 정보가 새지 않도록 응답에는 고정 문구만 주고, 자세한 내용은 로그로 남긴다."""
        self.logger.error(f"처리하지 못한 오류: {exception!r}", exc_info=exception)
        return self._to_response(500, ErrorCode.INTERNAL_ERROR, "서버 오류가 발생했습니다.")

    def _to_response(self, status_code: int, error_code: ErrorCode, message: str) -> JSONResponse:
        """공통 오류 응답을 만든다."""
        return JSONResponse(status_code=status_code, content=ErrorResponse(code=error_code.value, message=message).model_dump())
