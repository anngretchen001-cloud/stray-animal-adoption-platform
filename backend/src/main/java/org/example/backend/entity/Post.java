package org.example.backend.entity;



import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("post")
public class Post {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文章标题 */
    private String title;

    /** 文章内容 */
    private String content;

    /** 作者ID */
    @TableField("author_id")
    private Long authorId;

    /** 作者角色：USER / ORG / ADMIN */
    @TableField("author_role")
    private String authorRole;

    /** 文章类型 */
    private String type;

    /** 封面图 */
    @TableField("cover_url")
    private String coverUrl;

    /** 状态：NORMAL / HIDDEN */
    private String status;

    /** 浏览量 */
    @TableField("view_count")
    private Integer viewCount;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
