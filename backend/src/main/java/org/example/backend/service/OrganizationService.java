package org.example.backend.service;

import org.example.backend.entity.Organization;
import org.example.backend.entity.User;

public interface OrganizationService {
    void registerOrganization(User user, Organization organization);  // 注册组织
    Organization getByUserId(Long id);
}
