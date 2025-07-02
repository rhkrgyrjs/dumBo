package com.dumbo.repository.dao.daoImpl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dumbo.domain.dto.ArticleDTO;
import com.dumbo.domain.dto.CursorResult;
import com.dumbo.domain.dto.PostDTO;
import com.dumbo.domain.entity.User;
import com.dumbo.repository.dao.PostDao;
import com.dumbo.repository.dao.elasticsearch.ArticleElasticsearchDao;
import com.dumbo.repository.rdbms.DBConnectionMaker;

import java.util.List;

import com.dumbo.util.UUIDGenerator;

@Repository
public class PostDaoImpl implements PostDao
{
    @Autowired
    private DBConnectionMaker connectionMaker;

    @Autowired
    private ArticleElasticsearchDao esDao;

    @Autowired
    private UUIDGenerator uuidGenerator;

    public String insertPostIdAndReturnId(User user) throws SQLException
    {
        String sql = "INSERT INTO posts (es_id, author_id) VALUES (?, ?)";
        String uuid = uuidGenerator.generate();
        try (Connection c = connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, uuid);
            ps.setString(2, user.getId());
            ps.executeUpdate();
        }
        return uuid;
    }

    public void pushPost(String postId, User user, PostDTO postDto) throws IOException
    {   
        esDao.saveArticle(postId, user, postDto);
    }

    public boolean existsById(String postId) throws SQLException, IOException
    {
        String sql = "SELECT es_id FROM posts WHERE es_id = ?";
        try (Connection c = connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return esDao.isArticleExists(rs.getString("es_id"));
            else return false;
        }
    }

    public void deletePostId(String postId) throws SQLException
    {
        String sql = "DELETE FROM posts WHERE es_id = ?";
        try (Connection c = connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, postId);
            ps.executeUpdate();
        }
    }

    public void deletePostContent(String postId) throws IOException
    {
        esDao.deleteArticle(postId);
    }


    public List<String> getImageNamesByPostId(String postId) throws IOException
    {
        return esDao.getImageNamesByArticleId(postId);
    }

    
    // postId로 es에 저장된 게시글 하나 찾는 메소드
    public ArticleDTO getArticleByPostId(String postId) throws IOException
    {
        return esDao.getArticleById(postId);
    }


    public CursorResult<ArticleDTO> getArticleFeed(Long createdAtCursor, String postIdCursor, int limit, boolean reverse) throws IOException 
    {
        return esDao.getArticles(createdAtCursor, postIdCursor, limit, reverse);
    }

    public List<String> getAllImageNamesByAuthorId(String authorId) throws IOException 
    {
        return esDao.getImageNamesByAuthorId(authorId);
    }


    public void deleteAllPostsByAuthorId(String authorId) throws IOException 
    {
        esDao.deleteArticlesByAuthorId(authorId);
    }
}
