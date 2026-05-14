import { useEffect, useState } from "react";
import { Container, Table, Button, Badge } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const API_BASE = "http://localhost:8080/api";

const AdminManageUsers = () => {
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const [users, setUsers] = useState([]);

  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }

    fetch(`${API_BASE}/admin/users`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => res.json())
      .then(setUsers);
  }, [navigate, token]);

  const toggleStatus = async (id) => {
    await fetch(`${API_BASE}/admin/users/${id}/status`, {
      method: "PUT",
      headers: { Authorization: `Bearer ${token}` }
    });
    refresh();
  };

  const changeRole = async (id, role) => {
    await fetch(
      `${API_BASE}/admin/users/${id}/role?role=${role}`,
      {
        method: "PUT",
        headers: { Authorization: `Bearer ${token}` }
      }
    );
    refresh();
  };

  const refresh = () => {
    fetch(`${API_BASE}/admin/users`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => res.json())
      .then(setUsers);
  };

  return (
    <Container className="mt-5">
      <div className="glass p-4 mb-4">
        <h4>👥 Admin – Manage Users</h4>
        <p className="text-muted mb-0">
          Control user roles and access
        </p>
      </div>

      <div className="glass p-4">
        <Table hover responsive>
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Mobile</th>
              <th>Role</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>

          <tbody>
            {users.map(u => (
              <tr key={u.id}>
                <td>{u.name}</td>
                <td>{u.email}</td>
                <td>{u.mobile}</td>
                <td>
                  <Badge bg="info">{u.role}</Badge>
                </td>
                <td>
                  <Badge bg={u.active ? "success" : "danger"}>
                    {u.active ? "ACTIVE" : "BLOCKED"}
                  </Badge>
                </td>
                <td className="d-flex gap-2">
                  <Button
                    size="sm"
                    variant={u.active ? "danger" : "success"}
                    onClick={() => toggleStatus(u.id)}
                  >
                    {u.active ? "Disable" : "Enable"}
                  </Button>

                  <select
                    className="form-select form-select-sm"
                    value={u.role}
                    onChange={(e) =>
                      changeRole(u.id, e.target.value)
                    }
                  >
                    <option value="USER">USER</option>
                    <option value="SUB_REGISTRAR">SUB_REGISTRAR</option>
                    <option value="TEHSILDAR">TEHSILDAR</option>
                  </select>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      </div>
    </Container>
  );
};

export default AdminManageUsers;
