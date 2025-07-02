package com.dumbo.repository.dao.daoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dumbo.domain.dto.UserModifyDTO;
import com.dumbo.domain.dto.UserRegisterDTO;
import com.dumbo.domain.entity.User;
import com.dumbo.repository.dao.UserDao;
import com.dumbo.repository.rdbms.DBConnectionMaker;
import com.dumbo.util.Bcrypt;

@Repository
public class UserDaoImpl implements UserDao
{
    @Autowired
    private DBConnectionMaker connectionMaker;

    public User loginCheck(String email, String password) throws SQLException
    {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection c = this.connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next() && Bcrypt.verifyPassword(password, rs.getString("password")))
                {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setNickname(rs.getString("nickname"));
                    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return user;
                }
                else { return null; }
            }
        }
    }

    public User findUserByUserId(String userId) throws SQLException
    {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection c = this.connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setNickname(rs.getString("nickname"));
                    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return user;
                }
                else { return null; }
            }
        }
    }

    public User findUserByNickname(String nickname) throws SQLException
    {
        String sql = "SELECT * FROM users WHERE nickname = ?";
        try (Connection c = this.connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, nickname);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setNickname(rs.getString("nickname"));
                    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return user;
                }
                else { return null; }
            }
        }
    }

    public User findUserByEmail(String email) throws SQLException
    {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection c = this.connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setNickname(rs.getString("nickname"));
                    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return user;
                }
                else { return null; }
            }
        }
    }

    public void createUser(UserRegisterDTO userDto) throws SQLException
    {
        String sql = "INSERT INTO users (email, password, nickname) VALUES (?, ?, ?)";

        try (Connection c = this.connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, userDto.getEmail());
            ps.setString(2, Bcrypt.hashPassword(userDto.getPassword())); // Bcrypt 암호화
            ps.setString(3, userDto.getNickname());
            ps.executeUpdate();
        }
    }


    public void modifyUser(String userId, UserModifyDTO userDto) throws SQLException
    {
        // UserModifyDTO는 최소 하나의 필드가 null이 아님을 보장하는 로직이 service에 존재
        String sql = "UPDATE users SET email = COALESCE(?, email), nickname = COALESCE(?, nickname), password = COALESCE(?, password) WHERE id = ?";

        try (Connection c = this.connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, userDto.getEmail());
            ps.setString(2, userDto.getNickname());
            if (userDto.getPassword() != null) ps.setString(3, Bcrypt.hashPassword(userDto.getPassword()));
            else ps.setNull(3, Types.CHAR);
            ps.setString(4, userId);
            ps.executeUpdate();
        }
    }


    public void deleteUser(String userId) throws SQLException
    {
        // 유저가 작성한 게시글, 댓글은 SQL DDL 레벨에서 DELETE CASCADE 됨
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection c = this.connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, userId);
            ps.executeUpdate();
        }
    }

}
