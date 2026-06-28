import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from '../api/axios';
import { useAuth } from '../context/AuthContext';

function Dashboard() {
  const { user } = useAuth();
  const [claims, setClaims] = useState([]);
  const [policies, setPolicies] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [claimRes, policyRes] = await Promise.all([
          axios.get('/claims/my'),
          axios.get('/policies/my'),
        ]);
        setClaims(claimRes.data.data || []);
        setPolicies(policyRes.data.data || []);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const statusBadge = status => {
    const map = {
      DRAFT: 'secondary', SUBMITTED: 'info', UNDER_REVIEW: 'warning',
      ASSESSMENT: 'warning', PENDING_SETTLEMENT: 'primary',
      SETTLED: 'success', REJECTED: 'danger', CLOSED: 'dark'
    };
    return <span className={`badge badge-${map[status] || 'secondary'}`}>{status.replace('_', ' ')}</span>;
  };

  if (loading) return <div className="text-center mt-5"><div className="spinner-border text-primary"></div></div>;

  return (
    <div className="container mt-4">
      <div className="mb-4">
        <h4>Welcome, {user.fullName}</h4>
        <p className="text-muted">{user.role} · {user.email}</p>
      </div>

      <div className="row mb-4">
        <div className="col-md-3">
          <div className="card border-primary text-center">
            <div className="card-body">
              <h2 className="text-primary">{claims.length}</h2>
              <p className="mb-0">Total Claims</p>
            </div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card border-success text-center">
            <div className="card-body">
              <h2 className="text-success">{claims.filter(c => c.status === 'SETTLED').length}</h2>
              <p className="mb-0">Settled</p>
            </div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card border-warning text-center">
            <div className="card-body">
              <h2 className="text-warning">{claims.filter(c => ['SUBMITTED','UNDER_REVIEW','ASSESSMENT','PENDING_SETTLEMENT'].includes(c.status)).length}</h2>
              <p className="mb-0">In Progress</p>
            </div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card border-info text-center">
            <div className="card-body">
              <h2 className="text-info">{policies.length}</h2>
              <p className="mb-0">Active Policies</p>
            </div>
          </div>
        </div>
      </div>

      <div className="row">
        <div className="col-md-8">
          <div className="card">
            <div className="card-header d-flex justify-content-between align-items-center">
              <strong>Recent Claims</strong>
              <Link to="/claims/new" className="btn btn-sm btn-primary">
                <i className="fas fa-plus mr-1"></i>New Claim
              </Link>
            </div>
            <div className="card-body p-0">
              {claims.length === 0 ? (
                <div className="text-center py-4 text-muted">No claims yet</div>
              ) : (
                <table className="table table-hover mb-0">
                  <thead><tr><th>Claim #</th><th>Type</th><th>Amount</th><th>Status</th><th></th></tr></thead>
                  <tbody>
                    {claims.slice(0, 5).map(c => (
                      <tr key={c.claimId}>
                        <td>{c.claimNumber}</td>
                        <td>{c.claimType}</td>
                        <td>₹{c.claimedAmount?.toLocaleString()}</td>
                        <td>{statusBadge(c.status)}</td>
                        <td><Link to={`/claims/${c.claimId}`} className="btn btn-xs btn-outline-primary">View</Link></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card">
            <div className="card-header"><strong>My Policies</strong></div>
            <div className="card-body p-0">
              {policies.length === 0 ? (
                <div className="text-center py-4 text-muted">No policies found</div>
              ) : (
                <ul className="list-group list-group-flush">
                  {policies.map(p => (
                    <li key={p.policyId} className="list-group-item">
                      <div className="d-flex justify-content-between">
                        <div>
                          <strong>{p.policyNumber}</strong>
                          <div className="text-muted small">{p.policyType}</div>
                        </div>
                        <span className="badge badge-success align-self-center">ACTIVE</span>
                      </div>
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
