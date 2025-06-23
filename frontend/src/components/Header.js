import { useSelector, useDispatch } from "react-redux";
import { clearUserInfo } from "../redux/authSlice"; 
import { showModal } from "../redux/modalStackSlice";
import { logout } from "../api/auth";

export default function Header()
{
    const { nickname } = useSelector((state) => state.auth);
    const dispatch = useDispatch();

    function handleLogout()
    {
        logout();
        dispatch(clearUserInfo());
    }

    return(
        <header className="sticky top-0 bg-gray-100 shadow-sm p-1 border-b-2 border-gray-300 z-10">
            <div className="flex justify-between items-center w-full">
                {/* 로고 - 왼쪽에 딱 붙음 */}
                <div className="text-xl font-bold">DumBo</div>

                {/* 네비게이션 
                <nav className="space-x-6 hidden md:block">
                <a href="#" className="text-gray-600 hover:text-black transition">Home</a>
                <a href="#" className="text-gray-600 hover:text-black transition">About</a>
                <a href="#" className="text-gray-600 hover:text-black transition">Contact</a>
                </nav>*/}

                {nickname ?
               
                 (<div className="flex items-center space-x-4">
                    <h1 className="font-bold">{nickname}</h1>
                    <button onClick={handleLogout} className="bg-indigo-500 text-white px-4 py-2 rounded hover:bg-indigo-700">
                    로그아웃
                    </button>
                </div>) :  (<button onClick={() => { dispatch(showModal('login')); }} className="bg-indigo-500 text-white px-4 py-2 rounded hover:bg-indigo-700">
                로그인
                </button>)
                }
            </div>
        </header>
    );
}