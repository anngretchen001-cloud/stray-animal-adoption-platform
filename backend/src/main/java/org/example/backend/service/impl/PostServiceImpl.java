package org.example.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.backend.common.PostType;
import org.example.backend.entity.Post;
import org.example.backend.exception.BizException;
import org.example.backend.mapper.PostMapper;
import org.example.backend.service.PostService;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl
        extends ServiceImpl<PostMapper, Post>
        implements PostService {

    public void createPost(Post post, String currentUserRole) {

        // 1. type 合法性
        if (!PostType.isValid(post.getType())) {
            throw new BizException("非法文章类型");
        }

        // 2. 权限限制：只有 ADMIN 能发公告
        if ("ANNOUNCEMENT".equals(post.getType())
                && !"ADMIN".equals(currentUserRole)) {
            throw new BizException("无权发布公告");
        }

        save(post);
    }

}
