package com.dumbo.domain.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 유저 -> 서버 유저정보 수정 DTO
 */
public class UserModifyDTO 
{
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_])[A-Za-z\\d!@#$%^&*()\\-_]+$",
            message = "비밀번호는 대문자, 소문자, 숫자, 특수문자(!@#$%^&*()-_)를 포함해야 합니다.")
    private String password;

    @Size(min = 2, max = 8, message = "닉네임은 2자 이상, 8자 이하여야 합니다..")
    @Pattern(regexp = "^(?!.*[!@#$%^&*()+={}\\\\[\\\\]|\\\\\\\\:;\\\"'<>,?/]).+$",
            message = "닉네임에 사용 불가한 특수문자가 포함되어 있습니다.")
    private String nickname;

    public UserModifyDTO() {}

    public String getPassword() { return password; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    
    public void setPassword(String password) { this.password = password; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setEmail(String email) { this.email = email; }
}
