package org.example.backend.service;

import org.example.backend.entity.User;
import org.example.backend.entity.Organization;

import java.util.List;

public interface AdminService {

    // 封禁或解封用户
    void setUserEnabled(Long userId, boolean enabled);

    // 封禁或解封组织
    void setOrganizationEnabled(Long orgId, boolean enabled);

    List<User> listUsers();

    List<Organization> listOrganizations();

    void approveOrganization(Long orgId);

    void rejectOrganization(Long orgId);
}
