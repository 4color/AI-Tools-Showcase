-- 为教程插入示例评论数据
-- 教程ID: 1 (ChatGPT入门指南), 2 (Midjourney图像生成技巧), 3 (GitHub Copilot使用教程)
-- 用户ID: 1 (admin), 2 (user1)

-- ChatGPT入门指南 (tutorial_id = 1) 的评论
INSERT INTO `comments` (`user_id`, `entity_type`, `entity_id`, `content`, `rating`, `created_at`) VALUES
(2, 'tutorial', 1, '这个教程非常详细，对于新手来说很容易上手。特别是提示词编写部分，让我学到了很多实用技巧。', 5, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 'tutorial', 1, '内容很全面，但是希望能有更多实际案例。总体来说还是很有帮助的。', 4, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 'tutorial', 1, '作为初学者，这个教程帮我快速入门了ChatGPT。现在我已经能够熟练使用它来辅助工作了。', 5, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 'tutorial', 1, '教程结构清晰，步骤明确。唯一不足的是缺少一些高级用法的介绍。', 4, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Midjourney图像生成技巧 (tutorial_id = 2) 的评论
INSERT INTO `comments` (`user_id`, `entity_type`, `entity_id`, `content`, `rating`, `created_at`) VALUES
(2, 'tutorial', 2, 'Midjourney的技巧讲解得很到位，特别是参数调整部分。按照教程操作，生成的图片质量明显提升了。', 5, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(1, 'tutorial', 2, '图像生成的效果确实不错，但是教程中的一些高级技巧还需要更多实践才能掌握。', 4, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 'tutorial', 2, '非常实用的教程！学会了如何控制图像风格和细节。推荐给所有想学习AI图像生成的朋友。', 5, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 'tutorial', 2, '教程内容不错，但希望能补充一些常见问题的解决方案。', 3, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- GitHub Copilot使用教程 (tutorial_id = 3) 的评论
INSERT INTO `comments` (`user_id`, `entity_type`, `entity_id`, `content`, `rating`, `created_at`) VALUES
(2, 'tutorial', 3, 'Copilot真的提高了我的编程效率！这个教程让我快速掌握了基本用法，现在写代码快多了。', 5, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(1, 'tutorial', 3, '对于开发者来说，这是一个必学的工具。教程讲解清晰，上手很快。', 5, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 'tutorial', 3, '刚开始用Copilot时不太习惯，但按照教程练习后，发现它确实能帮助我写出更好的代码。', 4, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, 'tutorial', 3, '教程很好，但希望能有更多关于如何编写有效注释来引导Copilot的技巧。', 4, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 'tutorial', 3, '作为一个编程新手，这个教程让我对AI辅助编程有了全新的认识。强烈推荐！', 5, NOW());
