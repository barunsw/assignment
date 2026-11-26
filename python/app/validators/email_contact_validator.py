import re
from logging import Logger

from app.exceptions.invalid_input_exception import InvalidInputException
from app.models.contact_model import ContactCreateRequest
from app.validators.contact_validator import ContactValidator

# 완전한 이메일 문법 검사 대신 "@ 앞뒤에 글자가 있고 도메인에 점이 있다"만 본다. 흔한 입력 실수를 거르는 정도면 충분하다.
EMAIL_PATTERN = re.compile(r"[^@\s]+@[^@\s]+\.[^@\s]+")
MAX_EMAIL_LENGTH = 100


class EmailContactValidator(ContactValidator):
    """이메일: 선택, 있으면 아이디@도메인 형식이고 100자 이하."""

    def __init__(self, logger: Logger):
        """의존 객체를 주입받는다."""
        self.logger = logger

    def validate(self, request: ContactCreateRequest) -> None:
        """이메일을 검증한다."""
        email = request.email
        if email is None or not email.strip():
            return
        if len(email.strip()) > MAX_EMAIL_LENGTH:
            raise InvalidInputException(f"이메일은 {MAX_EMAIL_LENGTH}자 이하여야 합니다.")
        if EMAIL_PATTERN.fullmatch(email.strip()) is None:
            raise InvalidInputException(f"이메일 형식이 올바르지 않습니다: {email}")
