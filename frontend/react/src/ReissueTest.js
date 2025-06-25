export default function ReissueTest() {
  const handleReissue = async (e) => {
    e.preventDefault(); // 폼 제출 시 페이지 리로드 방지

    try {
      const res = await fetch('http://localhost:8080/dumbo-backend/auth/reissue', {
        method: 'POST',
        credentials: 'include', // 쿠키를 서버에 자동으로 포함
        headers: {
          'Content-Type': 'application/json',
        }
      });

      const data = await res.json();

      if (!res.ok) {
        console.error('토큰 재발급 실패:', data.message || res.statusText);
        return;
      }

      console.log('재발급 받은 토큰:', data.accessToken);
      // 예: localStorage.setItem("accessToken", data.accessToken);
    } catch (error) {
      console.error('요청 중 오류 발생:', error.message);
    }
  };

  return (
    <div id="reissue-test">
      <form onSubmit={handleReissue}>
        <button type="submit" style={{ padding: '8px 16px', cursor: 'pointer' }}>
          토큰 재발급 테스트
        </button>
      </form>
    </div>
  );
}
