-- 为工具表添加浏览量字段
ALTER TABLE `tool` ADD COLUMN `view_count` INT DEFAULT 0 COMMENT '浏览量';
