-- V2__add_social_features.sql
-- Add social features: comments, likes, favorites, unified tags, and search indexes

-- Create unified categories table
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Create unified tags table
CREATE TABLE tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Create entity_categories table for polymorphic categorization
CREATE TABLE entity_categories (
    entity_type ENUM('tool', 'tutorial', 'api') NOT NULL,
    entity_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (entity_type, entity_id, category_id),
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    INDEX idx_entity_categories_type_id (entity_type, entity_id),
    INDEX idx_entity_categories_category (category_id)
);

-- Create entity_tags table for polymorphic tagging
CREATE TABLE entity_tags (
    entity_type ENUM('tool', 'tutorial', 'api') NOT NULL,
    entity_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (entity_type, entity_id, tag_id),
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
    INDEX idx_entity_tags_type_id (entity_type, entity_id),
    INDEX idx_entity_tags_tag (tag_id)
);

-- Migrate existing tool_tags to new system
INSERT INTO tags (name)
SELECT DISTINCT tt.tag FROM tool_tags tt
WHERE tt.tag NOT IN (SELECT name FROM tags);

INSERT INTO entity_tags (entity_type, entity_id, tag_id)
SELECT 'tool', tt.tool_id, t.id
FROM tool_tags tt
JOIN tags t ON t.name = tt.tag;

-- Migrate existing tool categories to new system
INSERT INTO categories (name)
SELECT DISTINCT category FROM tool
WHERE category IS NOT NULL AND category NOT IN (SELECT name FROM categories);

INSERT INTO entity_categories (entity_type, entity_id, category_id)
SELECT 'tool', t.id, c.id
FROM tool t
JOIN categories c ON c.name = t.category
WHERE t.category IS NOT NULL;

-- Migrate existing tutorial_tags to new system
INSERT INTO tags (name)
SELECT DISTINCT tt.tag FROM tutorial_tags tt
WHERE tt.tag NOT IN (SELECT name FROM tags);

INSERT INTO entity_tags (entity_type, entity_id, tag_id)
SELECT 'tutorial', tt.tutorial_id, t.id
FROM tutorial_tags tt
JOIN tags t ON t.name = tt.tag;

-- Create content_index table for global search
CREATE TABLE content_index (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type ENUM('tool', 'tutorial', 'api') NOT NULL,
    entity_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    content_type VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_entity (entity_type, entity_id),
    FULLTEXT INDEX ft_search (title, description)
);

-- Migrate existing data to content_index
INSERT INTO content_index (entity_type, entity_id, title, description, content_type, created_at, updated_at)
SELECT 'tool', id, name, description, category, created_at, created_at FROM tool;

INSERT INTO content_index (entity_type, entity_id, title, description, content_type, created_at, updated_at)
SELECT 'tutorial', id, title, description, 'Tutorial', created_at, created_at FROM tutorial;

INSERT INTO content_index (entity_type, entity_id, title, description, content_type, created_at, updated_at)
SELECT 'api', id, name, description, 'API', created_at, created_at FROM api_info;

-- Create comments table
CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    entity_type ENUM('tool', 'tutorial', 'api') NOT NULL,
    entity_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_comments_entity (entity_type, entity_id),
    INDEX idx_comments_user (user_id),
    INDEX idx_comments_created_at (created_at)
);

-- Create likes table
CREATE TABLE likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    entity_type ENUM('tool', 'tutorial', 'api') NOT NULL,
    entity_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_entity_like (user_id, entity_type, entity_id),
    INDEX idx_likes_entity (entity_type, entity_id)
);

-- Create favorites table
CREATE TABLE favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    entity_type ENUM('tool', 'tutorial', 'api') NOT NULL,
    entity_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_entity_favorite (user_id, entity_type, entity_id),
    INDEX idx_favorites_entity (entity_type, entity_id),
    INDEX idx_favorites_user (user_id)
);

-- Add fulltext indexes for search
ALTER TABLE tool ADD FULLTEXT INDEX ft_tool_search (name, description);
ALTER TABLE tutorial ADD FULLTEXT INDEX ft_tutorial_search (title, description, content);
ALTER TABLE api_info ADD FULLTEXT INDEX ft_api_search (name, description);

-- Update tool rating calculation (optional: calculate from comments)
-- This could be done in application logic, but here's a sample trigger
DELIMITER //
CREATE TRIGGER update_tool_rating_after_comment
    AFTER INSERT ON comments
    FOR EACH ROW
BEGIN
    IF NEW.entity_type = 'tool' AND NEW.rating IS NOT NULL THEN
        UPDATE tool
        SET rating = (
            SELECT AVG(c.rating)
            FROM comments c
            WHERE c.entity_type = 'tool' AND c.entity_id = NEW.entity_id AND c.rating IS NOT NULL
        ),
        review_count = (
            SELECT COUNT(*)
            FROM comments c
            WHERE c.entity_type = 'tool' AND c.entity_id = NEW.entity_id AND c.rating IS NOT NULL
        )
        WHERE id = NEW.entity_id;
    END IF;
END//
DELIMITER ;

-- Remove old fulltext indexes (replaced by content_index)
ALTER TABLE tool DROP INDEX ft_tool_search;
ALTER TABLE tutorial DROP INDEX ft_tutorial_search;
ALTER TABLE api_info DROP INDEX ft_api_search;

-- Drop old tag tables after migration
DROP TABLE tool_tags;
DROP TABLE tutorial_tags;