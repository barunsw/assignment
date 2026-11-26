from abc import ABC, abstractmethod

from app.models.contact_model import ContactCreateRequest


class ContactValidator(ABC):
    """연락처 입력 검증 규칙 하나. 규칙에 맞지 않으면 InvalidInputException을 던진다."""

    @abstractmethod
    def validate(self, request: ContactCreateRequest) -> None:
        """요청을 검증한다."""
        pass
