import React, { useState } from "react";
import AuthModalManager from "./modals/AuthModalManager";

export default function App() {
  const [showAuthModal, setShowAuthModal] = useState(false);

  return (
    <div>
      <h1>게시판 메인 페이지 (임시)</h1>
      <button onClick={() => setShowAuthModal(true)}>로그인</button>

      {showAuthModal && (
        <AuthModalManager />
      )}
    </div>
  );
}
