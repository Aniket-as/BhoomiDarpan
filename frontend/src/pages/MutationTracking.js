import React, { useEffect, useState } from "react";
import { Container, Table, Badge } from "react-bootstrap";

const API_BASE = "http://localhost:8080/api";

const MutationTracking = () => {
  const [mutations, setMutations] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");

    fetch(`${API_BASE}/mutation/my`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to load mutations");
        return res.json();
      })
      .then((data) => {
        setMutations(Array.isArray(data) ? data : []);
        setLoading(false);
      })
      .catch(() => {
        setMutations([]);
        setLoading(false);
      });
  }, []);

  if (loading) return <div className="glass p-4">Loading mutations...</div>;

  return (
    <Container className="mt-5">
      <div className="glass p-4">
        <h4>Mutation Tracking</h4>

        {mutations.length === 0 ? (
          <p className="text-muted mt-3">No mutation records found.</p>
        ) : (
          <Table bordered hover responsive className="mt-3">
            <thead>
              <tr>
                <th>Property Code</th>
                <th>Mutation No</th>
                <th>Status</th>
                <th>Approved By</th>
              </tr>
            </thead>
            <tbody>
              {mutations.map((m) => (
                <tr key={m.id}>
                  <td>{m.propertyCode}</td>
                  <td>{m.mutationNumber}</td>
                  <td>
                    <Badge bg={
                      m.status === "APPROVED"
                        ? "success"
                        : m.status === "REJECTED"
                        ? "danger"
                        : "warning"
                    }>
                      {m.status}
                    </Badge>
                  </td>
                  <td>{m.approvedByName || "-"}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        )}
      </div>
    </Container>
  );
};

export default MutationTracking;
