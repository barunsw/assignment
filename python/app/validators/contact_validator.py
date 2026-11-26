from abc import ABC, abstractmethod

from app.models.contact_model import ContactCreateRequest


class ContactValidator(ABC):
    """연락처 입력 검증 규칙 하나. 규칙에 맞지 않으면 InvalidInputException을 던진다.

    구현 객체를 app_factory.py에서 목록으로 만들어 ContactService에 넣는다. 그래서 규칙을 추가할 때 서비스 코드는 고치지 않는다.
    """

    @abstractmethod
    def validate(self, request: ContactCreateRequest) -> None:
        """요청을 검증한다."""
        pass
