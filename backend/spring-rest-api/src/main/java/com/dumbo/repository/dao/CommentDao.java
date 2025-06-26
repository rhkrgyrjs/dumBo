package com.dumbo.repository.dao;

import java.sql.SQLException;

import org.apache.kafka.common.message.DescribeTopicPartitionsRequestData.Cursor;

import com.dumbo.domain.dto.CommentDTO;
import com.dumbo.domain.dto.CursorResult;
import com.dumbo.domain.entity.Comment;
import com.dumbo.domain.entity.User;

public interface CommentDao 
{
    public boolean createComment(User user, String postId, String content);
    public CursorResult<CommentDTO> getCommentFeed(String postId, Long createdAtCursor, String commentIdCursor, int limit, boolean reverse) throws SQLException;
    public Comment getCommentById(String commentId) throws SQLException;

    public void createReply(User user, Comment comment, String postId, String content) throws SQLException;
    public void incReplyCount(String commentId) throws SQLException;
    public CursorResult<CommentDTO> getReplysByComment(Comment comment, Long createdAtCursor, String replyIdCursor, int limit) throws SQLException;
}
