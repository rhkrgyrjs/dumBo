export const Card = ({ children, className = '' }) => (
  <div className={`bg-white shadow rounded-xl p-4 ${className}`}>
    {children}
  </div>
);

export const CardContent = ({ children }) => <div>{children}</div>;
