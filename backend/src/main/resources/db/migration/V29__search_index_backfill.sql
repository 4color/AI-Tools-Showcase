
-- 正确的语法：DEFAULT 后必须写完整的 CURRENT_TIMESTAMP
ALTER TABLE tutorial ADD updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE api_info ADD updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- 将现有工具、教程、提示词、API 数据写入 search_index（首次建表后回填）
INSERT INTO search_index (entity_type, entity_id, title, content, created_at, updated_at)
SELECT 'tool', id, name, IFNULL(LEFT(description, 2000), ''), created_at, updated_at FROM tool
ON DUPLICATE KEY UPDATE title = VALUES(title), content = VALUES(content), updated_at = VALUES(updated_at);

INSERT INTO search_index (entity_type, entity_id, title, content, created_at, updated_at)
SELECT 'tutorial', id, title, IFNULL(LEFT(description, 2000), ''), created_at, updated_at FROM tutorial
ON DUPLICATE KEY UPDATE title = VALUES(title), content = VALUES(content), updated_at = VALUES(updated_at);

INSERT INTO search_index (entity_type, entity_id, title, content, created_at, updated_at)
SELECT 'prompt', id, title, IFNULL(LEFT(description, 2000), ''), created_at, updated_at FROM prompt
ON DUPLICATE KEY UPDATE title = VALUES(title), content = VALUES(content), updated_at = VALUES(updated_at);

INSERT INTO search_index (entity_type, entity_id, title, content, created_at, updated_at)
SELECT 'api', id, name, IFNULL(LEFT(description, 2000), ''), created_at, IFNULL(updated_at, created_at) FROM api_info
ON DUPLICATE KEY UPDATE title = VALUES(title), content = VALUES(content), updated_at = VALUES(updated_at);
