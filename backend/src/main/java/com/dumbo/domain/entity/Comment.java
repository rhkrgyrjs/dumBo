package com.dumbo.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment 
{
    @Id
    private String id;

    private String postId;
    private String replyTo;
    private String authorId;
    private String authorNickname;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer replyCount;

    public String getId() { return this.id; }
    public String getPostId() { return this.postId; }
    public String getReplyTo() { return this.replyTo; }
    public String getAuthorId() { return this.authorId; }
    public String getAuthorNickname() { return this.authorNickname; }
    public String getContent() { return this.content; }
    public LocalDateTime getCreatedAt() { return this.createdAt; }
    public LocalDateTime getUpdatedAt() { return this.updatedAt; }
    public Integer getReplyCount() { return this.replyCount; }

    public void setId(String commentId) { this.id = commentId; }
    public void setPostId(String postId) { this.postId = postId; }
    public void setReplyTo(String commentId) { this.replyTo = commentId; }
    public void setAuthorId(String userId) { this.authorId = userId; }
    public void setAuthorNickname(String nickname) { this.authorNickname = nickname; }
    public void setContent(String content) { this.content = content; }
    public void setCreatedAt(LocalDateTime createdAt ) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setReplyCount(Integer count) { this.replyCount = count; }
}
