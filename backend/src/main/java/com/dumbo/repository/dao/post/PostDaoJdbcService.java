package com.dumbo.repository.dao.post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import com.dumbo.repository.db.rdbms.DBConnectionMaker;

public class PostDaoJdbcService implements PostDao
{
    @Autowired
    private DBConnectionMaker connectionMaker;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public String createArticle() throws SQLException
    {
        String sql = "INSERT INTO () VALUES () RETURNING es_id";  // INSERT .. RETURNING 구문은 MySQL 8.0.21 이상만 지원
        try (Connection c = this.connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql);)
        {
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                // 여기에 Kafka에 본문 저장하는 루틴
                
                return rs.getString("es_id");
            }
            else return null;
        }
    }
}
