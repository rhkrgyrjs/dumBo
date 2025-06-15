package com.dumbo.repository.dto;

import java.util.List;

public class CursorResult {
    private List<ArticleDTO> data;
    private Long nextCreatedAt;
    private String nextPostId;
    private boolean hasMore;

    public CursorResult() {}

    public CursorResult(List<ArticleDTO> data, Long nextCreatedAt, String nextPostId, boolean hasMore) {
        this.data = data;
        this.nextCreatedAt = nextCreatedAt;
        this.nextPostId = nextPostId;
        this.hasMore = hasMore;
    }

    public List<ArticleDTO> getData() {
        return data;
    }

    public void setData(List<ArticleDTO> data) {
        this.data = data;
    }

    public Long getNextCreatedAt() {
        return nextCreatedAt;
    }

    public void setNextCreatedAt(Long nextCreatedAt) {
        this.nextCreatedAt = nextCreatedAt;
    }

    public String getNextPostId() {
        return nextPostId;
    }

    public void setNextPostId(String nextPostId) {
        this.nextPostId = nextPostId;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
