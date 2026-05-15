package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.common.Result;
import org.example.backend.entity.Building;
import org.example.backend.service.BuildingImageService;
import org.example.backend.service.BuildingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/building")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;
    private final BuildingImageService buildingImageService;

    @GetMapping("/list")
    public Result<List<Building>> list(@RequestParam(required = false) String category) {
        if (category != null && !category.isEmpty()) {
            return Result.success(buildingService.lambdaQuery().eq(Building::getCategory, category).list());
        }
        return Result.success(buildingService.list());
    }

    @GetMapping("/categories")
    public Result<List<String>> categories() {
        return Result.success(buildingService.list().stream()
                .map(Building::getCategory)
                .filter(c -> c != null && !c.isEmpty())
                .distinct()
                .toList());
    }

    @GetMapping("/detail/{id}")
    public Result<Building> detail(@PathVariable Long id) {
        Building building = buildingService.getById(id);
        if (building == null) {
            return Result.error("建筑不存在");
        }
        return Result.success(building);
    }

    @GetMapping("/search")
    public Result<List<Building>> search(@RequestParam(required = false) String keyword) {
        return Result.success(buildingService.searchByName(keyword));
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Building building) {
        if (building.getName() == null || building.getMapX() == null || building.getMapY() == null) {
            return Result.error("建筑名称和地图坐标不能为空");
        }
        buildingService.save(building);
        return Result.success("新增成功", null);
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Building building) {
        if (building.getId() == null) {
            return Result.error("建筑ID不能为空");
        }
        buildingService.updateById(building);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        buildingImageService.deleteByBuildingId(id);
        buildingService.removeById(id);
        return Result.success("删除成功", null);
    }
}
