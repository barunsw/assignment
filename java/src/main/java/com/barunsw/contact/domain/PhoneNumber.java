package com.barunsw.contact.domain;

import java.util.regex.Pattern;

/**
 * 전화번호 값 객체. 숫자만 남겨서 보관하므로 "010-1234-5678"과 "01012345678"은 같은 번호다.
 */
public final class PhoneNumber {

    private static final Pattern NON_DIGIT_PATTERN = Pattern.compile("[^0-9]");
    private static final Pattern VALID_DIGITS_PATTERN = Pattern.compile("01\\d{8,9}");
    private static final int LONG_NUMBER_LENGTH = 11;
    private static final int SHORT_NUMBER_LENGTH = 10;

    // 만든 뒤 바뀌지 않는다. 그래서 HashSet이나 HashMap의 키로 써도 안전하다.
    private final String digits;

    // 숫자만 남기는 처리를 빠뜨리지 않도록 생성은 of()로만 한다.
    private PhoneNumber(String digits) {
        this.digits = digits;
    }

    /**
     * 입력에서 숫자만 남겨 만든다. null도 빈 번호로 받아서, 형식이 맞는지는 isValid() 한곳에서 판단하게 한다.
     */
    public static PhoneNumber of(String rawPhone) {
        if (rawPhone == null) {
            return new PhoneNumber("");
        }
        return new PhoneNumber(NON_DIGIT_PATTERN.matcher(rawPhone).replaceAll(""));
    }

    /**
     * 숫자만 남긴 번호. DB에는 이 값을 저장한다.
     */
    public String getDigits() {
        return digits;
    }

    /**
     * 01로 시작하는 10~11자리인지 확인한다.
     */
    public boolean isValid() {
        return VALID_DIGITS_PATTERN.matcher(digits).matches();
    }

    /**
     * 로그에 남길 때 쓴다. 가운데 자리를 가린다. 예: 010-****-5678
     */
    public String toMaskedString() {
        String[] partArray = toString().split("-");
        if (partArray.length != 3) {
            return "*".repeat(digits.length());
        }
        return partArray[0] + "-" + "*".repeat(partArray[1].length()) + "-" + partArray[2];
    }

    /**
     * 숫자가 같으면 같은 번호다. 하이픈이 있든 없든 같다.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PhoneNumber otherPhoneNumber)) {
            return false;
        }
        return digits.equals(otherPhoneNumber.digits);
    }

    /**
     * equals와 같은 값(digits)으로 만든다.
     * HashSet은 hashCode로 자리를 먼저 찾고 그 안에서 equals로 비교하므로, 둘이 어긋나면 같은 번호가 두 번 들어간다.
     */
    @Override
    public int hashCode() {
        return digits.hashCode();
    }

    /**
     * 화면/응답용 형식. 11자리는 3-4-4, 10자리는 3-3-4, 그 밖에는 숫자 그대로.
     */
    @Override
    public String toString() {
        if (digits.length() == LONG_NUMBER_LENGTH) {
            return digits.substring(0, 3) + "-" + digits.substring(3, 7) + "-" + digits.substring(7);
        } else if (digits.length() == SHORT_NUMBER_LENGTH) {
            return digits.substring(0, 3) + "-" + digits.substring(3, 6) + "-" + digits.substring(6);
        } else {
            return digits;
        }
    }
}
