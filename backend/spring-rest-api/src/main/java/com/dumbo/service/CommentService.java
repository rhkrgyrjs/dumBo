package com.dumbo.service;

import com.dumbo.domain.dto.CommentDTO;
import com.dumbo.domain.dto.CursorResult;
import com.dumbo.domain.entity.User;
import com.dumbo.exception.DatabaseReadException;
import com.dumbo.exception.DatabaseWriteException;
import com.dumbo.exception.PostNotFoundException;

/**
 * 댓글/답글 관련 기능 로직
 * 컨트롤러에서 사용할 간략화된 로직들을 구현
 * 로직 중 에러 발생 시 DumboException을 상속받은 예외 throw
 * 실제 구현체가 구현해야 할 메소드 정의
 */
public interface CommentService 
{
    /**
     * 댓글 작성하는 메소드
     * 
     * @param user 댓글을 작성할 유저
     * @param postId 댓글을 작성할 게시글의 ID
     * @param content 댓글 본문
     * 
     * @throws PostNotFoundException 댓글을 작성할 게시글이 존재하지 않을 때
     * @throws DatabaseReadException RDBMS/Elasticsearch 읽기 중 오류 발생
     * @throws DatabaseWriteException RDBMS/Elasticsearch 쓰기 중 오류 발생
     */
    public void createComment (User user, String postId, String content) throws PostNotFoundException, DatabaseReadException, DatabaseWriteException;

    /**
     * 답글 작성하는 메소드
     * 
     * @param user 답글을 작성할 유저
     * @param commentId 답글을 작성할 댓글의 ID
     * @param postId 답글을 작성할 댓글이 달린 게시글의 ID
     * @param content 답글 본문
     */
    public void createReply(User user, String commentId, String postId, String content);

    /**
     * 댓글 피드(묶음)를 리턴받는 메소드
     * 
     * @param postId 댓글이 작성된 게시글의 ID
     * @param createdAtCursor 생성 시각 커서
     * @param commentIdCursor 댓글ID 커서
     * @param limit 불러올 댓글의 갯수
     * @param reverse 댓글 순서 반전(역방향 스크롤 대비)
     * 
     * @return 댓글 정보를 담은 응답용 객체
     * 
     * @throws DatabaseReadException RDBMS/Elasticsearch 읽기 중 오류 발생
     */
    public CursorResult<CommentDTO> getCommentFeed(String postId, Long createdAtCursor, String commentIdCursor, int limit, boolean reverse) throws DatabaseReadException;

    /**
     * 답글 피드(묶음)를 리턴받는 메소드
     * 
     * @param postId 답글이 작성된 게시글의 ID
     * @param commentId 답글이 작성된 댓글의 ID
     * @param createdAtCursor 생성 시각 커서
     * @param replyIdCursor 답글ID 커서
     * @param limit 불러올 답글의 갯수
     * 
     * @return 답글 정보를 담은 응답용 객체
     * 
     * @throws DatabaseReadException RDBMS/Elasticsearch 읽기 중 오류 발생
     */
    public CursorResult<CommentDTO> getReplyFeed(String postId, String commentId, Long createdAtCursor, String replyIdCursor, int limit) throws DatabaseReadException;
}
