import React from "react";

export default function SignupModal({ onClose }) {
  return (
    <div className="modal">
      <h2>회원가입</h2>
      <input type="text" placeholder="아이디" />
      <input type="password" placeholder="비밀번호" />
      <input type="password" placeholder="비밀번호 확인" />
      <button>회원가입</button>
      <button onClick={onClose}>닫기</button>
    </div>
  );
}
