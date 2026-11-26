package com.barun.contact.validator;

import com.barun.contact.dto.ContactCreateRequest;
import com.barun.contact.exception.InvalidInputException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class NameContactValidator implements ContactValidator {

    private static final int MAX_NAME_LENGTH = 50;

    @Override
    public void validate(ContactCreateRequest request) {
        String name = request.name();
        if (name == null || name.isBlank()) {
            throw new InvalidInputException("이름은 필수입니다.");
        }
        if (name.strip().length() > MAX_NAME_LENGTH) {
            throw new InvalidInputException("이름은 " + MAX_NAME_LENGTH + "자 이하여야 합니다.");
        }
    }
}
