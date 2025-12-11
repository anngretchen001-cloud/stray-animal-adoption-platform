package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("adoption_application")
public class AdoptionApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long animalId;
    private Long userId;
    private String status;       // pending / approved / rejected / cancelled
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
