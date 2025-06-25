import { useSelector } from "react-redux";

export default function FadeBackground()
{
    const { fadeBackground } = useSelector((state) => state.modal);

    return(
        <div
          className={fadeBackground ? "fixed inset-0 bg-black bg-opacity-30 backdrop-blur-sm z-20" : ""}></div>
    );
}