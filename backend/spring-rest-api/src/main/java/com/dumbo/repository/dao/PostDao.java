package com.dumbo.repository.dao;

import com.dumbo.domain.dto.ArticleDTO;
import com.dumbo.domain.dto.CursorResult;
import com.dumbo.domain.dto.PostDTO;
import com.dumbo.domain.entity.User;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 게시글과 관련된 CRUD 작업을 담당하는 DAO
 * 서비스(로직) 코드에서는 해당 인터페이스를 구현한 실제 구현체를 주입받아 사용
 */
public interface PostDao 
{
    // public Post createArticle(User user, PostDTO postDto) throws SQLException, JsonProcessingException;
    // public boolean deleteArticle(String postId) throws SQLException, IOException;

    /**
     * 게시글을 게시글 ID를 통해 가져오는 메소드
     * 
     * @param postId 가져올 게시글의 ID
     * 
     * @return 게시글 정보
     */
    public ArticleDTO getArticleByPostId(String postId);

    /**
     * 게시글을 묶음으로(피드) 가져오는 메소드
     * 생성시각을 메인 커서로, 게시글ID를 서브 커서로 정렬
     * 
     * @param createdAtCursor 메인 커서(생성시각)
     * @param postIdCursor 서브 커서(게시글 ID)
     * @param limit 가져올 게시글 개수
     * @param reverse 게시글 순서 반전(역방향 스크롤 대응)
     * 
     * @return 게시글 정보를 담은 응답용 객체
     * 
     * @throws IOException Elasticsearch 읽기 작업 중 오류 발생할 경우
     */
    public CursorResult<ArticleDTO> getArticleFeed(Long createdAtCursor, String postIdCursor, int limit, boolean reverse) throws IOException;

    /**
     * RDBMS에 게시글의 ID 생성하는 메소드
     * 이후 본문은 Elasticsearch에 저장하는 과정을 거쳐야 정합성이 유지됨
     * 
     * @return 생성된 게시글 ID
     * 
     * @throws SQLException RDBMS 쓰기 작업 중 오류 발생할 경우
     */
    public String insertPostIdAndReturnId() throws SQLException;

    /**
     * Elasticsearch에 게시글 정보 저장하는 메소드
     * 
     * @param postId 저장할 게시글의 ID : RDBMS에 미리 생성해놓아야 함
     * @param user 게시글을 작성하는 유저
     * @param postDto 작성할 게시글의 정보
     * 
     * @throws IOException Elasticsearch 쓰기 작업 중 오류 발생할 경우
     */
    public void pushPost(String postId, User user, PostDTO postDto) throws IOException;

    /**
     * 게시글이 정합성을 유지한 채로 존재하는지 여부를 알려주는 메소드
     * 
     * @param postId 존재를 확인할 게시글의 ID
     * 
     * @return 게시글의 정합성 유지한 존재 여부
     * 
     * @throws SQLException RDBMS 읽기 작업 중 오류 발생할 경우 : 게시글의 ID 찾는 작업에서의 오류
     * @throws IOException Elasticsearch 읽기 작업 중 오류 발생할 경우 : 게시글의 내용 찾는 작업에서의 오류
     */
    public boolean existsById(String postId) throws SQLException, IOException;

    /**
     * RDBMS에 저장된 게시글 ID 삭제하는 메소드
     * 게시글에 달린 댓글/게시글에 달린 댓글에 대한 답글도 DELETE CASCADE가 DDL 레벨에서 정의되어 있음
     * 
     * @param postId 삭제할 게시글의 ID
     * 
     * @throws SQLException RDBMS 삭제 작업 중 오류 발생할 경우
     */
    public void deletePostId(String postId) throws SQLException;

    /**
     * Elasticsearch에 저장된 게시글 정보 삭제하는 메소드
     * 실제 API를 통해 노출되는 게시글은 이 메소드를 통해 지워야 함
     * 
     * @param postId 삭제할 게시글의 ID
     * 
     * @throws IOException Elasticsearch 삭제 작업 중 오류 발생할 경우
     */
    public void deletePostContent(String postId) throws IOException;
}
