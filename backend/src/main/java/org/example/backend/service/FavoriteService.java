package org.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.entity.Favorite;

public interface FavoriteService extends IService<Favorite> {
    boolean toggleFavorite(Long userId, Long buildingId);
    boolean isFavorited(Long userId, Long buildingId);
    java.util.List<Long> getFavoriteBuildingIds(Long userId);
}
