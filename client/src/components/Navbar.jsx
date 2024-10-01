import React from 'react';
import { Link } from 'react-router-dom';

const Navbar = () => {
  return (
    <nav className="bg-blue-500 p-4 fixed w-full top-0 left-0 shadow-lg">
      <div className="container mx-auto flex justify-between items-center">
        <div className="text-white font-bold text-lg">
            <Link
              to="/"
              className="text-white font-bold hover:text-yellow-400 transition-colors duration-300"
            >
              SignaPay
            </Link>
        </div>

        <ul className="flex space-x-8">
          <li>
            <Link
              to="/accounts"
              className="text-white font-bold hover:text-yellow-400 transition-colors duration-300"
            >
              Account Charts
            </Link>
          </li>
          <li>
            <Link
              to="/collections"
              className="text-white font-bold hover:text-yellow-400 transition-colors duration-300"
            >
              Collections
            </Link>
          </li>
          <li>
            <Link
              to="/transactions"
              className="text-white font-bold hover:text-yellow-400 transition-colors duration-300"
            >
              Transactions
            </Link>
          </li>
        </ul>
      </div>
    </nav>
  );
};

export default Navbar;
