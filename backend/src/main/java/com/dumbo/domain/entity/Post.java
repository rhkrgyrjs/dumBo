package com.dumbo.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "posts")
public class Post 
{
    @Id
    private String esId;

    public String getEsId() { return this.esId; }
    
    public void setEsId(String esId) { this.esId = esId; }
}
