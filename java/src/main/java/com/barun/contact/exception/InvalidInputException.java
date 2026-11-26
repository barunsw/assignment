package com.barun.contact.exception;

/**
 * 입력값이 사양에 맞지 않을 때.
 */
public class InvalidInputException extends AppException {

    public InvalidInputException(String message) {
        super(ErrorCode.INVALID_INPUT, message);
    }
}
