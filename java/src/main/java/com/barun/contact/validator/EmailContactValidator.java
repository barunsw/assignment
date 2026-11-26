package com.barun.contact.validator;

import com.barun.contact.dto.ContactCreateRequest;
import com.barun.contact.exception.InvalidInputException;
import java.util.regex.Pattern;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class EmailContactValidator implements ContactValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int MAX_EMAIL_LENGTH = 100;

    @Override
    public void validate(ContactCreateRequest request) {
        String email = request.email();
        if (email == null || email.isBlank()) {
            return;
        }
        if (email.strip().length() > MAX_EMAIL_LENGTH) {
            throw new InvalidInputException("이메일은 " + MAX_EMAIL_LENGTH + "자 이하여야 합니다.");
        }
        if (!EMAIL_PATTERN.matcher(email.strip()).matches()) {
            throw new InvalidInputException("이메일 형식이 올바르지 않습니다: " + email);
        }
    }
}
