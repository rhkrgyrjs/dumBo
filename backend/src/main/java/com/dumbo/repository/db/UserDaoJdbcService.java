package com.dumbo.repository.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dumbo.repository.dao.UserDao;
import com.dumbo.repository.dto.UserDTO;
import com.dumbo.repository.entity.User;
import com.dumbo.util.Bcrypt;

@Repository
public class UserDaoJdbcService implements UserDao
{
    @Autowired
    private DBConnectionMaker connectionMaker;

    public User loginCheck(String username, String password) throws SQLException
    {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection c = this.connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next() && Bcrypt.verifyPassword(password, rs.getString("password")))
                {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setNickname(rs.getString("nickname"));
                    user.setEmail(rs.getString("email"));
                    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return user;
                }
                else { return null; }
            }
        }
    }

    public User findUserByUsername(String username) throws SQLException
    {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection c = this.connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setNickname(rs.getString("nickname"));
                    user.setEmail(rs.getString("email"));
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
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setNickname(rs.getString("nickname"));
                    user.setEmail(rs.getString("email"));
                    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return user;
                }
                else { return null; }
            }
        }
    }

    public void createUser(UserDTO userDto) throws SQLException
    {
        String sql = "INSERT INTO users (username, password, email, nickname) VALUES (?, ?, ?, ?)";

        try (Connection c = this.connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, userDto.getUsername());
            ps.setString(2, Bcrypt.hashPassword(userDto.getPassword())); // Bcrypt 암호화
            ps.setString(3, userDto.getEmail());
            ps.setString(4, userDto.getNickname());
            
            if (ps.executeUpdate() == 0) throw new SQLException("Creating user failed, no rows affected.");
        }
    }
}
