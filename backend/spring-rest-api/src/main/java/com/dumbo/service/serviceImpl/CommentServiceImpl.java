package com.dumbo.service.serviceImpl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dumbo.domain.dto.CommentDTO;
import com.dumbo.domain.dto.CursorResult;
import com.dumbo.domain.entity.Comment;
import com.dumbo.domain.entity.User;
import com.dumbo.exception.CommentNotFoundException;
import com.dumbo.exception.DatabaseReadException;
import com.dumbo.exception.DatabaseWriteException;
import com.dumbo.exception.PostNotFoundException;
import com.dumbo.repository.dao.CommentDao;
import com.dumbo.service.CommentService;
import com.dumbo.service.PostService;

@Service
public class CommentServiceImpl implements CommentService
{
    @Autowired
    private PostService postServ;

    @Autowired
    private CommentDao commentDao;

    public void createComment (User user, String postId, String content) throws PostNotFoundException, DatabaseReadException, DatabaseWriteException
    {
        // 게시글이 존재하는지 확인
        if (!postServ.isPostExist(postId)) throw new PostNotFoundException();

        // 댓글 작성
        if (!commentDao.createComment(user, postId, content)) throw new DatabaseWriteException("댓글 작성에 실패했습니다.");
    }

    @Transactional
    public void createReply(User user, String commentId, String postId, String content)
    {
        // 게시글이 존재하는지 확인
        if (!postServ.isPostExist(postId)) throw new PostNotFoundException();

        // 댓글이 존재하는지 확인
        Comment comment = null;
        try 
        {
            comment = commentDao.getCommentById(commentId);
            if (comment == null) throw new CommentNotFoundException();
        }
        catch (SQLException e) { throw new DatabaseReadException("댓글 정보를 불러오는 데 실패했습니다."); }

        // 답글 작성
        try { commentDao.createReply(user, comment, postId, content); }
        catch (SQLException e) { throw new DatabaseWriteException("답글을 작성하는 데 실패했습니다."); }

        // 원 댓글의 reply_count 증가
        try { commentDao.incReplyCount(comment.getId()); }
        catch (SQLException e) { throw new DatabaseWriteException("댓글의 답글 수를 증가시키는 데 실패했습니다."); }
        
    }


    public CursorResult<CommentDTO> getCommentFeed(String postId, Long createdAtCursor, String commentIdCursor, int limit, boolean reverse) throws DatabaseReadException
    {
        // 댓글 캐싱이 이루어질 경우 여기서 처리

        // 게시글이 존재하는지 확인
        if (!postServ.isPostExist(postId)) throw new PostNotFoundException();

        // 댓글 피드 생성
        try { return commentDao.getCommentFeed(postId, createdAtCursor, commentIdCursor, limit, reverse); }
        catch (SQLException e) { throw new DatabaseReadException("댓글 피드 정보를 불러오는 데 실패했습니다."); }
    }


    public CursorResult<CommentDTO> getReplyFeed(String postId, String commentId, Long createdAtCursor, String replyIdCursor, int limit) throws DatabaseReadException
    {
        // 답글 캐싱이 이루어질 경우 여기서 처리

        // 게시글이 존재하는지 확인
        if (!postServ.isPostExist(postId)) throw new PostNotFoundException();

        // 댓글이 존재하는지 확인
        Comment comment = null;
        try 
        {
            comment = commentDao.getCommentById(commentId);
            if (comment == null) throw new CommentNotFoundException();
        }
        catch (SQLException e) { throw new DatabaseReadException("댓글 정보를 불러오는 데 실패했습니다."); }

        // 답글 피드 생성
        try { return commentDao.getRepliesByComment(comment, createdAtCursor, replyIdCursor, limit); }
        catch (SQLException e) { throw new DatabaseReadException("답글 피드 정보를 불러오는 데 실패했습니다."); }
    }

}
