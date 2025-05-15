package com.dumbo.repository.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class MysqlConnectionMaker implements DBConnectionMaker
{
    @Autowired
    private DataSource dataSource;

    @Override
    public Connection makeConnection() throws SQLException
    {
        return dataSource.getConnection();
    }
}
