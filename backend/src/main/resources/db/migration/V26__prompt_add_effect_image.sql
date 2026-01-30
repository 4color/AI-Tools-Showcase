-- 提示词生成效果图（以提示词内容为 AI 生成来源，展示在详情页提示词上方）
ALTER TABLE `prompt` ADD COLUMN `effect_image` VARCHAR(512) NULL COMMENT '生成效果图虚拟路径' AFTER `usage_guide`;
