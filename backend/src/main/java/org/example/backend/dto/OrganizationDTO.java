package org.example.backend.dto;

import lombok.Data;

@Data
public class OrganizationDTO {
    private Long id;
    private String name;
    private String certificateNo;
    private String contactName;
    private String email;
    private String phone;
    private String address;
    private String description;
    private Boolean enabled;
    private String status; // pending / approved / rejected
    private String createTime;
}
