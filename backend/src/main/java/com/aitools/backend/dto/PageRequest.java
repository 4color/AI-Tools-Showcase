package com.aitools.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "分页请求参数")
public class PageRequest {
    
    @Schema(description = "页码，从0开始", defaultValue = "0")
    private Long page = 0L;
    
    @Schema(description = "每页大小", defaultValue = "10")
    private Long size = 10L;
    
    public PageRequest() {}
    
    public PageRequest(Long page, Long size) {
        this.page = page != null ? page : 0L;
        this.size = size != null ? size : 10L;
    }
    
    public Long getPage() {
        return page;
    }
    
    public void setPage(Long page) {
        this.page = page != null ? page : 0L;
    }
    
    public Long getSize() {
        return size;
    }
    
    public void setSize(Long size) {
        this.size = size != null ? size : 10L;
    }
    
    public Long getOffset() {
        return page * size;
    }
}