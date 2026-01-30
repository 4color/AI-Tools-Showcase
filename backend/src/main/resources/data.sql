-- 插入示例用户数据
INSERT INTO user (username, email, password, role, created_at) VALUES
('admin', 'admin@example.com', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', 'ADMIN', NOW()),
('user1', 'user1@example.com', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', 'USER', NOW());

-- 插入示例工具数据
INSERT INTO tool (name, description, category, url, price, rating, review_count, created_at) VALUES
('ChatGPT', '强大的对话AI助手，可以回答问题、写作、编程等', '文本生成', 'https://chat.openai.com', 'FREEMIUM', 4.8, 12500, NOW()),
('Midjourney', 'AI图像生成工具，创建高质量的艺术图像', '图像生成', 'https://midjourney.com', 'PAID', 4.7, 8900, NOW()),
('GitHub Copilot', 'AI编程助手，帮助开发者编写代码', '编程助手', 'https://github.com/features/copilot', 'PAID', 4.6, 15600, NOW()),
('Claude', 'Anthropic开发的AI助手，专注于安全和有用性', '文本生成', 'https://claude.ai', 'FREEMIUM', 4.5, 7200, NOW()),
('Stable Diffusion', '开源的文本到图像生成模型', '图像生成', 'https://stability.ai', 'FREE', 4.4, 9800, NOW());

-- 插入工具标签
INSERT INTO tool_tags (tool_id, tag) VALUES
(1, 'AI'), (1, '对话'), (1, '写作'), (1, '编程'),
(2, 'AI'), (2, '图像'), (2, '艺术'), (2, '设计'),
(3, 'AI'), (3, '编程'), (3, '代码'), (3, '开发'),
(4, 'AI'), (4, '对话'), (4, '安全'), (4, '分析'),
(5, 'AI'), (5, '图像'), (5, '开源'), (5, '生成');

-- 插入工具特性
INSERT INTO tool_features (tool_id, feature) VALUES
(1, '自然语言对话'), (1, '代码生成'), (1, '文本创作'), (1, '多语言支持'),
(2, '高质量图像生成'), (2, '艺术风格多样'), (2, '快速生成'), (2, '社区分享'),
(3, '智能代码补全'), (3, '多语言支持'), (3, 'IDE集成'), (3, '学习代码模式'),
(4, '安全对话'), (4, '长文本处理'), (4, '文档分析'), (4, '代码理解'),
(5, '开源模型'), (5, '本地部署'), (5, '自定义训练'), (5, '社区支持');

-- 插入API信息
INSERT INTO api_info (name, provider, description, documentation, pricing, category, created_at) VALUES
('OpenAI API', 'OpenAI', '强大的AI API，包括GPT系列模型、DALL-E图像生成等', 'https://platform.openai.com/docs', '按使用量付费，有免费额度', '文本生成', NOW()),
('Gemini API', 'Google', 'Google的下一代AI模型API', 'https://ai.google.dev/docs', '按使用量付费，有免费额度', '多模态', NOW()),
('Claude API', 'Anthropic', '专注于安全性和有用性的AI API', 'https://docs.anthropic.com', '按使用量付费', '文本生成', NOW()),
('Stability AI API', 'Stability AI', '开源图像生成模型API', 'https://stability.ai/docs', '按使用量付费', '图像生成', NOW());

-- 插入API特性
INSERT INTO api_features (api_id, feature) VALUES
(1, 'GPT-4支持'), (1, 'DALL-E图像生成'), (1, '语音转文字'), (1, '微调功能'),
(2, '多模态支持'), (2, '长文本处理'), (2, '实时响应'), (2, 'Google生态集成'),
(3, '长上下文窗口'), (3, '安全对齐'), (3, '多语言支持'), (3, '工具使用能力'),
(4, 'Stable Diffusion'), (4, '图像编辑'), (4, '视频生成'), (4, '开源模型');

-- 插入示例教程
INSERT INTO tutorial (title, description, content, tool_id, author_id, difficulty, read_time, created_at) VALUES
('ChatGPT入门指南', '学习如何使用ChatGPT进行高效对话和创作', 'ChatGPT是一个强大的AI助手...', 1, 2, 'BEGINNER', 10, NOW()),
('Midjourney图像生成技巧', '掌握Midjourney创建高质量图像的技巧', 'Midjourney是一个专业的AI图像生成工具...', 2, 2, 'INTERMEDIATE', 15, NOW()),
('GitHub Copilot使用教程', '提高编程效率的AI助手使用指南', 'GitHub Copilot是GitHub推出的AI编程助手...', 3, 2, 'BEGINNER', 12, NOW());

-- 插入教程标签
INSERT INTO tutorial_tags (tutorial_id, tag) VALUES
(1, '入门'), (1, 'ChatGPT'), (1, 'AI对话'),
(2, '进阶'), (2, 'Midjourney'), (2, '图像生成'),
(3, '编程'), (3, 'Copilot'), (3, '效率');