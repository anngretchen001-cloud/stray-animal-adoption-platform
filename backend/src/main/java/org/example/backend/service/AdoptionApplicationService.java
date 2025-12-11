package org.example.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.backend.dto.AdoptionApplicationDTO;
import org.example.backend.dto.AdoptionApplicationOrgDTO;
import org.example.backend.entity.AdoptionApplication;

public interface AdoptionApplicationService {

    // 用户提交领养申请
    void createApplication(AdoptionApplication application, Long userId);

    // 用户取消申请
    void cancelApplication(Long applicationId, Long userId);

    // 用户/管理员查看单条申请
    AdoptionApplication getById(Long id);

    // 分页查看用户自己的申请
    Page<AdoptionApplication> listUserApplications(Long userId, int pageNum, int pageSize);

    Page<AdoptionApplicationDTO> listUserApplicationsWithAnimal(Long userId, int pageNum, int pageSize);

    Page<AdoptionApplicationOrgDTO> listOrgApplications(Long orgUserId, int pageNum, int pageSize, String status);
    // 分页查看组织收到的所有申请（组织管理员）


    // 审核：组织同意申请（需要同时把动物状态改为“adopted”）
    void approve(Long applicationId, Long orgUserId);

    // 审核：组织拒绝申请
    void reject(Long applicationId, Long orgUserId);
}

