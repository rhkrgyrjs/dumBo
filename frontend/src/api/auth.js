import request from './axios/request';

// 로그인(토큰), 회원가입 등 API 요청 처리

// 액세스 토큰 저장
function storeAccessToken(accessToken)
{

}

// 리프레시 토큰 저장
function storeRefreshToken(refreshToken)
{

}

// 토큰 페어 요청 : 로그인과는 다른 API 사용, Access Token 필요
function requestTokenPair(accessToken)
{
    
}

// 로그인
async function login(username, password)
{
    // 로그인 요청
    // 로그인 성공 시 토큰 저장
    // 로그인 실패 시 응답 리턴
    let res = await request.post('/users/login', {'username' : username, 'password' : password});
    if (res)

}

// 회원가입
function signup(username, email, password, passwordCheck)
{
    // 기입 정보 체크 -> 기입 정보가 올바르지 않다면 요청 보내지 않음
    // 회원가입 요청
    // 요청 성공/실패 여부 리턴
}
