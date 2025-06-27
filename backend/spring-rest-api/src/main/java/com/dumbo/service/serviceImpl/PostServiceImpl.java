package com.dumbo.service.serviceImpl;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dumbo.domain.dto.ArticleDTO;
import com.dumbo.domain.dto.CursorResult;
import com.dumbo.domain.dto.PostDTO;
import com.dumbo.domain.entity.Post;
import com.dumbo.domain.entity.User;
import com.dumbo.exception.DatabaseDeleteException;
import com.dumbo.exception.DatabaseReadException;
import com.dumbo.exception.DatabaseWriteException;
import com.dumbo.exception.ForbiddenActionException;
import com.dumbo.exception.PostNotFoundException;
import com.dumbo.repository.dao.PostDao;
import com.dumbo.service.PostService;

@Service
public class PostServiceImpl implements PostService
{
    @Autowired
    private PostDao postDao;

    public Post createPost (User user, PostDTO postDto) throws DatabaseWriteException
    {
        String postId = null;
        try { postId = postDao.insertPostIdAndReturnId(); }
        catch (SQLException e ) { throw new DatabaseWriteException("게시글 키 생성에 실패했습니다."); } // RDBMS에 post.id 생성

        try { postDao.pushPost(postId, user, postDto); }
        catch (IOException e )
        {
            try { postDao.deletePostId(postId); } catch(SQLException s) {}
            throw new DatabaseWriteException("게시글 내용 저장에 실패했습니다.");
        }

        Post post = new Post();
        post.setEsId(postId);
        return post;
    }

    public CursorResult<ArticleDTO> getArticleFeed(Long createdAtCursor, String postIdCursor, int limit, boolean reverse) throws DatabaseReadException
    {
        // 게시글 캐싱이 이루어질 경우 여기서 처리

        // 게시글 피드 생성
        try { return postDao.getArticleFeed(createdAtCursor, postIdCursor, limit, reverse); }
        catch (IOException e) { throw new DatabaseReadException("게시글 피드 정보를 불러오는 데 실패했습니다."); }
    }

    public void deletePost(User user, String postId)
    {
        // 게시글이 존재하지 않을 경우
        ArticleDTO article = postDao.getArticleByPostId(postId);
        if (article == null) throw new PostNotFoundException();
        
        // 게시글을 삭제할 권한이 사용자에게 없을 경우
        if (!user.getId().equals(article.getAuthorId())) throw new ForbiddenActionException("다른 사람의 게시글은 삭제할 수 없습니다.");

        try { postDao.deletePostContent(postId); }
        catch (IOException e) { throw new DatabaseDeleteException("게시글 내용을 삭제하는 데 실패했습니다."); }

        try { postDao.deletePostId(postId); }
        catch(SQLException e) { throw new DatabaseDeleteException("게시글 키를 삭제하는 데 실패했습니다."); }
    }


    public boolean isPostExist(String postid) throws DatabaseReadException
    {
        try { return postDao.existsById(postid); }
        catch(SQLException | IOException e) { throw new DatabaseReadException("게시글 정보를 불러오는 데 실패했습니다."); }
    }
}
