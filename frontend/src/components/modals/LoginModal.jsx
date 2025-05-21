import React from "react";

export default function LoginModal({ onSignUpClick, onClose }) {
  return (
    <div style={modalOverlayStyle}>
      <div style={modalContentStyle}>
        <h2>로그인</h2>
        <input type="text" placeholder="아이디" />
        <input type="password" placeholder="비밀번호" />
        <button>로그인</button>
        <br />
        <button onClick={onSignUpClick}>회원가입</button>
        <button onClick={onClose}>닫기</button>
      </div>
    </div>
  );
}

const modalOverlayStyle = {
  position: "fixed",
  top: 0,
  left: 0,
  width: "100vw",
  height: "100vh",
  backgroundColor: "rgba(0,0,0,0.5)",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  zIndex: 1000,
};

const modalContentStyle = {
  backgroundColor: "white",
  padding: "30px",
  borderRadius: "8px",
  minWidth: "300px",
  boxShadow: "0 4px 20px rgba(0,0,0,0.3)",
};
