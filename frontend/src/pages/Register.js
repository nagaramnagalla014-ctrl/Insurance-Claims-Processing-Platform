import React, { useState } from 'react';
import { useHistory, Link } from 'react-router-dom';
import axios from '../api/axios';

function Register() {
  const [form, setForm] = useState({ firstName: '', lastName: '', email: '', password: '', phone: '', address: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const history = useHistory();

  const handleSubmit = async e => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await axios.post('/auth/register', form);
      history.push('/login');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  const update = field => e => setForm({...form, [field]: e.target.value});

  return (
    <div className="container mt-4">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card shadow">
            <div className="card-body p-4">
              <h4 className="card-title text-center mb-4">
                <i className="fas fa-user-plus text-primary mr-2"></i>Create Account
              </h4>
              {error && <div className="alert alert-danger">{error}</div>}
              <form onSubmit={handleSubmit}>
                <div className="form-row">
                  <div className="form-group col-md-6">
                    <label>First Name</label>
                    <input className="form-control" required value={form.firstName} onChange={update('firstName')} />
                  </div>
                  <div className="form-group col-md-6">
                    <label>Last Name</label>
                    <input className="form-control" required value={form.lastName} onChange={update('lastName')} />
                  </div>
                </div>
                <div className="form-group">
                  <label>Email</label>
                  <input type="email" className="form-control" required value={form.email} onChange={update('email')} />
                </div>
                <div className="form-group">
                  <label>Password</label>
                  <input type="password" className="form-control" required value={form.password} onChange={update('password')} />
                </div>
                <div className="form-group">
                  <label>Phone</label>
                  <input className="form-control" value={form.phone} onChange={update('phone')} />
                </div>
                <div className="form-group">
                  <label>Address</label>
                  <textarea className="form-control" rows="2" value={form.address} onChange={update('address')}></textarea>
                </div>
                <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
                  {loading ? <span className="spinner-border spinner-border-sm mr-2"></span> : null}
                  Register
                </button>
              </form>
              <div className="text-center mt-3">
                <Link to="/login">Already have an account? Sign in</Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Register;
