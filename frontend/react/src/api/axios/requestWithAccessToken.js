import axios from 'axios';
import { useSelector } from 'react-redux';

const axiosInstance = axios.create(
    {
        baseURL : process.env.REACT_APP_API_BASE_URL,
        headers : { 'Content-Type' : 'application/json' }
    }
);


export async function GetRequestWithAccessToken(accessToken, api, data)
{
    axiosInstance.interceptors.request.use(
        (config) => 
        {
            if (accessToken !== null) { config.headers.Authorization = `Bearer ${accessToken}`; }
            return config;
        }, (error) => Promise.reject(error)
    );

    return axiosInstance.get(api, data);
}

async function PostRequestWithAccessToken(accessToken, api, data)
{
    axiosInstance.interceptors.request.use(
        (config) => 
        {
            if (accessToken !== null) { config.headers.Authorization = `Bearer ${accessToken}`; }
            return config;
        }, (error) => Promise.reject(error)
    );

    return axiosInstance.post(api, data);
}

export async function DeleteRequestWithAccessToekn(accessToken, api)
{
    axiosInstance.interceptors.request.use(
        (config) => 
        {
            if (accessToken !== null) { config.headers.Authorization = `Bearer ${accessToken}`; }
            return config;
        }, (error) => Promise.reject(error)
    );

    return axiosInstance.delete(api);
}

export async function DeleteRequestWithAccessTokenWithBody(accessToken, api, body) {
    axiosInstance.interceptors.request.use(
        (config) => {
            if (accessToken !== null) {
                config.headers.Authorization = `Bearer ${accessToken}`;
            }
            return config;
        }, 
        (error) => Promise.reject(error)
    );

    return axiosInstance.delete(api, {
        data: body // 여기!
    });
}


export default PostRequestWithAccessToken;