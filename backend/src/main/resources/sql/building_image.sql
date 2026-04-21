CREATE TABLE building_image (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                building_id BIGINT NOT NULL COMMENT '建筑ID',
                                image_url VARCHAR(255) NOT NULL COMMENT '图片路径',
                                sort_num INT DEFAULT 0 COMMENT '排序号',
                                create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ,
                                update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='建筑图片表';