-- Make prompt use shared categories table (no prompt-owned category system)

-- 1) Add category_id first (keep old category temporarily for migration)
ALTER TABLE `prompt` ADD COLUMN `category_id` BIGINT NULL AFTER `description`;

-- 2) Ensure prompt categories exist in unified categories table
INSERT INTO categories (`name`, `type`, `description`)
SELECT v.name, 'prompt', v.description
FROM (
  SELECT 'text'  AS name, '文本生成' AS description
  UNION ALL SELECT 'image', '图像生成'
  UNION ALL SELECT 'video', '视频生成'
  UNION ALL SELECT 'code',  '代码编程'
) v
WHERE NOT EXISTS (
  SELECT 1 FROM categories c WHERE c.name = v.name AND c.type = 'prompt'
);

-- 3) Backfill category_id from old prompt.category
UPDATE `prompt` p
JOIN categories c ON c.name = p.category AND c.type = 'prompt'
SET p.category_id = c.id
WHERE p.category_id IS NULL;

-- 4) Enforce NOT NULL and FK, then drop old category column
ALTER TABLE `prompt`
  MODIFY COLUMN `category_id` BIGINT NOT NULL,
  ADD CONSTRAINT fk_prompt_category FOREIGN KEY (`category_id`) REFERENCES categories(`id`) ON DELETE RESTRICT,
  ADD INDEX idx_prompt_category_id (`category_id`);

ALTER TABLE `prompt` DROP COLUMN `category`;

