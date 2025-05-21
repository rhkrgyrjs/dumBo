package com.dumbo.repository.dao;

import java.sql.SQLException;

import com.dumbo.repository.dto.UserDTO;

import com.dumbo.repository.entity.User;

public interface UserDao
{
    public User loginCheck(String username, String password) throws SQLException; // 로그인 체크 함수
    public User findUserByUsername(String username) throws SQLException;          // username으로 유저 정보 찾기
    public User findUserByEmail(String email) throws SQLException;                // 이메일로 유저 정보 찾기
    public void createUser(UserDTO userDto) throws SQLException;                  // 회원가입(DB에 유저 정보 Row 생성)
}
