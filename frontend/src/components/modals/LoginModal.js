import { useState, useEffect } from "react";
import { useDispatch } from "react-redux";
import { registerModal, popModal } from "../../redux/modalStackSlice";
import { setUserInfo } from "../../redux/authSlice";
import { login } from "../../api/auth";
import FocusLock from "react-focus-lock";

export default function LoginModal() 
{
  const [modalShow, setModalShow] = useState(false);
  const [modalInfo, setModalInfo] = useState({fade : false, z : 0});
  // show : 모달을 띄울 것인가, fade : 모달을 블러처리 할 것인가, z : 모달의 z인덱스

  const dispatch = useDispatch();
  
  async function handleLogin()
  {
    let res = await login(document.getElementById("login-username").value, document.getElementById("login-password").value);
    if (res !== null)
    {
      dispatch(setUserInfo({ 'nickname' : res.nickname, 'username' : res.username, 'accessToken' : res.accessToken }));
      dispatch(popModal());
    }
  }

  function modalClear()
  {
    document.getElementById("login-username").value = "";
    document.getElementById("login-password").value = "";
  }


  useEffect(() => 
  {
    dispatch(registerModal({ 'modalName' : 'login', 'modalClear' : modalClear, 'setModalShow' : setModalShow, 'setModalInfo' : setModalInfo }));
  }, [dispatch]);

  return (
  <div id="login-modal">
    {modalShow && (
      <div
        className={"fixed inset-0 flex justify-center items-center z-" + modalInfo.z}
        onClick={() => {dispatch(popModal()); }}>

        <div
          className={"bg-white relative rounded-lg p-6 w-96 max-h-[80vh] overflow-y-auto" + (modalInfo.fade ? "fixed inset-0 bg-black bg-opacity-30 backdrop-blur-sm" : "")}
          onClick={(e) => e.stopPropagation()}>

          <button
            className="absolute top-2 right-2 px-2 py-0.8 bg-gray-100 rounded hover:bg-gray-200"
            onClick={() => {dispatch(popModal()); }}>
              ×
          </button>

          <h2 className="text-xl font-semibold mb-4">로그인</h2>
          <FocusLock>
            <form
              onSubmit={async (e) => {
                e.preventDefault(); // 새로고침 막기
                await handleLogin();
              }}
              className="flex flex-col space-y-4"
            >
              <input
                type="text"
                id="login-username"
                placeholder="username"
                className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-300"
                required
              />
              <input
                type="password"
                id="login-password"
                placeholder="password"
                className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-300"
                required
              />
              <button
                type="submit"
                className="bg-indigo-500 text-white px-4 py-2 rounded hover:bg-indigo-700"
              >
                로그인
              </button>
            </form>
          </FocusLock>

        </div>
      </div>
    )}
  </div>
);
}
