-- Tutorials use shared categories table (common interface)

-- 1) Add category_id to tutorial (nullable first)
ALTER TABLE tutorial ADD COLUMN category_id BIGINT NULL AFTER content;

-- 2) Ensure a default tutorial category exists
INSERT INTO categories (`name`, `type`, `description`)
SELECT 'general', 'tutorial', '默认教程分类'
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.name = 'general' AND c.type = 'tutorial');

-- 3) Backfill existing tutorials to default category
UPDATE tutorial t
JOIN categories c ON c.name = 'general' AND c.type = 'tutorial'
SET t.category_id = c.id
WHERE t.category_id IS NULL;

-- 4) Enforce NOT NULL + FK + index
ALTER TABLE tutorial
  MODIFY COLUMN category_id BIGINT NOT NULL,
  ADD CONSTRAINT fk_tutorial_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
  ADD INDEX idx_tutorial_category_id (category_id);

