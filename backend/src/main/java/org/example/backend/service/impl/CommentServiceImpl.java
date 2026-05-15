package org.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.backend.entity.Comment;
import org.example.backend.entity.CommentLike;
import org.example.backend.mapper.CommentLikeMapper;
import org.example.backend.mapper.CommentMapper;
import org.example.backend.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final CommentLikeMapper commentLikeMapper;

    @Override
    public List<Comment> getCommentsByBuildingId(Long buildingId, Long currentUserId) {
        List<Comment> allComments = baseMapper.selectWithUserInfo(buildingId);
        
        // 获取当前用户点赞过的评论ID
        Set<Long> likedCommentIds = new java.util.HashSet<>();
        if (currentUserId != null) {
            likedCommentIds = commentLikeMapper.selectList(
                    new LambdaQueryWrapper<CommentLike>().eq(CommentLike::getUserId, currentUserId)
            ).stream().map(CommentLike::getCommentId).collect(Collectors.toSet());
        }

        // 1. 建立ID到评论的映射，方便快速查找
        Map<Long, Comment> commentMap = allComments.stream()
                .collect(Collectors.toMap(Comment::getId, c -> c));

        List<Comment> rootComments = new ArrayList<>();
        
        final Set<Long> finalLikedCommentIds = likedCommentIds;
        // 2. 遍历所有评论，将其归类到父评论下
        for (Comment comment : allComments) {
            comment.setIsLiked(finalLikedCommentIds.contains(comment.getId()));
            if (comment.getParentId() == null) {
                // 根评论
                rootComments.add(comment);
            } else {
                // 回复评论
                Comment parent = commentMap.get(comment.getParentId());
                if (parent != null) {
                    if (parent.getReplies() == null) {
                        parent.setReplies(new ArrayList<>());
                    }
                    // 设置被回复人的名字
                    comment.setReplyToUser(parent.getUsername());
                    parent.getReplies().add(comment);
                } else {
                    // 如果父评论找不到（可能被删了），则作为根评论
                    rootComments.add(comment);
                }
            }
        }
        
        return rootComments;
    }

    @Override
    @Transactional
    public boolean toggleLike(Long commentId, Long userId) {
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<CommentLike>()
                .eq(CommentLike::getCommentId, commentId)
                .eq(CommentLike::getUserId, userId);
        CommentLike exist = commentLikeMapper.selectOne(wrapper);
        
        Comment comment = baseMapper.selectById(commentId);
        if (comment == null) return false;

        if (exist != null) {
            // 取消点赞
            commentLikeMapper.deleteById(exist.getId());
            comment.setLikes(Math.max(0, comment.getLikes() - 1));
            baseMapper.updateById(comment);
            return false; // 返回当前点赞状态：未点赞
        } else {
            // 点赞
            CommentLike cl = new CommentLike();
            cl.setCommentId(commentId);
            cl.setUserId(userId);
            commentLikeMapper.insert(cl);
            comment.setLikes((comment.getLikes() == null ? 0 : comment.getLikes()) + 1);
            baseMapper.updateById(comment);
            return true; // 返回当前点赞状态：已点赞
        }
    }

    @Override
    @Transactional
    public boolean removeById(java.io.Serializable id) {
        // 级联删除回复
        this.remove(new LambdaQueryWrapper<Comment>().eq(Comment::getParentId, id));
        // 删除点赞记录
        commentLikeMapper.delete(new LambdaQueryWrapper<CommentLike>().eq(CommentLike::getCommentId, id));
        // 删除自己
        return super.removeById(id);
    }
}
