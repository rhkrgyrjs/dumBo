package com.dumbo.service.comment;

import org.springframework.stereotype.Service;

import com.dumbo.domain.dto.CommentDTO;
import com.dumbo.domain.dto.CursorResult;
import com.dumbo.domain.dto.PostDTO;
import com.dumbo.domain.entity.Comment;
import com.dumbo.domain.entity.Post;
import com.dumbo.domain.entity.User;
import com.dumbo.exception.DatabaseReadException;
import com.dumbo.exception.DatabaseWriteException;
import com.dumbo.exception.PostNotFoundException;

@Service
public interface CommentService 
{
    public void createComment (User user, String postId, String content) throws PostNotFoundException, DatabaseReadException, DatabaseWriteException;
    public void createReply(User user, String commentId, String postId, String content);
    public CursorResult<CommentDTO> getCommentFeed(String postId, Long createdAtCursor, String commentIdCursor, int limit, boolean reverse) throws DatabaseReadException;
    public CursorResult<CommentDTO> getReplyFeed(String postId, String commentId, Long createdAtCursor, String replyIdCursor, int limit) throws DatabaseReadException;
}
