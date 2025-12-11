package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.backend.dto.AdoptionApplicationDTO;
import org.example.backend.dto.AdoptionApplicationOrgDTO;
import org.example.backend.entity.AdoptionApplication;
import org.example.backend.entity.Animals;
import org.example.backend.entity.Organization;
import org.example.backend.entity.User;
import org.example.backend.exception.UserNotFoundException;
import org.example.backend.mapper.AdoptionApplicationMapper;
import org.example.backend.mapper.AnimalMapper;
import org.example.backend.mapper.OrganizationMapper;
import org.example.backend.mapper.UserMapper;
import org.example.backend.service.AdoptionApplicationService;
import org.example.backend.service.OrganizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdoptionApplicationServiceImpl implements AdoptionApplicationService {
    private final AdoptionApplicationMapper adoptionApplicationMapper;
    private final AnimalMapper animalMapper;
    private final OrganizationMapper organizationMapper;
    private final OrganizationService organizationService;
    private final UserMapper userMapper;  // 需要添加这个Mapper

    public AdoptionApplicationServiceImpl(AdoptionApplicationMapper adoptionApplicationMapper,
                                          AnimalMapper animalMapper,
                                          OrganizationMapper organizationMapper,
                                          OrganizationService organizationService,
                                          UserMapper userMapper) {  // 添加这个参数
        this.adoptionApplicationMapper = adoptionApplicationMapper;
        this.animalMapper = animalMapper;
        this.organizationMapper = organizationMapper;
        this.organizationService = organizationService;
        this.userMapper = userMapper;  // 初始化
    }

    // 用户提交申请
    @Override
    @Transactional
    public void createApplication(AdoptionApplication app, Long userId) {
        Animals animal = animalMapper.selectById(app.getAnimalId());
        if (animal == null) throw new UserNotFoundException("动物不存在");
        if (!"available".equals(animal.getStatus())) {
            throw new RuntimeException("该动物不可申请");
        }

        Long count = adoptionApplicationMapper.selectCount(
                new LambdaQueryWrapper<AdoptionApplication>()
                        .eq(AdoptionApplication::getAnimalId, app.getAnimalId())
                        .eq(AdoptionApplication::getUserId, userId)
                        .eq(AdoptionApplication::getStatus, "pending")
        );
        if (count > 0) throw new RuntimeException("已提交过申请，请等待审核");

        app.setUserId(userId);
        app.setStatus("pending");
        app.setCreatedAt(LocalDateTime.now());
        adoptionApplicationMapper.insert(app);
    }

    // 用户取消申请
    @Override
    @Transactional
    public void cancelApplication(Long applicationId, Long userId) {
        AdoptionApplication app = adoptionApplicationMapper.selectById(applicationId);
        if (app == null) throw new RuntimeException("申请不存在");
        if (!app.getUserId().equals(userId)) throw new RuntimeException("不能取消别人的申请");
        if (!"pending".equals(app.getStatus())) throw new RuntimeException("只有待审核的申请可以取消");

        app.setStatus("cancelled");
        app.setUpdatedAt(LocalDateTime.now());
        adoptionApplicationMapper.updateById(app);
    }

    @Override
    public AdoptionApplication getById(Long id) {
        return adoptionApplicationMapper.selectById(id);
    }

    /**
     * 用户的自己申请（原始方法）
     */
    @Override
    public Page<AdoptionApplication> listUserApplications(Long userId, int pageNum, int pageSize) {
        Page<AdoptionApplication> page = new Page<>(pageNum, pageSize);
        return adoptionApplicationMapper.selectPage(
                page,
                new LambdaQueryWrapper<AdoptionApplication>()
                        .eq(AdoptionApplication::getUserId, userId)
                        .orderByDesc(AdoptionApplication::getCreatedAt)
        );
    }

    /**
     * 用户的自己申请（封装动物信息 DTO + 一次性多表查询）
     */
    @Override
    public Page<AdoptionApplicationDTO> listUserApplicationsWithAnimal(Long userId, int pageNum, int pageSize) {
        Page<AdoptionApplication> page = new Page<>(pageNum, pageSize);
        page = adoptionApplicationMapper.selectPage(
                page,
                new LambdaQueryWrapper<AdoptionApplication>()
                        .eq(AdoptionApplication::getUserId, userId)
                        .orderByDesc(AdoptionApplication::getCreatedAt)
        );

        // 获取所有动物 ID
        List<Long> animalIds = page.getRecords().stream()
                .map(AdoptionApplication::getAnimalId)
                .collect(Collectors.toList());

        // 一次性查询所有动物信息
        List<Animals> animals = animalIds.isEmpty()
                ? Collections.emptyList()
                : animalMapper.selectBatchIds(animalIds);

        // 转换为 DTO
        List<AdoptionApplicationDTO> dtoList = page.getRecords().stream().map(app -> {
            AdoptionApplicationDTO dto = new AdoptionApplicationDTO();
            dto.setId(app.getId());
            dto.setAnimalId(app.getAnimalId());
            dto.setStatus(app.getStatus());
            dto.setReason(app.getReason());
            dto.setCreatedAt(app.getCreatedAt());

            // 匹配动物
            animals.stream()
                    .filter(animal -> animal.getId().equals(app.getAnimalId()))
                    .findFirst()
                    .ifPresent(animal -> {
                        dto.setAnimalName(animal.getName());
                        dto.setAge(animal.getAge());
                        dto.setBreed(animal.getBreed());
                    });
            return dto;
        }).collect(Collectors.toList());

        Page<AdoptionApplicationDTO> dtoPage = new Page<>();
        dtoPage.setRecords(dtoList);
        dtoPage.setCurrent(page.getCurrent());
        dtoPage.setSize(page.getSize());
        dtoPage.setTotal(page.getTotal());
        dtoPage.setPages(page.getPages());

        return dtoPage;
    }

    /**
     * 组织查看自己收到的申请 - 返回组织专用DTO
     */
    @Override
    public Page<AdoptionApplicationOrgDTO> listOrgApplications(Long orgUserId, int pageNum, int pageSize, String status) {
        // 1. 验证组织权限
        Organization org = organizationService.getByUserId(orgUserId);
        if (org == null) {
            throw new RuntimeException("你没有组织权限");
        }
        System.out.println("=== 调试信息 ===");
        System.out.println("组织用户ID: " + orgUserId);
        System.out.println("查询到的组织信息: " + org);
        System.out.println("组织ID: " + org.getId());

        // 2. 查询申请列表
        Page<AdoptionApplication> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AdoptionApplication> wrapper = new LambdaQueryWrapper<AdoptionApplication>()
                .orderByDesc(AdoptionApplication::getCreatedAt);

        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(AdoptionApplication::getStatus, status.toUpperCase());
        }

        // 只查询属于本组织的动物相关的申请
        wrapper.inSql(
                AdoptionApplication::getAnimalId,
                "SELECT id FROM animals WHERE organization_id = " + org.getId()
        );

        page = adoptionApplicationMapper.selectPage(page, wrapper);

        // 如果没有数据，直接返回空
        if (page.getRecords().isEmpty()) {
            return new Page<>();
        }

        // 3. 收集所有动物ID和用户ID
        List<Long> animalIds = page.getRecords().stream()
                .map(AdoptionApplication::getAnimalId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> userIds = page.getRecords().stream()
                .map(AdoptionApplication::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 4. 批量查询动物信息
        List<Animals> animals = animalIds.isEmpty()
                ? Collections.emptyList()
                : animalMapper.selectBatchIds(animalIds);
        Map<Long, Animals> animalMap = animals.stream()
                .collect(Collectors.toMap(Animals::getId, animal -> animal));

        // 5. 批量查询用户信息
        List<User> users = userIds.isEmpty()
                ? Collections.emptyList()
                : userMapper.selectBatchIds(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // 6. 转换为组织专用DTO
        List<AdoptionApplicationOrgDTO> dtoList = page.getRecords().stream().map(app -> {
            AdoptionApplicationOrgDTO dto = new AdoptionApplicationOrgDTO();

            // 设置申请基本信息
            dto.setId(app.getId());
            dto.setAnimalId(app.getAnimalId());
            dto.setUserId(app.getUserId());
            dto.setStatus(app.getStatus());
            dto.setReason(app.getReason());
            dto.setCreatedAt(app.getCreatedAt());
            dto.setUpdatedAt(app.getUpdatedAt());

            // 设置动物信息
            Animals animal = animalMap.get(app.getAnimalId());
            if (animal != null) {
                dto.setAnimalName(animal.getName());
                dto.setBreed(animal.getBreed());
                dto.setAge(animal.getAge());
                dto.setGender(animal.getGender());
                dto.setAnimalImageUrl(animal.getImageUrl());
                dto.setAnimalType(animal.getType());
            }

            // 设置用户信息
            User user = userMap.get(app.getUserId());
            if (user != null) {
                dto.setApplicantName(user.getUsername());
                dto.setApplicantEmail(user.getEmail());
                dto.setApplicantPhone(user.getPhone());
            }

            return dto;
        }).collect(Collectors.toList());

        // 7. 创建返回的Page对象
        Page<AdoptionApplicationOrgDTO> dtoPage = new Page<>();
        dtoPage.setRecords(dtoList);
        dtoPage.setCurrent(page.getCurrent());
        dtoPage.setSize(page.getSize());
        dtoPage.setTotal(page.getTotal());
        dtoPage.setPages(page.getPages());

        return dtoPage;
    }

    /**
     * 组织同意申请
     */
    @Override
    @Transactional
    public void approve(Long applicationId, Long orgUserId) {
        AdoptionApplication app = adoptionApplicationMapper.selectById(applicationId);
        if (app == null) throw new RuntimeException("申请不存在");

        Animals animal = animalMapper.selectById(app.getAnimalId());
        if (animal == null) throw new RuntimeException("动物不存在");

        Organization org = organizationService.getByUserId(orgUserId);
        if (org == null || !org.getId().equals(animal.getOrganizationId())) {
            throw new RuntimeException("你无权审核此申请");
        }

        if (!"pending".equals(app.getStatus()))
            throw new RuntimeException("申请状态错误，无法审核");

        app.setStatus("approved");
        app.setUpdatedAt(LocalDateTime.now());
        adoptionApplicationMapper.updateById(app);

        animal.setStatus("adopted");
        animalMapper.updateById(animal);
    }

    /**
     * 组织拒绝申请
     */
    @Override
    @Transactional
    public void reject(Long applicationId, Long orgUserId) {
        AdoptionApplication app = adoptionApplicationMapper.selectById(applicationId);
        if (app == null) throw new RuntimeException("申请不存在");

        Animals animal = animalMapper.selectById(app.getAnimalId());
        if (animal == null) throw new RuntimeException("动物不存在");

        Organization org = organizationService.getByUserId(orgUserId);
        if (org == null || !org.getId().equals(animal.getOrganizationId())) {
            throw new RuntimeException("你无权审核此申请");
        }

        if (!"pending".equals(app.getStatus()))
            throw new RuntimeException("申请不可再审核");

        app.setStatus("rejected");
        app.setUpdatedAt(LocalDateTime.now());
        adoptionApplicationMapper.updateById(app);
    }
}