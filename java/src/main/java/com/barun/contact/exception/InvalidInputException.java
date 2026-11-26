package com.barun.contact.exception;

public class InvalidInputException extends AppException {

    public InvalidInputException(String message) {
        super(ErrorCode.INVALID_INPUT, message);
    }
}
