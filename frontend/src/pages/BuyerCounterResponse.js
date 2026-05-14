import React, { useEffect, useState } from "react";
import {
  Container,
  Navbar,
  Card,
  Button,
  Spinner,
  Alert,
  Form,
  Badge,
} from "react-bootstrap";
import { useParams, useNavigate } from "react-router-dom";
import { toast } from "react-hot-toast";

const API_BASE = "http://localhost:8080/api";

const BuyerCounterResponse = () => {
  const { requestId } = useParams();
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [newPrice, setNewPrice] = useState("");
  const [offerSubmitted, setOfferSubmitted] = useState(false);
  const [error, setError] = useState(null);

  // ================= LOAD REQUEST =================
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

        if (request.status === "PENDING") {
          setOfferSubmitted(true);
        }
      })
      .catch((err) => {
        setError(err.message);
        toast.error(err.message);
      })
      .finally(() => setLoading(false));
  }, [requestId, token, navigate]);

  // ================= ACCEPT COUNTER =================
  const acceptCounter = async () => {
    setSubmitting(true);

    try {
      const res = await fetch(
        `${API_BASE}/buy/counter/respond/${requestId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            accept: true,
          }),
        }
      );

      if (!res.ok) {
        const err = await res.text();
        throw new Error(err);
      }

      toast.success("Counter accepted!");

      navigate(`/buyer-appointment/${requestId}`);
    } catch (err) {
      toast.error(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  // ================= SEND NEW OFFER =================
  const sendNewOffer = async () => {
    if (!newPrice || Number(newPrice) <= 0) {
      toast.error("Enter valid offer amount");
      return;
    }

    if (Number(newPrice) === Number(data.offeredPrice)) {
      toast.error("Offer must be different from current price");
      return;
    }

    setSubmitting(true);

    try {
      const res = await fetch(
        `${API_BASE}/buy/counter/respond/${requestId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            accept: false,
            newPrice: parseFloat(newPrice),
          }),
        }
      );

      if (!res.ok) {
        const err = await res.text();
        throw new Error(err);
      }

      toast.success("New offer submitted!");

      // 🔥 Update UI instantly
      setData({
        ...data,
        offeredPrice: parseFloat(newPrice),
        status: "PENDING",
      });

      setOfferSubmitted(true);
      setNewPrice("");

    } catch (err) {
      toast.error(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  // ================= RENDER STATES =================
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

  if (!data)
    return (
      <Container className="mt-5">
        <h5>No Data Found</h5>
      </Container>
    );

  return (
    <>
      <Navbar className="px-4 py-3 bg-dark text-white">
        <span className="navbar-brand text-white">
          💬 Counter Offer Response
        </span>
      </Navbar>

      <Container className="mt-5">

        {/* REQUEST INFO */}
        <Card className="p-4 mb-4 shadow-sm">
          <h5>Property: {data.propertyCode}</h5>

          <p>
            <b>Owner Counter Price:</b> ₹{" "}
            {Number(data.offeredPrice).toLocaleString()}
          </p>

          <Badge bg="primary">{data.status}</Badge>
        </Card>

        {/* ACTIONS */}
        <Card className="p-4 shadow-sm">

          <h5>Choose Action</h5>

          {/* ACCEPT COUNTER */}
          {!offerSubmitted && data.status === "COUNTER_OFFERED" && (
            <div className="d-flex gap-3 mb-4">
              <Button
                variant="success"
                disabled={submitting}
                onClick={acceptCounter}
              >
                {submitting
                  ? <Spinner size="sm" />
                  : "Accept Counter & Schedule Visit"}
              </Button>
            </div>
          )}

          <hr />

          {/* NEW OFFER SECTION */}
          <h6>🔁 Propose New Price</h6>

          {!offerSubmitted && data.status === "COUNTER_OFFERED" ? (
            <>
              <Form.Group className="mb-3">
                <Form.Control
                  type="number"
                  placeholder="Enter your new offer"
                  value={newPrice}
                  onChange={(e) => setNewPrice(e.target.value)}
                />
              </Form.Group>

              <Button
                variant="warning"
                disabled={submitting}
                onClick={sendNewOffer}
              >
                {submitting
                  ? <Spinner size="sm" />
                  : "Send New Offer"}
              </Button>
            </>
          ) : (
            <Alert variant="info" className="mt-3">
              ✅ Your new offer has been submitted.
              Waiting for owner response.
            </Alert>
          )}

        </Card>

      </Container>
    </>
  );
};

export default BuyerCounterResponse;