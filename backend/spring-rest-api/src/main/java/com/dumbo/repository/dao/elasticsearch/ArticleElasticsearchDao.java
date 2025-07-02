package com.dumbo.repository.dao.elasticsearch;

import java.io.IOException;
import java.util.List;

import com.dumbo.domain.dto.ArticleDTO;
import com.dumbo.domain.dto.CursorResult;
import com.dumbo.domain.dto.PostDTO;
import com.dumbo.domain.entity.User;

/**
 * Elasticsearch에 저장되는 게시글 정보와 관련된 CRUD 작업을 담당하는 DAO
 * 서비스(로직) 코드에서는 해당 인터페이스를 구현한 실제 구현체를 주입받아 사용
 */
public interface ArticleElasticsearchDao 
{
    /**
     * Elasticsearch에 게시글 정보를 저장하는 메소드
     * 
     * @param articleId RDBMS에 저장된 저장할 게시글의 ID
     * @param user 게시글을 작성한 유저
     * @param postDto 작성할 게시글의 정보
     * 
     * @throws IOException Elasticsearch 쓰기 작업 중 오류 발생할 경우
     */
    public void saveArticle(String articleId, User user, PostDTO postDto) throws IOException;

    /**
     * Elasticsearch에 특정 게시글이 저장되어 있나 여부를 리턴하는 메소드
     * 
     * @param articleId 저장 여부를 확인할 게시글의 ID
     * 
     * @return 게시글의 존재 여부
     * 
     * @throws IOException Elasticsearch 읽기 작업 중 오류 발생할 경우
     */
    public boolean isArticleExists(String articleId) throws IOException;

    /**
     * Elasticsearch에 저장된 특정 게시글을 ID로 불러오는 메소드
     * 
     * @param postId 불러올 게시글의 ID
     * 
     * @return 게시글 정보
     * 
     * @throws IOException Elasticsearch 읽기 작업 중 오류 발생할 경우
     */
    public ArticleDTO getArticleById(String articleId) throws IOException;

    /**
     * Elasticsearch에 저장된 게시글 정보를 삭제하는 메소드
     * 
     * @param articleId 삭제할 게시글의 ID
     * 
     * @throws IOException Elasticsearch 삭제 작업 중 오류 발생할 경우
     */
    public void deleteArticle(String articleId) throws IOException;

    /**
     * 특정 게시글에 포함된 이미지의 이름들을 리턴하는 메소드
     * 
     * @param articleId 이미지의 이름들을 불러올 게시글의 ID
     * 
     * @return 이미지 이름들이 포함된 배열
     * 
     * @throws IOException Elasticsearch 읽기 작업 중 오류 발생할 경우
     */
    public List<String> getImageNamesByArticleId (String articleId) throws IOException;

    /**
     * 특정 유저가 삭정한 게시글들에 포함된 이미지의 이름들을 리턴하는 메소드
     * 
     * @param authorId 이미지의 이름들을 불러올 게시글을 작성한 작성자의 ID
     * 
     * @return 이미지 이름들이 포함된 배열
     * 
     * @throws IOException Elasticsearch 읽기 작업 중 오류 발생할 경우
     */
    public List<String> getImageNamesByAuthorId(String authorId) throws IOException;

    /**
     * 특정 유저가 작성한 게시글 정보들을 모두 삭제하는 메소드
     * 회원탈퇴 시 사용 목적으로 작성됨
     * 
     * @param authorId 작성한 게시글을 삭제할 작성자의 ID
     * 
     * @throws IOException Elasticsearch 삭제 작업 중 오류 발생할 경우
     */
    public void deleteArticlesByAuthorId(String authorId) throws IOException;

    /**
     * 이미지 피드를 불러오기 위한 메소드
     * 커서 기반 페이징 사용
     * 
     * @param createdAtCursor 생성시각 타임스탬프 : 메인 커서
     * @param articleIdCursor 게시글 ID : 서브 커서
     * @param limit 불러올 게시글 정보의 갯수
     * @param reverse 순서 역정렬 여부(위로 스크롤할 경우 대비)
     * 
     * @return 유저에게 리턴할 게시글 정보들이 담긴 객체
     * 
     * @throws IOException Elasticsearch 읽기 작업 중 오류 발생할 경우
     */
    public CursorResult<ArticleDTO> getArticles(Long createdAtCursor, String articleIdCursor, int limit, boolean reverse) throws IOException;
}
