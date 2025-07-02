import DarftModal from "./DraftModal";
import FadeBackground from "./FadeBackground";
import LoginModal from "./LoginModal";
import NewLoginModal from "./NewLoginModal";
import NewSignupModal from "./NewSignupModal";
import SignupModal from "./SignupModal";

export default function Modals()
{
    return (
        <div>
            <FadeBackground />
            <NewLoginModal />
            <NewSignupModal />
        </div>
    );
}