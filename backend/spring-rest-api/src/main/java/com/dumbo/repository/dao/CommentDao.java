package com.dumbo.repository.dao;

import java.sql.SQLException;

import com.dumbo.domain.dto.CommentDTO;
import com.dumbo.domain.dto.CursorResult;
import com.dumbo.domain.entity.Comment;
import com.dumbo.domain.entity.User;

/**
 * 댓글과 관련된 CRUD 작업을 담당하는 DAO
 * 서비스(로직) 코드에서는 해당 인터페이스를 구현한 실제 구현체를 주입받아 사용
 */
public interface CommentDao 
{
    /**
     * 댓글을 작성하는 메소드
     * 
     * @param user 댓글을 작성하는 유저
     * @param postId 댓글을 작성할 게시글 ID
     * @param content 댓글 본문
     * 
     * @return 댓글 작성 성공 여부
     */
    public boolean createComment(User user, String postId, String content);

    /**
     * 게시글에 작성된 댓글을 묶음으로 가져오는 메소드
     * 생성시각을 메인 커서로, 댓글ID를 서브 커서로 정렬
     * 
     * @param postId 댓글을 가져올 게시글 ID
     * @param createdAtCursor 메인 커서(생성시각)
     * @param commentIdCursor 서브 커서(댓글 ID)
     * @param limit 가져올 댓글 개수
     * @param reverse 댓글 순서 반전(역방향 스크롤 대응)
     * 
     * @return 댓글 정보를 담은 응답용 객체
     * 
     * @throws SQLException RDBMS 읽기 작업 중 오류 발생할 경우
     */
    public CursorResult<CommentDTO> getCommentFeed(String postId, Long createdAtCursor, String commentIdCursor, int limit, boolean reverse) throws SQLException;

    /**
     * 댓글을 댓글ID를 통해 가져오는 메소드
     * 
     * @param commentId 가져올 댓글의 ID
     * 
     * @return 댓글이 존재할 경우 댓글 정보 / 댓글이 존재하지 않을 경우 null
     * 
     * @throws SQLException
     */
    public Comment getCommentById(String commentId) throws SQLException;

    /**
     * 답글을 작성하는 메소드
     * 
     * @param user 답글을 작성하는 유저
     * @param comment 답글을 작성할 댓글 정보
     * @param postId 답글을 작성할 댓글이 달린 게시글 ID
     * @param content 답글 본문
     * 
     * @throws SQLException RDBMS 쓰기 작업 중 오류 발생할 경우
     */
    public void createReply(User user, Comment comment, String postId, String content) throws SQLException;

    /**
     * 답글 작성 시 해당 댓글의 답글 필드를 갱신하는 메소드
     * 
     * @param commentId 답글 갯수를 갱신할 댓글의 ID
     * 
     * @throws SQLException RDBMS 쓰기 작업 중 오류 발생할 경우
     */
    public void incReplyCount(String commentId) throws SQLException;

    /**
     * 댓글에 작성된 답글을 묶음으로 가져오는 메소드
     * 생성시각을 메인 커서로, 답글ID를 서브 커서로 정렬
     * 
     * @param comment 답글을 가져올 댓글 정보
     * @param createdAtCursor 메인 커서(생성시각)
     * @param replyIdCursor 서브 커서(답글 ID)
     * @param limit 가져올 답글 개수
     * 
     * @return 답글 정보를 담은 응답용 객체
     * 
     * @throws SQLException RDBMS 읽기 작업 중 오류 발생할 경우
     */
    public CursorResult<CommentDTO> getRepliesByComment(Comment comment, Long createdAtCursor, String replyIdCursor, int limit) throws SQLException;
}
