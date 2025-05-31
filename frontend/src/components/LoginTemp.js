import { login } from "../api/auth";
import { useDispatch } from "react-redux";
import { setUserInfo } from "../redux/authSlice";
import { blurrBackground } from "../redux/modalStackSlice";

export default function LoginTemp()
{
    const dispatch = useDispatch();

    async function handleLogin()
    {
        let res = await login(document.getElementById("login-username").value, document.getElementById("login-password").value);
        if (res !== null)
        {
            dispatch(setUserInfo({ 'nickname' : res.nickname, 'username' : res.username, 'accessToken' : res.accessToken }));
            console.log(res.username);
        }
    }

    return (
        <div>
            username : <input id="login-username" type="text"></input>
            <br />
            password : <input id="login-password" type="password"></input>
            <button onClick={handleLogin}>login</button>
            <br />
            <br />
        </div>
    );
}