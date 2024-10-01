import React from 'react';

const ResetButton = ({ onReset }) => {
  return (
    <div className="p-4">
      <button
        className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded"
        onClick={onReset}
      >
        Reset
      </button>
    </div>
  );
};

export default ResetButton;
