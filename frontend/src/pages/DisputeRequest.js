import React, { useState } from "react";
import { Container, Navbar, Form, Button, Alert } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const API_BASE = "http://localhost:8080/api";

const DisputeRequest = () => {

  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const [form, setForm] = useState({
    propertyCode: "",
    courtName: "",
    caseNumber: "",
    disputeType: "OWNERSHIP_CONFLICT",
    declaration: false
  });

  const [files, setFiles] = useState({
    courtOrder: null,
    petitionCopy: null
  });

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  if (!token) {
    navigate("/login");
    return null;
  }

  const handleChange = (e) => {

    const { name, value, type, checked } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value
    }));

  };

  const handleFile = (e) => {

    setFiles((prev) => ({
      ...prev,
      [e.target.name]: e.target.files[0]
    }));

  };

  const submitDispute = async () => {

    if (!form.propertyCode) {
      setMessage("Property Code is required");
      return;
    }

    if (!form.declaration) {
      setMessage("You must accept the declaration");
      return;
    }

    const data = new FormData();

    data.append("propertyCode", form.propertyCode);
    data.append("courtName", form.courtName);
    data.append("caseNumber", form.caseNumber);
    data.append("disputeType", form.disputeType);

    if (files.courtOrder) {
      data.append("courtOrder", files.courtOrder);
    }

    if (files.petitionCopy) {
      data.append("petitionCopy", files.petitionCopy);
    }

    try {

      setLoading(true);
      setMessage("");

      const res = await fetch(`${API_BASE}/dispute/request`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`
        },
        body: data
      });

      const text = await res.text();

      if (!res.ok) throw new Error(text);

      alert(text);

      navigate("/dispute-status");

    } catch (err) {

      setMessage(err.message);

    } finally {

      setLoading(false);

    }
  };

  return (
    <>
      <Navbar className="px-4 py-3 bg-dark text-white">
        <span className="navbar-brand text-white">
          ⚖️ BhoomiDarpan – Dispute Request
        </span>
      </Navbar>

      <Container className="mt-5">

        <div className="p-4 mb-4 border rounded shadow-sm">
          <h4>Request Property Dispute</h4>
          <p className="text-muted">
            Submit dispute only after filing a court case.
          </p>
        </div>

        {message && <Alert variant="danger">{message}</Alert>}

        <div className="p-4 mb-4 border rounded shadow-sm">

          <Form.Group>

            <Form.Label>Property Code</Form.Label>

            <Form.Control
              name="propertyCode"
              placeholder="Enter Property Code (Example: PROP-2007)"
              onChange={handleChange}
            />

          </Form.Group>

        </div>

        <div className="p-4 mb-4 border rounded shadow-sm">

          <Form.Group className="mb-3">

            <Form.Label>Court Name</Form.Label>

            <Form.Control
              name="courtName"
              placeholder="Example: Civil Court Tuljapur"
              onChange={handleChange}
            />

          </Form.Group>

          <Form.Group className="mb-3">

            <Form.Label>Case Number</Form.Label>

            <Form.Control
              name="caseNumber"
              placeholder="Example: CC/AGR/2026/118"
              onChange={handleChange}
            />

          </Form.Group>

          <Form.Group>

            <Form.Label>Dispute Type</Form.Label>

            <Form.Select name="disputeType" onChange={handleChange}>

              <option value="OWNERSHIP_CONFLICT">
                Ownership Conflict
              </option>

              <option value="FORGERY_SUSPECTED">
                Forgery Suspected
              </option>

              <option value="COURT_STAY_ORDER">
                Court Stay Order
              </option>

              <option value="MULTIPLE_OWNERSHIP_CLAIMS">
                Multiple Ownership Claims
              </option>

              <option value="INHERITANCE_OBJECTION">
                Inheritance Objection
              </option>

            </Form.Select>

          </Form.Group>

        </div>

        <div className="p-4 mb-4 border rounded shadow-sm">

          <Form.Group className="mb-3">

            <Form.Label>Court Order (PDF)</Form.Label>

            <Form.Control
              type="file"
              name="courtOrder"
              accept=".pdf"
              onChange={handleFile}
            />

          </Form.Group>

          <Form.Group>

            <Form.Label>Petition Copy (PDF)</Form.Label>

            <Form.Control
              type="file"
              name="petitionCopy"
              accept=".pdf"
              onChange={handleFile}
            />

          </Form.Group>

        </div>

        <div className="p-4 mb-4 border rounded shadow-sm">

          <Form.Check
            type="checkbox"
            name="declaration"
            label="I declare that the court case details are genuine"
            onChange={handleChange}
          />

        </div>

        <div className="p-4 mb-5 border rounded shadow-sm">

          <Button
            variant="danger"
            className="w-100"
            disabled={loading}
            onClick={submitDispute}
          >
            ⚖️ Submit Dispute Request
          </Button>

        </div>

      </Container>
    </>
  );
};

export default DisputeRequest;