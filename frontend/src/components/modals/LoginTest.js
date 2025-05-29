import { login } from "../../api/auth";

export default function LoginTest()
{
    async function handleLogin()
    {
        await login(document.getElementById("id").value, document.getElementById("password").value);
    }

    return (
        <div className="login-test">
            <input type="text" id="id"></input>
            <input type="password" id="password"></input>
            <button onClick={handleLogin}>로그인 테스트</button>
        </div>
    );
}