import axios from 'axios';

/**
 * 백엔드 REST API 요청을 보내기 위한 Axios 객체
 * 리프레시 토큰을 포함한 쿠키를 요청에 같이 보냄(withCredentials true)
 */
const requestWithCredentials = axios.create(
    {
        baseURL : process.env.REACT_APP_API_BASE_URL,
        headers : { 'Content-Type' : 'application/json' },
        withCredentials : true
    }
);

export default requestWithCredentials;