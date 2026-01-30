-- Add tutorial cover image (banner) and LLM image selection flags

-- Tutorial banner (cover image)
ALTER TABLE `tutorial`
  ADD COLUMN `cover_image` VARCHAR(512) NULL;

-- Mark default image model provider in api_info
ALTER TABLE `api_info`
  ADD COLUMN `is_default_image_provider` TINYINT(1) NOT NULL DEFAULT 0;

-- Mark image generation models in api_model
ALTER TABLE `api_model`
  ADD COLUMN `is_image_model` TINYINT(1) NOT NULL DEFAULT 0;


