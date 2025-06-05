package com.dumbo.repository.dao.post;

import java.sql.SQLException;

public interface PostDao 
{
    public String createArticle() throws SQLException;  // 생성된 Article의 posts.es_id 리턴, Kafka에 본문 정보 저장
}
