import { useState, useEffect } from "react";
import { useDispatch } from "react-redux";
import { registerModal, popModal } from "../../redux/modalStackSlice";
import { setUserInfo } from "../../redux/authSlice";
import { emailCheck, login, nicknameCheck, signup } from "../../api/auth";
import FocusLock from "react-focus-lock";

export default function SignupModal()
{
    const [modalShow, setModalShow] = useState(false);
    const [modalInfo, setModalInfo] = useState({fade : false, z : 0});
    // show : 모달을 띄울 것인가, fade : 모달을 블러처리 할 것인가, z : 모달의 z인덱스

    const dispatch = useDispatch();

    const [message, setMessage] = useState({ "message" : null, "isError" : null });
    
    const [nicknameError, setNicknameError] = useState(false);
    const [emailError, setEmailError] = useState(false);
    const [disableInput, setDisableInput] = useState(false);
    
    async function handleSignup()
    {
      // 0. 모든 칸이 초록불인지 체크
      if (!(nicknameValid && emailValid && passwordValid && passwordConfirmValid))
      {
        setMessage({ "message" : "모든 항목에 올바른 값을 입력해주세요.", "isError" : true });
        return;
      }
      // 1. 닉네임 중복체크
      // 2. 이메일 중복체크
      let nicknameRes = await nicknameCheck(nickname);
      let emailRes = await emailCheck(email);
      // 3. 닉네임/이메일 중복체크 결과에 문제가 있으면 화면 업데이트
      if (nicknameRes === false)
      {
        setNickname("");
        setNicknameError(true);
      }
      if (emailRes === false)
      {
        setEmail("");
        setEmailError(true);
      }
      if (nicknameRes === false || emailRes === false) return;
      // 4. 닉네임/이메일 중복체크 결과에 문제가 없다면 회원가입 요청
      let signupRes = await signup(email, password, nickname);
      // 5. 회원가입이 성공적으로 처리되었다면 회원가입 성공! 화면 업데이트
      if (signupRes === true)
      {
        setMessage({ "message" : "회원가입이 완료되었습니다!", "isError" : false });
        // 입력창과 버튼 비활성화하기
        setDisableInput(true);
        return;
      }
      // 6. 회원가입에 오류가 생겼다면 화면 업데이트
      else
      {
        setMessage({ "message" : "알 수 없는 오류로 회원가입에 실패했습니다.", "isError" : true });
        return;
      }
      
    }

    const [showPassword, setShowPassword] = useState(false);
    function togglePasswordVisibility() { setShowPassword((prev) => !prev); };


    // 말풍선 처리 관련
    
    // 입력값 상태
    const [nickname, setNickname] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [passwordConfirm, setPasswordConfirm] = useState("");

    // 포커스 상태
    const [nicknameFocus, setNicknameFocus] = useState(false);
    const [emailFocus, setEmailFocus] = useState(false);
    const [passwordFocus, setPasswordFocus] = useState(false);
    const [passwordConfirmFocus, setPasswordConfirmFocus] = useState(false);

    function modalClear()
    {
        setNickname("");
        setEmail("");
        setPassword("");
        setPasswordConfirm("");
        setNicknameFocus(false);
        setEmailFocus(false);
        setPasswordFocus(false);
        setPasswordConfirmFocus(false);
        setShowPassword(false);
        setMessage({ "message" : null, "isError" : null });
        setNicknameError(false);
        setEmailError(false);
        setDisableInput(false);
    }

    useEffect(() => 
    {
        dispatch(registerModal({ 'modalName' : 'signup', 'modalClear' : modalClear, 'setModalShow' : setModalShow, 'setModalInfo' : setModalInfo }));
    }, [dispatch]);

    // 검사 정규식
    const nicknameConditions = 
    {
        length: nickname.length >= 2 && nickname.length <= 8,
        onlyAllowedChars: /^(?!.*[!@#$%^&*()+={}\[\]|\\:;"'<>,?/]).+$/.test(nickname),
    };
    const emailConditions = 
    {
        validFormat: (/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)) && (email.length > 0 && email.length <= 254),
    };
    const passwordConditions = 
    {
        length: password.length >= 8 && password.length <= 20,
        hasUpper: /[A-Z]/.test(password),
        hasLower: /[a-z]/.test(password),
        hasNumber: /\d/.test(password),
        hasSpecial: /[!@#$%^&*()_\-]/.test(password),
        onlyAllowedChars: /^[A-Za-z\d!@#$%^&*()_\-]+$/.test(password),
    };
    const passwordConfirmConditions = 
    {
        comfirm: passwordConfirm === password && password !== "",
    };

    // 상태가 전부 맞는지 체크
    const nicknameValid = Object.values(nicknameConditions).every(Boolean);
    const emailValid = Object.values(emailConditions).every(Boolean);
    const passwordValid = Object.values(passwordConditions).every(Boolean);
    const passwordConfirmValid = Object.values(passwordConfirmConditions).every(Boolean);

    // 입력창 테두리 색상 CSS
    function inputBorderClass(length, allValid)
    {
        return(length === 0
            ? "border-gray-300 focus:ring-indigo-300"
            : allValid
            ? "border-green-500 focus:ring-green-300"
            : "border-red-500 focus:ring-red-300"
        )
    }


    return(
        <div id="signup-modal">
      {modalShow && (
        <div style={{ zIndex: modalInfo.z }}
          className="fixed inset-0 flex justify-center items-center"
          onClick={() => { dispatch(popModal()) }}
        >
          <div
            className={
              "bg-white relative rounded-lg p-6 w-96 max-h-[80vh] overflow-visible" +
              (modalInfo.fade
                ? " fixed inset-0 bg-black bg-opacity-30 backdrop-blur-sm"
                : "")
            }
            onClick={(e) => e.stopPropagation()}
          >
            <button
              className="absolute top-2 right-2 px-2 py-0.8 bg-gray-100 rounded hover:bg-gray-200"
              onClick={() => {
                dispatch(popModal());
              }}
            >
              ×
            </button>

            <h2 className="text-xl font-semibold mb-4">회원가입</h2>
            <FocusLock>
              <form
                onSubmit={async (e) => {
                  e.preventDefault();
                  await handleSignup();
                }}
                className="flex flex-col space-y-4 items-center"
              >
                <div className="flex items-center gap-x-2 w-full">
                  <span className="mr-2">👤</span>
                <div className="relative flex-1 min-w-0">
                <input
                  type="text"
                  id="signup-nickname"
                  value={nickname}
                  placeholder={`${(nicknameError && nickname === "") ? "이미 사용중인 닉네임입니다." : "닉네임"}`}
                  autoComplete="off"
                  className={`${(nicknameError && nickname === "") ? "bg-red-300" : ""} disabled:bg-gray-100 disabled:text-gray-400 disabled:cursor-not-allowed w-full flex-1 min-w-0 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-300 ${inputBorderClass(nickname.length, nicknameValid)}`}
                  onChange={(e) => setNickname(e.target.value)}
                  onFocus={() => setNicknameFocus(true)}
                  onBlur={() => setNicknameFocus(false)}
                  required
                  disabled={disableInput}
                />
                {nicknameFocus && !nicknameValid &&
                (
              <div style={{ zIndex: modalInfo.z + 10 }}
                    className="absolute top-full left-0 mt-2 w-full bg-white border border-gray-300 shadow-md rounded p-3 text-sm">
                <p className="mb-1 font-semibold text-gray-800">닉네임 조건:</p>
                <ul className="space-y-1 text-gray-700">
                  <li className={nicknameConditions.length ? "text-green-600" : "text-gray-500"}>
                    {nicknameConditions.length ? "✅" : "⬜"} 2~8자 사이
                  </li>
                  <li
                    className={
                      nickname.length === 0
                        ? "text-gray-500"
                        : nicknameConditions.onlyAllowedChars
                        ? "text-green-600"
                        : "text-red-600"
                    }
                  >
                    {nickname.length === 0
                      ? "⬜"
                      : nicknameConditions.onlyAllowedChars
                      ? "✅"
                      : "❌"}{" "}
                    허용된 문자만 사용
                  </li>
                </ul>
              </div>
            )
                }
                </div>
                </div>
                
                <div className="flex items-center gap-x-2 w-full">
                  <span className="mr-2">📧</span>
                
                <div className="relative flex-1 min-w-0">
                  <input
                    type="email"
                    id="signup-email"
                    value={email}
                    placeholder={`${(emailError && email === "") ? "이미 사용중인 이메일입니다." : "이메일"}`}
                    autoComplete="new-email"
                    className={`${(emailError && email === "") ? "bg-red-300" : ""} disabled:bg-gray-100 disabled:text-gray-400 disabled:cursor-not-allowed w-full flex-1 min-w-0 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-300 ${inputBorderClass(email.length, emailValid)}`}
                    onChange={(e) => setEmail(e.target.value)}
                    onFocus={() => setEmailFocus(true)}
                    onBlur={() => setEmailFocus(false)}
                    required
                    disabled={disableInput}
                  />
                  {emailFocus && !emailValid &&
                (
              <div style={{ zIndex: modalInfo.z + 10 }}
                    className="absolute top-full left-0 mt-2 w-full bg-white border border-gray-300 shadow-md rounded p-3 text-sm">
                <p className="mb-1 font-semibold text-gray-800">이메일 조건:</p>
                <ul className="space-y-1 text-gray-700">
                  <li className={emailConditions.validFormat ? "text-green-600" : "text-gray-500"}>
                    {emailConditions.validFormat ? "✅" : "⬜"} 이메일 형식
                  </li>
                </ul>
              </div>
            )
                }
                </div>
                </div>

                <div className="flex items-center gap-x-2 w-full relative">
                    <span className="mr-2">🔒</span>
                <div className="relative flex-1 min-w-0">
                    <input
                        type={showPassword ? "text" : "password"}
                        id="signup-password"
                        value={password}
                        placeholder="비밀번호"
                        autoComplete="new-password"
                        className={`disabled:bg-gray-100 disabled:text-gray-400 disabled:cursor-not-allowed w-full px-4 py-2 pr-10 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-300 ${inputBorderClass(password.length, passwordValid)}`}
                        required
                        disabled={disableInput}
                        onChange={(e) => setPassword(e.target.value)}
                        onFocus={() => { setPasswordFocus(true); document.getElementById("signup-password-confirm").value=""; setPasswordConfirm(""); }}
                        onBlur={() => setPasswordFocus(false)}
                    />
                    <button
                        type="button"
                        onClick={togglePasswordVisibility}
                        className="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
                        tabIndex={-1}  // 포커스 방지
                    >
                        {showPassword ? "🕶️" : "👀"}
                    </button>
                {passwordFocus && !passwordValid &&
                (
              <div style={{ zIndex: modalInfo.z + 10 }}
                    className="absolute top-full left-0 mt-2 w-full bg-white border border-gray-300 shadow-md rounded p-3 text-sm">
                <p className="mb-1 font-semibold text-gray-800">비밀번호 조건:</p>
                <ul className="space-y-1 text-gray-700">
                  <li className={passwordConditions.length ? "text-green-600" : "text-gray-500"}>
                    {passwordConditions.length ? "✅" : "⬜"} 8~20자 사이
                  </li>
                  <li className={passwordConditions.hasUpper ? "text-green-600" : "text-gray-500"}>
                    {passwordConditions.hasUpper ? "✅" : "⬜"} 대문자 포함
                  </li>
                  <li className={passwordConditions.hasLower ? "text-green-600" : "text-gray-500"}>
                    {passwordConditions.hasLower ? "✅" : "⬜"} 소문자 포함
                  </li>
                  <li className={passwordConditions.hasNumber ? "text-green-600" : "text-gray-500"}>
                    {passwordConditions.hasNumber ? "✅" : "⬜"} 숫자 포함
                  </li>
                  <li className={passwordConditions.hasSpecial ? "text-green-600" : "text-gray-500"}>
                    {passwordConditions.hasSpecial ? "✅" : "⬜"} 특수문자(!@#$%^&*()_-) 포함
                  </li>
                  <li
                    className={
                      password.length === 0
                        ? "text-gray-500"
                        : passwordConditions.onlyAllowedChars
                        ? "text-green-600"
                        : "text-red-600"
                    }
                  >
                    {password.length === 0
                      ? "⬜"
                      : passwordConditions.onlyAllowedChars
                      ? "✅"
                      : "❌"}{" "}
                    허용된 문자만 사용
                  </li>
                </ul>
              </div>
            )
                }
                </div>
                </div>

                <div className="flex items-center gap-x-2 w-full">
                  <span className="mr-2">🔏</span>

                <div className="relative flex-1 min-w-0">
                <input
                  type={showPassword ? "text" : "password"}
                  id="signup-password-confirm"
                  value={passwordConfirm}
                  placeholder="비밀번호 확인"
                  autoComplete="new-password"
                  className={`disabled:bg-gray-100 disabled:text-gray-400 disabled:cursor-not-allowed w-full flex-1 min-w-0 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-300 ${inputBorderClass(passwordConfirm.length, passwordConfirmValid) && inputBorderClass(password.length, passwordValid)}`}
                  required
                  disabled={disableInput}
                  onChange={(e) => setPasswordConfirm(e.target.value)}
                  onFocus={() => setPasswordConfirmFocus(true)}
                  onBlur={() => setPasswordConfirmFocus(false)}
                />
                {passwordConfirmFocus && !passwordConfirmValid &&
                (
              <div style={{ zIndex: modalInfo.z + 10 }}
                    className="absolute top-full left-0 mt-2 w-full bg-white border border-gray-300 shadow-md rounded p-3 text-sm">
                <p className="mb-1 font-semibold text-gray-800">비밀번호 확인 :</p>
                <ul className="space-y-1 text-gray-700">
                  <li className={passwordConfirmConditions.comfirm ? "text-green-600" : "text-gray-500"}>
                    {passwordConfirmConditions.comfirm ? "✅" : "⬜"} 비밀번호 일치
                  </li>
                </ul>
              </div>
            )
                }
                </div>
                </div>

                { message.message !== null && message.isError !== null &&
                (<p className={`${message.isError === true ? "text-red-500" : "text-green-500"}`}>{ message.message }</p>)
                }
                <button
                  type="submit"
                  className="disabled:bg-gray-100 disabled:text-gray-400 disabled:cursor-not-allowed w-full bg-indigo-500 text-white px-4 py-2 rounded hover:bg-indigo-700"
                  disabled={disableInput}
                >
                  회원가입
                </button>
              </form>
            </FocusLock>
          </div>
        </div>
      )}
    </div>
    );
}