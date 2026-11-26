package com.barunsw.contact.vo;

/**
 * 연락처 등록 요청 본문.
 * 필수 여부와 형식은 ContactValidator 구현들이 검사하므로, 여기서는 모든 필드를 null이어도 받는다.
 */
public record ContactCreateRequest(String name, String phone, String email, Long groupId) {
}
