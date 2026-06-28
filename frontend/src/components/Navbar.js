import React from 'react';
import { Link, useHistory } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Navbar() {
  const { user, logout } = useAuth();
  const history = useHistory();

  const handleLogout = () => {
    logout();
    history.push('/login');
  };

  const isAdjuster = user && (user.role === 'ADJUSTER' || user.role === 'MANAGER' || user.role === 'ADMIN');
  const isAdmin = user && (user.role === 'MANAGER' || user.role === 'ADMIN');

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
      <Link className="navbar-brand font-weight-bold" to="/">
        <i className="fas fa-shield-alt mr-2"></i>InsureClaims
      </Link>
      <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navMenu">
        <span className="navbar-toggler-icon"></span>
      </button>
      <div className="collapse navbar-collapse" id="navMenu">
        <ul className="navbar-nav mr-auto">
          {user && (
            <>
              <li className="nav-item">
                <Link className="nav-link" to="/dashboard">Dashboard</Link>
              </li>
              <li className="nav-item">
                <Link className="nav-link" to="/claims">My Claims</Link>
              </li>
              {!isAdjuster && (
                <li className="nav-item">
                  <Link className="nav-link" to="/claims/new">New Claim</Link>
                </li>
              )}
              {isAdjuster && (
                <li className="nav-item">
                  <Link className="nav-link" to="/adjuster/queue">Adjuster Queue</Link>
                </li>
              )}
              {isAdmin && (
                <li className="nav-item">
                  <Link className="nav-link" to="/admin">Admin</Link>
                </li>
              )}
            </>
          )}
        </ul>
        <ul className="navbar-nav">
          {user ? (
            <li className="nav-item dropdown">
              <a className="nav-link dropdown-toggle" href="#" data-toggle="dropdown">
                <i className="fas fa-user mr-1"></i>{user.fullName}
              </a>
              <div className="dropdown-menu dropdown-menu-right">
                <span className="dropdown-item-text text-muted small">{user.role}</span>
                <div className="dropdown-divider"></div>
                <button className="dropdown-item" onClick={handleLogout}>
                  <i className="fas fa-sign-out-alt mr-2"></i>Logout
                </button>
              </div>
            </li>
          ) : (
            <li className="nav-item">
              <Link className="nav-link" to="/login">Login</Link>
            </li>
          )}
        </ul>
      </div>
    </nav>
  );
}

export default Navbar;
