import React, { useEffect, useState } from "react";
import { Container, Navbar, Table, Badge, Alert } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const API_BASE = "http://localhost:8080/api";

const DisputeStatus = () => {
  const [disputes, setDisputes] = useState([]);
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }

    fetch(`${API_BASE}/dispute/my-disputes`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
      .then(res => {
        if (!res.ok) throw new Error("Failed");
        return res.json();
      })
      .then(setDisputes)
      .catch(err => {
            console.error(err);
            if (err.message.includes("401")) {
                navigate("/login");
            }
        });

  }, [navigate, token]);

  return (
    <>
      {/* NAVBAR */}
      <Navbar className="px-4 py-3">
        <span className="navbar-brand">
          ⚖️ BhoomiDarpan – Dispute Status
        </span>
      </Navbar>

      <Container className="mt-5">
        {/* HEADER */}
        <div className="glass p-4 mb-4">
          <h4 style={{ fontFamily: "Questrial" }}>
            Property Dispute Status
          </h4>
          <p className="text-muted mb-0">
            Track legal objections or court cases associated with your properties.
          </p>
        </div>

        {/* DISPUTES TABLE */}
        <div className="glass p-4">
          {disputes.length === 0 ? (
            <p className="text-muted text-center">
              No disputes found
            </p>
          ) : (
            <Table hover responsive>
              <thead>
                <tr>
                  <th>Dispute Code</th>
                  <th>Property</th>
                  <th>Raised By</th>
                  <th>Case No</th>
                  <th>Court</th>
                  <th>Status</th>
                  <th>OCR</th>
                </tr>
              </thead>
              <tbody>
                {disputes.map(d => (
                  <tr key={d.id}>
                    <td>{d.disputeCode}</td>
                    <td>{d.propertyCode}</td>
                    <td>{d.raisedByName}</td>
                    <td>{d.caseNumber}</td>
                    <td>{d.courtName}</td>
                    <td>
                      <Badge bg={
                        d.status === "ACTIVE"
                          ? "danger"
                          : d.status === "REQUESTED"
                          ? "warning"
                          : "success"
                      }>
                        {d.status}
                      </Badge>
                    </td>
                    <td>
                      <Badge bg={
                        d.ocrValidation === "VALID"
                          ? "success"
                          : "danger"
                      }>
                        {d.ocrValidation}
                      </Badge>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          )}

          {disputes.some(d => d.status === "ACTIVE") && (
            <Alert variant="danger" className="mt-4 small">
              🚫 Properties under dispute are locked.
              Sale, mutation, and registration are blocked until dispute resolution.
            </Alert>
          )}
        </div>
      </Container>

      <div className="footer mt-5 text-center">
        © 2026 BhoomiDarpan • Dispute transparency prevents fraud
      </div>
    </>
  );
};

export default DisputeStatus;
