package com.aitools.backend.mapper;

import com.aitools.backend.entity.ApiModel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApiModelMapper extends BaseMapper<ApiModel> {

    default List<ApiModel> findByApiId(Long apiId) {
        return selectList(new LambdaQueryWrapper<ApiModel>()
                .eq(ApiModel::getApiId, apiId)
                .orderByAsc(ApiModel::getId));
    }
}

