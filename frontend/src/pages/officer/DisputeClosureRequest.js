import React, { useEffect, useState } from "react";
import {
  Container,
  Navbar,
  Form,
  Button,
  Alert
} from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";

const API_BASE = "http://localhost:8080/api";

const DisputeClosureRequest = () => {

  const navigate = useNavigate();
  const { id } = useParams(); // 🔥 get disputeId from URL
  const token = localStorage.getItem("token");

  const [form, setForm] = useState({
    disputeId: id,
    closureReason: "",
    judgmentOrder: null,
    settlementDeed: null,
    declaration: false
  });

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  /* ================= AUTH CHECK ================= */

  useEffect(() => {
    if (!token) {
      navigate("/login");
    }
  }, [navigate, token]);

  /* ================= INPUT HANDLER ================= */

  const handleChange = e => {
    const { name, value, type, checked, files } = e.target;

    if (type === "file") {
      setForm(prev => ({ ...prev, [name]: files[0] }));
    } else if (type === "checkbox") {
      setForm(prev => ({ ...prev, [name]: checked }));
    } else {
      setForm(prev => ({ ...prev, [name]: value }));
    }
  };

  /* ================= SUBMIT ================= */

  const handleSubmit = async e => {
    e.preventDefault();
    setError("");
    setSuccess("");

    if (!form.declaration) {
      setError("You must confirm declaration");
      return;
    }

    const fd = new FormData();
    fd.append("disputeId", form.disputeId);
    fd.append("closureReason", form.closureReason);
    fd.append("judgmentOrder", form.judgmentOrder);
    fd.append("settlementDeed", form.settlementDeed);

    try {
      const res = await fetch(`${API_BASE}/dispute/close`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`
        },
        body: fd
      });

      if (!res.ok) {
        throw new Error();
      }

      setSuccess("Dispute closure request submitted successfully");

      setTimeout(() => {
        navigate("/sub-registrar/dashboard");
      }, 2000);

    } catch {
      setError("Dispute closure request failed");
    }
  };

  /* ================= UI ================= */

  return (
    <>
      {/* NAVBAR */}
      <Navbar className="px-4 py-3">
        <span className="navbar-brand">
          🔓 BhoomiDarpan – Dispute Closure
        </span>
      </Navbar>

      <Container className="mt-5">

        <div className="glass p-4 mb-4">
          <h4>Close Dispute</h4>
          <p className="text-muted mb-0">
            Submit documents to close selected dispute
          </p>
        </div>

        {error && <Alert variant="danger">{error}</Alert>}
        {success && <Alert variant="success">{success}</Alert>}

        <Form className="glass p-4" onSubmit={handleSubmit}>

          {/* 🔥 SELECTED DISPUTE (READ ONLY) */}
          <Form.Group className="mb-3">
            <Form.Label>Selected Dispute ID</Form.Label>
            <Form.Control
              type="text"
              value={form.disputeId}
              disabled
            />
          </Form.Group>

          {/* CLOSURE REASON */}
          <Form.Group className="mb-3">
            <Form.Label>Closure Reason</Form.Label>
            <Form.Select
              name="closureReason"
              required
              onChange={handleChange}
            >
              <option value="">Select reason</option>
              <option value="Case Dismissed">Case Dismissed</option>
              <option value="Case Withdrawn">Case Withdrawn</option>
              <option value="Settlement Reached">Settlement Reached</option>
              <option value="Stay Vacated">Stay Vacated</option>
            </Form.Select>
          </Form.Group>

          {/* DOCUMENTS */}
          <Form.Group className="mb-3">
            <Form.Label>Court Judgment / Order</Form.Label>
            <Form.Control
              type="file"
              name="judgmentOrder"
              required
              onChange={handleChange}
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Settlement / Withdrawal Document</Form.Label>
            <Form.Control
              type="file"
              name="settlementDeed"
              required
              onChange={handleChange}
            />
          </Form.Group>

          {/* DECLARATION */}
          <Form.Check
            className="mb-3"
            type="checkbox"
            name="declaration"
            label="I confirm that the dispute is legally resolved"
            onChange={handleChange}
          />

          <Button type="submit" className="btn-green w-100">
            🔓 Submit Closure Request
          </Button>
        </Form>
      </Container>
    </>
  );
};

export default DisputeClosureRequest;