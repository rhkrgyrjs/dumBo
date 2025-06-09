import axios from 'axios';
import authStore from '../../redux/authStore';
import { useSelector } from 'react-redux';

const axiosInstance = axios.create(
    {
        baseURL : process.env.REACT_APP_API_BASE_URL,
        headers : { 'Content-Type' : 'application/json' }
    }
);

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


export default PostRequestWithAccessToken;