package org.example.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.dto.PostDTO;
import org.example.backend.entity.Post;

public interface PostService extends IService<Post> {

    /** 发布文章 */
    void createPost(Post post, String currentUserRole);

    IPage<PostDTO> pagePostsDTO(Integer pageNum, Integer pageSize, String keyword, String type);
    PostDTO getPostDetail(Long postId);

}
