package com.barun.contact.mapper;

import com.barun.contact.domain.ContactGroup;
import com.barun.contact.domain.GroupSummary;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * contact_group 테이블 SQL. SQL은 resources/mapper/GroupMapper.xml에 있다.
 */
@Mapper
public interface GroupMapper {

    /**
     * 전체 그룹 목록(id 순).
     */
    List<ContactGroup> selectGroupList();

    /**
     * id로 그룹 하나를 조회한다. 없으면 null.
     */
    ContactGroup selectGroupById(@Param("id") long id);

    /**
     * 그룹별 연락처 수. 연락처가 없는 그룹도 포함한다.
     */
    List<GroupSummary> selectGroupSummaryList();
}
