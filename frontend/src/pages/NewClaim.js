import React, { useState, useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import axios from '../api/axios';

const CLAIM_TYPES = ['ACCIDENT','THEFT','FIRE','FLOOD','MEDICAL','DEATH','DISABILITY','TRAVEL_DELAY','OTHER'];

function NewClaim() {
  const [policies, setPolicies] = useState([]);
  const [form, setForm] = useState({
    policyId: '', claimType: '', incidentDate: '', incidentLocation: '',
    description: '', claimedAmount: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const history = useHistory();

  useEffect(() => {
    axios.get('/policies/my').then(res => setPolicies(res.data.data || []));
  }, []);

  const update = field => e => setForm({...form, [field]: e.target.value});

  const handleSubmit = async e => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const res = await axios.post('/claims', {
        ...form,
        policyId: parseInt(form.policyId),
        claimedAmount: parseFloat(form.claimedAmount),
      });
      history.push(`/claims/${res.data.data.claimId}`);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit claim');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-4">
      <div className="row justify-content-center">
        <div className="col-md-8">
          <div className="card shadow-sm">
            <div className="card-header bg-primary text-white">
              <h5 className="mb-0"><i className="fas fa-file-alt mr-2"></i>Submit New Claim</h5>
            </div>
            <div className="card-body">
              {error && <div className="alert alert-danger">{error}</div>}
              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label>Policy <span className="text-danger">*</span></label>
                  <select className="form-control" required value={form.policyId} onChange={update('policyId')}>
                    <option value="">-- Select Policy --</option>
                    {policies.map(p => (
                      <option key={p.policyId} value={p.policyId}>
                        {p.policyNumber} - {p.policyType} (Sum Insured: ₹{p.sumInsured?.toLocaleString()})
                      </option>
                    ))}
                  </select>
                </div>
                <div className="form-row">
                  <div className="form-group col-md-6">
                    <label>Claim Type <span className="text-danger">*</span></label>
                    <select className="form-control" required value={form.claimType} onChange={update('claimType')}>
                      <option value="">-- Select Type --</option>
                      {CLAIM_TYPES.map(t => <option key={t} value={t}>{t.replace('_', ' ')}</option>)}
                    </select>
                  </div>
                  <div className="form-group col-md-6">
                    <label>Incident Date <span className="text-danger">*</span></label>
                    <input type="date" className="form-control" required
                      value={form.incidentDate} onChange={update('incidentDate')} />
                  </div>
                </div>
                <div className="form-group">
                  <label>Incident Location</label>
                  <input className="form-control" placeholder="City, State"
                    value={form.incidentLocation} onChange={update('incidentLocation')} />
                </div>
                <div className="form-group">
                  <label>Description <span className="text-danger">*</span></label>
                  <textarea className="form-control" rows="4" required
                    placeholder="Describe what happened in detail..."
                    value={form.description} onChange={update('description')}></textarea>
                </div>
                <div className="form-group">
                  <label>Claimed Amount (₹) <span className="text-danger">*</span></label>
                  <input type="number" className="form-control" required min="1"
                    value={form.claimedAmount} onChange={update('claimedAmount')} />
                </div>
                <div className="d-flex justify-content-end">
                  <button type="button" className="btn btn-secondary mr-2"
                    onClick={() => history.push('/claims')}>Cancel</button>
                  <button type="submit" className="btn btn-primary" disabled={loading}>
                    {loading ? <span className="spinner-border spinner-border-sm mr-2"></span> : null}
                    Submit Claim
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default NewClaim;
