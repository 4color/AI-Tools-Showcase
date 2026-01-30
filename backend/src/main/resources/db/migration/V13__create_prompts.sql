-- Create prompts table
CREATE TABLE IF NOT EXISTS `prompt` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `content` TEXT NOT NULL,
    `description` TEXT,
    `category` VARCHAR(50) NOT NULL,
    `creator_id` BIGINT NOT NULL,
    `likes` INT DEFAULT 0,
    `views` INT DEFAULT 0,
    `comment_count` INT DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`creator_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    INDEX (`category`),
    INDEX (`creator_id`),
    INDEX (`created_at`),
    FULLTEXT INDEX ft_prompt_search (`title`, `description`, `content`)
);

-- Create prompt_tags table
CREATE TABLE IF NOT EXISTS `prompt_tags` (
    `prompt_id` BIGINT NOT NULL,
    `tag` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`prompt_id`, `tag`),
    FOREIGN KEY (`prompt_id`) REFERENCES `prompt`(`id`) ON DELETE CASCADE,
    INDEX (`tag`)
);

-- Insert sample prompts
INSERT INTO `prompt` (`title`, `content`, `description`, `category`, `creator_id`, `likes`, `views`, `comment_count`) VALUES
('专业文章写作助手', '请作为一位专业的内容创作者，为我撰写一篇关于[主题]的文章。文章要求：1. 目标读者为[受众]；2. 文章长度为 1500-2000 字；3. 包含 3-5 个关键要点；4. 使用清晰的结构和小标题；5. 语言风格专业且易于理解。请确保内容原创、数据准确、观点独到。', '适用于各类专业文章、博客、新闻稿的高效提示词', 'text', 2, 256, 3420, 18),
('Midjourney 人物肖像', 'Portrait of a young woman with flowing silver hair, cyberpunk style, neon lighting, detailed facial features, 8k resolution, cinematic lighting, dramatic atmosphere --ar 3:4 --v 6 --s 250', '生成赛博朋克风格人物肖像的高质量提示词', 'image', 2, 512, 8930, 45),
('React 组件自动生成', 'Create a React functional component for [component_name] with TypeScript. Include: 1. Proper TypeScript interfaces for props; 2. State management using useState if needed; 3. useEffect for data fetching if required; 4. Proper error handling and loading states; 5. Accessibility attributes (aria-*); 6. Styled with Tailwind CSS. The component should follow best practices and be production-ready.', '快速生成高质量 React 组件的提示词', 'code', 2, 328, 4560, 23),
('产品营销文案', '为[产品名称]撰写一段引人注目的营销文案。目标用户是[目标受众]，核心卖点包括：1. [卖点1]；2. [卖点2]；3. [卖点3]。文案需要突出产品价值，包含情感共鸣和行动号召（CTA）。风格：年轻、活力、有创意。长度：100-150字。', '适用于产品推广、广告创意的营销文案提示词', 'text', 2, 189, 2340, 12),
('Stable Diffusion 室内设计', 'Interior design of a modern living room, minimalist style, natural light flowing through large windows, Scandinavian furniture, warm color palette, 8k ultra detailed, photorealistic rendering, architectural photography --negative ugly, deformed, noisy, blurry', '生成现代简约风格室内设计效果图的提示词', 'image', 2, 421, 6780, 34),
('Python 数据分析脚本', 'Write a Python script for data analysis that: 1. Loads data from [data_source]; 2. Performs data cleaning and preprocessing; 3. Generates descriptive statistics; 4. Creates visualizations using matplotlib/seaborn; 5. Saves results to [output_path]. Include error handling, logging, and documentation. Use pandas, numpy, and matplotlib libraries.', '自动化数据分析和可视化的 Python 脚本提示词', 'code', 2, 267, 3890, 19);

-- Insert prompt tags
INSERT INTO `prompt_tags` (`prompt_id`, `tag`) VALUES
(1, '写作'), (1, '文章'), (1, '内容创作'),
(2, 'Midjourney'), (2, '肖像'), (2, '赛博朋克'),
(3, 'React'), (3, 'TypeScript'), (3, '组件'),
(4, '营销'), (4, '文案'), (4, '广告'),
(5, 'Stable Diffusion'), (5, '室内设计'), (5, '效果图'),
(6, 'Python'), (6, '数据分析'), (6, '脚本');
