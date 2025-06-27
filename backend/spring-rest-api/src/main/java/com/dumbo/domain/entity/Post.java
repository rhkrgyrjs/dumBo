package com.dumbo.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * RDBMS dumbo.posts 테이블과 대응되는 Entity
 * posts 테이블에는 es_id만 저장
 * 게시글의 전체 정보는 Elasticsearch에 저장됨
 */
@Entity
@Table(name = "posts")
public class Post 
{
    @Id
    private String esId;

    public String getEsId() { return this.esId; }
    
    public void setEsId(String esId) { this.esId = esId; }
}
