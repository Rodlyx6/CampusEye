CREATE TABLE comment_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    comment_id BIGINT NOT NULL COMMENT '评论ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_comment (user_id, comment_id)
) COMMENT='评论点赞表';
