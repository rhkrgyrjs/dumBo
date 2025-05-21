import React, { useState } from 'react';

export default function AuthForm() {
  const [isLogin, setIsLogin] = useState(true);

  const [signupData, setSignupData] = useState({
    username: '',
    email: '',
    password: '',
    passwordConfirm: '',
  });

  const [loginData, setLoginData] = useState({
    username: '',
    password: '',
  });

  const [message, setMessage] = useState(''); // 서버 응답 메시지

  const handleChange = (e, form) => {
    const { name, value } = e.target;
    if (form === 'signup') {
      setSignupData(prev => ({ ...prev, [name]: value }));
    } else {
      setLoginData(prev => ({ ...prev, [name]: value }));
    }
    setMessage(''); // 입력시 메시지 초기화
  };

  // 중복체크 공통 함수
  const checkDuplicate = async (type) => {
    const urlMap = {
      username: 'http://localhost:8080/dumbo-backend/users/signup/usernameCheck',
      email: 'http://localhost:8080/dumbo-backend/users/signup/emailCheck',
    };
    const value = signupData[type];
    if (!value) {
      setMessage(`${type === 'username' ? '유저명' : '이메일'}을 입력해주세요.`);
      return;
    }

    try {
      const res = await fetch(urlMap[type], {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ [type]: value }),
      });
      const data = await res.json();

      if (res.ok) {
        setMessage(data.message || `${type === 'username' ? '유저명' : '이메일'} 사용 가능`);
      } else {
        setMessage(data.message || `${type === 'username' ? '유저명' : '이메일'} 중복입니다.`);
      }
    } catch (error) {
      setMessage('중복체크 오류: ' + error.message);
    }
  };

  const handleSignup = async e => {
    e.preventDefault();
    setMessage('');

    if (signupData.password !== signupData.passwordConfirm) {
      setMessage('비밀번호가 일치하지 않습니다.');
      return;
    }

    try {
      const res = await fetch('http://localhost:8080/dumbo-backend/users/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: signupData.username,
          email: signupData.email,
          password: signupData.password,
          passwordConfirm: signupData.passwordConfirm
        }),
      });

      const data = await res.json();

      if (!res.ok) {
        setMessage('회원가입 실패: ' + (data.message || res.statusText));
        return;
      }

      setMessage('회원가입 성공! 로그인 해주세요.');
      setIsLogin(true);
    } catch (error) {
      setMessage('회원가입 중 오류 발생: ' + error.message);
    }
  };

  const handleLogin = async e => {
    e.preventDefault();
    setMessage('');

    try {
      const res = await fetch('http://localhost:8080/dumbo-backend/users/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: loginData.username,
          password: loginData.password,
        }),
      });

      const data = await res.json();

      if (!res.ok) {
        setMessage('로그인 실패: ' + (data.message || res.statusText));
        return;
      }

      setMessage('로그인 성공! 환영합니다, ' + loginData.username);
      // 로그인 성공 후 처리(토큰 저장 등) 여기에 추가 가능
    } catch (error) {
      setMessage('로그인 중 오류 발생: ' + error.message);
    }
  };

  return (
    <div style={{ maxWidth: 400, margin: 'auto', padding: 20, fontFamily: 'Arial' }}>
      <div style={{ marginBottom: 20 }}>
        <button
          onClick={() => { setIsLogin(true); setMessage(''); }}
          style={{
            marginRight: 10,
            padding: '8px 16px',
            backgroundColor: isLogin ? '#4CAF50' : '#ddd',
            color: isLogin ? 'white' : 'black',
            border: 'none',
            cursor: 'pointer',
          }}
        >
          로그인
        </button>
        <button
          onClick={() => { setIsLogin(false); setMessage(''); }}
          style={{
            padding: '8px 16px',
            backgroundColor: !isLogin ? '#4CAF50' : '#ddd',
            color: !isLogin ? 'white' : 'black',
            border: 'none',
            cursor: 'pointer',
          }}
        >
          회원가입
        </button>
      </div>

      {isLogin ? (
        <form onSubmit={handleLogin}>
          <div style={{ marginBottom: 10 }}>
            <label>
              유저명:<br />
              <input
                type="text"
                name="username"
                value={loginData.username}
                onChange={e => handleChange(e, 'login')}
                required
                style={{ width: '100%', padding: 6 }}
              />
            </label>
          </div>
          <div style={{ marginBottom: 10 }}>
            <label>
              비밀번호:<br />
              <input
                type="password"
                name="password"
                value={loginData.password}
                onChange={e => handleChange(e, 'login')}
                required
                style={{ width: '100%', padding: 6 }}
              />
            </label>
          </div>
          <button type="submit" style={{ padding: '8px 16px', cursor: 'pointer' }}>
            로그인
          </button>
        </form>
      ) : (
        <form onSubmit={handleSignup}>
          <div style={{ marginBottom: 10, display: 'flex', alignItems: 'center' }}>
            <label style={{ flex: 1 }}>
              유저명:<br />
              <input
                type="text"
                name="username"
                value={signupData.username}
                onChange={e => handleChange(e, 'signup')}
                required
                style={{ width: '100%', padding: 6 }}
              />
            </label>
            <button
              type="button"
              onClick={() => checkDuplicate('username')}
              style={{ marginLeft: 10, padding: '8px 12px', cursor: 'pointer' }}
            >
              중복체크
            </button>
          </div>

          <div style={{ marginBottom: 10, display: 'flex', alignItems: 'center' }}>
            <label style={{ flex: 1 }}>
              이메일:<br />
              <input
                type="email"
                name="email"
                value={signupData.email}
                onChange={e => handleChange(e, 'signup')}
                required
                style={{ width: '100%', padding: 6 }}
              />
            </label>
            <button
              type="button"
              onClick={() => checkDuplicate('email')}
              style={{ marginLeft: 10, padding: '8px 12px', cursor: 'pointer' }}
            >
              중복체크
            </button>
          </div>

          <div style={{ marginBottom: 10 }}>
            <label>
              비밀번호:<br />
              <input
                type="password"
                name="password"
                value={signupData.password}
                onChange={e => handleChange(e, 'signup')}
                required
                style={{ width: '100%', padding: 6 }}
              />
            </label>
          </div>

          <div style={{ marginBottom: 10 }}>
            <label>
              비밀번호 확인:<br />
              <input
                type="password"
                name="passwordConfirm"
                value={signupData.passwordConfirm}
                onChange={e => handleChange(e, 'signup')}
                required
                style={{ width: '100%', padding: 6 }}
              />
            </label>
          </div>

          <button type="submit" style={{ padding: '8px 16px', cursor: 'pointer' }}>
            회원가입
          </button>
        </form>
      )}

      {/* 서버 응답 메시지 보여주기 */}
      {message && (
        <div
          style={{
            marginTop: 20,
            padding: 10,
            border: '1px solid #ccc',
            backgroundColor: '#f9f9f9',
            whiteSpace: 'pre-wrap',
          }}
        >
          {message}
        </div>
      )}
    </div>
  );
}
