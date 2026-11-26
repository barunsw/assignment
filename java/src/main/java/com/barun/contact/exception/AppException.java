package com.barun.contact.exception;

/**
 * 비즈니스 오류의 공통 부모. 서비스는 HTTP를 모르고 오류 코드만 정한다.
 */
public abstract class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    protected AppException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
