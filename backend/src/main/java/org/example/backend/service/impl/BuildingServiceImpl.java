package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.backend.entity.Building;
import org.example.backend.mapper.BuildingMapper;
import org.example.backend.service.BuildingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements BuildingService {

    @Override
    public List<Building> searchByName(String keyword) {
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Building::getName, keyword.trim());
        }
        wrapper.orderByAsc(Building::getId);
        return list(wrapper);
    }
}
