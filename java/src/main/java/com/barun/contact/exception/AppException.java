package com.barun.contact.exception;

/**
 * 비즈니스 오류의 공통 부모. 서비스는 HTTP를 모르고 오류 코드만 정한다.
 * 그래야 같은 서비스를 HTTP가 아닌 곳(배치, 테스트)에서도 그대로 쓸 수 있다.
 * RuntimeException을 상속해서 메서드마다 throws를 적지 않아도 된다.
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
