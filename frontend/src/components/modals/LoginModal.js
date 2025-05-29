import { registerModal, modalPop } from './ModalManager';
import { useState, useEffect } from 'react';
import { login } from '../../api/auth';

const modalName = "login";

export default function LoginModal() 
{
    const [isOpen, setIsOpen] = useState(false);
    function modalClear()
    {
        document.getElementById("id").value="";
        document.getElementById("password").value="";
    }

    useEffect(() => { registerModal({ 'name' : modalName, "setIsOpen" : setIsOpen, "modalClear" : modalClear }) }, []);
    
    async function handleLogin() { await login(document.getElementById("id").value, document.getElementById("password").value); }

  return (
    <>
      {isOpen && (
        <div
          className="shadow-sm fixed inset-0 bg-black bg-opacity-30 flex justify-center items-center z-50"
          onClick={modalPop}>

          <div
            className="bg-white relative rounded-lg p-6 w-96 max-h-[80vh] overflow-y-auto"
            onClick={(e) => e.stopPropagation()}>
            <button
              className="absolute top-2 right-2 px-2 py-0.8 bg-gray-100 rounded hover:bg-gray-200"
              onClick={modalPop}>
                ×
            </button>

            <h2 className="text-xl font-semibold mb-4">로그인</h2>
            <div className="flex flex-col space-y-4">
            <input
                type="text"
                id="id"
                placeholder="아이디 입력"
                className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <input
                type="password"
                id="password"
                placeholder="비밀번호 입력"
                className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <button
                onClick={handleLogin}
                className="bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 transition-colors duration-200"
            >
                로그인
            </button>
            </div>

          </div>
        </div>
      )}
    </>
  );
}
