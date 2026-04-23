package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.backend.entity.BuildingImage;
import org.example.backend.mapper.BuildingImageMapper;
import org.example.backend.service.BuildingImageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingImageServiceImpl extends ServiceImpl<BuildingImageMapper, BuildingImage> implements BuildingImageService {

    @Override
    public List<BuildingImage> listByBuildingId(Long buildingId) {
        return list(new LambdaQueryWrapper<BuildingImage>()
                .eq(BuildingImage::getBuildingId, buildingId)
                .orderByAsc(BuildingImage::getSortNum));
    }

    @Override
    public void deleteByBuildingId(Long buildingId) {
        remove(new LambdaQueryWrapper<BuildingImage>()
                .eq(BuildingImage::getBuildingId, buildingId));
    }
}
