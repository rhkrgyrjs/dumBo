package com.dumbo.repository.dao;

import com.dumbo.domain.dto.ArticleDTO;
import com.dumbo.domain.dto.CursorResult;
import com.dumbo.domain.dto.PostDTO;
import com.dumbo.domain.entity.Post;
import com.dumbo.domain.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface PostDao 
{
    public Post createArticle(User user, PostDTO postDto) throws SQLException, JsonProcessingException;
    public ArticleDTO getArticleByPostId(String postId);
    public boolean deleteArticle(String postId) throws SQLException, IOException;
    public CursorResult<ArticleDTO> getArticleFeed(Long createdAtCursor, String postIdCursor, int limit, boolean reverse) throws IOException;
}
