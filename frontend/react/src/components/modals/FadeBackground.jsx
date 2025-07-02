import { useSelector } from "react-redux";

/**
 * 모달이 열릴 때 백그라운드의 블러 처리를 담당하는 컴포넌트
 */
export default function FadeBackground()
{
    const { fadeBackground } = useSelector((state) => state.modal);

    // 모달이 열릴 때 사용되는 백그라운드 블러의 z-index는 20
    return(<div className={fadeBackground ? "fixed inset-0 bg-black bg-opacity-30 backdrop-blur-sm z-20" : ""}></div>);
}