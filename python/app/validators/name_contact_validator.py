from logging import Logger

from app.exceptions.invalid_input_exception import InvalidInputException
from app.models.contact_model import ContactCreateRequest
from app.validators.contact_validator import ContactValidator

MAX_NAME_LENGTH = 50


class NameContactValidator(ContactValidator):
    """이름: 필수, 앞뒤 공백 제외 50자 이하."""

    def __init__(self, logger: Logger):
        """의존 객체를 주입받는다."""
        self.logger = logger

    def validate(self, request: ContactCreateRequest) -> None:
        """이름을 검증한다."""
        name = request.name
        if name is None or not name.strip():
            raise InvalidInputException("이름은 필수입니다.")
        if len(name.strip()) > MAX_NAME_LENGTH:
            raise InvalidInputException(f"이름은 {MAX_NAME_LENGTH}자 이하여야 합니다.")
