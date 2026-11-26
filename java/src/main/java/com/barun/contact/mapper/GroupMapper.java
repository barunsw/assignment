package com.barun.contact.mapper;

import com.barun.contact.domain.ContactGroup;
import com.barun.contact.domain.GroupSummary;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GroupMapper {

    List<ContactGroup> selectGroupList();

    ContactGroup selectGroupById(@Param("id") long id);

    List<GroupSummary> selectGroupSummaryList();
}
