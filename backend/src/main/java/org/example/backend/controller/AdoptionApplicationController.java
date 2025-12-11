package org.example.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.backend.common.R;
import org.example.backend.dto.AdoptionApplicationDTO;
import org.example.backend.dto.AdoptionApplicationOrgDTO;
import org.example.backend.entity.AdoptionApplication;
import org.example.backend.entity.Organization;
import org.example.backend.service.AdoptionApplicationService;
import org.example.backend.service.OrganizationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class AdoptionApplicationController {

    private final AdoptionApplicationService adoptionApplicationService;
    private final OrganizationService organizationService;

    public AdoptionApplicationController(AdoptionApplicationService adoptionApplicationService,
                                         OrganizationService organizationService) {
        this.adoptionApplicationService = adoptionApplicationService;
        this.organizationService = organizationService;
    }


    /**
     * 提交领养申请
     */
    @PostMapping
    public R<?> createApplication(@RequestBody Map<String, Object> params,
                                  @RequestAttribute("userId") Long userId) {
        try {
            AdoptionApplication app = new AdoptionApplication();

            // 处理animalId
            Object animalIdObj = params.get("animalId");
            if (animalIdObj != null) {
                app.setAnimalId(Long.valueOf(animalIdObj.toString()));
            }

            // 处理申请理由：从前端的message映射到后端的reason
            Object message = params.get("message");
            if (message != null) {
                app.setReason(message.toString());
            }

            // 如果前端直接传reason，也可以支持
            Object reason = params.get("reason");
            if (reason != null) {
                app.setReason(reason.toString());
            }

            adoptionApplicationService.createApplication(app, userId);
            return R.ok("申请提交成功");

        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            return R.error("申请提交失败");
        }
    }

    /**
     * 用户取消申请
     */
    @PostMapping("/{id}/cancel")
    public R<?> cancelApplication(@PathVariable Long id,
                                  @RequestAttribute("userId") Long userId) {
        adoptionApplicationService.cancelApplication(id, userId);
        return R.okMsg("申请已取消");
    }

    /**
     * 用户查看自己的申请（分页）
     */
    @GetMapping("/my")
    public R<?> listMyApplications(@RequestAttribute("userId") Long userId,
                                   @RequestParam(defaultValue = "1") int pageNum,
                                   @RequestParam(defaultValue = "10") int pageSize) {

        Page<AdoptionApplicationDTO> page = adoptionApplicationService
                .listUserApplicationsWithAnimal(userId, pageNum, pageSize);

        return R.ok(page);
    }


    /**
     * 组织查看收到的申请（分页 + 可选状态筛选）- 返回组织专用DTO
     */
    @GetMapping("/org")
    public R<?> listOrgApplications(@RequestAttribute("userId") Long orgUserId,
                                    @RequestParam(defaultValue = "1") int pageNum,
                                    @RequestParam(defaultValue = "10") int pageSize,
                                    @RequestParam(required = false) String status) {

        try {
            // 找出当前用户所属组织
            Organization org = organizationService.getByUserId(orgUserId);
            if (org == null) {
                return R.error("你不是组织用户，无权限查看");
            }

            // 调用service方法，返回组织专用DTO
            Page<AdoptionApplicationOrgDTO> page = adoptionApplicationService
                    .listOrgApplications(orgUserId, pageNum, pageSize, status);

            return R.ok(page);

        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
//            log.error("组织查询申请列表失败", e);
            return R.error("查询申请列表失败");
        }
    }

    /**
     * 审核通过
     */
    @PostMapping("/{id}/approve")
    public R<?> approve(@PathVariable Long id,
                        @RequestAttribute("userId") Long orgUserId) {
        adoptionApplicationService.approve(id, orgUserId);
        return R.okMsg("审核已通过");
    }

    /**
     * 审核拒绝
     */
    @PostMapping("/{id}/reject")
    public R<?> reject(@PathVariable Long id,
                       @RequestAttribute("userId") Long orgUserId) {
        adoptionApplicationService.reject(id, orgUserId);
        return R.okMsg("审核已拒绝");
    }
}
