package org.example.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.common.R;
import org.example.backend.entity.Comment;
import org.example.backend.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /** 添加评论 */
    @PostMapping
    public R<?> addComment(@RequestBody Comment comment, HttpServletRequest request) {

        Long UserId = Long.parseLong(request.getParameter("userId"));

        comment.setUserId(UserId);

        commentService.save(comment);
        return R.ok("评论成功");
    }

    /** 删除评论 */
    @DeleteMapping("/{id}")
    public R<?> deleteComment(@PathVariable Long id) {
        commentService.removeById(id);
        return R.ok("删除成功");
    }

    /** 根据文章ID查询评论列表 */
    @GetMapping("/post/{postId}")
    public R<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        List<Comment> comments = commentService.list(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId)
                        .orderByAsc(Comment::getCreatedAt)
        );
        return R.ok(comments);
    }

    /** 查询单条评论 */
    @GetMapping("/{id}")
    public R<Comment> getComment(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        return R.ok(comment);
    }

    /** 分页查询评论（可选，前端分页用） */
    @GetMapping("/post/{postId}/page")
    public R<Page<Comment>> getCommentsByPostPage(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {

        Page<Comment> page = commentService.page(
                new Page<>(current, size),
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId)
                        .orderByAsc(Comment::getCreatedAt)
        );
        return R.ok(page);
    }
}
