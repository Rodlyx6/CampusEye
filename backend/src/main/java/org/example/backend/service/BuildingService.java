package org.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.entity.Building;

import java.util.List;

public interface BuildingService extends IService<Building> {

    List<Building> searchByName(String keyword);
}
