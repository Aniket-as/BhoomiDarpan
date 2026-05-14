import { useEffect, useState } from "react";
import { Table, Badge, Button } from "react-bootstrap";

const API_BASE = "http://localhost:8080/api";

const AdminDisputes = () => {
  const token = localStorage.getItem("token");
  const [disputes, setDisputes] = useState([]);
  const [filter, setFilter] = useState("ALL");

  const loadDisputes = async () => {
    const url =
      filter === "ALL"
        ? `${API_BASE}/admin/disputes`
        : `${API_BASE}/admin/disputes/status/${filter}`;

    const res = await fetch(url, {
      headers: { Authorization: `Bearer ${token}` }
    });

    const data = await res.json();
    setDisputes(data);
  };

  useEffect(() => {
    loadDisputes();
  }, [filter]);

  const badge = (status) => {
    if (status === "REQUESTED") return "warning";
    if (status === "ACTIVE") return "primary";
    if (status === "CLOSED") return "success";
    return "secondary";
  };

  return (
    <div className="container mt-4">
      <div className="glass p-4 mb-4">
        <h4>⚖️ Dispute Management</h4>
        <p className="text-muted mb-0">
          View and monitor all disputes
        </p>
      </div>

      {/* FILTER */}
      <div className="mb-3">
        <select
          className="form-select w-25"
          value={filter}
          onChange={(e) => setFilter(e.target.value)}
        >
          <option value="ALL">All</option>
          <option value="REQUESTED">Requested</option>
          <option value="ACTIVE">Active</option>
          <option value="CLOSED">Closed</option>
        </select>
      </div>

      <div className="glass p-4">
        <Table bordered hover responsive>
          <thead>
            <tr>
              <th>Dispute Code</th>
              <th>Property</th>
              <th>Raised By</th>
              <th>Case No</th>
              <th>Court</th>
              <th>Type</th>
              <th>Status</th>
              <th>Created</th>
            </tr>
          </thead>
          <tbody>
            {disputes.map((d) => (
              <tr key={d.id}>
                <td>{d.disputeCode}</td>
                <td>{d.propertyCode}</td>
                <td>{d.raisedByName}</td>
                <td>{d.caseNumber}</td>
                <td>{d.courtName}</td>
                <td>{d.disputeType}</td>
                <td>
                  <Badge bg={badge(d.status)}>
                    {d.status}
                  </Badge>
                </td>
                <td>{d.createdAt}</td>
              </tr>
            ))}
          </tbody>
        </Table>
      </div>
    </div>
  );
};

export default AdminDisputes;
