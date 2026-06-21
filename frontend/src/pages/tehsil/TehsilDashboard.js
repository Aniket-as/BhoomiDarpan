import { useEffect, useState } from "react";
import {
  Container,
  Row,
  Col,
  Navbar,
  Table,
  Badge,
  Button,
  Spinner,
  Alert,
} from "react-bootstrap";
import { toast } from "react-hot-toast";
import { useNavigate } from "react-router-dom";

const API_BASE = "https://bhoomidarpan-5.onrender.com/api";

const TehsilDashboard = () => {
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const [mutations, setMutations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const loadMutations = async () => {
    try {
      const res = await fetch(`${API_BASE}/mutation/pending`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) throw new Error("Failed to load mutations");

      const data = await res.json();
      setMutations(data);
    } catch (err) {
      setError(err.message);
      toast.error(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadMutations();
  }, []);

  // 🔵 OPEN PROCESS PAGE
  const handleApprove = (id) => {
    navigate(`/mutation-process/${id}`);
  };

  // 🔴 DIRECT REJECT
  const rejectMutation = async (id) => {
    const remarks = prompt("Enter rejection remarks");
    if (!remarks) return;

    try {
      const res = await fetch(
        `${API_BASE}/mutation/reject/${id}?remarks=${encodeURIComponent(
          remarks
        )}`,
        {
          method: "POST",
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (!res.ok) throw new Error("Rejection failed");

      toast.success("Mutation rejected successfully");
      loadMutations();
    } catch (err) {
      toast.error(err.message);
    }
  };

  return (
    <>
      <Navbar className="px-4 py-3">
        <span className="navbar-brand">🏢 BhoomiDarpan – Tehsil Office</span>
      </Navbar>

      <Container className="mt-5">
        <h4 className="mb-4">Tehsil Officer Dashboard</h4>

        {loading && <Spinner animation="border" />}
        {error && <Alert variant="danger">{error}</Alert>}

        {!loading && !error && (
          <Table hover responsive>
            <thead>
              <tr>
                <th>Mutation No</th>
                <th>Property</th>
                <th>Status</th>
                <th>Remarks</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {mutations.length === 0 ? (
                <tr>
                  <td colSpan="5" className="text-center text-muted">
                    No pending mutations
                  </td>
                </tr>
              ) : (
                mutations.map((m) => (
                  <tr key={m.id}>
                    <td>{m.mutationNumber}</td>
                    <td>{m.propertyCode}</td>
                    <td>
                      <Badge bg="warning">{m.status}</Badge>
                    </td>
                    <td>{m.remarks || "-"}</td>
                    <td>
                      <Button
                        size="sm"
                        variant="success"
                        className="me-2"
                        onClick={() => handleApprove(m.id)}
                      >
                        Approve
                      </Button>

                      <Button
                        size="sm"
                        variant="danger"
                        onClick={() => rejectMutation(m.id)}
                      >
                        Reject
                      </Button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </Table>
        )}
      </Container>
    </>
  );
};

export default TehsilDashboard;
