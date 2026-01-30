package com.aitools.backend.service;

import com.aitools.backend.entity.Tag;
import com.aitools.backend.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagMapper tagMapper;

    public List<Tag> getAllTags() {
        return tagMapper.selectList(null);
    }

    /**
     * 获取某个栏目(type)下的去重标签名称列表
     */
    public List<String> getTagNamesByType(String type) {
        // 使用 MyBatis-Plus wrapper（不使用 @Select）
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Tag> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Tag>()
                        .select("DISTINCT name")
                        .eq("type", type)
                        .orderByAsc("name");
        return tagMapper.selectObjs(wrapper).stream().map(String::valueOf).collect(Collectors.toList());
    }

    public Optional<Tag> getTagById(Long id) {
        return Optional.ofNullable(tagMapper.selectById(id));
    }

    public Optional<Tag> getTagByName(String name) {
        return tagMapper.selectList(null).stream()
                .filter(t -> name.equals(t.getName()))
                .findFirst();
    }

    public Tag saveTag(Tag tag) {
        if (tag.getId() == null) {
            tagMapper.insert(tag);
        } else {
            tagMapper.updateById(tag);
        }
        return tag;
    }

    public void deleteTag(Long id) {
        tagMapper.deleteById(id);
    }
}