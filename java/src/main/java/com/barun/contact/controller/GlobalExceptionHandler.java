package com.barun.contact.controller;

import com.barun.contact.exception.AppException;
import com.barun.contact.exception.ErrorCode;
import com.barun.contact.vo.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 서비스가 던진 오류 코드를 HTTP 상태 코드로 바꾼다.
 * 모든 오류를 여기서 같은 형식(ErrorResponse)으로 만들어서, 컨트롤러와 서비스는 오류 응답 형식을 신경 쓰지 않는다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 업무 규칙 위반. 예상한 오류이므로 debug로만 남긴다.
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException e) {
        log.debug("요청 처리 실패: {} {}", e.getErrorCode(), e.getMessage());
        return toResponse(toHttpStatus(e.getErrorCode()), e.getErrorCode(), e.getMessage());
    }

    /**
     * JSON 문법 오류, 숫자 자리에 문자가 온 경우 같은 요청 형식 오류.
     */
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
        return toResponse(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_INPUT, "요청 형식이 올바르지 않습니다.");
    }

    /**
     * 아직 구현하지 않은 기능.
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ErrorResponse> handleNotImplemented(UnsupportedOperationException e) {
        return toResponse(HttpStatus.NOT_IMPLEMENTED, ErrorCode.NOT_IMPLEMENTED, e.getMessage());
    }

    /**
     * 처리하지 못한 오류. 내부 정보가 새지 않도록 응답에는 고정 문구만 주고, 자세한 내용은 로그로 남긴다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        // 파일 필드 누락, 없는 경로 같은 스프링 자체 오류는 스프링이 정한 상태 코드를 따른다.
        if (e instanceof org.springframework.web.ErrorResponse springErrorResponse) {
            HttpStatusCode statusCode = springErrorResponse.getStatusCode();
            if (statusCode.is4xxClientError()) {
                return toResponse(statusCode, ErrorCode.INVALID_INPUT, e.getMessage());
            }
        }
        log.error("처리하지 못한 오류", e);
        return toResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR, "서버 오류가 발생했습니다.");
    }

    /**
     * 오류 코드를 HTTP 상태로 바꾼다.
     * default를 두지 않아서 ErrorCode에 값이 추가되면 여기를 고칠 때까지 컴파일 오류가 난다.
     */
    private static HttpStatus toHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case INVALID_INPUT -> HttpStatus.BAD_REQUEST;
            case CONTACT_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DUPLICATE_CONTACT -> HttpStatus.CONFLICT;
            case NOT_IMPLEMENTED -> HttpStatus.NOT_IMPLEMENTED;
            case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private static ResponseEntity<ErrorResponse> toResponse(HttpStatusCode statusCode, ErrorCode errorCode, String message) {
        return ResponseEntity.status(statusCode).body(new ErrorResponse(errorCode.name(), message));
    }
}
