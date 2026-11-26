package com.barunsw.contact.vo;

import java.util.List;

/**
 * 연락처 목록 한 페이지. totalCount는 이 페이지가 아니라 전체 연락처 수다.
 */
public record ContactPageResponse(int page, int size, long totalCount, List<ContactResponse> items) {
}
