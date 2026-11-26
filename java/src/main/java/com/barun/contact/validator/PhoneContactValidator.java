package com.barun.contact.validator;

import com.barun.contact.domain.PhoneNumber;
import com.barun.contact.dto.ContactCreateRequest;
import com.barun.contact.exception.InvalidInputException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
