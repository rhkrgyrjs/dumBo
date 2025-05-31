import { signup } from "../api/auth";

export default function SignupTemp()
{
    async function handleSignup()
    {
        signup(document.getElementById("signup-username").value, document.getElementById("signup-password").value, document.getElementById("signup-password").value, document.getElementById("signup-nickname").value, document.getElementById("signup-email").value);
    }

    return(
        <div>
            username : <input id="signup-username" type="text"></input>
            <br />
            password : <input id="signup-password" type="text"></input>
            <br />
            nickname : <input id="signup-nickname" type="text"></input>
            <br />
            email : <input id="signup-email" type="email"></input>
            <button onClick={handleSignup}>signup</button>
            <br />
        </div>
    );
}