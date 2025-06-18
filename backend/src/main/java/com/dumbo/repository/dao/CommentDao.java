package com.dumbo.repository.dao;

import com.dumbo.repository.entity.User;

public interface CommentDao 
{
    public boolean createComment(User user, String postId, String content);
    // public Comment createReply(String commentId);
}
