package com.aitools.backend.controller;

import com.aitools.backend.common.Result;
import com.aitools.backend.entity.SearchIndex;
import com.aitools.backend.service.SearchIndexService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@CrossOrigin
@Tag(name = "统一搜索", description = "基于 search_index 的全站搜索")
public class SearchController {

    @Autowired
    private SearchIndexService searchIndexService;

    @GetMapping
    @Operation(summary = "全站搜索", description = "按关键词搜索工具/教程/提示词/API；关键词为空时返回最近更新")
    public Result<List<SearchIndex>> search(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String q
    ) {
        String keyword = (q != null && !q.trim().isEmpty()) ? q.trim() : null;
        List<SearchIndex> list = searchIndexService.search(keyword);
        return Result.success(list);
    }
}
