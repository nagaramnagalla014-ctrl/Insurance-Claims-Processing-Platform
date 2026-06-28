import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from '../api/axios';
import { useAuth } from '../context/AuthContext';

function ClaimDetail() {
  const { id } = useParams();
  const { user } = useAuth();
  const [claim, setClaim] = useState(null);
  const [documents, setDocuments] = useState([]);
  const [notes, setNotes] = useState([]);
  const [noteText, setNoteText] = useState('');
  const [loading, setLoading] = useState(true);
  const [uploadFile, setUploadFile] = useState(null);
  const [docType, setDocType] = useState('PHOTOS');

  const isAdjuster = user && ['ADJUSTER','MANAGER','ADMIN'].includes(user.role);

  useEffect(() => {
    fetchAll();
  }, [id]);

  const fetchAll = async () => {
    try {
      const [claimRes, docRes, noteRes] = await Promise.all([
        axios.get(`/claims/${id}`),
        axios.get(`/documents/claim/${id}`),
        axios.get(`/notes/claim/${id}`),
      ]);
      setClaim(claimRes.data.data);
      setDocuments(docRes.data.data || []);
      setNotes(noteRes.data.data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const addNote = async e => {
    e.preventDefault();
    if (!noteText.trim()) return;
    await axios.post(`/notes/claim/${id}`, {
      content: noteText,
      noteType: isAdjuster ? 'CUSTOMER_VISIBLE' : 'CUSTOMER_VISIBLE'
    });
    setNoteText('');
    fetchAll();
  };

  const uploadDocument = async e => {
    e.preventDefault();
    if (!uploadFile) return;
    const fd = new FormData();
    fd.append('file', uploadFile);
    fd.append('documentType', docType);
    await axios.post(`/documents/upload/${id}`, fd, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    setUploadFile(null);
    fetchAll();
  };

  const statusBadge = status => {
    const map = {
      DRAFT:'secondary',SUBMITTED:'info',UNDER_REVIEW:'warning',ASSESSMENT:'warning',
      PENDING_SETTLEMENT:'primary',SETTLED:'success',REJECTED:'danger',CLOSED:'dark'
    };
    return <span className={`badge badge-${map[status]||'secondary'} badge-pill px-3 py-2`}>{status.replace(/_/g,' ')}</span>;
  };

  if (loading) return <div className="text-center mt-5"><div className="spinner-border text-primary"></div></div>;
  if (!claim) return <div className="container mt-4"><div className="alert alert-danger">Claim not found</div></div>;

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-start mb-4">
        <div>
          <h5>{claim.claimNumber}</h5>
          <p className="text-muted mb-0">{claim.claimType?.replace('_',' ')} · Filed {claim.submittedAt?.substring(0,10)}</p>
        </div>
        {statusBadge(claim.status)}
      </div>

      <div className="row">
        <div className="col-md-8">
          <div className="card mb-3">
            <div className="card-header"><strong>Claim Details</strong></div>
            <div className="card-body">
              <div className="row">
                <div className="col-6">
                  <p className="mb-1 text-muted small">Policy</p>
                  <p className="font-weight-bold">{claim.policy?.policyNumber}</p>
                </div>
                <div className="col-6">
                  <p className="mb-1 text-muted small">Incident Date</p>
                  <p className="font-weight-bold">{claim.incidentDate}</p>
                </div>
                <div className="col-6">
                  <p className="mb-1 text-muted small">Location</p>
                  <p>{claim.incidentLocation || '-'}</p>
                </div>
                <div className="col-6">
                  <p className="mb-1 text-muted small">Claimed Amount</p>
                  <p className="font-weight-bold text-primary">₹{claim.claimedAmount?.toLocaleString()}</p>
                </div>
                {claim.approvedAmount && (
                  <div className="col-6">
                    <p className="mb-1 text-muted small">Approved Amount</p>
                    <p className="font-weight-bold text-success">₹{claim.approvedAmount?.toLocaleString()}</p>
                  </div>
                )}
              </div>
              <hr />
              <p className="mb-1 text-muted small">Description</p>
              <p>{claim.description}</p>
              {claim.rejectionReason && (
                <div className="alert alert-danger mt-2">
                  <strong>Rejection Reason:</strong> {claim.rejectionReason}
                </div>
              )}
            </div>
          </div>

          <div className="card mb-3">
            <div className="card-header"><strong>Documents ({documents.length})</strong></div>
            <div className="card-body">
              {documents.length === 0 ? (
                <p className="text-muted">No documents uploaded yet.</p>
              ) : (
                <table className="table table-sm">
                  <thead><tr><th>Type</th><th>File</th><th>Status</th></tr></thead>
                  <tbody>
                    {documents.map(d => (
                      <tr key={d.documentId}>
                        <td>{d.documentType?.replace('_',' ')}</td>
                        <td>{d.fileName}</td>
                        <td>
                          <span className={`badge badge-${d.verificationStatus==='VERIFIED'?'success':d.verificationStatus==='REJECTED'?'danger':'warning'}`}>
                            {d.verificationStatus}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
              {!isAdjuster && claim.status !== 'SETTLED' && claim.status !== 'REJECTED' && (
                <form onSubmit={uploadDocument} className="mt-3">
                  <div className="form-row align-items-end">
                    <div className="col-md-4">
                      <select className="form-control form-control-sm" value={docType} onChange={e => setDocType(e.target.value)}>
                        {['POLICE_REPORT','MEDICAL_REPORT','REPAIR_ESTIMATE','PHOTOS','RECEIPTS','ID_PROOF','OTHER'].map(t =>
                          <option key={t} value={t}>{t.replace('_',' ')}</option>
                        )}
                      </select>
                    </div>
                    <div className="col-md-5">
                      <input type="file" className="form-control-file form-control-sm"
                        onChange={e => setUploadFile(e.target.files[0])} />
                    </div>
                    <div className="col-md-3">
                      <button type="submit" className="btn btn-sm btn-outline-primary" disabled={!uploadFile}>
                        Upload
                      </button>
                    </div>
                  </div>
                </form>
              )}
            </div>
          </div>

          <div className="card">
            <div className="card-header"><strong>Communication ({notes.length})</strong></div>
            <div className="card-body">
              {notes.map(n => (
                <div key={n.noteId} className="mb-3">
                  <div className="d-flex justify-content-between">
                    <strong className="small">{n.author?.fullName || n.author?.firstName}</strong>
                    <span className="text-muted small">{n.createdAt?.substring(0,16).replace('T',' ')}</span>
                  </div>
                  <p className="mb-0 mt-1">{n.content}</p>
                  <hr className="mt-2 mb-0" />
                </div>
              ))}
              <form onSubmit={addNote} className="mt-3">
                <div className="form-group">
                  <textarea className="form-control" rows="2" placeholder="Add a message..."
                    value={noteText} onChange={e => setNoteText(e.target.value)}></textarea>
                </div>
                <button type="submit" className="btn btn-sm btn-primary">Send</button>
              </form>
            </div>
          </div>
        </div>

        <div className="col-md-4">
          <div className="card mb-3">
            <div className="card-header"><strong>Assigned Adjuster</strong></div>
            <div className="card-body text-center">
              {claim.assignedAdjuster ? (
                <>
                  <i className="fas fa-user-tie fa-2x text-secondary mb-2"></i>
                  <p className="font-weight-bold mb-0">{claim.assignedAdjuster.firstName} {claim.assignedAdjuster.lastName}</p>
                  <p className="text-muted small">{claim.assignedAdjuster.email}</p>
                </>
              ) : (
                <p className="text-muted">Not yet assigned</p>
              )}
            </div>
          </div>
          <div className="card">
            <div className="card-header"><strong>Claim Timeline</strong></div>
            <div className="card-body">
              <div className="small text-muted">Submitted: {claim.submittedAt?.substring(0,10) || '-'}</div>
              <div className="small text-muted mt-2">Last Updated: {claim.updatedAt?.substring(0,10) || '-'}</div>
              {claim.resolvedAt && <div className="small text-muted mt-2">Resolved: {claim.resolvedAt?.substring(0,10)}</div>}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ClaimDetail;
