-- 为工具插入示例评论数据
-- 工具ID: 1 (ChatGPT), 2 (Midjourney), 3 (GitHub Copilot), 4 (Claude), 5 (Stable Diffusion)
-- 用户ID: 1 (admin), 2 (user1)

-- ChatGPT (tool_id = 1) 的评论
INSERT INTO `comments` (`user_id`, `entity_type`, `entity_id`, `content`, `rating`, `created_at`) VALUES
(2, 'tool', 1, 'ChatGPT真的太强大了！我用它来写代码、写文章、回答问题，几乎无所不能。免费版已经很好用了，Plus版本更是如虎添翼。', 5, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(1, 'tool', 1, '作为开发者，ChatGPT帮我解决了很多编程问题。它的代码生成能力很强，但有时候需要仔细检查，不能完全依赖。', 4, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(2, 'tool', 1, '我用ChatGPT来学习新知识，它解释得很清楚。不过有时候会生成一些不太准确的信息，需要自己验证一下。', 4, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(1, 'tool', 1, 'ChatGPT的对话能力确实很强，但免费版在高峰期经常无法访问，这点比较影响使用体验。', 3, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 'tool', 1, '强烈推荐ChatGPT！无论是工作还是学习，它都是我的得力助手。特别是GPT-4版本，回答质量明显提升。', 5, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 'tool', 1, 'ChatGPT帮我写了很多邮件和文档，节省了大量时间。唯一不足的是有时候会生成比较通用的回答。', 4, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Midjourney (tool_id = 2) 的评论
INSERT INTO `comments` (`user_id`, `entity_type`, `entity_id`, `content`, `rating`, `created_at`) VALUES
(2, 'tool', 2, 'Midjourney生成的图像质量真的很高！我用它来创作概念艺术，效果超出预期。订阅价格虽然不便宜，但值得。', 5, DATE_SUB(NOW(), INTERVAL 12 DAY)),
(1, 'tool', 2, 'Midjourney的参数控制很强大，可以精确控制图像风格。不过学习曲线有点陡，需要花时间熟悉各种参数。', 4, DATE_SUB(NOW(), INTERVAL 9 DAY)),
(2, 'tool', 2, '我用Midjourney为我的项目生成了很多素材，节省了找设计师的成本。图像质量确实很棒，艺术感很强。', 5, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(1, 'tool', 2, 'Midjourney的图像生成速度还可以，但有时候需要多次尝试才能得到满意的结果。提示词编写需要技巧。', 4, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 'tool', 2, 'Midjourney是我用过最好的AI图像生成工具。虽然需要Discord，但社区很活跃，可以学到很多技巧。', 5, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, 'tool', 2, 'Midjourney的图像质量确实不错，但订阅费用对于个人用户来说有点高。如果能提供更灵活的定价方案就好了。', 4, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- GitHub Copilot (tool_id = 3) 的评论
INSERT INTO `comments` (`user_id`, `entity_type`, `entity_id`, `content`, `rating`, `created_at`) VALUES
(2, 'tool', 3, 'GitHub Copilot真的改变了我的编程方式！代码补全非常智能，大大提高了开发效率。强烈推荐给所有开发者。', 5, DATE_SUB(NOW(), INTERVAL 11 DAY)),
(1, 'tool', 3, 'Copilot的代码建议很准确，特别是对于常见的编程模式。不过有时候会生成一些不太优化的代码，需要自己改进。', 4, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(2, 'tool', 3, '我用Copilot来学习新的编程语言，它生成的代码示例很有帮助。但要注意，不能完全依赖它，还是要理解代码逻辑。', 4, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(1, 'tool', 3, 'Copilot帮我写了很多重复性的代码，节省了很多时间。对于快速原型开发特别有用。', 5, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 'tool', 3, 'Copilot的代码补全功能很强，但有时候会打断我的思路。需要学会如何合理使用它。', 4, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 'tool', 3, 'Copilot对于Python和JavaScript的支持最好，其他语言的支持还有待提升。总体来说是个很好的工具。', 4, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Claude (tool_id = 4) 的评论
INSERT INTO `comments` (`user_id`, `entity_type`, `entity_id`, `content`, `rating`, `created_at`) VALUES
(2, 'tool', 4, 'Claude的安全性确实做得很好，我可以用它来处理一些敏感信息。长文本处理能力也很强，可以分析很长的文档。', 5, DATE_SUB(NOW(), INTERVAL 9 DAY)),
(1, 'tool', 4, 'Claude的回答质量很高，特别是对于复杂问题的分析。但免费版的使用次数有限，经常需要等待。', 4, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(2, 'tool', 4, '我用Claude来分析长文档和代码，它的理解能力很强。安全性也是我选择它的重要原因。', 5, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 'tool', 4, 'Claude的对话体验很好，回答比较可靠。但相比ChatGPT，功能上可能稍显单一一些。', 4, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 'tool', 4, 'Claude特别适合需要安全可靠对话的场景。我用它来处理工作文档，效果很好。', 5, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 'tool', 4, 'Claude的长上下文窗口很实用，可以处理超长文档。但免费版的限制比较多，Pro版本价格有点高。', 4, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Stable Diffusion (tool_id = 5) 的评论
INSERT INTO `comments` (`user_id`, `entity_type`, `entity_id`, `content`, `rating`, `created_at`) VALUES
(2, 'tool', 5, 'Stable Diffusion是开源的，可以在本地运行，这点很棒！我用它生成了很多图像，完全免费，而且保护隐私。', 5, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(1, 'tool', 5, 'Stable Diffusion的模型质量很好，而且有很多社区模型可以选择。但需要一定的GPU资源，对硬件要求较高。', 4, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(2, 'tool', 5, '我用Stable Diffusion来生成项目需要的图像素材，完全免费，而且可以离线使用。开源社区很活跃，有很多资源。', 5, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(1, 'tool', 5, 'Stable Diffusion的图像质量不错，但相比Midjourney可能稍逊一筹。不过考虑到它是免费的，已经很好了。', 4, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 'tool', 5, 'Stable Diffusion的WebUI界面很友好，容易上手。我可以在本地训练自己的模型，这点很吸引人。', 5, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 'tool', 5, 'Stable Diffusion适合有一定技术基础的用户。虽然设置有点复杂，但一旦配置好，使用体验很好。', 4, DATE_SUB(NOW(), INTERVAL 1 DAY));
