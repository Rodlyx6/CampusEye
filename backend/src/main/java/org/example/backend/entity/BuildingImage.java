package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("building_image")
public class BuildingImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long buildingId;

    private String imageUrl;

    private Integer sortNum;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
