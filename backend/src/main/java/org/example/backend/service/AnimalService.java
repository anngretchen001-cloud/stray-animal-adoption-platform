package org.example.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.backend.entity.Animals;

public interface AnimalService {
    // 添加动物
    void addAnimal(Animals animal);

    // 删除动物
    void deleteAnimal(Long animalId, Long currentUserId, String role);

    // 更新动物信息
    void updateAnimal(Animals animal, Long currentUserId, String role);

    // 根据ID查询
    Animals getById(Long animalId);

    // 分页查询所有动物，可按类型、状态过滤
    Page<Animals> listAnimals(int pageNum, int pageSize, String type, String status,String search, String sortByAge);

    Page<Animals> listAnimalsByOrgId(Long orgId, int pageNum, int pageSize);
}
