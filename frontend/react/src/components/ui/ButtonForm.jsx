export default function ButtonForm({ type="button", onClick, disabled, className="", children })
{
    return(
    <button
    type={type}
    onClick={onClick}
    disabled={disabled}
    className={`w-full bg-indigo-500 text-white px-4 py-2 rounded hover:bg-indigo-700 disabled:bg-gray-500 disabled:cursor-not-allowed ${className}`}>
        {children}
    </button>
    );
}