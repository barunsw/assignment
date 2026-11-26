package com.barunsw.contact.validator;

import com.barunsw.contact.domain.PhoneNumber;
import com.barunsw.contact.exception.InvalidInputException;
import com.barunsw.contact.vo.ContactCreateRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 전화번호: 필수, 숫자만 남겨서 01로 시작하는 10~11자리.
 */
@Component
@Order(2)
public class PhoneContactValidator implements ContactValidator {

    @Override
    public void validate(ContactCreateRequest request) {
        if (request.phone() == null || request.phone().isBlank()) {
            throw new InvalidInputException("전화번호는 필수입니다.");
        }
        if (!PhoneNumber.of(request.phone()).isValid()) {
            throw new InvalidInputException("전화번호 형식이 올바르지 않습니다: " + request.phone());
        }
    }
}
