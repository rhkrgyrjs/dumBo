import React, { useState } from "react";
import LoginModal from "./LoginModal";
import SignupModal from "./SignupModal";

export default function AuthModalManager() {
  const [currentModal, setCurrentModal] = useState("login");

  return (
    <div className="modal-overlay">
      {currentModal === "login" && (
        <LoginModal
          onSignupClick={() => setCurrentModal("signup")}
          onClose={() => setCurrentModal(null)}
        />
      )}

      {currentModal === "signup" && (
        <SignupModal
          onClose={() => setCurrentModal("login")}
        />
      )}
    </div>
  );
}
