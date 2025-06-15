import requestWithCredentials from "./axios/requestWithCredentials";
import request from './axios/request'

// 로그인(토큰), 회원가입 등 API 요청 처리

// 토큰 페어 요청 : 로그인과는 다른 API 사용
export async function requestTokenPair()
{
    try
    {
        const res = await requestWithCredentials.post(
            '/auth/reissue'
        );
        return { 'nickname' : res.data.nickname, 'userId' : res.data.accessToken.userId, 'accessToken' : res.data.accessToken.token };
    }
    catch (error)
    {
        if (error.response)
        {
            // 서버로부터 응답은 받았지만, 토큰 재발급이 실패한 경우
            // 재로그인을 유도해야 함
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
        return null;
    }
}

// 로그인
export async function login(email, password)
{
    try
    {
        // 로그인 요청
        const res = await requestWithCredentials.post(
            '/auth/login',
            { 'email' : email, 'password' : password}
        );
        return { 'nickname' : res.data.nickname, 'userId' : res.data.accessToken.userId, 'accessToken' : res.data.accessToken.token };
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
        return null;
    }
}

export async function logout()
{
    try { let res = await requestWithCredentials.post('/auth/logout'); console.log(res.data); }
    catch (error) { if (error.request) console.log('서버로부터 요청이 오지 않음'); }
}



// 닉네임 사용 가능한지 체크
export async function nicknameCheck(nickname)
{
    try
    {
        let res = await request.get('/auth/signup/nickname-check', { params : { 'nickname' : nickname } });
        console.log(res.data.message);
        return res.data.useable;
    }
    catch (error)
    {
        console.log(error);
        return false;
    }
}

// 이메일 사용 가능한지 체크
export async function emailCheck(email)
{
    try
    {
        let res = await request.get('/auth/signup/email-check', {params : { 'email' : email } });
        console.log(res.data.message);
        return res.data.useable;
    }
    catch (error)
    {
        console.log(error);
        return false;
    }
}

// 회원가입 --> 일단 임시로 짜놓음
export async function signup(email, password, nickname)
{
    // 기입 정보 체크 -> 기입 정보가 올바르지 않다면 요청 보내지 않음
    // 회원가입 요청
    // 요청 성공/실패 여부 리턴
    
    try
    {
        // 로그인 요청
        const res = await request.post(
            '/auth/signup',
            { 'email' : email, 'password' : password, 'nickname' : nickname }
        );
        return true;
    } catch (error)
    {
        console.log(error);
        return false;
    }
}