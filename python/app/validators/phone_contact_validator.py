from logging import Logger

from app.domain.phone_number import PhoneNumber
from app.exceptions.invalid_input_exception import InvalidInputException
from app.models.contact_model import ContactCreateRequest
from app.validators.contact_validator import ContactValidator


class PhoneContactValidator(ContactValidator):
    """전화번호: 필수, 숫자만 남겨서 01로 시작하는 10~11자리."""

    def __init__(self, logger: Logger):
        self.logger = logger

    def validate(self, request: ContactCreateRequest) -> None:
        """전화번호를 검증한다."""
        if request.phone is None or not request.phone.strip():
            raise InvalidInputException("전화번호는 필수입니다.")
        if not PhoneNumber(request.phone).is_valid():
            raise InvalidInputException(f"전화번호 형식이 올바르지 않습니다: {request.phone}")
