package com.barun.contact.dto;

import com.barun.contact.domain.GroupSummary;

public record GroupResponse(Long id, String name, long contactCount) {

    public static GroupResponse from(GroupSummary groupSummary) {
        return new GroupResponse(groupSummary.getId(), groupSummary.getName(), groupSummary.getContactCount());
    }
}
