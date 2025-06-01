package com.dumbo.repository.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserDTO 
{
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_])[A-Za-z\\d!@#$%^&*()\\-_]+$",
            message = "비밀번호는 대문자, 소문자, 숫자, 특수문자(!@#$%^&*()-_)를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호를 검증해주세요.")
    private String passwordConfirm; // 비밀번호 확인용

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 8, message = "닉네임은 2자 이상, 8자 이하여야 합니다..")
    @Pattern(regexp = "^(?!.*[!@#$%^&*()+={}\\\\[\\\\]|\\\\\\\\:;\\\"'<>,?/]).+$",
            message = "닉네임에 사용 불가한 특수문자가 포함되어 있습니다.")
    private String nickname;


    public UserDTO() {}

    public String getPassword() { return password; }
    public String getPasswordConfirm() { return passwordConfirm; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    
    public void setPassword(String password) { this.password = password; }
    public void setPasswordConfirm(String passwordConfirm) { this.passwordConfirm = passwordConfirm; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setEmail(String email) { this.email = email; }
}
