package com.dumbo.domain.dto;

import java.util.List;

// 게시글 정보, 댓글 정보, 답글(댓글에 대한 댓글) 정보를 담아 리턴 -> 제네릭으로 구현
// 커서 기반 페이징을 구현하기 위해 ID, 생성일자, 다음 항목 유무를 저장하고 리턴하는 기능 구현

public class CursorResult<T> 
{
    private List<T> data;
    private Long nextCreatedAt;
    private String id;
    private boolean hasMore;

    public CursorResult() {}

    public CursorResult(List<T> data, Long nextCreatedAt, String id, boolean hasMore) 
    {
        this.data = data;
        this.nextCreatedAt = nextCreatedAt;
        this.id = id;
        this.hasMore = hasMore;
    }

    public List<T> getData() { return data; }
    public Long getNextCreatedAt() { return nextCreatedAt; }
    public String getId() { return id; }
    public boolean getHasMore() { return hasMore; }

    public void setData(List<T> data) { this.data = data; }
    public void setNextCreatedAt(Long nextCreatedAt) { this.nextCreatedAt = nextCreatedAt; }
    public void setId(String id) { this.id = id; }
    public void setHasMore(boolean hasMore) { this.hasMore = hasMore; }
}
