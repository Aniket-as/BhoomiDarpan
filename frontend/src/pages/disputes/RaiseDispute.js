import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Container, Navbar, Button, Alert } from "react-bootstrap";

const API_BASE = "http://localhost:8080/api";

const RaiseDispute = () => {
  const navigate = useNavigate();
  const { propertyId } = useParams(); // optional, if routed like /disputes/raise/:propertyId
  const token = localStorage.getItem("token");

  const [form, setForm] = useState({
    disputeType: "",
    courtName: "",
    caseNumber: "",
    reason: "",
    courtOrder: null,
    petitionCopy: null
  });

  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleFile = (e) => {
    setForm({ ...form, [e.target.name]: e.target.files[0] });
  };

  const submitDispute = async () => {
    if (!window.confirm("Submit this dispute?")) return;

    const fd = new FormData();
    fd.append("propertyId", propertyId);
    fd.append("disputeType", form.disputeType);
    fd.append("courtName", form.courtName);
    fd.append("caseNumber", form.caseNumber);
    fd.append("reason", form.reason);
    fd.append("courtOrder", form.courtOrder);
    fd.append("petitionCopy", form.petitionCopy);

    try {
      setLoading(true);

      const res = await fetch(`${API_BASE}/dispute/request`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`
        },
        body: fd
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg);
      }

      alert("Dispute submitted successfully");
      navigate("/dashboard");

    } catch (err) {
      alert(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Navbar className="px-4 py-3">
        <span className="navbar-brand">⚖️ BhoomiDarpan – Judicial Dispute</span>
      </Navbar>

      <Container className="mt-5">

        {/* HEADER */}
        <div className="glass p-4 mb-4">
          <h4 style={{ fontFamily: "Questrial" }}>Raise Property Dispute</h4>
          <p className="text-muted mb-0">
            Raise a judicial or legal dispute with documentary proof.
          </p>
        </div>

        {/* DISPUTE FORM */}
        <div className="glass p-4 mb-4">
          <h6>⚠️ Dispute Information</h6>

          <label className="form-label">Dispute Type</label>
          <select
            className="form-select mb-3"
            name="disputeType"
            onChange={handleChange}
            required
          >
            <option value="">Select Dispute Type</option>
            <option value="OWNERSHIP_CONFLICT">Ownership Conflict</option>
            <option value="FORGERY">Forgery Suspected</option>
            <option value="COURT_STAY">Court Stay Order</option>
            <option value="MULTIPLE_CLAIMS">Multiple Ownership Claims</option>
            <option value="INHERITANCE">Inheritance Objection</option>
            <option value="OTHER">Other Legal Issue</option>
          </select>

          <label className="form-label">Court / Authority Name</label>
          <input
            className="form-control mb-3"
            name="courtName"
            onChange={handleChange}
            placeholder="e.g. Pune Civil Court"
            required
          />

          <label className="form-label">Case / Reference Number</label>
          <input
            className="form-control mb-3"
            name="caseNumber"
            onChange={handleChange}
            placeholder="e.g. CS/234/2026"
            required
          />

          <label className="form-label">Detailed Reason</label>
          <textarea
            className="form-control mb-3"
            rows="4"
            name="reason"
            onChange={handleChange}
            required
          />

          <label className="form-label">Court Order</label>
          <input
            type="file"
            className="form-control mb-3"
            name="courtOrder"
            onChange={handleFile}
            required
          />

          <label className="form-label">Petition Copy</label>
          <input
            type="file"
            className="form-control"
            name="petitionCopy"
            onChange={handleFile}
            required
          />
        </div>

        {/* WARNING */}
        <div className="glass p-4 mb-4">
          <Alert variant="danger" className="small mb-0">
            ⚠ Once approved, the property will be frozen and all transactions blocked.
          </Alert>
        </div>

        {/* ACTIONS */}
        <div className="glass p-4 mb-5 d-flex gap-3">
          <Button
            className="btn-red w-50"
            disabled={loading}
            onClick={submitDispute}
          >
            ⚖️ Submit Dispute
          </Button>

          <Button
            className="btn-violet w-50"
            onClick={() => navigate(-1)}
          >
            Cancel
          </Button>
        </div>
      </Container>

      <div className="footer mt-5 text-center">
        © 2026 BhoomiDarpan • Judicial disputes are legally binding
      </div>
    </>
  );
};

export default RaiseDispute;
