

export default function HintBoxForm({ show, title, conditions })
{
    if (!show) return null;

    function getSymbol(state)
    {
        if (state === true) return "✅";
        if (state === false) return "⬜";
        return "❌";
    }

    function getColor(state)
    {
        if (state === true) return "text-green-600";
        if (state === false) return "text-gray-500";
        return "text-red-600";
    }

    return(
    <div className="absolute top-full left-0 mt-2 w-full bg-white border border-gray-300 shadow-md rounded p-3 text-sm z-30">
        <p className="mb-1 font-semibold text-gray-800">{title}</p>
        <ul className="space-y-1 text-gray-700">
            {conditions.map((cond, idx) => (
                <li key={idx} className={getColor(cond.state)}>
                    {getSymbol(cond.state)} {cond.label}
                </li>
            ))}
        </ul>
    </div>
    );
}