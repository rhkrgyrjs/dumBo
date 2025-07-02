import { useState } from "react";
import HintBoxForm from "./HintBoxForm";

export default function InputForm({ icon, id, type="text", placeholder, value, onChangeSetState, onFocus, onBlur, required, disabled, autoComplete="off", className="", hintBox=null })
{
    const [showPassword, setShowPassword] = useState(false);
    function togglePasswordVisibility() { setShowPassword((prev) => !prev); };

    return(
    <div className="w-full">
        <div className="relative flex items-center gap-x-2 w-full">
            <span className="mr-2">{icon}</span>
            <div className="relative flex-1 min-w-0">
                <input
                id={id}
                type={type === "toggle" ? (showPassword ? "text" : "password") : type}
                placeholder={placeholder}
                value={value}
                onChange={(e) => onChangeSetState?.(e.target.value)}
                onFocus={(e) => onFocus?.(e)}
                onBlur={(e) => onBlur?.(e)}
                required={required}
                disabled={disabled}
                autoComplete={autoComplete}
                className={`flex-1 min-w-0 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-300 ${className}`} />
                {(type === "toggle") &&
                <button
                type="button"
                onClick={togglePasswordVisibility}
                className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
                tabIndex={-1}>
                    {showPassword ? "🕶️" : "👀"}
                </button>}
                {(hintBox !== null) &&
                <HintBoxForm 
                show={hintBox.show}
                title={hintBox.title}
                conditions={hintBox.conditions}
                />}
            </div>
        </div>
    </div>
    );
}