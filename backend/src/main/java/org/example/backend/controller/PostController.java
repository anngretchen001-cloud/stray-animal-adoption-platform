package org.example.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.common.R;
import org.example.backend.dto.PostDTO;
import org.example.backend.entity.Post;
import org.example.backend.exception.BizException;
import org.example.backend.service.PostService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /** 发布文章 */
    @PostMapping
    public R<?> createPost(
            @RequestBody Post post,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        post.setAuthorId(userId);
        post.setAuthorRole(role);

        postService.createPost(post, role);
        return R.ok("发布成功");
    }

    /** 分页查询文章（DTO 包含作者信息） */
    @GetMapping("/page")
    public R<IPage<PostDTO>> pagePosts(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type
    ) {
        IPage<PostDTO> page = postService.pagePostsDTO(pageNum, pageSize, keyword, type);
        return R.ok(page);
    }

    /** 获取文章详情 */
    @GetMapping("/{id}")
    public R<PostDTO> getPost(@PathVariable Long id) {
        PostDTO dto = postService.getPostDetail(id);
        if (dto == null) return R.error("文章不存在");
        return R.ok(dto);
    }

    /** 更新文章 */
    @PutMapping("/{id}")
    public R<?> updatePost(
            @PathVariable Long id,
            @RequestBody Post post,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        Post dbPost = postService.getById(id);
        if (dbPost == null) {
            return R.error("文章不存在");
        }

        if (!dbPost.getAuthorId().equals(userId)
                && !"ADMIN".equals(role)) {
            throw new BizException("无权修改文章");
        }

        // 普通用户不能改成公告
        if ("ANNOUNCEMENT".equals(post.getType())
                && !"ADMIN".equals(role)) {
            throw new BizException("无权修改为公告");
        }

        post.setId(id);
        post.setAuthorId(dbPost.getAuthorId());
        post.setAuthorRole(dbPost.getAuthorRole());

        postService.updateById(post);
        return R.ok("更新成功");
    }

    /** 删除文章 */
    @DeleteMapping("/{id}")
    public R<?> deletePost(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        Post post = postService.getById(id);
        if (post == null) {
            return R.error("文章不存在");
        }

        if (!post.getAuthorId().equals(userId)
                && !"ADMIN".equals(role)) {
            throw new BizException("无权删除文章");
        }

        postService.removeById(id);
        return R.ok("删除成功");
    }
}
