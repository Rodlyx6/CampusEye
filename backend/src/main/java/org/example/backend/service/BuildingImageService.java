package org.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.entity.BuildingImage;

import java.util.List;

public interface BuildingImageService extends IService<BuildingImage> {

    List<BuildingImage> listByBuildingId(Long buildingId);

    void deleteByBuildingId(Long buildingId);
}
