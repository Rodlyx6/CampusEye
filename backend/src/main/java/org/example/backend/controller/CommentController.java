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
    public Result<List<Comment>> list(@PathVariable Long buildingId) {
        return Result.success(commentService.getCommentsByBuildingId(buildingId));
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Comment comment, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        comment.setUserId(user.getId());
        commentService.save(comment);
        return Result.success("发表成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        // 只有管理员或本人可以删除
        if (!"admin".equals(user.getRole()) && !comment.getUserId().equals(user.getId())) {
            return Result.error("无权限删除");
        }
        commentService.removeById(id);
        return Result.success("删除成功");
    }
}
