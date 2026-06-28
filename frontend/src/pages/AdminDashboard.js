import React, { useState, useEffect } from 'react';
import axios from '../api/axios';

function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [tab, setTab] = useState('overview');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      axios.get('/admin/dashboard'),
      axios.get('/admin/users'),
    ]).then(([statsRes, usersRes]) => {
      setStats(statsRes.data.data);
      setUsers(usersRes.data.data || []);
      setLoading(false);
    }).catch(() => setLoading(false));
  }, []);

  const toggleUser = async userId => {
    await axios.put(`/admin/users/${userId}/toggle`);
    const res = await axios.get('/admin/users');
    setUsers(res.data.data || []);
  };

  if (loading) return <div className="text-center mt-5"><div className="spinner-border text-primary"></div></div>;

  return (
    <div className="container mt-4">
      <h5 className="mb-4">Admin Dashboard</h5>
      {stats && (
        <div className="row mb-4">
          {[
            { label: 'Total Claims', val: stats.totalClaims, color: 'primary' },
            { label: 'Total Policies', val: stats.totalPolicies, color: 'info' },
            { label: 'Total Users', val: stats.totalUsers, color: 'secondary' },
            { label: 'Submitted', val: stats.submitted, color: 'warning' },
            { label: 'Settled', val: stats.settled, color: 'success' },
            { label: 'Rejected', val: stats.rejected, color: 'danger' },
          ].map(s => (
            <div key={s.label} className="col-md-2 col-6 mb-2">
              <div className={`card border-${s.color} text-center`}>
                <div className="card-body py-2">
                  <h4 className={`text-${s.color} mb-0`}>{s.val}</h4>
                  <p className="mb-0 small">{s.label}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      <ul className="nav nav-tabs mb-3">
        <li className="nav-item">
          <button className={`nav-link ${tab==='overview'?'active':''}`} onClick={() => setTab('overview')}>Overview</button>
        </li>
        <li className="nav-item">
          <button className={`nav-link ${tab==='users'?'active':''}`} onClick={() => setTab('users')}>Users</button>
        </li>
      </ul>

      {tab === 'overview' && stats && (
        <div className="card">
          <div className="card-header"><strong>Recent Claims</strong></div>
          <div className="card-body p-0">
            <table className="table mb-0">
              <thead className="thead-light">
                <tr><th>Claim #</th><th>Type</th><th>Status</th><th>Amount</th><th>Submitted</th></tr>
              </thead>
              <tbody>
                {(stats.recentClaims || []).map(c => (
                  <tr key={c.claimId}>
                    <td>{c.claimNumber}</td>
                    <td>{c.claimType?.replace('_',' ')}</td>
                    <td><span className="badge badge-info">{c.status}</span></td>
                    <td>₹{c.claimedAmount?.toLocaleString()}</td>
                    <td>{c.submittedAt?.substring(0,10) || '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {tab === 'users' && (
        <div className="card">
          <div className="card-body p-0">
            <table className="table mb-0">
              <thead className="thead-light">
                <tr><th>Name</th><th>Email</th><th>Role</th><th>Status</th><th>Actions</th></tr>
              </thead>
              <tbody>
                {users.map(u => (
                  <tr key={u.userId}>
                    <td>{u.firstName} {u.lastName}</td>
                    <td>{u.email}</td>
                    <td><span className="badge badge-secondary">{u.role}</span></td>
                    <td>
                      <span className={`badge badge-${u.isActive ? 'success' : 'danger'}`}>
                        {u.isActive ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td>
                      <button className={`btn btn-sm btn-outline-${u.isActive?'danger':'success'}`}
                        onClick={() => toggleUser(u.userId)}>
                        {u.isActive ? 'Deactivate' : 'Activate'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}

export default AdminDashboard;
