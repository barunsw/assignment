package com.barun.contact.validator;

import com.barun.contact.exception.InvalidInputException;
import com.barun.contact.vo.ContactCreateRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 이름: 필수, 앞뒤 공백 제외 50자 이하.
 */
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
