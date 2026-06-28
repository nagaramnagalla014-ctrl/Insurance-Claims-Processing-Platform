import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from '../api/axios';

function AdjusterQueue() {
  const [openClaims, setOpenClaims] = useState([]);
  const [myClaims, setMyClaims] = useState([]);
  const [loading, setLoading] = useState(true);
  const [tab, setTab] = useState('open');

  useEffect(() => {
    Promise.all([
      axios.get('/claims/open'),
      axios.get('/claims/adjuster/my'),
    ]).then(([openRes, myRes]) => {
      setOpenClaims(openRes.data.data || []);
      setMyClaims(myRes.data.data || []);
      setLoading(false);
    }).catch(() => setLoading(false));
  }, []);

  const assignSelf = async claimId => {
    await axios.post(`/claims/${claimId}/assign`);
    const openRes = await axios.get('/claims/open');
    const myRes = await axios.get('/claims/adjuster/my');
    setOpenClaims(openRes.data.data || []);
    setMyClaims(myRes.data.data || []);
  };

  const statusBadge = status => {
    const map = { SUBMITTED:'info', UNDER_REVIEW:'warning', ASSESSMENT:'primary' };
    return <span className={`badge badge-${map[status]||'secondary'}`}>{status.replace('_',' ')}</span>;
  };

  const ClaimTable = ({ claims, showAssign }) => (
    claims.length === 0 ? <p className="text-muted p-3">No claims.</p> : (
      <table className="table table-hover mb-0">
        <thead className="thead-light">
          <tr><th>Claim #</th><th>Type</th><th>Claimant</th><th>Amount</th><th>Status</th><th>Actions</th></tr>
        </thead>
        <tbody>
          {claims.map(c => (
            <tr key={c.claimId}>
              <td>{c.claimNumber}</td>
              <td>{c.claimType?.replace('_',' ')}</td>
              <td>{c.claimant?.firstName} {c.claimant?.lastName}</td>
              <td>₹{c.claimedAmount?.toLocaleString()}</td>
              <td>{statusBadge(c.status)}</td>
              <td>
                <Link to={`/claims/${c.claimId}`} className="btn btn-sm btn-outline-primary mr-1">View</Link>
                {showAssign && c.status === 'SUBMITTED' && (
                  <button className="btn btn-sm btn-outline-success" onClick={() => assignSelf(c.claimId)}>
                    Assign to Me
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    )
  );

  if (loading) return <div className="text-center mt-5"><div className="spinner-border text-primary"></div></div>;

  return (
    <div className="container mt-4">
      <h5 className="mb-4">Adjuster Workqueue</h5>
      <ul className="nav nav-tabs mb-3">
        <li className="nav-item">
          <button className={`nav-link ${tab==='open'?'active':''}`} onClick={() => setTab('open')}>
            Open Claims <span className="badge badge-info ml-1">{openClaims.length}</span>
          </button>
        </li>
        <li className="nav-item">
          <button className={`nav-link ${tab==='mine'?'active':''}`} onClick={() => setTab('mine')}>
            Assigned to Me <span className="badge badge-primary ml-1">{myClaims.length}</span>
          </button>
        </li>
      </ul>
      <div className="card">
        <div className="card-body p-0">
          {tab === 'open'
            ? <ClaimTable claims={openClaims} showAssign={true} />
            : <ClaimTable claims={myClaims} showAssign={false} />
          }
        </div>
      </div>
    </div>
  );
}

export default AdjusterQueue;
