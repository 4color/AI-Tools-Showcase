package com.aitools.backend.mapper;

import com.aitools.backend.entity.Tag;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    default List<String> findNamesByTypeAndEntityId(String type, Long entityId) {
        if (type == null || entityId == null) {
            return java.util.Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getType, type)
                .eq(Tag::getEntityId, entityId))
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

    /** 删除指定 type + entity_id 的所有标签（用于更新时先删后插） */
    default int deleteByTypeAndEntityId(String type, Long entityId) {
        if (type == null || entityId == null) return 0;
        return delete(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getType, type)
                .eq(Tag::getEntityId, entityId));
    }

}