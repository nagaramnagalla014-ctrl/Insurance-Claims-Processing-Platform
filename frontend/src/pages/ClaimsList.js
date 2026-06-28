import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from '../api/axios';

function ClaimsList() {
  const [claims, setClaims] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => {
    axios.get('/claims/my').then(res => {
      setClaims(res.data.data || []);
      setLoading(false);
    }).catch(() => setLoading(false));
  }, []);

  const statusBadge = status => {
    const map = {
      DRAFT: 'secondary', SUBMITTED: 'info', UNDER_REVIEW: 'warning',
      ASSESSMENT: 'warning', PENDING_SETTLEMENT: 'primary',
      SETTLED: 'success', REJECTED: 'danger', CLOSED: 'dark'
    };
    return <span className={`badge badge-${map[status] || 'secondary'}`}>{status.replace(/_/g,' ')}</span>;
  };

  const filtered = claims.filter(c =>
    c.claimNumber.toLowerCase().includes(search.toLowerCase()) ||
    c.claimType.toLowerCase().includes(search.toLowerCase()) ||
    c.status.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) return <div className="text-center mt-5"><div className="spinner-border text-primary"></div></div>;

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h5>My Claims</h5>
        <Link to="/claims/new" className="btn btn-primary">
          <i className="fas fa-plus mr-1"></i>New Claim
        </Link>
      </div>
      <div className="form-group">
        <input className="form-control" placeholder="Search by claim number, type, or status..."
          value={search} onChange={e => setSearch(e.target.value)} />
      </div>
      {filtered.length === 0 ? (
        <div className="alert alert-info">No claims found.</div>
      ) : (
        <div className="table-responsive">
          <table className="table table-bordered table-hover">
            <thead className="thead-light">
              <tr>
                <th>Claim #</th><th>Policy</th><th>Type</th><th>Incident Date</th>
                <th>Claimed Amount</th><th>Status</th><th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map(c => (
                <tr key={c.claimId}>
                  <td><strong>{c.claimNumber}</strong></td>
                  <td>{c.policy?.policyNumber}</td>
                  <td>{c.claimType?.replace('_',' ')}</td>
                  <td>{c.incidentDate}</td>
                  <td>₹{c.claimedAmount?.toLocaleString()}</td>
                  <td>{statusBadge(c.status)}</td>
                  <td>
                    <Link to={`/claims/${c.claimId}`} className="btn btn-sm btn-outline-primary">
                      <i className="fas fa-eye mr-1"></i>View
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default ClaimsList;
