import { useState, useEffect } from "react";
import { useDispatch } from "react-redux";
import { registerModal, popModal } from "../../redux/modalStackSlice";
import PostTemp from "../PostTemp";

// 글쓰기 창 띄우는 모달
export default function DarftModal()
{
    const [modalShow, setModalShow] = useState(false);
    const [modalInfo, setModalInfo] = useState({fade : false, z : 0});
    // show : 모달을 띄울 것인가, fade : 모달을 블러처리 할 것인가, z : 모달의 z인덱스

    const dispatch = useDispatch();
    
    function modalClear()
    {

    }

    useEffect(() => 
    {
        dispatch(registerModal({ 'modalName' : 'draft', 'modalClear' : modalClear, 'setModalShow' : setModalShow, 'setModalInfo' : setModalInfo }));
    }, [dispatch]);

    return (
        <div id="draft-modal">
            {modalShow &&
                <div  style={{ zIndex: modalInfo.z }} className="fixed inset-0 flex justify-center items-center" onClick={() => {dispatch(popModal()); }}>
                    <div className={"bg-white relative rounded-lg p-6 w-2/5 h-6/7 max-h-[100vh] overflow-y-auto" + (modalInfo.fade ? "fixed inset-0 bg-black bg-opacity-30 backdrop-blur-sm" : "")} onClick={(e) => e.stopPropagation()}>
                        <button className="absolute top-2 right-2 px-2 py-0.8 bg-gray-100 rounded hover:bg-gray-200" onClick={() => {dispatch(popModal()); }}>×</button>
                        <h2 className="text-xl font-semibold mb-4">글쓰기</h2> {/* 게시판을 주제별로 분류했을 경우 게시판의 루트명을 덧붙여 써주기 */}
                        <PostTemp />
                    </div>
                </div>
            }
        </div>
    );
}