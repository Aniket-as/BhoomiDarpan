import React, { useEffect, useState, useCallback } from "react";
import { Container, Navbar, Button, Badge, Spinner, Alert } from "react-bootstrap";
import { useParams } from "react-router-dom";
import { toast } from "react-hot-toast";

const API_BASE = "https://bhoomidarpan-5.onrender.com/api";

const OwnerAppointment = () => {
  const { requestId } = useParams();
  const token = localStorage.getItem("token");

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [confirming, setConfirming] = useState(false);

  const loadData = useCallback(async () => {
    try {
      setError(null);
      const res = await fetch(`${API_BASE}/buy/request/${requestId}/visit`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) throw new Error("Failed to load visit details");
      const response = await res.json();
      setData(response);
    } catch (err) {
      setError(err.message);
      toast.error(err.message);
    } finally {
      setLoading(false);
    }
  }, [requestId, token]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const confirmVisit = async (approve) => {
    setConfirming(true);
    try {
      const res = await fetch(`${API_BASE}/buy/request/${requestId}/confirm-visit-date`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ approve }),
      });
      if (!res.ok) throw new Error("Failed to confirm visit");
      toast.success(`Visit ${approve ? "accepted" : "rejected"} successfully`);
      loadData();
    } catch (err) {
      toast.error(err.message);
    } finally {
      setConfirming(false);
    }
  };

  if (loading)
    return (
      <div className="text-center mt-5">
        <Spinner animation="border" />
      </div>
    );

  if (error) return <Container className="mt-5"><Alert variant="danger">{error}</Alert></Container>;
  if (!data) return null;

  const showVisitDecision = data.status === "VISIT_DATE_PROPOSED";

  return (
    <>
      <Navbar className="px-4 py-3">
        <span className="navbar-brand">Owner Appointment</span>
      </Navbar>

      <Container className="mt-5">
        <div className="glass p-4 mb-4">
          <h5>{data.propertyCode}</h5>
          <p><b>Location:</b> {data.location}</p>
          <Badge bg="info">{data.status}</Badge>
        </div>

        {data.visitDate && (
          <div className="glass p-4 mb-4">
            <p><b>Visit Date:</b> {data.visitDate}</p>
            <p><b>Time Slot:</b> {data.timeSlot}</p>
          </div>
        )}

        {showVisitDecision && (
          <div className="glass p-4">
            <div className="d-flex gap-3">
              <Button
                className="btn-success w-50"
                disabled={confirming}
                onClick={() => confirmVisit(true)}
              >
                {confirming ? <Spinner size="sm" /> : "Accept Visit"}
              </Button>
              <Button
                className="btn-danger w-50"
                disabled={confirming}
                onClick={() => confirmVisit(false)}
              >
                {confirming ? <Spinner size="sm" /> : "Reject Visit"}
              </Button>
            </div>
          </div>
        )}
      </Container>
    </>
  );
};

export default OwnerAppointment;