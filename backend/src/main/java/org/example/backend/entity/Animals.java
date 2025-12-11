package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("animals")
public class Animals {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String type;         // cat / dog / other
    private String breed;
    private Integer age;
    private String gender;       // male / female
    private String description;
    private String imageUrl;
    private Long organizationId;
    private String status;       // available / adopted
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
