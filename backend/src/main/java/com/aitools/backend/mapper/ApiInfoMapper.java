package com.aitools.backend.mapper;

import com.aitools.backend.dto.PageRequest;
import com.aitools.backend.entity.ApiInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ApiInfoMapper extends BaseMapper<ApiInfo> {

    default List<ApiInfo> findByProvider(String provider) {
        return selectList(new LambdaQueryWrapper<ApiInfo>().eq(ApiInfo::getProvider, provider));
    }

    default List<ApiInfo> findByCategoryId(Long categoryId) {
        return selectList(new LambdaQueryWrapper<ApiInfo>().eq(ApiInfo::getCategoryId, categoryId));
    }

    default List<String> findAllProviders() {
        return selectList(
                new LambdaQueryWrapper<ApiInfo>()
                        .select(ApiInfo::getProvider)
                        .orderByAsc(ApiInfo::getProvider)
        ).stream()
                .map(ApiInfo::getProvider)
                .distinct()
                .collect(Collectors.toList());
    }

    default List<ApiInfo> selectPage(PageRequest pageRequest) {
        Page<ApiInfo> page = new Page<>(pageRequest.getPage().intValue() + 1, pageRequest.getSize().intValue());
        QueryWrapper<ApiInfo> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("id");
        IPage<ApiInfo> result = this.selectPage(page, wrapper);
        return result.getRecords();
    }

    default Long selectCount(Object condition) {
        return Long.valueOf(this.selectCount(null));
    }
}
