package com.dumbo.repository.dao;

import com.dumbo.repository.entity.Post;
import com.dumbo.repository.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;

import com.dumbo.repository.dto.PostDTO;

public interface PostDao 
{
    public Post createArticle(User user, PostDTO postDto) throws SQLException, JsonProcessingException;
}
