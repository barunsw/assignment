package com.barun.contact.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PhoneNumberTest {

    @Test
    void 하이픈_유무가_달라도_같은_번호다() {
        PhoneNumber withHyphen = PhoneNumber.of("010-1234-5678");
        PhoneNumber withoutHyphen = PhoneNumber.of("01012345678");

        assertThat(withHyphen).isEqualTo(withoutHyphen);
        assertThat(withHyphen.hashCode()).isEqualTo(withoutHyphen.hashCode());
    }

    @Test
    void Set에_같은_번호를_두_번_넣으면_하나만_남는다() {
        Set<PhoneNumber> phoneNumberSet = new HashSet<>();
        phoneNumberSet.add(PhoneNumber.of("010-1234-5678"));
        phoneNumberSet.add(PhoneNumber.of("010 1234 5678"));

        assertThat(phoneNumberSet).hasSize(1);
    }

    @Test
    void 자릿수에_맞게_하이픈을_넣어_출력한다() {
        assertThat(PhoneNumber.of("01012345678")).hasToString("010-1234-5678");
        assertThat(PhoneNumber.of("0111234567")).hasToString("011-123-4567");
        assertThat(PhoneNumber.of("12345")).hasToString("12345");
    }

    @Test
    void 로그용_문자열은_가운데_자리를_가린다() {
        assertThat(PhoneNumber.of("01012345678").toMaskedString()).isEqualTo("010-****-5678");
    }

    @Test
    void 형식_검사() {
        assertThat(PhoneNumber.of("010-1234-5678").isValid()).isTrue();
        assertThat(PhoneNumber.of("011-123-4567").isValid()).isTrue();
        assertThat(PhoneNumber.of("02-123-4567").isValid()).isFalse();
        assertThat(PhoneNumber.of("010-12").isValid()).isFalse();
        assertThat(PhoneNumber.of(null).isValid()).isFalse();
    }
}
