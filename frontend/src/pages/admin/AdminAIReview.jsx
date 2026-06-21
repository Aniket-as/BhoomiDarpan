import React, { useEffect, useState } from "react";
import { Container, Table, Button, Badge } from "react-bootstrap";

const API_BASE = "https://bhoomidarpan-5.onrender.com/api";

const AdminAIReview = () => {
  const token = localStorage.getItem("token");
  const [registrations, setRegistrations] = useState([]);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    const res = await fetch(`${API_BASE}/admin/registrations/on-hold`, {
      headers: { Authorization: `Bearer ${token}` }
    });

    const data = await res.json();
    setRegistrations(data);
  };

  const approveAnyway = async (id) => {
    await fetch(`${API_BASE}/admin/registrations/${id}/approve-anyway`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` }
    });

    fetchData();
  };

  const rejectFraud = async (id) => {
    await fetch(`${API_BASE}/admin/registrations/${id}/reject-fraud`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` }
    });

    fetchData();
  };

  return (
    <Container className="mt-5">
      <h4>🤖 AI Flagged Registrations</h4>

      <Table hover responsive className="mt-4">
        <thead>
          <tr>
            <th>ID</th>
            <th>Property</th>
            <th>Buyer</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {registrations.length === 0 && (
            <tr>
              <td colSpan="5" className="text-center">
                No AI flagged cases
              </td>
            </tr>
          )}

          {registrations.map(r => (
            <tr key={r.id}>
              <td>{r.id}</td>
              <td>{r.property.propertyCode}</td>
              <td>{r.buyer.name}</td>
              <td>
                <Badge bg="danger">AI REVIEW REQUIRED</Badge>
              </td>
              <td>
                <Button
                  variant="success"
                  size="sm"
                  onClick={() => approveAnyway(r.id)}
                  className="me-2"
                >
                  Approve Anyway
                </Button>

                <Button
                  variant="danger"
                  size="sm"
                  onClick={() => rejectFraud(r.id)}
                >
                  Reject Fraud
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </Container>
  );
};

export default AdminAIReview;
