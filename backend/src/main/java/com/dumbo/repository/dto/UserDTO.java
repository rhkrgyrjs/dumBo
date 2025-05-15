package com.dumbo.repository.dto;

public class UserDTO 
{
    private String username;
    private String password;
    private String passwordConfirm; // 비밀번호 확인용
    private String email;

    public UserDTO() {}

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getPasswordConfirm() { return passwordConfirm; }
    public String getEmail() { return email; }
    
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setPasswordConfirm(String passwordConfirm) { this.passwordConfirm = passwordConfirm; }
    public void setEmail(String email) { this.email = email; }
}
