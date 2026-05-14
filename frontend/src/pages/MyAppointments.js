import React, { useEffect, useState } from "react";
import {
  Container,
  Navbar,
  Table,
  Badge,
  Button,
  Spinner
} from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const API_BASE = "http://localhost:8080/api";

const MyAppointments = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }

    fetch(`${API_BASE}/buy/my-appointments`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => {
        if (!res.ok) throw new Error("Unauthorized");
        return res.json();
      })
      .then(data => {
        // 🔥 FILTER ONLY VISIT STAGE REQUESTS
        const visitRequests = data.filter(a =>
          a.status?.includes("VISIT")
        );

        setAppointments(visitRequests);
        setLoading(false);
      })
      .catch(() => {
        localStorage.removeItem("token");
        navigate("/login");
      });

  }, [navigate, token]);

  const handleView = (appointment) => {
    if (appointment.role === "BUYER") {
      navigate(`/buyer-appointment/${appointment.requestId}`);
    } else {
      navigate(`/owner-appointment/${appointment.requestId}`);
    }
  };

  const getBadgeColor = (status) => {
    switch (status) {
      case "VISIT_DATE_PROPOSED":
        return "warning";
      case "VISIT_DATE_CONFIRMED":
        return "success";
      case "VISIT_RESCHEDULE_PROPOSED":
        return "info";
      case "VISIT_CANCELLED":
        return "danger";
      default:
        return "secondary";
    }
  };

  return (
    <>
      <Navbar className="px-4 py-3">
        <span className="navbar-brand">🏛️ BhoomiDarpan</span>
      </Navbar>

      <Container className="mt-5">
        <div className="glass p-4 mb-4">
          <h4>📅 My Visit Appointments</h4>
          <p className="text-muted mb-0">
            View and manage scheduled property visits
          </p>
        </div>

        <div className="glass p-4">

          {loading ? (
            <div className="text-center">
              <Spinner animation="border" />
              <p className="mt-2">Loading appointments...</p>
            </div>
          ) : appointments.length === 0 ? (
            <p className="text-muted text-center">
              No visit appointments found.
            </p>
          ) : (
            <Table hover responsive>
              <thead>
                <tr>
                  <th>Property</th>
                  <th>Location</th>
                  <th>Date</th>
                  <th>Slot</th>
                  <th>Status</th>
                  <th>Role</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {appointments.map(a => (
                  <tr key={a.requestId}>
                    <td>{a.propertyCode}</td>
                    <td>{a.location}</td>
                    <td>{a.visitDate || "-"}</td>
                    <td>{a.timeSlot || "-"}</td>

                    <td>
                      <Badge bg={getBadgeColor(a.status)}>
                        {a.status}
                      </Badge>
                    </td>

                    <td>
                      <Badge bg={a.role === "BUYER" ? "primary" : "dark"}>
                        {a.role}
                      </Badge>
                    </td>

                    <td>
                      <Button
                        size="sm"
                        className="btn-violet"
                        onClick={() => handleView(a)}
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

export default MyAppointments;
