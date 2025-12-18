package org.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.entity.Post;

public interface PostService extends IService<Post> {
    /** 带权限校验的发布文章 */
    void createPost(Post post, String currentUserRole);
}
