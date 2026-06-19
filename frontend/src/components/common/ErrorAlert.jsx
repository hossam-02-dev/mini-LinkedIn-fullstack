import React from 'react';
import { FaExclamationTriangle } from 'react-icons/fa';

const ErrorAlert = ({ message, onClose }) => {
  if (!message) return null;

  return (
    <div className="bg-red-50 border-l-4 border-red-500 p-4 rounded-md mb-4">
      <div className="flex items-start">
        <div className="flex-shrink-0">
          <FaExclamationTriangle className="text-red-500" />
        </div>
        <div className="ml-3 flex-1">
          <p className="text-sm text-red-700">{message}</p>
        </div>
        {onClose && (
          <button
            onClick={onClose}
            className="ml-auto text-red-500 hover:text-red-700"
          >
            ×
          </button>
        )}
      </div>
    </div>
  );
};

export default ErrorAlert;