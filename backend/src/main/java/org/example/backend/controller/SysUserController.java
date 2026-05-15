package org.example.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpSession;
import org.example.backend.common.Result;
import org.example.backend.entity.SysUser;
import org.example.backend.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/list")
    public Result<List<SysUser>> list(HttpSession session) {
        // 权限校验已在拦截器中处理，但这里可以再次确认
        List<SysUser> list = sysUserService.list();
        list.forEach(u -> u.setPassword(null)); // 隐藏密码
        return Result.success(list);
    }

    @PostMapping("/admin/update")
    public Result<String> update(@RequestBody SysUser user) {
        sysUserService.updateById(user);
        return Result.success("更新成功");
    }

    @DeleteMapping("/admin/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.success("删除成功");
    }

    @PostMapping("/updateProfile")
    public Result<String> updateProfile(@RequestBody SysUser user, HttpSession session) {
        SysUser currentUser = (SysUser) session.getAttribute("currentUser");
        user.setId(currentUser.getId());
        user.setUsername(null); // 不允许修改用户名
        user.setRole(null);     // 不允许修改角色
        sysUserService.updateById(user);
        
        // 更新session中的信息
        SysUser updated = sysUserService.getById(currentUser.getId());
        updated.setPassword(null);
        session.setAttribute("currentUser", updated);
        
        return Result.success("个人信息更新成功");
    }
}
