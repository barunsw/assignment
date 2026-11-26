from app.domain.phone_number import PhoneNumber


def test_phone_number_하이픈_유무가_달라도_같은_번호다():
    with_hyphen = PhoneNumber("010-1234-5678")
    without_hyphen = PhoneNumber("01012345678")

    assert with_hyphen == without_hyphen
    assert hash(with_hyphen) == hash(without_hyphen)


def test_phone_number_set에_같은_번호를_두_번_넣으면_하나만_남는다():
    phone_number_set = {PhoneNumber("010-1234-5678"), PhoneNumber("010 1234 5678")}

    assert len(phone_number_set) == 1


def test_phone_number_자릿수에_맞게_하이픈을_넣어_출력한다():
    assert str(PhoneNumber("01012345678")) == "010-1234-5678"
    assert str(PhoneNumber("0111234567")) == "011-123-4567"
    assert str(PhoneNumber("12345")) == "12345"


def test_phone_number_repr는_디버깅용으로_숫자만_보여준다():
    phone_number = PhoneNumber("010-1234-5678")

    assert repr(phone_number) == "PhoneNumber('01012345678')"
    # 리스트 같은 컨테이너를 문자열로 바꾸면 원소에는 __str__이 아니라 __repr__이 쓰인다.
    assert str([phone_number]) == "[PhoneNumber('01012345678')]"


def test_phone_number_로그용_문자열은_가운데_자리를_가린다():
    assert PhoneNumber("01012345678").to_masked_string() == "010-****-5678"


def test_phone_number_형식_검사():
    assert PhoneNumber("010-1234-5678").is_valid()
    assert PhoneNumber("011-123-4567").is_valid()
    assert not PhoneNumber("02-123-4567").is_valid()
    assert not PhoneNumber("010-12").is_valid()
    assert not PhoneNumber(None).is_valid()
