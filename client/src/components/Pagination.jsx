import React from 'react';

const Pagination = ({ itemsPerPage, totalItems, paginate, currentPage }) => {
  const totalPages = Math.ceil(totalItems / itemsPerPage);

  const handleNext = () => {
    if (currentPage < totalPages) {
      paginate(currentPage + 1);
    }
  };

  const handlePrevious = () => {
    if (currentPage > 1) {
      paginate(currentPage - 1);
    }
  };

  return (
    <div className="flex justify-center mt-2">
      <ul className="inline-flex space-x-1">
        <li>
          <button
            onClick={handlePrevious}
            className={`px-2 py-1 text-sm border rounded ${
              currentPage === 1 ? 'bg-gray-500 text-gray-300 cursor-not-allowed' : 'bg-gray-700 text-blue-500'
            }`}
            disabled={currentPage === 1}
          >
            &lt;
          </button>
        </li>
        <li>
          <span className="px-3 py-1 text-sm border bg-gray-800 text-white rounded">{currentPage}</span>
        </li>
        <li>
          <button
            onClick={handleNext}
            className={`px-2 py-1 text-sm border rounded ${
              currentPage === totalPages ? 'bg-gray-500 text-gray-300 cursor-not-allowed' : 'bg-gray-700 text-blue-500'
            }`}
            disabled={currentPage === totalPages}
          >
            &gt;
          </button>
        </li>
      </ul>
    </div>
  );
};

export default Pagination;
