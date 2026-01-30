-- 用户表增加 office_id 字段（可选，用于组织/部门标识）
ALTER TABLE `user` ADD COLUMN `office_id` VARCHAR(100) NULL DEFAULT NULL AFTER `role`;
CREATE INDEX idx_user_office_id ON `user` (`office_id`);
