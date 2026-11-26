package com.barun.contact.exception;

public class ContactNotFoundException extends AppException {

    public ContactNotFoundException(long id) {
        super(ErrorCode.CONTACT_NOT_FOUND, "연락처를 찾을 수 없습니다. id=" + id);
    }
}
