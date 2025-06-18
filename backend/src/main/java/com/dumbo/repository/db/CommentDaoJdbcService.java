package com.dumbo.repository.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dumbo.repository.dao.CommentDao;
import com.dumbo.repository.entity.Comment;

import com.dumbo.repository.entity.User;

@Repository
public class CommentDaoJdbcService implements CommentDao
{
    @Autowired
    private DBConnectionMaker connectionMaker;

    public boolean createComment(User user, String postId, String content)
    {
        String sql = "INSERT INTO comments (post_id, author_id, author_nickname, content) VALUES (?, ?, ?, ?)";
        try (Connection c = connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, postId);
            ps.setString(2, user.getId());
            ps.setString(3, user.getNickname());
            ps.setString(4, content);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }
}
