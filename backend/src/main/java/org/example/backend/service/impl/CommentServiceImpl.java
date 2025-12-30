package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.CommentDTO;
import org.example.backend.entity.Comment;
import org.example.backend.entity.User;
import org.example.backend.mapper.CommentMapper;
import org.example.backend.service.CommentService;
import org.example.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final UserService userService;

    public CommentServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public IPage<CommentDTO> pageCommentsByPostId(Long postId, Integer pageNum, Integer pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Comment> qw = new QueryWrapper<>();
        qw.eq("post_id", postId).orderByAsc("created_at");

        IPage<Comment> commentPage = this.page(page, qw);

        List<CommentDTO> dtoList = commentPage.getRecords().stream().map(comment -> {
            CommentDTO dto = new CommentDTO();
            dto.setId(comment.getId());
            dto.setPostId(comment.getPostId());
            dto.setUserId(comment.getUserId());
            dto.setContent(comment.getContent());
            dto.setCreatedAt(comment.getCreatedAt());

            // 查询评论者信息
            User author = userService.getById(comment.getUserId());
            if (author != null) {
                dto.setAuthorName(author.getUsername());
                dto.setAuthorAvatar(author.getAvatarUrl());
            } else {
                dto.setAuthorName("匿名");
                dto.setAuthorAvatar("/default-pet.jpg");
            }

            return dto;
        }).collect(Collectors.toList());

        IPage<CommentDTO> dtoPage = new Page<>();
        dtoPage.setCurrent(commentPage.getCurrent());
        dtoPage.setSize(commentPage.getSize());
        dtoPage.setTotal(commentPage.getTotal());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public void createComment(Comment comment, HttpServletRequest request) {
        // 设置用户ID，这里假设你有方法获取当前登录用户ID
        if (comment.getUserId() == null) {
            comment.setUserId((long)request.getAttribute("userId")); // 你自己实现
        }

        // 设置创建时间
        if (comment.getCreatedAt() == null) {
            comment.setCreatedAt(LocalDateTime.now());
        }

        this.save(comment);
    }

}
