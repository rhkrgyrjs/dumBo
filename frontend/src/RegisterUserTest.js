import React, { useState } from "react";
import axios from "axios";

export default function SignupForm() {
  const [form, setForm] = useState({
    username: "",
    password: "",
    passwordConfirm: "",
    email: "",
  });
  const [message, setMessage] = useState("");

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (form.password !== form.passwordConfirm) {
      setMessage("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
      return;
    }

    try {
      const response = await axios.post("http://localhost:8080/dumbo-backend/users/signup", {
        username: form.username,
        password: form.password,
        passwordConfirm: form.passwordConfirm,
        email: form.email,
      });

      if (response.status === 200) {
        setMessage("회원가입 성공!");
        setForm({ username: "", password: "", passwordConfirm: "", email: "" });
      } else {
        setMessage(`회원가입 실패: ${response.statusText}`);
      }
    } catch (error) {
      if (error.response) {
        // 서버가 상태 코드로 에러 응답했을 때
        setMessage(`회원가입 실패: ${error.response.data}`);
      } else {
        // 네트워크 오류 등
        setMessage("서버와 통신 중 오류가 발생했습니다.");
      }
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        name="username"
        placeholder="아이디"
        value={form.username}
        onChange={handleChange}
      />
      <input
        name="password"
        type="password"
        placeholder="비밀번호"
        value={form.password}
        onChange={handleChange}
      />
      <input
        name="passwordConfirm"
        type="password"
        placeholder="비밀번호 확인"
        value={form.passwordConfirm}
        onChange={handleChange}
      />
      <input
        name="email"
        placeholder="이메일"
        value={form.email}
        onChange={handleChange}
      />
      <button type="submit">회원가입</button>
      <div>{message}</div>
    </form>
  );
}
