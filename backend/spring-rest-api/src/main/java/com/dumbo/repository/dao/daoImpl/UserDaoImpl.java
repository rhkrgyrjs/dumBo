package com.dumbo.repository.dao.daoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dumbo.domain.dto.UserDTO;
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

    public void createUser(UserDTO userDto) throws SQLException
    {
        String sql = "INSERT INTO users (email, password, nickname) VALUES (?, ?, ?)";

        try (Connection c = this.connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, userDto.getEmail());
            ps.setString(2, Bcrypt.hashPassword(userDto.getPassword())); // Bcrypt 암호화
            ps.setString(3, userDto.getNickname());
            
            if (ps.executeUpdate() == 0) throw new SQLException("Creating user failed, no rows affected.");
        }
    }
}
