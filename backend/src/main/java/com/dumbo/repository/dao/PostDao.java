package com.dumbo.repository.dao;

import com.dumbo.repository.entity.Post;
import com.dumbo.repository.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.sql.SQLException;

import com.dumbo.repository.dto.PostDTO;

import java.util.List;
import com.dumbo.repository.dto.ArticleDTO;
import com.dumbo.repository.dto.CursorResult;

public interface PostDao 
{
    public Post createArticle(User user, PostDTO postDto) throws SQLException, JsonProcessingException;
    public CursorResult getArticles(Long createdAtCursor, String postIdCursor, int limit, boolean reverse) throws IOException;
}
