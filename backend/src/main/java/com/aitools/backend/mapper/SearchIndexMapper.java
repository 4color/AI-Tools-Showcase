package com.aitools.backend.mapper;

import com.aitools.backend.entity.SearchIndex;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SearchIndexMapper extends BaseMapper<SearchIndex> {

    @Insert("INSERT INTO search_index (entity_type, entity_id, title, content, created_at, updated_at) " +
            "VALUES (#{entityType}, #{entityId}, #{title}, #{content}, #{createdAt}, #{updatedAt}) " +
            "ON DUPLICATE KEY UPDATE title = VALUES(title), content = VALUES(content), updated_at = VALUES(updated_at)")
    void upsert(SearchIndex row);

    @Delete("DELETE FROM search_index WHERE entity_type = #{entityType} AND entity_id = #{entityId}")
    void deleteByEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId);
}
