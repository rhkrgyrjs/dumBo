import { useState } from "react";
import { useSelector, useDispatch } from "react-redux";
import { clearUserInfo } from "../redux/authSlice";
import { showModal } from "../redux/modalStackSlice";
import { logout } from "../api/auth";
import { Menu } from "lucide-react"; // 햄버거 아이콘

export default function Header() {
    const { nickname } = useSelector((state) => state.auth);
    const dispatch = useDispatch();
    const [drawerOpen, setDrawerOpen] = useState(false);

    function handleLogout() {
        logout();
        dispatch(clearUserInfo());
    }

    return (
        <>
            <header className="sticky top-0 bg-gray-100 shadow-sm p-2 border-b-2 border-gray-300 z-10">
                <div className="flex justify-between items-center w-full">
                    {/* 로고 + 햄버거 버튼 */}
                    <div className="flex items-center space-x-2">
                        <button onClick={() => setDrawerOpen(true)}>
                            <Menu className="w-6 h-6" />
                        </button>
                        <div className="text-xl font-bold">DumBo</div>
                    </div>

                    {/* 로그인 / 로그아웃 영역 */}
                    {nickname ? (
                        <div className="flex items-center space-x-4">
                            <h1 className="font-bold">{nickname}</h1>
                            <button
                                onClick={handleLogout}
                                className="bg-indigo-500 text-white px-4 py-2 rounded hover:bg-indigo-700"
                            >
                                로그아웃
                            </button>
                        </div>
                    ) : (
                        <button
                            onClick={() => dispatch(showModal("login"))}
                            className="bg-indigo-500 text-white px-4 py-2 rounded hover:bg-indigo-700"
                        >
                            로그인
                        </button>
                    )}
                </div>
            </header>

            {/* 드로어 오버레이 */}
            <div
                className={`fixed inset-0 bg-black bg-opacity-30 z-40 transition-opacity duration-300 ${
                    drawerOpen ? "opacity-100" : "opacity-0 pointer-events-none"
                }`}
                onClick={() => setDrawerOpen(false)}
            />

            {/* 드로어 본체 */}
            <div
                className={`fixed top-0 left-0 h-full w-64 bg-white shadow-lg z-50 p-4 transform transition-transform duration-300 ease-in-out ${
                    drawerOpen ? "translate-x-0" : "-translate-x-full"
                }`}
            >
                <h2 className="text-lg font-bold mb-4">메뉴</h2>
                <ul className="space-y-2">
                    <li>
                        <a href="#" className="block hover:text-indigo-500">
                            홈
                        </a>
                    </li>
                    <li>
                        <a href="#" className="block hover:text-indigo-500">
                            내 활동
                        </a>
                    </li>
                    <li>
                        <a href="#" className="block hover:text-indigo-500">
                            설정
                        </a>
                    </li>
                </ul>
                <button
                    className="mt-6 text-sm text-gray-500 hover:text-black"
                    onClick={() => setDrawerOpen(false)}
                >
                    닫기
                </button>
            </div>
        </>
    );
}
