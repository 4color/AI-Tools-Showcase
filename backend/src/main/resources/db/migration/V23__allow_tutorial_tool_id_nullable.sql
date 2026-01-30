-- Allow tutorial.tool_id to be nullable (previously NOT NULL in initial migration)
ALTER TABLE `tutorial`
  MODIFY COLUMN `tool_id` BIGINT NULL;

-- If any existing rows have tool_id=0 (unlikely), convert to NULL
UPDATE `tutorial` SET `tool_id` = NULL WHERE `tool_id` = 0;

