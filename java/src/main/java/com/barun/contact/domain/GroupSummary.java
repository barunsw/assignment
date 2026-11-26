package com.barun.contact.domain;

/**
 * 그룹과 그 그룹에 속한 연락처 수.
 */
public class GroupSummary {

    private Long id;
    private String name;
    private long contactCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getContactCount() {
        return contactCount;
    }

    public void setContactCount(long contactCount) {
        this.contactCount = contactCount;
    }
}
