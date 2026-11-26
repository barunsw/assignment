package com.barun.contact.dto;

import com.barun.contact.domain.Contact;
import com.barun.contact.domain.PhoneNumber;

public record ContactResponse(Long id, String name, String phone, String email, Long groupId, String groupName) {

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
