package com.barunsw.contact.vo;

import com.barunsw.contact.domain.PhoneNumber;

/**
 * contact 테이블 한 행 + 조인한 그룹 이름. phone에는 숫자만 들어 있다.
 */
public class Contact {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private Long groupId;
    private String groupName;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * 로그에 개인정보가 그대로 남지 않도록 전화번호를 가리고 이메일은 뺀다.
     */
    @Override
    public String toString() {
        return "Contact{id=" + id
                + ", name='" + name + "'"
                + ", phone='" + PhoneNumber.of(phone).toMaskedString() + "'"
                + ", groupId=" + groupId + "}";
    }
}
