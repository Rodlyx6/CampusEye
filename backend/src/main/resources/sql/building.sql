CREATE TABLE building (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                          name VARCHAR(100) NOT NULL COMMENT '建筑名称',
                          category VARCHAR(50) DEFAULT NULL COMMENT '建筑分类',
                          description VARCHAR(255) DEFAULT NULL COMMENT '建筑简介',
                          detail TEXT COMMENT '建筑详细介绍',
                          cover_image VARCHAR(255) DEFAULT NULL COMMENT '封面图片路径',
                          vr_url VARCHAR(255) DEFAULT NULL COMMENT 'VR展示链接',
                          map_x INT NOT NULL COMMENT '地图横坐标',
                          map_y INT NOT NULL COMMENT '地图纵坐标',
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='建筑信息表';