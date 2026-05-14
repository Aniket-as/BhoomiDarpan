import React, { useEffect, useState } from "react";
import {
  Container,
  Navbar,
  Card,
  Button,
  Spinner,
  Alert,
  Badge,
  Row,
  Col,
} from "react-bootstrap";
import { useParams, useNavigate } from "react-router-dom";
import { toast } from "react-hot-toast";

const API_BASE = "http://localhost:8080/api";

const OwnerVisitDecision = () => {
  const { requestId } = useParams();
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  // ================= LOAD DATA =================
  useEffect(() => {
    fetch(`${API_BASE}/buy/owner-requests`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(async (res) => {
        if (!res.ok) throw new Error("Failed to load request");
        return res.json();
      })
      .then((list) => {
        const request = list.find(
          (r) => r.id === Number(requestId)
        );

        if (!request) throw new Error("Request not found");

        setData(request);
      })
      .catch((err) => {
        setError(err.message);
        toast.error(err.message);
      })
      .finally(() => setLoading(false));
  }, [requestId, token]);

  // ================= CONFIRM VISIT =================
  const confirmVisit = async (approve) => {
    setSubmitting(true);

    try {
      const res = await fetch(
        `${API_BASE}/buy/request/${requestId}/confirm-visit-date`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            approve: approve,
          }),
        }
      );

      if (!res.ok) {
        const err = await res.text();
        throw new Error(err);
      }

      toast.success(
        approve
          ? "Visit confirmed successfully!"
          : "Visit rejected successfully!"
      );

      navigate("/owner-requests");

    } catch (err) {
      toast.error(err.message);
    } finally {
      setSubmitting(false);
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

  if (!data) return null;

  return (
    <>
      <Navbar className="px-4 py-3 bg-dark text-white">
        <span className="navbar-brand text-white">
          📅 Visit Confirmation Panel
        </span>
      </Navbar>

      <Container className="mt-5">

        {/* PROPERTY DETAILS */}
        <Card className="p-4 mb-4 shadow-sm">
          <h5>🏠 Property Details</h5>
          <hr />

          <Row>
            <Col md={6}>
              <p><b>Property Code:</b> {data.propertyCode}</p>
              <p><b>Location:</b> {data.location}</p>
              <p><b>Land Type:</b> {data.landType}</p>
            </Col>

            <Col md={6}>
              <p><b>Area:</b> {data.area} sq.ft</p>
              <p><b>Status:</b> <Badge bg="warning">{data.status}</Badge></p>
            </Col>
          </Row>
        </Card>

        {/* BUYER DETAILS */}
        <Card className="p-4 mb-4 shadow-sm">
          <h5>👤 Buyer Details</h5>
          <hr />

          <p><b>Name:</b> {data.buyerName}</p>
          <p><b>Email:</b> {data.buyerEmail}</p>
          <p>
            <b>Offered Price:</b> ₹{" "}
            {Number(data.offeredPrice).toLocaleString()}
          </p>
        </Card>

        {/* VISIT DETAILS */}
        <Card className="p-4 shadow-sm">
          <h5>📆 Proposed Visit Date</h5>
          <hr />

          <p>
            <b>Date:</b>{" "}
            {data.visitDate
              ? new Date(data.visitDate).toLocaleDateString()
              : "Not Available"}
          </p>

          <div className="d-flex gap-3 mt-4">
            <Button
              variant="success"
              disabled={submitting}
              onClick={() => confirmVisit(true)}
            >
              {submitting ? <Spinner size="sm" /> : "Approve Visit"}
            </Button>

            <Button
              variant="danger"
              disabled={submitting}
              onClick={() => confirmVisit(false)}
            >
              {submitting ? <Spinner size="sm" /> : "Reject Visit"}
            </Button>
          </div>
        </Card>

      </Container>
    </>
  );
};

export default OwnerVisitDecision;