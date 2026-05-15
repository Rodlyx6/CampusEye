package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("comment")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long buildingId;

    private Long userId;

    private String content;

    private Long parentId;

    private Integer likes;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 冗余字段或关联查询字段
    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String avatar;

    @TableField(exist = false)
    private List<Comment> replies;

    @TableField(exist = false)
    private String replyToUser;

    @TableField(exist = false)
    private Boolean isLiked;
}
