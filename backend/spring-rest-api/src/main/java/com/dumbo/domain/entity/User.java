package com.dumbo.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import java.time.LocalDateTime;

/**
 * RDBMS dumbo.users 테이블과 대응되는 Entity
 */
@Entity
@Table(name = "users")
public class User 
{
    @Id
    private String id;
    
    private String password;
    private String nickname;
    private String email;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public String getId() { return this.id; }
    public String getPassword() { return this.password; }
    public String getNickname() { return this.nickname; }
    public String getEmail() { return this.email; }
    public LocalDateTime getCreatedAt() { return this.createdAt; }

    public void setId(String id) { this.id = id; }
    public void setPassword(String password) { this.password = password; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setEmail(String email) { this.email = email; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
