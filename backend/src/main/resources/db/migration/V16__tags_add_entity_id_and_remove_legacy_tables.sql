-- tags table is shared across modules.
-- Use tags.type as entityType (tool/tutorial/api/prompt), and add entity_id to point to entity primary key.
-- Migrate legacy tables: tool_features and prompt_tags into tags, then drop them.

-- 1) Add entity_id to tags (nullable for existing global tags)
ALTER TABLE tags ADD COLUMN entity_id BIGINT NULL AFTER type;

-- 2) Drop UNIQUE constraint on tags.name (it was for global dictionary, but now tags are per-entity mappings)
-- MySQL uses an index for UNIQUE(name), usually named `name`
ALTER TABLE tags DROP INDEX name;

-- 3) Index for per-entity lookup
CREATE INDEX idx_tags_type_entity ON tags (type, entity_id);

-- 4) Migrate tool_features -> tags(type='tool', entity_id=tool_id, name=feature)
INSERT INTO tags (name, type, entity_id, created_at)
SELECT tf.feature, 'tool', tf.tool_id, NOW()
FROM tool_features tf;

-- 5) Migrate prompt_tags -> tags(type='prompt', entity_id=prompt_id, name=tag)
INSERT INTO tags (name, type, entity_id, created_at)
SELECT pt.tag, 'prompt', pt.prompt_id, NOW()
FROM prompt_tags pt;

-- 6) Drop legacy tables
DROP TABLE tool_features;
DROP TABLE prompt_tags;

