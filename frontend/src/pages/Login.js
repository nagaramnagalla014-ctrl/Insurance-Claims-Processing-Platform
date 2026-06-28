import React, { useState } from 'react';
import { useHistory, Link } from 'react-router-dom';
import axios from '../api/axios';
import { useAuth } from '../context/AuthContext';

function Login() {
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const history = useHistory();

  const handleSubmit = async e => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const res = await axios.post('/auth/login', form);
      const { token, ...userData } = res.data.data;
      login(userData, token);
      history.push('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-5">
          <div className="card shadow">
            <div className="card-body p-4">
              <div className="text-center mb-4">
                <i className="fas fa-shield-alt fa-3x text-primary"></i>
                <h3 className="mt-2">InsureClaims Portal</h3>
                <p className="text-muted">Sign in to your account</p>
              </div>
              {error && <div className="alert alert-danger">{error}</div>}
              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label>Email</label>
                  <input type="email" className="form-control" required
                    value={form.email} onChange={e => setForm({...form, email: e.target.value})} />
                </div>
                <div className="form-group">
                  <label>Password</label>
                  <input type="password" className="form-control" required
                    value={form.password} onChange={e => setForm({...form, password: e.target.value})} />
                </div>
                <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
                  {loading ? <span className="spinner-border spinner-border-sm mr-2"></span> : null}
                  Sign In
                </button>
              </form>
              <hr />
              <div className="text-center">
                <p className="mb-0">Don't have an account? <Link to="/register">Register</Link></p>
              </div>
              <div className="mt-3 p-3 bg-light rounded small">
                <strong>Demo:</strong><br />
                Policyholder: john.doe@insurance.com / password123<br />
                Adjuster: adjuster@insurance.com / admin123<br />
                Manager: manager@insurance.com / admin123
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;
