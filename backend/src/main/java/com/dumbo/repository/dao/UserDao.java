package com.dumbo.repository.dao;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dumbo.repository.db.DBConnectionMaker;
import com.dumbo.repository.dto.UserDTO;
import com.dumbo.util.Bcrypt;

@Repository
public class UserDao extends BaseDao
{
    @Autowired
    public UserDao(DBConnectionMaker connectionMaker) { super(connectionMaker); }

    public void createUser(UserDTO userDto) throws SQLException
    {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (Connection c = this.connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, userDto.getUsername());
            ps.setString(2, Bcrypt.hashPassword(userDto.getPassword())); // Bcrypt 암호화
            ps.setString(3, userDto.getEmail());
            
            if (ps.executeUpdate() == 0) throw new SQLException("Creating user failed, no rows affected.");
        }
    }


}
