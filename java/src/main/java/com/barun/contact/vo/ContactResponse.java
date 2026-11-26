package com.barun.contact.vo;

import com.barun.contact.domain.Contact;
import com.barun.contact.domain.PhoneNumber;

/**
 * 연락처 응답. phone은 하이픈을 넣은 형식이다.
 */
public record ContactResponse(Long id, String name, String phone, String email, Long groupId, String groupName) {

    /**
     * DB 조회 결과를 응답으로 바꾼다.
     */
    public static ContactResponse from(Contact contact) {
        return new ContactResponse(
                contact.getId(),
                contact.getName(),
                PhoneNumber.of(contact.getPhone()).toString(),
                contact.getEmail(),
                contact.getGroupId(),
                contact.getGroupName());
    }
}
