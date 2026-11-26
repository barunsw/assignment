package com.barun.contact.service;

import com.barun.contact.mapper.GroupMapper;
import com.barun.contact.vo.GroupResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 그룹 조회.
 */
@Service
public class GroupService {

    private final GroupMapper groupMapper;

    public GroupService(GroupMapper groupMapper) {
        this.groupMapper = groupMapper;
    }

    /**
     * 그룹마다 속한 연락처 수를 함께 돌려준다.
     */
    @Transactional(readOnly = true)
    public List<GroupResponse> getGroupList() {
        return groupMapper.selectGroupSummaryList().stream()
                .map(GroupResponse::from)
                .toList();
    }
}
