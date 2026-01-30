package com.aitools.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "分页响应包装类")
public class PageResponse<T> {
    
    @Schema(description = "数据列表")
    private T[] content;
    
    @Schema(description = "总记录数")
    private Long total;
    
    @Schema(description = "当前页码")
    private Integer page;
    
    @Schema(description = "每页大小")
    private Integer size;
    
    @Schema(description = "总页数")
    private Integer totalPages;
    
    @Schema(description = "是否为第一页")
    private Boolean first;
    
    @Schema(description = "是否为最后一页")
    private Boolean last;
    
    public PageResponse() {}
    
    public PageResponse(T[] content, Long total, Integer page, Integer size) {
        this.content = content;
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = (int) Math.ceil((double) total / size);
        this.first = page == 0;
        this.last = page >= totalPages - 1;
    }
    
    public T[] getContent() {
        return content;
    }
    
    public void setContent(T[] content) {
        this.content = content;
    }
    
    public Long getTotal() {
        return total;
    }
    
    public void setTotal(Long total) {
        this.total = total;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
    
    public Integer getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
    
    public Boolean getFirst() {
        return first;
    }
    
    public void setFirst(Boolean first) {
        this.first = first;
    }
    
    public Boolean getLast() {
        return last;
    }
    
    public void setLast(Boolean last) {
        this.last = last;
    }
}