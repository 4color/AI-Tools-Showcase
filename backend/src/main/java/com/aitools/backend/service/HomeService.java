package com.aitools.backend.service;

import com.aitools.backend.dto.HomeStatsResponse;
import com.aitools.backend.entity.Tool;
import com.aitools.backend.mapper.ToolMapper;
import com.aitools.backend.mapper.TutorialMapper;
import com.aitools.backend.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final ToolMapper toolMapper;
    private final TutorialMapper tutorialMapper;
    private final UserMapper userMapper;

    public HomeStatsResponse getStats() {
        HomeStatsResponse resp = new HomeStatsResponse();
        resp.setToolCount(Long.valueOf(toolMapper.selectCount(null)));
        resp.setTutorialCount(Long.valueOf(tutorialMapper.selectCount(null)));
        resp.setUserCount(Long.valueOf(userMapper.selectCount(null)));

        QueryWrapper<Tool> q = new QueryWrapper<>();
        q.select("AVG(rating) as rating");
        Tool one = toolMapper.selectOne(q);
        double avg = (one != null && one.getRating() != null) ? one.getRating() : 0.0;
        resp.setAverageRating(Math.round(avg * 10) / 10.0);
        return resp;
    }
}
