import React from "react";

function ArticleFeedTest() {
  const fetchArticles = () => {
    fetch("http://localhost:8080/dumbo-backend/post")
      .then(async (res) => {
        const text = await res.text();
        try {
          const data = JSON.parse(text);
          if (!res.ok) {
            throw new Error(data.message || "서버 에러");
          }
          console.log("게시글 응답:", data);
        } catch (e) {
          console.error("JSON 파싱 실패 또는 서버 에러:", text);
        }
      })
      .catch((err) => {
        console.error("게시글 요청 실패:", err);
      });
  };

  return (
    <div>
      <button onClick={fetchArticles}>게시글 불러오기</button>
    </div>
  );
}

export default ArticleFeedTest;
