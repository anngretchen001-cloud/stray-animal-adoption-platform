package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.backend.entity.Animals;
import org.example.backend.exception.UserNotFoundException;
import org.example.backend.mapper.AnimalMapper;
import org.example.backend.service.AnimalService;
import org.example.backend.service.OrganizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnimalServiceImpl implements AnimalService {
    private final AnimalMapper animalMapper;;
    private final OrganizationService organizationService;
    public AnimalServiceImpl(AnimalMapper animalMapper, OrganizationService organizationService) {
        this.animalMapper = animalMapper;
        this.organizationService = organizationService;
    }

    @Override
    public void addAnimal(Animals animal)
    {
        animal.setStatus("available");
        animalMapper.insert(animal);
    }

    @Override
    @Transactional
    public void deleteAnimal(Long animalId,Long currentUserId,String role)
    {
        Animals animal = animalMapper.selectById(animalId);
        if(animal==null) throw new UserNotFoundException("动物不存在");

        //权限校验，只有动物发布组织以及管理员才能删除
        if(!role.equals("ADMIN") && !animal.getOrganizationId().equals(organizationService.getByUserId(currentUserId).getId())){
            throw new RuntimeException("没有权限删除该动物");
        }
        animalMapper.deleteById(animalId);
    }

    @Override
    @Transactional
    public void updateAnimal(Animals animal, Long currentUserId, String role) {
        Animals exist = animalMapper.selectById(animal.getId());
        if (exist == null) throw new UserNotFoundException("动物不存在");


        // 权限校验
        if (!role.equals("ADMIN") && !exist.getOrganizationId().equals(organizationService.getByUserId(currentUserId).getId())) {
            throw new RuntimeException("没有权限更新该动物");
        }
        animalMapper.updateById(animal);
    }

    @Override
    public Animals getById(Long animalId) {
        Animals animal = animalMapper.selectById(animalId);
        if (animal == null) throw new UserNotFoundException("动物不存在");
        return animal;
    }

    @Override
    public Page<Animals> listAnimals(int pageNum, int pageSize,
                                     String type, String status,
                                     String search, String sortByAge) {
        Page<Animals> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Animals> wrapper = new QueryWrapper<>();

        // 类型和状态过滤
        if (type != null && !type.isEmpty()) wrapper.eq("type", type);
        if (status != null && !status.isEmpty()) wrapper.eq("status", status);

        // 模糊搜索：名字或品种
        if (search != null && !search.isEmpty()) {
            wrapper.and(w -> w.like("name", search)
                    .or()
                    .like("breed", search));
        }

        // 按年龄排序
        if ("asc".equalsIgnoreCase(sortByAge)) {
            wrapper.orderByAsc("age");
        } else if ("desc".equalsIgnoreCase(sortByAge)) {
            wrapper.orderByDesc("age");
        }

        return animalMapper.selectPage(page, wrapper);
    }
    @Override
    public Page<Animals> listAnimalsByOrgId(Long orgId, int pageNum, int pageSize) {
        Page<Animals> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Animals> wrapper = new QueryWrapper<>();
        wrapper.eq("organization_id", orgId);
        return animalMapper.selectPage(page, wrapper);
    }






}
