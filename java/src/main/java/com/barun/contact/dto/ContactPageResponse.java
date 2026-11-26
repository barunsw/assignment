package com.barun.contact.dto;

import java.util.List;

public record ContactPageResponse(int page, int size, long totalCount, List<ContactResponse> items) {
}
