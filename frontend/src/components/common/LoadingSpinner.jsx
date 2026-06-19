import React from 'react';

const LoadingSpinner = ({ size = 'md', fullScreen = false }) => {
  const sizeClass = {
    sm: 'spinner-sm',
    md: 'spinner-md',
    lg: 'spinner-lg',
  }[size] || 'spinner-md';

  const spinner = (
    <div className="flex justify-center items-center py-8">
      <div className={`spinner ${sizeClass}`} />
    </div>
  );

  if (fullScreen) {
    return (
      <div className="spinner-overlay">
        <div className={`spinner ${sizeClass}`} />
      </div>
    );
  }

  return spinner;
};

export default LoadingSpinner;
