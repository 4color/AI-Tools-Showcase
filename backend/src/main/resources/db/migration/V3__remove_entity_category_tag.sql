-- V3__remove_entity_category_tag.sql
-- Remove EntityCategory and EntityTag tables and add type fields to Category and Tag

-- Add type column to Category table
ALTER TABLE categories ADD COLUMN type VARCHAR(255);

-- Add type column to Tag table
ALTER TABLE tags ADD COLUMN type VARCHAR(255);

-- Drop EntityCategory table
DROP TABLE entity_categories;

-- Drop EntityTag table
DROP TABLE entity_tags;