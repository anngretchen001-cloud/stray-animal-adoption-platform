package org.example.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.CommentDTO;
import org.example.backend.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {

    /** 根据文章ID分页查询评论并封装成DTO */
    IPage<CommentDTO> pageCommentsByPostId(Long postId, Integer pageNum, Integer pageSize);

    /** 发布评论 */
    void createComment(Comment comment, HttpServletRequest request);
}
