import React, { useEffect, useState } from "react";
import {
  Container,
  Navbar,
  Table,
  Badge,
  Button,
  Spinner,
  Alert,
} from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { toast } from "react-hot-toast";

const API_BASE = "http://localhost:8080/api";

const MyRequests = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }

    const fetchRequests = async () => {
      try {
        const res = await fetch(`${API_BASE}/buy/my-requests`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!res.ok) {
          const err = await res.text();
          throw new Error(err);
        }

        const data = await res.json();
        setRequests(data);
      } catch (err) {
        setError(err.message);
        toast.error(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchRequests();
  }, [navigate, token]);

  // ===============================
  // 🎨 STATUS COLOR
  // ===============================
  const statusColor = (status) => {
    switch (status) {
      case "PENDING":
        return "warning";
      case "APPROVED":
        return "success";
      case "COUNTER_OFFERED":
        return "primary";
      case "VISIT_DATE_PROPOSED":
        return "warning";
      case "VISIT_DATE_CONFIRMED":
        return "info";
      case "REJECTED":
        return "danger";
      case "VISIT_CANCELLED":
        return "secondary";
      default:
        return "secondary";
    }
  };

  // ===============================
  // 🎯 ACTION HANDLER
  // ===============================
  const renderActionButton = (r) => {
    switch (r.status) {
      case "COUNTER_OFFERED":
        return (
          <Button
            size="sm"
            variant="primary"
            onClick={() =>
              navigate(`/buyer-counter-response/${r.id}`)
            }
          >
            Respond to Counter
          </Button>
        );

      case "APPROVED":
        return (
          <Button
            size="sm"
            variant="success"
            onClick={() =>
              navigate(`/buyer-appointment/${r.id}`)
            }
          >
            Schedule Visit
          </Button>
        );

      case "VISIT_DATE_CONFIRMED":
        return (
          <Button
            size="sm"
            variant="info"
            onClick={() =>
              navigate(`/appointment-details/${r.id}`)
            }
          >
            View Appointment
          </Button>
        );

      case "VISIT_CANCELLED":
        return (
          <Button
            size="sm"
            variant="secondary"
            onClick={() =>
              navigate(`/buyer-appointment/${r.id}`)
            }
          >
            Reschedule
          </Button>
        );

      case "VISIT_DATE_PROPOSED":
        return (
          <Badge bg="warning">
            Waiting Owner Confirmation
          </Badge>
        );

      case "REJECTED":
        return <Badge bg="danger">Rejected</Badge>;

      case "PENDING":
        return <Badge bg="warning">Waiting Owner</Badge>;

      default:
        return <Badge bg="secondary">No Action</Badge>;
    }
  };

  if (loading)
    return (
      <div className="text-center mt-5">
        <Spinner animation="border" />
      </div>
    );

  if (error)
    return (
      <Container className="mt-5">
        <Alert variant="danger">{error}</Alert>
      </Container>
    );

  return (
    <>
      <Navbar className="px-4 py-3 bg-dark text-white">
        <span className="navbar-brand text-white">
          📑 BhoomiDarpan – My Requests
        </span>
      </Navbar>

      <Container className="mt-5">
        <div className="glass p-4 mb-4">
          <h4>My Requests</h4>
        </div>

        <div className="glass p-4 mb-5">
          {requests.length === 0 ? (
            <p className="text-muted text-center">
              No requests found
            </p>
          ) : (
            <Table hover responsive>
              <thead>
                <tr>
                  <th>Property</th>
                  <th>Owner</th>
                  <th>Offered Price</th>
                  <th>Status</th>
                  <th>Created At</th>
                  <th>Action</th>
                </tr>
              </thead>

              <tbody>
                {requests.map((r) => (
                  <tr key={r.id}>
                    <td>{r.propertyCode}</td>

                    <td>{r.ownerName || "-"}</td>

                    <td>
                      ₹{" "}
                      {r.offeredPrice
                        ? Number(r.offeredPrice).toLocaleString()
                        : "-"}
                    </td>

                    <td>
                      <Badge bg={statusColor(r.status)}>
                        {r.status}
                      </Badge>
                    </td>

                    <td>
                      {r.createdAt
                        ? new Date(
                            r.createdAt
                          ).toLocaleDateString()
                        : "-"}
                    </td>

                    <td>{renderActionButton(r)}</td>
                  </tr>
                ))}
              </tbody>
            </Table>
          )}
        </div>
      </Container>
    </>
  );
};

export default MyRequests;