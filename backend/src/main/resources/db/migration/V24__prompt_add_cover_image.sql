-- 提示词 Banner 图（列表页头图，与教程 cover_image 一致）
ALTER TABLE `prompt` ADD COLUMN `cover_image` VARCHAR(512) NULL COMMENT 'Banner/封面图虚拟路径' AFTER `comment_count`;
