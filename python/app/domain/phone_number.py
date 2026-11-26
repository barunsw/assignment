import re

NON_DIGIT_PATTERN = re.compile(r"[^0-9]")
VALID_DIGITS_PATTERN = re.compile(r"01\d{8,9}")
LONG_NUMBER_LENGTH = 11
SHORT_NUMBER_LENGTH = 10


class PhoneNumber:
    """전화번호 값 객체. 숫자만 남겨서 보관하므로 "010-1234-5678"과 "01012345678"은 같은 번호다."""

    def __init__(self, raw_phone: str | None):
        """입력에서 숫자만 남긴다. None도 빈 번호로 받아서, 형식이 맞는지는 is_valid() 한곳에서 판단하게 한다."""
        self._digits = NON_DIGIT_PATTERN.sub("", raw_phone or "")

    @property
    def digits(self) -> str:
        """숫자만 남긴 번호. DB에는 이 값을 저장한다. 읽기 전용이라 만든 뒤 바뀌지 않는다."""
        return self._digits

    def is_valid(self) -> bool:
        """01로 시작하는 10~11자리인지 확인한다."""
        return VALID_DIGITS_PATTERN.fullmatch(self._digits) is not None

    def to_masked_string(self) -> str:
        """로그에 남길 때 쓴다. 가운데 자리를 가린다. 예: 010-****-5678"""
        part_list = str(self).split("-")
        if len(part_list) != 3:
            return "*" * len(self._digits)
        return f"{part_list[0]}-{'*' * len(part_list[1])}-{part_list[2]}"

    def __eq__(self, other: object) -> bool:
        """숫자가 같으면 같은 번호다. PhoneNumber가 아니면 NotImplemented를 돌려 파이썬이 상대 쪽 비교를 시도하게 한다."""
        if not isinstance(other, PhoneNumber):
            return NotImplemented
        return self._digits == other._digits

    def __hash__(self) -> int:
        """__eq__와 같은 값(숫자)으로 만든다. set은 해시로 자리를 먼저 찾고 그 안에서 __eq__로 비교한다.

        __eq__만 정의하면 파이썬이 __hash__를 None으로 바꿔서 set이나 dict 키로 쓸 수 없게 된다.
        """
        return hash(self._digits)

    def __str__(self) -> str:
        """화면/응답용 형식. 11자리는 3-4-4, 10자리는 3-3-4, 그 밖에는 숫자 그대로."""
        digits = self._digits
        if len(digits) == LONG_NUMBER_LENGTH:
            return f"{digits[:3]}-{digits[3:7]}-{digits[7:]}"
        elif len(digits) == SHORT_NUMBER_LENGTH:
            return f"{digits[:3]}-{digits[3:6]}-{digits[6:]}"
        else:
            return digits

    def __repr__(self) -> str:
        """디버깅용 표현. 리스트처럼 여러 값을 담은 객체를 문자열로 바꾸면 원소에는 __str__이 아니라 이것이 쓰인다."""
        return f"PhoneNumber('{self._digits}')"
