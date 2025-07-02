import { useDispatch } from "react-redux";
import { useState } from "react";
import Modal from "./Modal";
import FocusLock from "react-focus-lock";
import InputForm from "../ui/InputForm";
import ButtonForm from "../ui/ButtonForm";
import { emailCheck, nicknameCheck, signup } from "../../api/auth";

export default function NewSignupModal()
{
    // 디스패치
    const dispatch = useDispatch();

    // 상태
    // 입력값
    const [nickname, setNickname] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [passwordConfirm, setPasswordConfirm] = useState("");
    // 포커스
    const [nicknameFocus, setNicknameFocus] = useState(false);
    const [emailFocus, setEmailFocus] = useState(false);
    const [passwordFocus, setPasswordFocus] = useState(false);
    const [passwordConfirmFocus, setPasswordConfirmFocus] = useState(false);
    // 닉네임/이메일 중복
    const [nicknameDup, setNicknameDup] = useState(false);
    const [emailDup, setEmailDup] = useState(false);
    // 회원가입 완료 시 입력 막기
    const [disableInput, setDisableInput] = useState(false);
    // 알림 메시지
    const [message, setMessage] = useState({ "message" : null, "isError" : null });

    // 정규식
    // 형식 검사
    const nicknameCond = { length: nickname.length >= 2 && nickname.length <= 8, onlyAllowedChars: /^(?!.*[!@#$%^&*()+={}\[\]|\\:;"'<>,?/]).+$/.test(nickname), };
    const emailCond = { validFormat: (/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)) && (email.length > 0 && email.length <= 254), };
    const passwordCond = { length: password.length >= 8 && password.length <= 20, hasUpper: /[A-Z]/.test(password), hasLower: /[a-z]/.test(password), hasNumber: /\d/.test(password), hasSpecial: /[!@#$%^&*()_\-]/.test(password), onlyAllowedChars: /^[A-Za-z\d!@#$%^&*()_\-]+$/.test(password), };
    const passwordConfirmCond = { confirm: passwordConfirm === password && password !== "", };
    // 체크
    const nicknameValid = Object.values(nicknameCond).every(Boolean);
    const emailValid = Object.values(emailCond).every(Boolean);
    const passwordValid = Object.values(passwordCond).every(Boolean);
    const passwordConfirmValid = Object.values(passwordConfirmCond).every(Boolean);

    // 함수
    function clear()
    {
        setNickname(""); setNicknameFocus(false); setNicknameDup(false);
        setEmail(""); setEmailFocus(false); setEmailDup(false);
        setPassword(""); setPasswordFocus(false);
        setPasswordConfirm(""); setPasswordConfirmFocus(false);
        setDisableInput(false); setMessage({ "message" : null, "isError" : null });
    }

    function inputBorderStyle(length, valid) { return(length === 0 ? "border-gray-300 focus:ring-indigo-300" : valid ? "border-green-500 focus:ring-green-300" : "border-red-500 focus:ring-red-300"); }

    async function handleSignup()
    {
        if(!(nicknameValid && emailValid && passwordValid && passwordConfirmValid)) { setMessage({ message: "모든 항목에 올바른 값을 입력해주세요.", isError: true }); return; }
        let nicknameDuplicationCheck = await nicknameCheck(nickname);
        let emailDuplicationCheck = await emailCheck(email);
        if (nicknameDuplicationCheck === false) { setNickname(""); setNicknameDup(true); }
        if (emailDuplicationCheck === false) { setEmail(""); setEmailDup(true); }
        if (nicknameDuplicationCheck === false || emailDuplicationCheck === false) return;
        let signupCheck = await signup(email, password, nickname);
        if (signupCheck === true) { setMessage({ message: "회원가입에 성공했습니다!", isError: false }); setDisableInput(true); return; }
        else { setMessage({ message : "알 수 없는 오류로 회원가입에 실패했습니다.", isError : true }); return; }
    }

    return(
    <Modal modalName={"signup"} title={"회원가입"} clear={clear}>
        <FocusLock>
            <form onSubmit={async (e) => {e.preventDefault(); await handleSignup(); }} className="flex flex-col space-y-4 items-center">
                <InputForm 
                icon={"👤"}
                id={"signup-nickname"}
                type={"text"}
                placeholder={`${(nicknameDup && nickname === "") ? "사용중인 닉네임입니다." : "닉네임"}`}
                value={nickname}
                onChangeSetState={setNickname}
                onFocus={() => setNicknameFocus(true)}
                onBlur={() => setNicknameFocus(false)}
                required={true}
                disabled={disableInput}
                autoComplete={"off"}
                className={`${(nicknameDup && nickname === "") ? "bg-red-300" : ""} disabled:bg-gray-100 disabled:text-gray-400 disabled:cursor-not-allowed w-full min-w-0 px-4 py-2 border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-300 ${inputBorderStyle(nickname.length, nicknameValid)}`}
                hintBox={
                    { 
                        show: nicknameFocus && !nicknameValid,
                        title: "닉네임 조건 : ",
                        conditions: [
                            { label: "2~8자 사이", state: nicknameCond.length },
                            { label: "허용된 문자만 사용", state: nicknameCond.onlyAllowedChars },
                        ],
                    }
                }
                />

                <InputForm
                icon={"📧"}
                id={"signup-email"}
                type={"email"}
                placeholder={`${(emailDup && email === "") ? "사용중인 이메일입니다." : "이메일"}`}
                value={email}
                onChangeSetState={setEmail}
                onFocus={() => setEmailFocus(true)}
                onBlur={() => setEmailFocus(false)}
                required={true}
                disabled={disableInput}
                autoComplete={"off"}
                className={`${(emailDup && email === "") ? "bg-red-300" : ""} disabled:bg-gray-100 disabled:text-gray-400 disabled:cursor-not-allowed w-full min-w-0 px-4 py-2 border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-300 ${inputBorderStyle(email.length, emailValid)}`}
                hintBox={
                    {
                        show: emailFocus && !emailValid,
                        title: "이메일 조건 : ",
                        conditions: [
                            { label: "이메일 형식", state: emailCond.validFormat },
                        ],
                    }
                }
                />

                <InputForm
                icon={"🔒"}
                id={"signup-password"}
                type={"toggle"}
                placeholder={"비밀번호"}
                value={password}
                onChangeSetState={setPassword}
                onFocus={() => setPasswordFocus(true)}
                onBlur={() => setPasswordFocus(false)}
                required={true}
                disabled={disableInput}
                autoComplete={"off"}
                className={`disabled:bg-gray-100 disabled:text-gray-400 disabled:cursor-not-allowed w-full min-w-0 px-4 py-2 border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-300 ${inputBorderStyle(password.length, passwordValid)}`}
                hintBox={
                    {
                        show: passwordFocus && !passwordValid,
                        title: "비밀번호 조건 : ",
                        conditions: [
                            { label: "8~20자 사이", state: passwordCond.length },
                            { label: "대문자 포함", state: passwordCond.hasUpper },
                            { label: "소문자 포함", state: passwordCond.hasLower },
                            { label: "숫자 포함", state: passwordCond.hasNumber },
                            { label: "특수문자(!@#$%^&*()_-) 포함", state: passwordCond.hasSpecial },
                            { label: "허용된 문자만 사용", state: passwordCond.onlyAllowedChars ? true : (password.length === 0 ? false : undefined) },
                        ],
                    }
                }
                />

                <InputForm
                icon={"🔏"}
                id={"signup-password-confirm"}
                type={"toggle"}
                placeholder={"비밀번호 확인"}
                value={passwordConfirm}
                onChangeSetState={setPasswordConfirm}
                onFocus={() => setPasswordConfirmFocus(true)}
                onBlur={() => setPasswordConfirmFocus(false)}
                required={true}
                disabled={disableInput}
                autoComplete={"off"}
                className={`disabled:bg-gray-100 disabled:text-gray-400 disabled:cursor-not-allowed w-full min-w-0 px-4 py-2 border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-300 ${inputBorderStyle(passwordConfirm.length, passwordConfirmValid)}`}
                hintBox={
                    {
                        show: passwordConfirmFocus && !passwordConfirmValid,
                        title: "비밀번호 확인 : ",
                        conditions: [
                            { label: "비밀번호 일치" , state: passwordConfirmCond.confirm }
                        ],
                    }
                }
                />

                { message.message !== null && message.isError !== null && (<p className={`${message.isError === true ? "text-red-500" : "text-green-500"}`}>{ message.message }</p>) }

                <ButtonForm 
                type={"submit"}
                disabled={disableInput}      
                >회원가입</ButtonForm>
            </form>
        </FocusLock>
    </Modal>
    );
}