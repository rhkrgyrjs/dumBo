import { useState, useEffect } from "react";
import { useDispatch } from "react-redux";
import { popModal, registerModal } from "../../redux/modalStackSlice";

export default function Modal({ modalName, title, clear, children })
{
    const [show, setShow] = useState(false);
    const dispatch = useDispatch();

    useEffect(() =>
    {
        dispatch(registerModal({ 'name': modalName, 'clear': clear, 'setShow': setShow }));
    }, [dispatch]);

    return(
    <div id={`${modalName}-modal`}>
    {show && (
    <div className="fixed inset-0 flex justify-center items-center z-20" onClick={() => {dispatch(popModal()); }}>
        <div className="bg-white relative rounded-lg p-6 shadow-lg" onClick={(e) => e.stopPropagation()}>
            <button className="absolute top-2 right-2 px-2 py-0.8 bg-gray-100 rounded hover:bg-gray-200" onClick={() => dispatch(popModal())}>×</button>
            {title && <h2 className="text-xl font-semibold mb-4">{title}</h2>}
            {children}
        </div>
    </div>
    )} 
    </div>
    );
}