package com.dumbo.domain.dto;

// 클라이언트 -> 서버로 글 작성(Post) 요청에 쓰이는 DTO
// 단순히 유저가 작성한 글의 형식(글자 수 등)이 맞는지만 확인하는 용도

public class PostDTO 
{

    // title, content 포멧 설정해야 함.

    private String title;
    private String content;

    public String getTitle() { return this.title; }
    public String getContent() { return this.content; }

    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
}
