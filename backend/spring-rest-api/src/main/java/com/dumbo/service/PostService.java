package com.dumbo.service;

import com.dumbo.domain.dto.ArticleDTO;
import com.dumbo.domain.dto.CursorResult;
import com.dumbo.domain.dto.PostDTO;
import com.dumbo.domain.entity.Post;
import com.dumbo.domain.entity.User;
import com.dumbo.exception.DatabaseReadException;
import com.dumbo.exception.DatabaseWriteException;

/**
 * 게시글 관련 기능 로직
 * 컨트롤러에서 사용할 간략화된 로직들을 구현
 * 로직 중 에러 발생 시 DumboException을 상속받은 예외 throw
 * 실제 구현체가 구현해야 할 메소드 정의
 */
public interface PostService 
{
    /**
     * 게시글 생성하는 메소드
     * 
     * @param user 게시글 작성한 유저 정보
     * @param postDto 작성할 게시글 원문
     * 
     * @return 작성된 게시글 엔티티
     * 
     * @throws DatabaseWriteException RDBMS/Elasticsearch 쓰기 중 오류 발생
     */
    public Post createPost (User user, PostDTO postDto) throws DatabaseWriteException;

    /**
     * 게시글 피드 리턴하는 메소드
     * 커서 기반 페이징 구현
     * 
     * @param createdAtCursor 생성 시각 커서
     * @param postIdCursor 게시글ID 커서
     * @param limit 불러올 게시글 수
     * @param reverse 순서 반전 여부
     * 
     * @return 게시글 피드
     * 
     * @throws DatabaseReadException RDBMS/Elasticsearch 읽기 중 오류 발생
     */
    public CursorResult<ArticleDTO> getArticleFeed(Long createdAtCursor, String postIdCursor, int limit, boolean reverse) throws DatabaseReadException;

    /**
     * 게시글 삭제하는 메소드
     * 
     * @param user 게시글 삭제할 유저(권한 판단용)
     * @param postId 삭제할 게시글의 ID
     */
    public void deletePost(User user, String postId);

    /**
     * 게시글이 존재하는지 확인하는 메소드
     * 
     * @param postid 존재 확인할 게시글의 ID
     * 
     * @return 게시글 존재 여부
     * 
     * @throws DatabaseReadException RDBMS/Elasticsearch 읽기 중 오류 발생
     */
    public boolean isPostExist(String postid) throws DatabaseReadException;
}
