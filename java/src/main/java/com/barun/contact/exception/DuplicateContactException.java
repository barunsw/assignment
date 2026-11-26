package com.barun.contact.exception;

public class DuplicateContactException extends AppException {

    public DuplicateContactException(String message) {
        super(ErrorCode.DUPLICATE_CONTACT, message);
    }
}
