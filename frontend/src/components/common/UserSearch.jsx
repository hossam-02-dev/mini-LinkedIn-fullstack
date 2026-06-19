import React, { useState, useEffect, useRef } from 'react';
import { userAPI } from '../../api/userAPI';
import { FaSearch, FaUser } from 'react-icons/fa';
import { Link } from 'react-router-dom';

const UserSearch = () => {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [showResults, setShowResults] = useState(false);
  const searchRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (searchRef.current && !searchRef.current.contains(event.target)) {
        setShowResults(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    const delayDebounce = setTimeout(() => {
      if (query.trim().length >= 2) {
        userAPI.searchUsers(query)
          .then(res => setResults(res.data))
          .catch(err => console.error(err));
        setShowResults(true);
      } else {
        setResults([]);
        setShowResults(false);
      }
    }, 300);
    return () => clearTimeout(delayDebounce);
  }, [query]);

  return (
    <div className="relative" ref={searchRef}>
      <div className="relative">
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Rechercher..."
          className="search-field"
        />
        <FaSearch className="absolute left-2.5 top-1/2 -translate-y-1/2 text-sm" style={{ color: 'var(--color-text-muted)' }} />
      </div>
      {showResults && results.length > 0 && (
        <div className="dropdown-menu left-0 right-auto w-64 max-h-60 overflow-y-auto">
          {results.map(user => (
            <Link
              key={user.id}
              to={`/profile/${user.id}`}
              className="dropdown-item"
              onClick={() => setShowResults(false)}
            >
              <FaUser style={{ color: 'var(--color-text-muted)' }} />
              <span>{user.firstName} {user.lastName}</span>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
};

export default UserSearch;
