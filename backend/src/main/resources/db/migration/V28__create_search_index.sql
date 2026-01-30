-- 统一搜索索引表：存储各栏目（工具/教程/提示词/API）的标题、简介，主键不用自增，用 (entity_type, entity_id) 联合主键
CREATE TABLE search_index (
    entity_type VARCHAR(32) NOT NULL COMMENT 'tool/tutorial/prompt/api',
    entity_id BIGINT NOT NULL COMMENT '对应表的主键',
    title VARCHAR(500) NOT NULL COMMENT '标题（工具名/教程标题/提示词标题/API名）',
    content TEXT COMMENT '简介/描述，用于全文搜索',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (entity_type, entity_id),
    FULLTEXT INDEX ft_search_index_title_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统一搜索索引';
