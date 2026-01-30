-- 教程置顶功能：新增 pinned 字段
ALTER TABLE tutorial ADD COLUMN pinned TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否置顶 0否 1是';
CREATE INDEX idx_tutorial_pinned ON tutorial (pinned);
