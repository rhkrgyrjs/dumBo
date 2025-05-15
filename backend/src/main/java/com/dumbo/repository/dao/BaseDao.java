package com.dumbo.repository.dao;

import org.springframework.beans.factory.annotation.Autowired;

import com.dumbo.repository.db.DBConnectionMaker;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseDao 
{
    protected final DBConnectionMaker connectionMaker;
    
    @Autowired
    public BaseDao(DBConnectionMaker connectionMaker) { this.connectionMaker = connectionMaker; }

    protected Connection getConnection() throws SQLException
    {
        return connectionMaker.makeConnection();
    }
}
