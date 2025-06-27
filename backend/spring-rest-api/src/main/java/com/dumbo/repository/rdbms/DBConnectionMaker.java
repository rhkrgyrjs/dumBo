// DB 커넥션 만드는 클래스

package com.dumbo.repository.rdbms;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * RDBMS의 변경 시에도 같은 방법으로 Connection을 가져오기 위한 클래스
 */
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