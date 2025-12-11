package org.example.backend.controller;

import org.example.backend.common.R;
import org.example.backend.entity.Organization;
import org.example.backend.entity.User;
import org.example.backend.service.OrganizationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /**
     * 注册组织
     * request: { "user": {...}, "organization": {...} }
     */
    @PostMapping("/register")
    public R<?> registerOrganization(@RequestBody Map<String, Object> request) {

        // user
        Map<String, String> userMap = (Map<String, String>) request.get("user");
        if (userMap == null) return R.error("缺少 user 信息");

        User user = new User();
        user.setUsername(userMap.get("username"));
        user.setPassword(userMap.get("password"));
        user.setEmail(userMap.get("email"));
        user.setPhone(userMap.get("phone"));
        user.setAvatarUrl(userMap.get("avatarUrl"));

        // organization
        Map<String, String> orgMap = (Map<String, String>) request.get("organization");
        if (orgMap == null) return R.error("缺少 organization 信息");

        Organization organization = new Organization();
        organization.setName(orgMap.get("name"));
        organization.setCertificateNo(orgMap.get("certificateNo"));
        organization.setContactName(orgMap.get("contactName"));
        organization.setEmail(orgMap.get("email"));
        organization.setPhone(orgMap.get("phone"));
        organization.setAddress(orgMap.get("address"));
        organization.setDescription(orgMap.get("description"));

        organizationService.registerOrganization(user, organization);

        return R.okMsg("组织注册成功，等待管理员审核");
    }
}
