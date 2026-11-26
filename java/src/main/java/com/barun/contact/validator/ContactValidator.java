package com.barun.contact.validator;

import com.barun.contact.vo.ContactCreateRequest;

/**
 * 연락처 입력 검증 규칙 하나. 규칙에 맞지 않으면 InvalidInputException을 던진다.
 * 구현 클래스에 @Component를 붙이면 스프링이 @Order 순서대로 List로 모아 ContactService에 넣어 준다.
 * 그래서 규칙을 추가할 때 서비스 코드는 고치지 않는다.
 */
public interface ContactValidator {

    /**
     * 요청을 검증한다.
     */
    void validate(ContactCreateRequest request);
}
