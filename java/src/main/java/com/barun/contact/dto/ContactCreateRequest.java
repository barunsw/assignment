package com.barun.contact.dto;

public record ContactCreateRequest(String name, String phone, String email, Long groupId) {
}
