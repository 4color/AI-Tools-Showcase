-- Add 'prompt' to entity_type ENUMs in all relevant tables

-- Update content_index table
ALTER TABLE content_index MODIFY COLUMN entity_type ENUM('tool', 'tutorial', 'api', 'prompt') NOT NULL;

-- Update comments table
ALTER TABLE comments MODIFY COLUMN entity_type ENUM('tool', 'tutorial', 'api', 'prompt') NOT NULL;

-- Update likes table
ALTER TABLE likes MODIFY COLUMN entity_type ENUM('tool', 'tutorial', 'api', 'prompt') NOT NULL;

-- Update favorites table
ALTER TABLE favorites MODIFY COLUMN entity_type ENUM('tool', 'tutorial', 'api', 'prompt') NOT NULL;
