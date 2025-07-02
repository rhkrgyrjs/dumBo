import { useState } from "react";
import { useDispatch } from "react-redux";
import { login } from "../../api/auth";
import { showModal, popModal } from "../../redux/modalStackSlice";
import { setUserInfo } from "../../redux/authSlice";
import FocusLock from "react-focus-lock";
import Modal from "./Modal";
import InputForm from "../ui/InputForm";
import ButtonForm from "../ui/ButtonForm";


export default function NewLoginModal()
{
    // 디스패치
    const dispatch = useDispatch();

    // 상태
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [loginErrorMessage, setLoginErrorMessage] = useState(null);

    // 함수
    function clear() { setEmail(""); setPassword(""); setLoginErrorMessage(null); }

    async function handleLogin()
    {
        let loginResult = await login(email, password);
        if (loginResult !== null)
        {
            dispatch(setUserInfo({ 'nickname' : loginResult.nickname, 'userId' : loginResult.userId, 'accessToken' : loginResult.accessToken }));
            dispatch(popModal());
        }
        else setLoginErrorMessage(loginResult);
    }

    return(
    <Modal modalName={"login"} title={"로그인"} clear={clear}>
        <FocusLock>
            <form
            onSubmit={async (e) => { e.preventDefault(); await handleLogin(); }}
            className="w-[300px] space-y-4">
                <InputForm 
                icon={"📧"}
                id={"login-email"}
                type={"text"}
                placeholder={"이메일"}
                value={email}
                onChangeSetState={setEmail}
                required={true} />

                <InputForm
                icon={"🔒"}
                id={"login-password"}
                type={"password"}
                placeholder={"비밀번호"}
                value={password}
                onChangeSetState={setPassword}
                required={true} />
                
                { loginErrorMessage && (<p className="text-red-500">{loginErrorMessage}</p>) }
                
                <ButtonForm type={"submit"}>로그인</ButtonForm>
            </form>
        </FocusLock>
        <div className="mt-4 text-center">
            <a 
            onClick={() => { dispatch(showModal('signup')); }}
            className="text-indigo-600 underline hover:text-indigo-800 cursor-pointer">처음이신가요?</a>
          </div>
    </Modal>
    );
}