import { useEffect, useState } from "react";
import { Table, Button, Badge } from "react-bootstrap";

const API_BASE = "http://localhost:8080/api";

const AdminManageOfficers = () => {
  const token = localStorage.getItem("token");
  const [officers, setOfficers] = useState([]);

  const loadOfficers = async () => {
    const res = await fetch(`${API_BASE}/admin/officers`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    const data = await res.json();
    setOfficers(data);
  };

  useEffect(() => {
    loadOfficers();
  }, []);

  const toggleStatus = async (id) => {
    await fetch(`${API_BASE}/admin/officers/${id}/status`, {
      method: "PUT",
      headers: { Authorization: `Bearer ${token}` }
    });
    loadOfficers();
  };

  const changeRole = async (id, role) => {
    await fetch(
      `${API_BASE}/admin/officers/${id}/role?role=${role}`,
      {
        method: "PUT",
        headers: { Authorization: `Bearer ${token}` }
      }
    );
    loadOfficers();
  };

  return (
    <div className="container mt-4">
      <div className="glass p-4 mb-4">
        <h4>👮 Manage Officers</h4>
        <p className="text-muted mb-0">
          Manage Sub-Registrars & Tehsildars
        </p>
      </div>

      <div className="glass p-4">
        <Table bordered hover responsive>
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Mobile</th>
              <th>Role</th>
              <th>Status</th>
              <th width="280">Actions</th>
            </tr>
          </thead>
          <tbody>
            {officers.map((u) => (
              <tr key={u.id}>
                <td>{u.name}</td>
                <td>{u.email}</td>
                <td>{u.mobile}</td>
                <td>
                  <Badge bg="info">{u.role}</Badge>
                </td>
                <td>
                  {u.active ? (
                    <Badge bg="success">ACTIVE</Badge>
                  ) : (
                    <Badge bg="danger">INACTIVE</Badge>
                  )}
                </td>
                <td>
                  <Button
                    size="sm"
                    variant={u.active ? "danger" : "success"}
                    onClick={() => toggleStatus(u.id)}
                    className="me-2"
                  >
                    {u.active ? "Deactivate" : "Activate"}
                  </Button>

                  {u.role === "SUB_REGISTRAR" ? (
                    <Button
                      size="sm"
                      variant="secondary"
                      onClick={() => changeRole(u.id, "TEHSILDAR")}
                    >
                      Make Tehsildar
                    </Button>
                  ) : (
                    <Button
                      size="sm"
                      variant="secondary"
                      onClick={() => changeRole(u.id, "SUB_REGISTRAR")}
                    >
                      Make Sub-Registrar
                    </Button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      </div>
    </div>
  );
};

export default AdminManageOfficers;
