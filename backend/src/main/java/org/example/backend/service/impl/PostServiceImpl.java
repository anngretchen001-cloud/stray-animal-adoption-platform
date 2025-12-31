package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.backend.common.PostType;
import org.example.backend.dto.PostDTO;
import org.example.backend.entity.Post;
import org.example.backend.entity.User;
import org.example.backend.exception.BizException;
import org.example.backend.mapper.PostMapper;
import org.example.backend.service.PostService;
import org.example.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final UserService userService;

    public PostServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void createPost(Post post, String currentUserRole) {

        // type 校验
        if (!PostType.isValid(post.getType())) {
            throw new BizException("非法文章类型");
        }

        // 公告权限
        if ("ANNOUNCEMENT".equals(post.getType())
                && !"ADMIN".equals(currentUserRole)) {
            throw new BizException("无权发布公告");
        }

        post.setStatus("NORMAL");
        post.setViewCount(0);
        save(post);
    }

    @Override
    public IPage<PostDTO> pagePostsDTO(Integer pageNum, Integer pageSize, String keyword, String type,Long userId) {
        Page<Post> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Post> qw = new QueryWrapper<>();
        qw.eq("status", "NORMAL");

        if (keyword != null && !keyword.isEmpty()) {
            qw.like("title", keyword);
        }
        if (type != null && !type.isEmpty()) {
            qw.eq("type", type);
        }
        if (userId != null) {
            qw.eq("author_id", userId);
        }

        qw.orderByDesc("created_at");

        IPage<Post> postPage = this.page(page, qw);

        // 封装成 DTO
        List<PostDTO> dtoList = postPage.getRecords().stream().map(post -> {
            PostDTO dto = new PostDTO();
            dto.setId(post.getId());
            dto.setTitle(post.getTitle());
            dto.setContent(post.getContent());
            dto.setType(post.getType());
            dto.setCoverUrl(post.getCoverUrl());
            dto.setViewCount(post.getViewCount());
            dto.setCreatedAt(post.getCreatedAt());

            dto.setAuthorId(post.getAuthorId());
            dto.setAuthorRole(post.getAuthorRole());

            // 查询作者信息
            User author = userService.getById(post.getAuthorId());
            if (author != null) {
                dto.setAuthorName(author.getUsername());
                dto.setAuthorAvatar(author.getAvatarUrl());
            } else {
                dto.setAuthorName("匿名");
                dto.setAuthorAvatar("/default-pet.jpg");
            }
            return dto;
        }).collect(Collectors.toList());

        IPage<PostDTO> dtoPage = new Page<>();
        dtoPage.setCurrent(postPage.getCurrent());
        dtoPage.setSize(postPage.getSize());
        dtoPage.setTotal(postPage.getTotal());
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    public PostDTO getPostDetail(Long postId) {
        Post post = this.getById(postId);
        if (post == null) return null;

        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setType(post.getType());
        dto.setCoverUrl(post.getCoverUrl());
        dto.setViewCount(post.getViewCount());
        dto.setCreatedAt(post.getCreatedAt());

        dto.setAuthorId(post.getAuthorId());
        dto.setAuthorRole(post.getAuthorRole());

        User author = userService.getById(post.getAuthorId());
        if (author != null) {
            dto.setAuthorName(author.getUsername());
            dto.setAuthorAvatar(author.getAvatarUrl());
        } else {
            dto.setAuthorName("匿名");
            dto.setAuthorAvatar("/default-pet.jpg");
        }

        return dto;
    }
}
