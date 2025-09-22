import React from 'react';

const LoadingSpinner = ({ size = 'medium', text = 'Loading...' }) => {
  const sizeClasses = {
    small: 'w-4 h-4',
    medium: 'w-8 h-8',
    large: 'w-12 h-12',
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-[200px]">
      <div className={`${sizeClasses[size]} border-4 border-gray-200 border-t-primary-500 rounded-full spinner`}></div>
      {text && (
        <p className="mt-4 text-sm text-gray-600 animate-pulse">{text}</p>
      )}
    </div>
  );
};

export default LoadingSpinner;
