package org.example.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 组织后台专用的申请DTO
 * 包含申请、动物、用户完整信息
 */
@Data
public class AdoptionApplicationOrgDTO {
    // 申请基本信息
    private Long id;
    private Long animalId;
    private Long userId;
    private String status;       // PENDING / APPROVED / REJECTED / CANCELLED
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 动物信息
    private String animalName;
    private String breed;
    private Integer age;
    private String gender;       // male / female
    private String animalImageUrl;
    private String animalType;   // dog / cat / other

    // 申请人信息
    private String applicantName;    // 申请人姓名
    private String applicantEmail;   // 申请人邮箱
    private String applicantPhone;   // 申请人电话

    // 可选的统计/状态信息
    private Boolean isNew;           // 是否是新申请（24小时内）
    private String statusText;       // 中文状态文本

    // 构造函数
    public AdoptionApplicationOrgDTO() {}

    // 获取中文状态
    public String getStatusText() {
        if (statusText != null) {
            return statusText;
        }

        switch (status) {
            case "PENDING":
                return "待处理";
            case "APPROVED":
                return "已通过";
            case "REJECTED":
                return "已拒绝";
            case "CANCELLED":
                return "已取消";
            default:
                return status;
        }
    }

    // 判断是否是新申请
    public Boolean getIsNew() {
        if (createdAt == null) return false;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAgo = now.minusDays(1);
        return createdAt.isAfter(oneDayAgo);
    }
}