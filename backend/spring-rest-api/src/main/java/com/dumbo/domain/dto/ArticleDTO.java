package com.dumbo.domain.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 유저 <-> 서버 작성된 게시글의 전체 정보를 담는 DTO
 */
public class ArticleDTO {

    @JsonProperty("post_id")
    private String id;

    @JsonProperty("author_id")
    private String authorId;

    @JsonProperty("author_nickname")
    private String authorNickname;

    @JsonProperty("title")
    private String title;

    @JsonProperty("thumbnail_img_url")
    private String thumbnailImgUrl;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("imgs")
    private List<String> imgNames;

    @JsonInclude(JsonInclude.Include.NON_NULL) // pure text는 검색용이라 리턴 안 해도 됨
    @JsonProperty("content_text")
    private String contentText;

    @JsonProperty("content_html")
    private String contentHtml;

    @JsonProperty("created_at")
    private Long createdAt; // 초 기준 타임스탬프

    @JsonProperty("updated_at")
    private Long updatedAt; // 초 기준 타임스탬프

    @JsonProperty("likes")
    private Long likes;

    @JsonProperty("dislikes")
    private Long dislikes;

    public String getId() { return this.id; }
    public String getAuthorId() { return this.authorId; }
    public String getAuthorNickname() { return this.authorNickname; }
    public String getTitle() { return this.title; }
    public String getThumbnailImgUrl() { return this.thumbnailImgUrl; }
    public List<String> getImgNames() { return this.imgNames; }
    public String getContentText() { return this.contentText; }
    public String getContentHtml() { return this.contentHtml; }
    public Long getCreatedAt() { return this.createdAt; }
    public Long getUpdatedAt() { return this.updatedAt; }
    public Long getLikes() { return this.likes; }
    public Long getDislikes() { return this.dislikes; }

    public void setId(String id) { this.id = id; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public void setAuthorNickname(String authorNickname) { this.authorNickname = authorNickname; }
    public void setTitle(String title) { this.title = title; }
    public void setThumbnailImgUrl(String url) { this.thumbnailImgUrl = url; }
    public void setImgNames(List<String> names) { this.imgNames = names; }
    public void setContentText(String contentText) { this.contentText = contentText; }
    public void setContentHtml(String contentHtml) { this.contentHtml = contentHtml; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
    public void setLikes(Long likes) { this.likes = likes; }
    public void setDislikes(Long dislikes) { this.dislikes = dislikes; }
}
