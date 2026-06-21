import React, { useEffect, useState } from "react";
import {
  Container,
  Navbar,
  Button,
  Badge,
  Spinner,
  Alert,
  Card,
  Row,
  Col,
  Form,
} from "react-bootstrap";
import { useParams } from "react-router-dom";
import { toast } from "react-hot-toast";

const API_BASE = "https://bhoomidarpan-5.onrender.com/api";

const OwnerRequestDetails = () => {
  const { requestId } = useParams();
  const token = localStorage.getItem("token");

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [counterPrice, setCounterPrice] = useState("");
  const [counterSent, setCounterSent] = useState(false);

  const loadData = async () => {
    try {
      setError(null);

      const res = await fetch(`${API_BASE}/buy/owner-requests`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) throw new Error("Failed to load requests");

      const requests = await res.json();
      const request = requests.find((r) => r.id === Number(requestId));

      if (!request) throw new Error("Request not found");

      setData(request);

      if (request.status === "COUNTER_OFFERED") {
  setCounterSent(true);
} else {
  setCounterSent(false);
}
    } catch (err) {
      setError(err.message);
      toast.error(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
    // eslint-disable-next-line
  }, []);

  const approve = async (approve) => {
    setSubmitting(true);
    try {
      const res = await fetch(
        `${API_BASE}/buy/consent/${data.myConsentId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ approve }),
        }
      );

      if (!res.ok) throw new Error("Action failed");

      toast.success(`Request ${approve ? "approved" : "rejected"}`);
      loadData();
    } catch (err) {
      toast.error(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  const sendCounterOffer = async () => {
    if (!counterPrice || counterPrice <= data.offeredPrice) {
      toast.error("Counter offer must be higher than offered price");
      return;
    }

    setSubmitting(true);

    try {
      const res = await fetch(
        `${API_BASE}/buy/counter/${data.id}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            newPrice: parseFloat(counterPrice),
          }),
        }
      );

      if (!res.ok) throw new Error("Counter offer failed");

      toast.success("Counter offer sent to buyer");
      setCounterPrice("");
      setCounterSent(true);
      loadData();
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

  if (!data)
    return (
      <Container className="mt-5">
        <h5>No Request Found</h5>
      </Container>
    );

  return (
    <>
      <Navbar className="px-4 py-3 bg-dark text-white">
        <span className="navbar-brand text-white">
          🏠 Owner Negotiation Panel
        </span>
      </Navbar>

      <Container className="mt-5">

        {/* PROPERTY DETAILS */}
        <Card className="p-4 mb-4 shadow-sm">
          <h5>Property Details</h5>
          <Row>
            <Col md={6}>
              <p><b>Property Code:</b> {data.propertyCode}</p>
              <p><b>Location:</b> {data.location}</p>
              <p><b>Status:</b> {data.propertyStatus}</p>
            </Col>
            <Col md={6}>
              <p><b>Area:</b> {data.area} sq.ft</p>
              <p><b>Land Type:</b> {data.landType}</p>
              <p><b>Listed For Sale:</b> {data.availableForSale ? "Yes" : "No"}</p>
            </Col>
          </Row>
        </Card>

        {/* BUYER DETAILS */}
        <Card className="p-4 mb-4 shadow-sm">
          <h5>Buyer Details</h5>
          <p><b>Name:</b> {data.buyerName}</p>
          <p><b>Email:</b> {data.buyerEmail}</p>
          <p><b>Offered Price:</b> ₹ {Number(data.offeredPrice).toLocaleString()}</p>

          <Badge bg="warning" className="mt-2">
            {data.status}
          </Badge>
        </Card>

        {/* ACTIONS */}
        {data.currentUserRole === "OWNER" &&
          data.myConsentStatus === "PENDING" && (
            <Card className="p-4 shadow-sm">

              <h5>Take Action</h5>

              <div className="d-flex gap-3 mb-4">
                <Button
                  variant="success"
                  disabled={submitting}
                  onClick={() => approve(true)}
                >
                  {submitting ? <Spinner size="sm" /> : "Approve"}
                </Button>

                <Button
                  variant="danger"
                  disabled={submitting}
                  onClick={() => approve(false)}
                >
                  {submitting ? <Spinner size="sm" /> : "Reject"}
                </Button>
              </div>

              <hr />

              <h6>💰 Counter Offer</h6>

              {!counterSent ? (
                <>
                  <Form.Group className="mb-3">
                    <Form.Control
                      type="number"
                      placeholder="Enter new price"
                      value={counterPrice}
                      onChange={(e) => setCounterPrice(e.target.value)}
                    />
                  </Form.Group>

                  <Button
                    variant="primary"
                    disabled={submitting}
                    onClick={sendCounterOffer}
                  >
                    {submitting ? <Spinner size="sm" /> : "Send Counter Offer"}
                  </Button>
                </>
              ) : (
                <Alert variant="info" className="mt-3">
                  ✅ Counter offer submitted. Waiting for buyer response.
                </Alert>
              )}

            </Card>
          )}

        {data.myConsentStatus === "APPROVED" && (
          <Alert variant="success" className="mt-3">
            You have approved this request.
          </Alert>
        )}

        {data.myConsentStatus === "REJECTED" && (
          <Alert variant="danger" className="mt-3">
            You have rejected this request.
          </Alert>
        )}
      </Container>
    </>
  );
};

export default OwnerRequestDetails;