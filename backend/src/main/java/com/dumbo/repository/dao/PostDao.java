package com.dumbo.repository.dao;

import com.dumbo.repository.entity.Post;

import java.sql.SQLException;

import com.dumbo.repository.dto.PostDTO;

public interface PostDao 
{
    public Post createArticle(String userId, PostDTO postDto) throws SQLException;
}
