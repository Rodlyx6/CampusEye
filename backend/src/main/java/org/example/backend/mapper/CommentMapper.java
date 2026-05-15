package org.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.backend.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("SELECT c.*, u.username, u.avatar FROM comment c " +
            "LEFT JOIN sys_user u ON c.user_id = u.id " +
            "WHERE c.building_id = #{buildingId} " +
            "ORDER BY c.create_time DESC")
    List<Comment> selectWithUserInfo(Long buildingId);
}
