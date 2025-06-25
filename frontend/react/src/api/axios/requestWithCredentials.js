import axios from 'axios';

const requestWithCredentials = axios.create(
    {
        baseURL : process.env.REACT_APP_API_BASE_URL,
        headers : { 'Content-Type' : 'application/json' },
        withCredentials : true
    }
);

export default requestWithCredentials;