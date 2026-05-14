import { useEffect, useState } from "react";
import { Table, Badge } from "react-bootstrap";

const API_BASE = "http://localhost:8080/api";

const AdminLogs = () => {
  const token = localStorage.getItem("token");
  const [logs, setLogs] = useState([]);

  useEffect(() => {
    fetch(`${API_BASE}/admin/logs`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
      .then(res => res.json())
      .then(setLogs);
  }, []);

  return (
    <div className="container mt-4">
      <div className="glass p-4 mb-4">
        <h4>📜 System Audit Logs</h4>
        <p className="text-muted mb-0">
          Track all administrative actions
        </p>
      </div>

      <div className="glass p-4">
        <Table hover responsive>
          <thead>
            <tr>
              <th>Action</th>
              <th>Module</th>
              <th>Performed By</th>
              <th>Role</th>
              <th>Description</th>
              <th>Time</th>
            </tr>
          </thead>
          <tbody>
            {logs.map(log => (
              <tr key={log.id}>
                <td>
                  <Badge bg="dark">{log.action}</Badge>
                </td>
                <td>{log.module}</td>
                <td>{log.performedBy}</td>
                <td>
                  <Badge bg="primary">{log.role}</Badge>
                </td>
                <td>{log.description}</td>
                <td>{log.createdAt}</td>
              </tr>
            ))}
          </tbody>
        </Table>
      </div>
    </div>
  );
};

export default AdminLogs;
