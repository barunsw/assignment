package com.barun.contact.controller;

import com.barun.contact.dto.GroupResponse;
import com.barun.contact.service.GroupService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public List<GroupResponse> getGroupList() {
        return groupService.getGroupList();
    }
}
