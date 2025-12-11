package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.backend.entity.Organization;
import org.example.backend.entity.User;
import org.example.backend.exception.UserNotFoundException;
import org.example.backend.mapper.OrganizationMapper;
import org.example.backend.mapper.UserMapper;
import org.example.backend.service.OrganizationService;
import org.example.backend.security.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final UserMapper userMapper;
    private final OrganizationMapper organizationMapper;

    public OrganizationServiceImpl(UserMapper userMapper, OrganizationMapper organizationMapper) {
        this.userMapper = userMapper;
        this.organizationMapper = organizationMapper;
    }

    @Override
    @Transactional
    public void registerOrganization(User user, Organization organization) {
        // 密码加密
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setRole("USER"); // 初始角色普通用户
        userMapper.insert(user);

        // 设置 organization.userId
        organization.setUserId(user.getId());
        organization.setStatus("pending"); // 默认待审核
        organizationMapper.insert(organization);
    }

    @Override
    public Organization getByUserId(Long userId) {
        QueryWrapper<Organization> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return organizationMapper.selectOne(wrapper);
    }
}
