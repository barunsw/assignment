package com.barun.contact.exception;

/**
 * 이미 등록된 전화번호.
 */
public class DuplicateContactException extends AppException {

    public DuplicateContactException(String message) {
        super(ErrorCode.DUPLICATE_CONTACT, message);
    }
}
