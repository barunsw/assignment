package com.barun.contact.controller;

import com.barun.contact.service.GroupService;
import com.barun.contact.vo.GroupResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 그룹 API. 그룹은 시작할 때 data.sql로 넣으므로 조회만 있다.
 */
@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * 그룹 목록과 그룹별 연락처 수를 조회한다.
     */
    @GetMapping
    public List<GroupResponse> getGroupList() {
        return groupService.getGroupList();
    }
}
