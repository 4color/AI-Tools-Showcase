-- Add category_id to api_info to share categories table (type='api')
ALTER TABLE api_info ADD COLUMN category_id BIGINT;


INSERT INTO categories (name, type, description) 
SELECT 'auth', 'api', '认证与安全' 
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'auth' AND type = 'api');

INSERT INTO categories (name, type, description) 
SELECT 'data', 'api', '数据服务' 
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'data' AND type = 'api');

INSERT INTO categories (name, type, description) 
SELECT 'ai', 'api', 'AI 与模型' 
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'ai' AND type = 'api');

INSERT INTO categories (name, type, description) 
SELECT 'utility', 'api', '工具与通知' 
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'utility' AND type = 'api');

-- Backfill category_id using existing category name (if present)
UPDATE api_info ai
JOIN categories c ON ai.category = c.name AND c.type = 'api'
SET ai.category_id = c.id;

-- Fallback to 'general' for rows still null
UPDATE api_info ai
SET ai.category_id = (SELECT id FROM categories WHERE name = 'general' AND type = 'api' LIMIT 1)
WHERE ai.category_id IS NULL;

-- Enforce NOT NULL and FK
ALTER TABLE api_info ADD CONSTRAINT fk_api_category FOREIGN KEY (category_id) REFERENCES categories(id);
CREATE INDEX idx_api_category_id ON api_info(category_id);

-- Drop legacy category text column
ALTER TABLE api_info DROP COLUMN category;
