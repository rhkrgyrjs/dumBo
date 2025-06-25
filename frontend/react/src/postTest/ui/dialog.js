import React from 'react';

export const Dialog = ({ open, onClose, children }) => {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40">
      <div className="bg-white p-6 rounded-lg relative">
        <button onClick={onClose} className="absolute top-2 right-2 text-gray-500">✕</button>
        {children}
      </div>
    </div>
  );
};

export const DialogContent = ({ children }) => <div>{children}</div>;
