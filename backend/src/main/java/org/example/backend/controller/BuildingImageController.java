package org.example.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.backend.common.Result;
import org.example.backend.entity.BuildingImage;
import org.example.backend.service.BuildingImageService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/building-image")
@RequiredArgsConstructor
public class BuildingImageController {

    private final BuildingImageService buildingImageService;

    @GetMapping("/listByBuildingId/{buildingId}")
    public Result<List<BuildingImage>> listByBuildingId(@PathVariable Long buildingId) {
        return Result.success(buildingImageService.listByBuildingId(buildingId));
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody BuildingImage buildingImage) {
        if (buildingImage.getBuildingId() == null || buildingImage.getImageUrl() == null) {
            return Result.error("建筑ID和图片路径不能为空");
        }
        buildingImageService.save(buildingImage);
        return Result.success("新增成功", null);
    }

    @PostMapping("/batchAdd")
    public Result<?> batchAdd(@RequestBody List<BuildingImage> buildingImages) {
        if (buildingImages == null || buildingImages.isEmpty()) {
            return Result.error("图片数据不能为空");
        }

        List<BuildingImage> validList = new ArrayList<>();
        for (BuildingImage item : buildingImages) {
            if (item != null && item.getBuildingId() != null && item.getImageUrl() != null && !item.getImageUrl().trim().isEmpty()) {
                validList.add(item);
            }
        }

        if (validList.isEmpty()) {
            return Result.error("无有效图片数据");
        }

        buildingImageService.saveBatch(validList);
        return Result.success("批量新增成功", null);
    }

    @PostMapping("/replace")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> replace(@RequestBody ReplaceImagesRequest request) {
        if (request == null || request.getBuildingId() == null) {
            return Result.error("建筑ID不能为空");
        }

        List<Long> keepImageIds = request.getKeepImageIds() == null ? new ArrayList<>() : request.getKeepImageIds();
        List<String> newImageUrls = request.getNewImageUrls() == null ? new ArrayList<>() : request.getNewImageUrls();

        List<BuildingImage> dbImages = buildingImageService.list(new LambdaQueryWrapper<BuildingImage>()
                .eq(BuildingImage::getBuildingId, request.getBuildingId())
                .orderByAsc(BuildingImage::getSortNum)
                .orderByAsc(BuildingImage::getId));

        Map<Long, BuildingImage> idMap = new HashMap<>();
        for (BuildingImage dbImage : dbImages) {
            idMap.put(dbImage.getId(), dbImage);
        }

        Set<Long> keepSet = new HashSet<>(keepImageIds);
        List<Long> removeIds = new ArrayList<>();
        for (BuildingImage dbImage : dbImages) {
            if (!keepSet.contains(dbImage.getId())) {
                removeIds.add(dbImage.getId());
            }
        }

        if (!removeIds.isEmpty()) {
            buildingImageService.removeByIds(removeIds);
        }

        int sort = 1;
        List<BuildingImage> updateSortList = new ArrayList<>();
        for (Long keepId : keepImageIds) {
            BuildingImage exists = idMap.get(keepId);
            if (exists != null && Objects.equals(exists.getBuildingId(), request.getBuildingId())) {
                exists.setSortNum(sort++);
                updateSortList.add(exists);
            }
        }

        if (!updateSortList.isEmpty()) {
            buildingImageService.updateBatchById(updateSortList);
        }

        List<BuildingImage> addList = new ArrayList<>();
        for (String url : newImageUrls) {
            if (url == null || url.trim().isEmpty()) {
                continue;
            }
            BuildingImage add = new BuildingImage();
            add.setBuildingId(request.getBuildingId());
            add.setImageUrl(url.trim());
            add.setSortNum(sort++);
            addList.add(add);
        }

        if (!addList.isEmpty()) {
            buildingImageService.saveBatch(addList);
        }

        return Result.success("图片保存成功", null);
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody BuildingImage buildingImage) {
        if (buildingImage.getId() == null) {
            return Result.error("图片ID不能为空");
        }
        buildingImageService.updateById(buildingImage);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        buildingImageService.removeById(id);
        return Result.success("删除成功", null);
    }

    @Data
    public static class ReplaceImagesRequest {
        private Long buildingId;
        private List<Long> keepImageIds;
        private List<String> newImageUrls;
    }
}
