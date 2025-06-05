package com.dumbo.repository.dao.user;

import java.sql.SQLException;

import com.dumbo.repository.dto.UserDTO;

import com.dumbo.repository.entity.User;

public interface UserDao
{
    public User loginCheck(String email, String password) throws SQLException;    // 로그인 체크 함수
    public User findUserByUserId(String userId) throws SQLException;              // userId로 유저 정보 찾기
    public User findUserByNickname(String nickname) throws SQLException;          // 닉네임으로 유저 정보 찾기
    public User findUserByEmail(String email) throws SQLException;                // 이메일로 유저 정보 찾기
    public void createUser(UserDTO userDto) throws SQLException;                  // 회원가입(DB에 유저 정보 Row 생성)
}
