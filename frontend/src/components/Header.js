export default function Header()
{
    return(
        <header className="bg-white bg-gray-200 shadow-sm p-4 border-b-2 border-gray-300 rounded">
            <div className="flex justify-between items-center w-full">
                {/* 로고 - 왼쪽에 딱 붙음 */}
                <div className="text-xl font-bold">DummyBoard</div>

                {/* 네비게이션 
                <nav className="space-x-6 hidden md:block">
                <a href="#" className="text-gray-600 hover:text-black transition">Home</a>
                <a href="#" className="text-gray-600 hover:text-black transition">About</a>
                <a href="#" className="text-gray-600 hover:text-black transition">Contact</a>
                </nav>*/}

                {/* 로그인 버튼 */}
                <button className="bg-indigo-500 text-white px-4 py-2 rounded hover:bg-gray-800 transition">
                Login
                </button>
            </div>
        </header>
    );
}