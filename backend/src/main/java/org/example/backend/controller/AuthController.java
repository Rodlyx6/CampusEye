package org.example.backend.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.backend.common.Result;
import org.example.backend.entity.SysUser;
import org.example.backend.service.SysUserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;

    @PostMapping("/register")
    public Result<?> register(@RequestBody SysUser user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return Result.error("用户名和密码不能为空");
        }
        SysUser existing = sysUserService.findByUsername(user.getUsername());
        if (existing != null) {
            return Result.error("用户名已存在");
        }
        user.setRole("user");
        sysUserService.save(user);
        return Result.success("注册成功", null);
    }

    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody SysUser user, HttpSession session) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return Result.error(400, "用户名和密码不能为空");
        }
        SysUser dbUser = sysUserService.findByUsername(user.getUsername());
        if (dbUser == null || !dbUser.getPassword().equals(user.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        dbUser.setPassword(null);
        session.setAttribute("currentUser", dbUser);
        return Result.success("登录成功", dbUser);
    }

    @GetMapping("/currentUser")
    public Result<SysUser> currentUser(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error(401, "用户未登录");
        }
        return Result.success(user);
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpSession session) {
        session.invalidate();
        return Result.success("退出成功", null);
    }
}
