import axios from 'axios';
import { getAccessToken } from '../token/accessToken';

const requestWithAccessToken = axios.create(
    {
        baseURL : process.env.REACT_APP_API_BASE_URL,
        headers : { 'Content-Type' : 'application/json' }
    }
);

requestWithAccessToken.interceptors.request.use(
    (config) => 
    {
        const token = getAccessToken();
        if (token !== null) { config.headers.Authorization = `Bearer ${token}`; }
        return config;
    }, (error) => Promise.reject(error)
);

export default requestWithAccessToken;