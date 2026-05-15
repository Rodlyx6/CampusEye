package org.example.backend.controller;

import jakarta.servlet.http.HttpSession;
import org.example.backend.common.Result;
import org.example.backend.entity.Comment;
import org.example.backend.entity.SysUser;
import org.example.backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/list/{buildingId}")
    public Result<List<Comment>> list(@PathVariable Long buildingId, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Long userId = user != null ? user.getId() : null;
        return Result.success(commentService.getCommentsByBuildingId(buildingId, userId));
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Comment comment, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }
        comment.setUserId(user.getId());
        comment.setLikes(0);
        commentService.save(comment);
        return Result.success("发表成功");
    }

    @PostMapping("/like/{id}")
    public Result<Boolean> like(@PathVariable Long id, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }
        return Result.success(commentService.toggleLike(id, user.getId()));
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error("请先登录");
        }
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        // 管理员或本人可以删除
        if (!"admin".equals(user.getRole()) && !comment.getUserId().equals(user.getId())) {
            return Result.error("无权限删除该评论");
        }
        commentService.removeById(id);
        return Result.success("删除成功");
    }
}
