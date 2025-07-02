import axios from 'axios';

/**
 * 백엔드 REST API 요청을 보내기 위한 Axios 객체
 */
const request = axios.create(
    {
        baseURL : process.env.REACT_APP_API_BASE_URL,
        headers : { 'Content-Type' : 'application/json' }
    }
);

export default request;