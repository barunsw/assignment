package com.barun.contact.domain;

import java.util.regex.Pattern;

/**
 * 전화번호 값 객체. 숫자만 남겨서 보관하므로 "010-1234-5678"과 "01012345678"은 같은 번호다.
 */
public final class PhoneNumber {

    private static final Pattern NON_DIGIT_PATTERN = Pattern.compile("[^0-9]");
    private static final Pattern VALID_DIGITS_PATTERN = Pattern.compile("01\\d{8,9}");
    private static final int LONG_NUMBER_LENGTH = 11;
    private static final int SHORT_NUMBER_LENGTH = 10;

    private final String digits;

    private PhoneNumber(String digits) {
        this.digits = digits;
    }

    public static PhoneNumber of(String rawPhone) {
        if (rawPhone == null) {
            return new PhoneNumber("");
        }
        return new PhoneNumber(NON_DIGIT_PATTERN.matcher(rawPhone).replaceAll(""));
    }

    public String getDigits() {
        return digits;
    }

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
