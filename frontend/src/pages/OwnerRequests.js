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

const API_BASE = "https://bhoomidarpan-5.onrender.com/api";

const OwnerRequests = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  // ================= LOAD DATA =================
  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }

    const fetchRequests = async () => {
      try {
        const res = await fetch(`${API_BASE}/buy/owner-requests`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!res.ok) throw new Error("Failed to load requests");

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
  }, [token, navigate]);

  // ================= STATUS COLOR =================
  const statusColor = (status) => {
    switch (status) {
      case "PENDING":
        return "warning";
      case "APPROVED":
        return "success";
      case "COUNTER_OFFERED":
        return "primary";
      case "VISIT_DATE_PROPOSED":
        return "info";
      case "VISIT_DATE_CONFIRMED":
        return "success";
      case "REJECTED":
        return "danger";
      case "VISIT_CANCELLED":
        return "secondary";
      default:
        return "secondary";
    }
  };

  // ================= ACTION ROUTING =================
  const handleManage = (request) => {
    switch (request.status) {
      case "VISIT_DATE_PROPOSED":
        navigate(`/owner-visit/${request.id}`);
        break;

      case "COUNTER_OFFERED":
      case "PENDING":
      case "APPROVED":
      default:
        navigate(`/owner-request/${request.id}`);
        break;
    }
  };

  if (loading)
    return (
      <div className="text-center mt-5">
        <Spinner />
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
          🏛️ Owner Requests
        </span>
      </Navbar>

      <Container className="mt-5">
        <div className="glass p-4 shadow-sm">
          {requests.length === 0 ? (
            <p className="text-muted text-center">
              No requests found
            </p>
          ) : (
            <Table hover responsive>
              <thead>
                <tr>
                  <th>Property</th>
                  <th>Buyer</th>
                  <th>Offer</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>

              <tbody>
                {requests.map((r) => (
                  <tr key={r.id}>
                    <td>{r.propertyCode}</td>

                    <td>{r.buyerName}</td>

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
                      <Button
                        size="sm"
                        variant="dark"
                        onClick={() => handleManage(r)}
                      >
                        Manage
                      </Button>
                    </td>
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

export default OwnerRequests;