// DB 커넥션 만드는 클래스

package com.dumbo.repository.db.rdbms;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DBConnectionMaker 
{
    @Autowired
    private DataSource dataSource;

    // java.sql.Connection 형태의 DB 커넥션을 리턴하는 함수 
    public Connection makeConnection() throws SQLException
    {
        return dataSource.getConnection();
    }
}