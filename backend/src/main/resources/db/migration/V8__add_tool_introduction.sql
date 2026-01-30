-- 为工具表添加介绍字段（用于存储Markdown格式的工具详细介绍）
ALTER TABLE `tool` ADD COLUMN `introduction` LONGTEXT COMMENT '工具详细介绍，Markdown格式';
