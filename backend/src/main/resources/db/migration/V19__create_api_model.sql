CREATE TABLE IF NOT EXISTS `api_model` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `api_id` BIGINT NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `context_length` INT,
    `capabilities` TEXT,
    `is_recommended` TINYINT(1) NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX (`api_id`),
    CONSTRAINT `fk_api_model_api_id` FOREIGN KEY (`api_id`) REFERENCES `api_info`(`id`) ON DELETE CASCADE
);

-- Seed a few default models for existing sample data (optional but useful for demo)
INSERT INTO `api_model` (`api_id`, `name`, `description`, `context_length`, `capabilities`, `is_recommended`)
SELECT 1, 'gpt-4o', '全能型模型，支持文本/图像等多模态', 128000, '文本,图像,音频', 1
WHERE EXISTS (SELECT 1 FROM `api_info` WHERE `id` = 1)
  AND NOT EXISTS (SELECT 1 FROM `api_model` WHERE `api_id` = 1 AND `name` = 'gpt-4o');

INSERT INTO `api_model` (`api_id`, `name`, `description`, `context_length`, `capabilities`, `is_recommended`)
SELECT 2, 'gemini-1.5-pro', '长上下文多模态模型', 1000000, '文本,图像,视频', 1
WHERE EXISTS (SELECT 1 FROM `api_info` WHERE `id` = 2)
  AND NOT EXISTS (SELECT 1 FROM `api_model` WHERE `api_id` = 2 AND `name` = 'gemini-1.5-pro');

INSERT INTO `api_model` (`api_id`, `name`, `description`, `context_length`, `capabilities`, `is_recommended`)
SELECT 3, 'claude-3.5-sonnet', '高质量文本与推理能力', 200000, '文本', 1
WHERE EXISTS (SELECT 1 FROM `api_info` WHERE `id` = 3)
  AND NOT EXISTS (SELECT 1 FROM `api_model` WHERE `api_id` = 3 AND `name` = 'claude-3.5-sonnet');
