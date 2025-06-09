package com.dumbo.repository.dto;

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
