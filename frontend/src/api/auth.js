import requestWithCredentials from "./axios/requestWithCredentials";
import { getAccessToken, setAccessToken } from "./token/accessToken";

// 로그인(토큰), 회원가입 등 API 요청 처리

// 액세스 토큰 저장
function storeAccessToken(accessToken)
{

}

// 토큰 페어 요청 : 로그인과는 다른 API 사용, Access Token 필요
function requestTokenPair(accessToken)
{
    
}

// 로그인
export async function login(username, password)
{
    try
    {
        // 로그인 요청
        const res = await requestWithCredentials.post(
            '/auth/login',
            { 'username' : username, 'password' : password}
        );
        // 로그인 성공 시 액세스 토큰 저장
        setAccessToken(res.data.accessToken.token);
    }
    catch (error)
    {
        if (error.response)
        {
            // 서버로부터 응답은 받았지만, 로그인이 실패한 경우
            // HTTP 응답 코드에 따라 분기할 것인지는 나중에 선택하자
            console.log(error.response);
        }
        else if (error.request)
        {
            // 서버로부터 응답이 오지 않을 경우
            console.log(error.request);
        }
        else
        {
            // 다른 에러 발생 시
            console.log(error);
        }
    }
}

// 회원가입
function signup(username, email, password, passwordCheck)
{
    // 기입 정보 체크 -> 기입 정보가 올바르지 않다면 요청 보내지 않음
    // 회원가입 요청
    // 요청 성공/실패 여부 리턴
}
