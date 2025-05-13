import React, { useEffect, useState } from 'react';
import axios from 'axios';

function App() {
  // KST 시간 데이터를 저장할 상태 변수
  const [time, setTime] = useState(null);

  // 컴포넌트가 마운트되면 API 요청을 보냄
  useEffect(() => {
    axios.get('http://localhost:8080/dumbo-backend/time')
      .then(response => {
        // 성공적으로 응답을 받으면 time 상태에 설정
        setTime(response.data.time_kst);
      })
      .catch(error => {
        console.error("There was an error fetching the time!", error);
      });
  }, []); // 빈 배열을 넘겨주면 컴포넌트가 처음 마운트될 때만 실행됨

  return (
    <div className="App">
      <h1>KST 현재 시간</h1>
      <p>{time ? time : '시간을 로딩 중입니다...'}</p>
    </div>
  );
}

export default App;
