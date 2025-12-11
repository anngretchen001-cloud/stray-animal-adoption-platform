package org.example.backend.service.impl;
import org.example.backend.entity.Organization;
import org.example.backend.entity.User;
import org.example.backend.exception.UserNotFoundException;
import org.example.backend.mapper.OrganizationMapper;
import org.example.backend.mapper.UserMapper;
import org.example.backend.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserMapper userMapper;
    private final OrganizationMapper organizationMapper;

    public AdminServiceImpl(UserMapper userMapper, OrganizationMapper organizationMapper) {
        this.userMapper = userMapper;
        this.organizationMapper = organizationMapper;
    }
    @Override
    public void setUserEnabled(Long userId, boolean enabled) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setEnabled(enabled); // enabled 字段要在 User 实体里添加
        userMapper.updateById(user);
    }

    @Override
    public void setOrganizationEnabled(Long orgId, boolean enabled) {
        Organization org = organizationMapper.selectById(orgId);
        if (org == null) {
            throw new RuntimeException("组织不存在");
        }
        org.setEnabled(enabled); // enabled 字段要在 Organization 实体里添加
        organizationMapper.updateById(org);
    }

    //查询所有用户
    @Override
    public List<User> listUsers() {
        return userMapper.selectList(null); // 查询所有用户
    }
    //查询所有组织
    @Override
    public List<Organization> listOrganizations() {
        return organizationMapper.selectList(null); // 查询所有组织
    }
    //通过组织资质
    @Override
    @Transactional
    //确保组织和用户状态的更新操作要么全部成功，要么全部失败回滚，从而保证数据的一致性。
    public void approveOrganization(Long orgId) {
        Organization org = organizationMapper.selectById(orgId);
        if (org == null) throw new UserNotFoundException("组织不存在");

        // 更新组织状态
        org.setStatus("approved");
        organizationMapper.updateById(org);

        // 更新对应用户 role 为 ORG
        User user = userMapper.selectById(org.getUserId());
        if (user != null) {
            user.setRole("ORG");
            userMapper.updateById(user);
        }
    }

    @Override
    @Transactional
    public void rejectOrganization(Long orgId) {
        Organization org = organizationMapper.selectById(orgId);
        if (org == null) throw new UserNotFoundException("组织不存在");

        // 更新组织状态
        org.setStatus("rejected");
        organizationMapper.updateById(org);

        // 更新对应用户 role 回退为 USER
        User user = userMapper.selectById(org.getUserId());
        if (user != null) {
            user.setRole("USER");
            userMapper.updateById(user);
        }
    }

}
