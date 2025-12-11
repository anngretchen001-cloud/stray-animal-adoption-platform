package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("organization")
public class Organization {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    private String name;
    private String certificateNo;
    private String contactName;
    private String email;
    private String phone;
    private String address;
    private String description;
    private Boolean enabled;
    private String status;       // pending / approved / rejected
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
