package org.example.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.entity.Post;
import org.example.backend.exception.BizException;
import org.example.backend.service.PostService;
import org.example.backend.common.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /** 发布文章 */
    @PostMapping
    public R<?> createPost(@RequestBody Post post,
                           HttpServletRequest request) {
        Long authorId = (Long) request.getAttribute("userId");
        String authorRole = (String) request.getAttribute("role");

        post.setAuthorId(authorId);
        post.setAuthorRole(authorRole);


        postService.createPost(post, authorRole);
        return R.ok("发布成功");
    }

    /** 获取文章详情 */
    @GetMapping("/{id}")
    public R<Post> getPost(@PathVariable Long id) {
        Post post = postService.getById(id);
        return R.ok(post);
    }

    /** 获取文章列表 */
    @GetMapping
    public R<List<Post>> listPosts() {
        List<Post> list = postService.list();
        return R.ok(list);
    }
    @GetMapping("/my")
    public R<List<Post>> getMyPosts(HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        List<Post> posts = postService.list(
                new QueryWrapper<Post>().eq("author_id", currentUserId)
        );
        return R.ok(posts);
    }
    @PutMapping("/{id}")
    public R<?> updatePost(@PathVariable Long id, @RequestBody Post post, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        String currentUserRole = (String) request.getAttribute("role");

        Post existing = postService.getById(id);
        if (existing == null) return R.error("文章不存在");

        if (!existing.getAuthorId().equals(currentUserId) && !"ADMIN".equals(currentUserRole)) {
            throw new BizException("无权编辑此文章");
        }

        // 可以限制普通用户不能改 type 为公告
        if ("ANNOUNCEMENT".equals(post.getType()) && !"ADMIN".equals(currentUserRole)) {
            throw new BizException("无权修改为公告");
        }

        post.setId(id);
        post.setAuthorId(existing.getAuthorId()); // 保证作者不变
        post.setAuthorRole(existing.getAuthorRole());
        postService.updateById(post);
        return R.ok("更新成功");
    }



    @DeleteMapping("/{id}")
    public R<?> deletePost(@PathVariable Long id, HttpServletRequest request) {
        // 从 token 获取用户信息
        Long currentUserId = (Long) request.getAttribute("userId");
        String currentUserRole = (String) request.getAttribute("role");

        // 先查文章
        Post post = postService.getById(id);
        if (post == null) {
            return R.error("文章不存在");
        }

        // 权限校验
        if (!post.getAuthorId().equals(currentUserId) && !"ADMIN".equals(currentUserRole)) {
            throw new BizException("无权删除此文章");
        }

        // 删除
        postService.removeById(id);
        return R.ok("删除成功");
    }

}
