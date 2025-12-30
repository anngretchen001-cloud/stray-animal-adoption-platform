package org.example.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.CommentDTO;
import org.example.backend.entity.Comment;
import org.example.backend.service.CommentService;
import org.example.backend.common.R;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /** 分页获取某篇文章的评论 */
    @GetMapping("/page")
    public R<IPage<CommentDTO>> pageComments(
            @RequestParam Long postId,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize
    ) {
        IPage<CommentDTO> page = commentService.pageCommentsByPostId(postId, pageNum, pageSize);
        return R.ok(page);
    }

    /** 发布评论 */
    @PostMapping
    public R<Void> createComment(@RequestBody Comment comment, HttpServletRequest request) {
        commentService.createComment(comment, request);
        return R.okMsg("发布成功");
    }
    /** 删除评论 */
    @DeleteMapping("/{id}")
    public R<Void> deleteComment(@PathVariable Long id, HttpServletRequest request) {
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return R.error("评论不存在");
        }

        // 从请求中取当前用户ID
        Long currentUserId = (Long) request.getAttribute("userId");

        // 权限判断：仅评论作者可以删除
        if (!comment.getUserId().equals(currentUserId)) {
            return R.error("无权限删除该评论");
        }

        commentService.removeById(id);
        return R.okMsg("删除成功");
    }

}
