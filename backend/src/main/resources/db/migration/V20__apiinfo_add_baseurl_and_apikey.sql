ALTER TABLE api_info
    ADD COLUMN base_url VARCHAR(500),
    ADD COLUMN api_key VARCHAR(500);

-- Optional: set base_url for initial sample data (if those rows exist)
UPDATE api_info SET base_url = 'https://api.openai.com/v1' WHERE name = 'OpenAI API' AND (base_url IS NULL OR base_url = '');
UPDATE api_info SET base_url = 'https://generativelanguage.googleapis.com/v1beta' WHERE name = 'Gemini API' AND (base_url IS NULL OR base_url = '');
UPDATE api_info SET base_url = 'https://api.anthropic.com/v1' WHERE name = 'Claude API' AND (base_url IS NULL OR base_url = '');
