package com.barunsw.contact.validator;

import com.barunsw.contact.exception.InvalidInputException;
import com.barunsw.contact.vo.ContactCreateRequest;
import java.util.regex.Pattern;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 이메일: 선택, 있으면 아이디@도메인 형식이고 100자 이하.
 */
@Component
@Order(3)
public class EmailContactValidator implements ContactValidator {

    // 완전한 이메일 문법 검사 대신 "@ 앞뒤에 글자가 있고 도메인에 점이 있다"만 본다. 흔한 입력 실수를 거르는 정도면 충분하다.
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
