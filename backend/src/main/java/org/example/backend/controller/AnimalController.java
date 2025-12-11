package org.example.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.common.R;
import org.example.backend.entity.Animals;
import org.example.backend.entity.Organization;
import org.example.backend.service.AnimalService;
import org.example.backend.service.OrganizationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {

    private final AnimalService animalService;
    private final OrganizationService organizationService;

    public AnimalController(AnimalService animalService, OrganizationService organizationService) {
        this.animalService = animalService;
        this.organizationService = organizationService;
    }

    /** 添加动物 */
    @PostMapping("/add")
    public R<?> addAnimal(HttpServletRequest request, @RequestBody Animals animal) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        if (!"ORG".equals(role)) {
            return R.error("无权限发布动物");
        }

        // 查找当前用户对应组织
        Organization org = organizationService.getByUserId(userId);
        if (org == null) {
            return R.error("组织不存在");
        }

        animal.setOrganizationId(org.getId());
        animalService.addAnimal(animal);

        return R.okMsg("添加成功");
    }

    /** 删除动物 */
    @DeleteMapping("/{id}")
    public R<?> deleteAnimal(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        animalService.deleteAnimal(id, userId, role);
        return R.okMsg("删除成功");
    }

    /** 更新动物 */
    @PutMapping("/{id}")
    public R<?> updateAnimal(HttpServletRequest request,
                             @PathVariable Long id,
                             @RequestBody Animals animal) {

        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        animal.setId(id);
        animalService.updateAnimal(animal, userId, role);

        return R.okMsg("更新成功");
    }

    /** 查询动物详情 */
    @GetMapping("/{id}")
    public R<Animals> getAnimal(@PathVariable Long id) {
        return R.ok(animalService.getById(id));
    }
    @GetMapping("/org")
    public R<Page<Animals>> listOrgAnimals(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId"); // 从token解析来的userId
        if(userId == null) return R.error("未登录");

        // 获取组织id
        Organization org = organizationService.getByUserId(userId);
        if(org == null) return R.error("当前用户没有所属组织");

        Page<Animals> page = animalService.listAnimalsByOrgId(org.getId(), pageNum, pageSize);
        return R.ok(page);
    }


    @GetMapping("/list")
    public R<Page<Animals>> listAnimals(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,    // 新增模糊搜索参数
            @RequestParam(required = false) String sortByAge  // 新增排序参数
    ) {
        return R.ok(animalService.listAnimals(pageNum, pageSize, type, status, search, sortByAge));
    }

}
