// DB 커넥션을 만드는 역할을 담당하는 클래스. 추상 클래스라 구현해서 쓰면 됨

package com.dumbo.repository.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBConnectionMaker 
{
    // java.sql.Connection 형태의 DB 커넥션을 리턴하는 함수
    Connection makeConnection() throws SQLException;
}