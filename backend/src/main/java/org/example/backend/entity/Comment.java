package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文章ID */
    @TableField("post_id")
    private Long postId;

    /** 评论用户ID */
    @TableField("user_id")
    private Long userId;

    /** 评论内容 */
    private String content;

    /** 创建时间，插入时自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
