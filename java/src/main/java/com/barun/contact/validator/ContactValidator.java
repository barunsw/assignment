package com.barun.contact.validator;

import com.barun.contact.dto.ContactCreateRequest;

/**
 * 연락처 입력 검증 규칙 하나. 규칙에 맞지 않으면 InvalidInputException을 던진다.
 */
public interface ContactValidator {

    void validate(ContactCreateRequest request);
}
