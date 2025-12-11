package org.example.backend.controller;

import org.example.backend.common.R;
import org.example.backend.dto.OrganizationDTO;
import org.example.backend.dto.UserDTO;
import org.example.backend.entity.Organization;
import org.example.backend.entity.User;
import org.example.backend.service.AdminService;
import org.example.backend.util.DTOConverter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public R<List<UserDTO>> listUsers() {
        return R.ok(DTOConverter.toUserDTOList(adminService.listUsers()));
    }

    @GetMapping("/organizations")
    public R<List<OrganizationDTO>> listOrganizations() {
        return R.ok(DTOConverter.toOrganizationDTOList(adminService.listOrganizations()));
    }


    // 审核通过
    @PutMapping("/organizations/{orgId}/approve")
    public R<?> approveOrganization(@PathVariable Long orgId) {
        adminService.approveOrganization(orgId);
        return R.okMsg("组织审核通过");
    }

    // 审核拒绝
    @PutMapping("/organizations/{orgId}/reject")
    public R<?> rejectOrganization(@PathVariable Long orgId) {
        adminService.rejectOrganization(orgId);
        return R.okMsg("组织审核拒绝");
    }

    // 封禁/解封用户
    @PutMapping("/users/{userId}/enable")
    public R<?> setUserEnabled(
            @PathVariable Long userId,
            @RequestParam boolean enabled
    ) {
        adminService.setUserEnabled(userId, enabled);
        return R.okMsg(enabled ? "用户已启用" : "用户已封禁");
    }

    // 封禁/解封组织
    @PutMapping("/organizations/{orgId}/enable")
    public R<?> setOrganizationEnabled(
            @PathVariable Long orgId,
            @RequestParam boolean enabled
    ) {
        adminService.setOrganizationEnabled(orgId, enabled);
        return R.okMsg(enabled ? "组织已启用" : "组织已封禁");
    }




}
