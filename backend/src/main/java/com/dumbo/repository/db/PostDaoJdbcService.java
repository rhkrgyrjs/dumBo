package com.dumbo.repository.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dumbo.repository.dto.PostDTO;
import com.dumbo.repository.entity.Post;

@Repository
public class PostDaoJdbcService 
{
    @Autowired
    private DBConnectionMaker connectionMaker;


    public Post createArticle(String userId, PostDTO postDto) throws SQLException
    {
        String sql = "INSERT INTO posts () VALUES () RETURNING es_id";
        try (Connection c = connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql);)
        {
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                Post post = new Post();
                post.setEsId(rs.getString("es_id"));

                // 여기에 카프카 설정 -> 엘라스틱서치가 postDto에 있는 값과 유저명 저장하도록
                // 메시지(그룹) : createArticle, 전달할 정보 : userId와 postDto(직렬화)

                return post;
            } else { return null; }
        }
    }
    
}
