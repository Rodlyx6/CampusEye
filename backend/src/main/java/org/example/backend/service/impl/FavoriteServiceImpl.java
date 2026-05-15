package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.backend.entity.Favorite;
import org.example.backend.mapper.FavoriteMapper;
import org.example.backend.service.FavoriteService;
import org.springframework.stereotype.Service;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Override
    public boolean toggleFavorite(Long userId, Long buildingId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId).eq(Favorite::getBuildingId, buildingId);
        Favorite existing = getOne(queryWrapper);
        if (existing != null) {
            removeById(existing.getId());
            return false;
        } else {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setBuildingId(buildingId);
            save(favorite);
            return true;
        }
    }

    @Override
    public boolean isFavorited(Long userId, Long buildingId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId).eq(Favorite::getBuildingId, buildingId);
        return count(queryWrapper) > 0;
    }

    @Override
    public java.util.List<Long> getFavoriteBuildingIds(Long userId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId);
        return list(queryWrapper).stream().map(Favorite::getBuildingId).collect(java.util.stream.Collectors.toList());
    }
}
