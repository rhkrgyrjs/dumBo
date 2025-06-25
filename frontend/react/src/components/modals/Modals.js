import DarftModal from "./DraftModal";
import FadeBackground from "./FadeBackground";
import LoginModal from "./LoginModal";
import SignupModal from "./SignupModal";

export default function Modals()
{
    return (
        <div>
            <FadeBackground />
            <LoginModal />
            <SignupModal />
            <DarftModal />
        </div>
    );
}