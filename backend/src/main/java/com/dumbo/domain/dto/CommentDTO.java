package com.dumbo.domain.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentDTO 
{
    @JsonProperty("comment_id")
    private String id;

    @JsonProperty("author_id")
    private String authorId;
    
    @JsonProperty("author_nickname")
    private String authorNickname;
    
    @JsonProperty("content")
    private String content;

    @JsonProperty("created_at")
    private Long createdAt;

    @JsonProperty("updated_at")
    private Long updatedAt;

    @JsonProperty("reply_count")
    private Integer replyCount;

    public String getId() { return this.id; }
    public String getAuthorId() { return this.authorId; }
    public String getAuthorNickname() { return this.authorNickname; }
    public String getContent() { return this.content; }
    public Long getCreatedAt() { return this.createdAt; }
    public Long getUpdatedAt() { return this.updatedAt; }
    public Integer getReplyCount() { return this.replyCount; }

    public void setId(String commentId) { this.id = commentId; }
    public void setAuthorId(String userId) { this.authorId = userId; }
    public void setAuthorNickname(String nickname) { this.authorNickname = nickname; }
    public void setContent(String content) { this.content = content; }
    public void setCreatedAt(Long createdAt ) { this.createdAt = createdAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
    public void setReplyCount(Integer count) { this.replyCount = count; }
}
