// App.js
import Header from './components/Header';
import PostTemp from './components/PostTemp';
import Modals from './components/modals/Modals';

function App() 
{
  return (
    <div>
      <Modals />
      <Header />
      <PostTemp />
    </div>
  );
}

export default App;


/*
import React, { useState } from "react";
import LoginModal from "./components/modals/LoginModal";  // 경로를 맞게 조정

function App() {
  const [modalOpen, setModalOpen] = useState(false);

  return (
    <div style={{ padding: "2rem" }}>
      <h1>메인 페이지</h1>

      <button
        onClick={() => setModalOpen(true)}
        style={{ padding: "0.5rem 1rem", fontSize: "1rem" }}
      >
        로그인 모달 열기
      </button>

      <LoginModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
      />
    </div>
  );
}
*/

