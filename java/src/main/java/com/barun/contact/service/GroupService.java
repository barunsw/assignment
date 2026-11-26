package com.barun.contact.service;

import com.barun.contact.dto.GroupResponse;
import com.barun.contact.mapper.GroupMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupService {

    private final GroupMapper groupMapper;

    public GroupService(GroupMapper groupMapper) {
        this.groupMapper = groupMapper;
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> getGroupList() {
        return groupMapper.selectGroupSummaryList().stream()
                .map(GroupResponse::from)
                .toList();
    }
}
