package com.barunsw.contact.vo;

/**
 * 그룹과 그 그룹에 속한 연락처 수.
 */
public record GroupResponse(Long id, String name, long contactCount) {

    /**
     * 그룹별 집계 결과를 응답으로 바꾼다.
     */
    public static GroupResponse from(GroupSummary groupSummary) {
        return new GroupResponse(groupSummary.getId(), groupSummary.getName(), groupSummary.getContactCount());
    }
}
