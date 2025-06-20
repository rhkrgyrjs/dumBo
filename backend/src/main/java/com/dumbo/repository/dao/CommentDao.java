package com.dumbo.repository.dao;

import java.sql.SQLException;

import com.dumbo.repository.dto.CommentDTO;
import com.dumbo.repository.dto.CursorResult;
import com.dumbo.repository.entity.User;

public interface CommentDao 
{
    public boolean createComment(User user, String postId, String content);
    public CursorResult<CommentDTO> getCommentFeed(String postId, Long createdAtCursor, String commentIdCursor, int limit, boolean reverse) throws SQLException;
    // public Comment createReply(String commentId);
}
