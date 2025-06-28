package com.dumbo.repository.dao;

import java.sql.SQLException;

import com.dumbo.domain.dto.UserModifyDTO;
import com.dumbo.domain.dto.UserRegisterDTO;
import com.dumbo.domain.entity.User;

/**
 * 유저 정보와 관련된 CRUD 작업을 담당하는 DAO
 * 서비스(로직) 코드에서는 해당 인터페이스를 구현한 실제 구현체를 주입받아 사용
 */
public interface UserDao
{
    /**
     * 이메일/비밀번호(평문)을 통해 유저의 로그인 정보를 체크하는 메소드
     * 
     * @param email 유저의 이메일
     * @param password 유저의 비밀번호
     * 
     * @return [성공 시] 로그인 시도한 유저 정보 / [실패 시] null
     * 
     * @throws SQLException RDBMS 읽기 작업 중 오류 발생할 경우
     */
    public User loginCheck(String email, String password) throws SQLException;

    /**
     * 유저 ID를 통해 유저 정보를 찾는 메소드
     * 
     * @param userId 정보를 찾을 유저의 ID
     * 
     * @return [성공 시] 유저 정보 / [실패 시] null
     * 
     * @throws SQLException RDBMS 읽기 작업 중 오류 발생할 경우
     */
    public User findUserByUserId(String userId) throws SQLException;

    /**
     * 유저의 닉네임을 통해 유저 정보를 찾는 메소드
     * 닉네임은 SQL DDL 레벨에서 중복을 허용하지 않음
     * 
     * @param nickname 정보를 찾을 유저의 닉네임
     * 
     * @return [성공 시] 유저 정보 / [실패 시] null
     * 
     * @throws SQLException RDBMS 읽기 작업 중 오류 발생할 경우
     */
    public User findUserByNickname(String nickname) throws SQLException;

    /**
     * 유저의 이메일을 통해 유저 정보를 찾는 메소드
     * 이메일은 SQL DDL 레벨에서 중복을 허용하지 않음
     * 
     * @param email 정보를 찾을 유저의 이메일
     * 
     * @return [성공 시] 유저 정보 / [실패 시] null
     * 
     * @throws SQLException RDBMS 읽기 작업 중 오류 발생할 경우
     */
    public User findUserByEmail(String email) throws SQLException;

    /**
     * 유저 정보를 RDBMS에 추가(회원가입)하는 메소드
     * 유저 정보의 정규식 검사는 담당하지 않음
     * 유저 정보의 정규식 검사는 HTTP Request에서 DTO를 받아올 때 수행
     * 
     * @param userDto 회원가입시킬 유저의 정보
     * 
     * @throws SQLException RDBMS 쓰기 작업 중 오류 발생할 경우
     */
    public void createUser(UserRegisterDTO userDto) throws SQLException;

    /**
     * 유저 정보를 수정하는 메소드
     * UserModifyDTO에 모든 정보가 null이 아닌지 검증 필요함
     * 
     * @param userId 수정할 유저의 ID
     * @param userDto 수정할 정보
     * 
     * @throws SQLException RDBMS 쓰기 작업 중 오류 발생할 경우
     */
    public void modifyUser(String userId, UserModifyDTO userDto) throws SQLException;

    /**
     * 유저 정보를 삭제하는 메소드
     * SQL DDL 레벨에서 유저가 작성한 게시글/댓글/답글, 유저가 작성한 게시글/댓글에 달린 댓글/답글도 전부 삭제됨
     * 
     * @param userId 삭제할 유저의 ID
     * 
     * @throws SQLException RDBMS 삭제 작업 중 오류 발생할 경우
     */
    public void deleteUser(String userId) throws SQLException;
}
