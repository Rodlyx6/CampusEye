package org.example.backend.controller;

import jakarta.servlet.http.HttpSession;
import org.example.backend.common.Result;
import org.example.backend.entity.SysUser;
import org.example.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/toggle/{buildingId}")
    public Result<Boolean> toggle(@PathVariable Long buildingId, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        boolean favorited = favoriteService.toggleFavorite(user.getId(), buildingId);
        return Result.success(favorited);
    }

    @GetMapping("/status/{buildingId}")
    public Result<Boolean> status(@PathVariable Long buildingId, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) return Result.success(false);
        return Result.success(favoriteService.isFavorited(user.getId(), buildingId));
    }

    @GetMapping("/my")
    public Result<java.util.List<Long>> myFavorites(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) return Result.error("未登录");
        return Result.success(favoriteService.getFavoriteBuildingIds(user.getId()));
    }
}
