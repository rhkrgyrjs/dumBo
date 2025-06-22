package com.dumbo.repository.dao;

import java.sql.SQLException;

import org.apache.kafka.common.message.DescribeTopicPartitionsRequestData.Cursor;

import com.dumbo.repository.dto.CommentDTO;
import com.dumbo.repository.dto.CursorResult;
import com.dumbo.repository.entity.Comment;
import com.dumbo.repository.entity.User;

public interface CommentDao 
{
    public boolean createComment(User user, String postId, String content);
    public CursorResult<CommentDTO> getCommentFeed(String postId, Long createdAtCursor, String commentIdCursor, int limit, boolean reverse) throws SQLException;
    public Comment getCommentById(String commentId) throws SQLException;

    public boolean createReply(User user, Comment comment, String postId, String content);
    public void incReplyCount(String commentId) throws SQLException;
    public CursorResult<CommentDTO> getReplysByComment(Comment comment, Long createdAtCursor, String replyIdCursor, int limit) throws SQLException;
}
