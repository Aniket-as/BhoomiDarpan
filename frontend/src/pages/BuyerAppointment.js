import React, { useEffect, useState } from "react";
import {
  Container,
  Navbar,
  Button,
  Form,
  Badge,
  Card,
  Spinner,
  Alert,
} from "react-bootstrap";
import { useParams, useNavigate } from "react-router-dom";
import { toast } from "react-hot-toast";

const API_BASE = "https://bhoomidarpan-5.onrender.com/api";

const BuyerAppointment = () => {
  const { requestId } = useParams();
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const [data, setData] = useState(null);
  const [date, setDate] = useState("");
  const [slotInfo, setSlotInfo] = useState(null); // 🔥 NEW
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  // ================= LOAD DATA =================
  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }

    fetch(`${API_BASE}/buy/my-requests`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(async (res) => {
        if (!res.ok) {
          const err = await res.text();
          throw new Error(err);
        }
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
  }, [requestId, token, navigate]);

  // ================= CHECK SLOT AVAILABILITY =================
  const checkAvailability = async (selectedDate) => {
    try {
      const res = await fetch(
        `${API_BASE}/buy/appointment-availability?date=${selectedDate}`
      );

      const data = await res.json();
      setSlotInfo(data);

      if (data.remaining === 0) {
        toast.error("❌ Slots full for selected date");
      }

    } catch (err) {
      console.error(err);
    }
  };

  // ================= PROPOSE DATE =================
  const proposeDate = async () => {
    if (!date) {
      toast.error("Please select a visit date");
      return;
    }

    if (slotInfo && slotInfo.remaining === 0) {
      toast.error("Slots full! Choose another date");
      return;
    }

    setSubmitting(true);

    try {
      const res = await fetch(
        `${API_BASE}/buy/request/${requestId}/propose-visit-date`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            visitDate: date,
          }),
        }
      );

      if (!res.ok) {
        const err = await res.text();
        throw new Error(err);
      }

      toast.success("Visit date proposed successfully!");

      setData({
        ...data,
        status: "VISIT_DATE_PROPOSED",
      });

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

  const today = new Date().toISOString().split("T")[0];

  return (
    <>
      <Navbar className="px-4 py-3 bg-dark text-white">
        <span className="navbar-brand text-white">
          📅 Schedule Property Visit
        </span>
      </Navbar>

      <Container className="mt-5">

        {/* PROPERTY DETAILS */}
        <Card className="p-4 mb-4 shadow-sm">
          <h5>🏠 Property Details</h5>
          <hr />

          <p><b>Property Code:</b> {data.propertyCode}</p>
          <p><b>Location:</b> {data.location || "-"}</p>
          <p><b>Land Type:</b> {data.landType || "-"}</p>
          <p><b>Area:</b> {data.area || "-"} sq.ft</p>

          <p>
            <b>Your Offered Price:</b> ₹{" "}
            {Number(data.offeredPrice).toLocaleString()}
          </p>

          <Badge bg="primary">{data.status}</Badge>
        </Card>

        {/* OWNER DETAILS */}
        <Card className="p-4 mb-4 shadow-sm">
          <h5>👤 Owner Details</h5>
          <hr />

          <p><b>Name:</b> {data.ownerName || "Owner"}</p>
          <p><b>Email:</b> {data.ownerEmail || "-"}</p>
        </Card>

        {/* VISIT PROPOSAL */}
        {data.status === "APPROVED" && (
          <Card className="p-4 shadow-sm">
            <h5>📆 Select Visit Date</h5>
            <hr />

            <Form.Control
              type="date"
              min={today}
              value={date}
              onChange={(e) => {
                setDate(e.target.value);
                checkAvailability(e.target.value);
              }}
              className={`mb-3 ${
                slotInfo
                  ? slotInfo.remaining === 0
                    ? "border-danger"
                    : "border-success"
                  : ""
              }`}
            />

            {/* 🔥 SLOT STATUS */}
            {slotInfo && (
              <Alert variant={slotInfo.remaining === 0 ? "danger" : "success"}>
                {slotInfo.remaining === 0
                  ? "❌ Slots Full"
                  : `✅ ${slotInfo.remaining} slots available`}
              </Alert>
            )}

            <Button
              onClick={proposeDate}
              disabled={submitting || (slotInfo && slotInfo.remaining === 0)}
              className="btn-violet w-100"
            >
              {submitting ? <Spinner size="sm" /> : "Propose Visit"}
            </Button>
          </Card>
        )}

        {data.status === "VISIT_DATE_PROPOSED" && (
          <Alert variant="warning" className="mt-4">
            ⏳ Waiting for owner confirmation.
          </Alert>
        )}

        {data.status === "VISIT_DATE_CONFIRMED" && (
          <Alert variant="success" className="mt-4">
            ✅ Visit confirmed! Please check appointment details.
          </Alert>
        )}

      </Container>
    </>
  );
};

export default BuyerAppointment;
