import { useEffect, useState } from "react";
import {
  Container,
  Card,
  Form,
  Button,
  Row,
  Col,
  Spinner,
  Alert
} from "react-bootstrap";
import { useParams, useNavigate } from "react-router-dom";
import { toast } from "react-hot-toast";

const API_BASE = "http://localhost:8080/api";

const VerifyGift = () => {

  const { id } = useParams();
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [gift, setGift] = useState(null);

  const [form, setForm] = useState({
    giftDeed: null,
    buyerPhoto: null,
    donorPhoto: null,
    buyerFingerprint: null,
    donorFingerprint: null,
    remarks: ""
  });

  const headers = {
    Authorization: `Bearer ${token}`
  };

  /* ================= FETCH DATA ================= */

  useEffect(() => {
    fetchGift();
  }, []);

  const fetchGift = async () => {
    try {
      const res = await fetch(`${API_BASE}/gift-deed/${id}`, { headers });

      if (!res.ok) throw new Error("Failed to load data");

      const data = await res.json();
      console.log("Gift Data:", data); // 🔥 DEBUG
      setGift(data);

    } catch (err) {
      setError(err.message);
      toast.error(err.message);
    } finally {
      setLoading(false);
    }
  };

  /* ================= HANDLE INPUT ================= */

  const handleChange = (e) => {
    const { name, files, value } = e.target;

    setForm(prev => ({
      ...prev,
      [name]: files ? files[0] : value
    }));
  };

  /* ================= SUBMIT ================= */

  const handleSubmit = async (approve) => {
    try {

      if (approve && !form.giftDeed) {
        return toast.error("Please upload gift deed document");
      }

      const data = new FormData();

      data.append("approve", approve);
      data.append("remarks", form.remarks || "");

      if (approve) {
        if (form.giftDeed) data.append("giftDeed", form.giftDeed);
        if (form.buyerPhoto) data.append("buyerPhoto", form.buyerPhoto);
        if (form.donorPhoto) data.append("donorPhoto", form.donorPhoto);
        if (form.buyerFingerprint) data.append("buyerFingerprint", form.buyerFingerprint);
        if (form.donorFingerprint) data.append("donorFingerprint", form.donorFingerprint);
      }

      const res = await fetch(
        `${API_BASE}/registration/verify-gift/${id}`,
        {
          method: "PUT",
          headers,
          body: data
        }
      );

      if (!res.ok) throw new Error("Verification failed");

      toast.success(approve ? "Gift Approved ✅" : "Gift Rejected ❌");

      navigate("/sub-registrar-dashboard");

    } catch (err) {
      toast.error(err.message);
    }
  };

  /* ================= UI ================= */

  return (
    <Container className="mt-4">

      {loading && <Spinner />}
      {error && <Alert variant="danger">{error}</Alert>}

      {!loading && gift && (
        <Card className="p-4 glass-card">

          <h4 className="mb-3">🕊️ Verify Gift Deed</h4>

          {/* 🔥 FIXED SAFE DATA */}
          <Row className="mb-3">
            <Col>
              <strong>Property:</strong>{" "}
              {gift?.propertyCode || gift?.property?.propertyCode || "N/A"}
            </Col>

            <Col>
              <strong>Donor:</strong>{" "}
              {gift?.donorName || "N/A"}
            </Col>

            <Col>
              <strong>Child:</strong>{" "}
              {gift?.childName || "N/A"}
            </Col>
          </Row>

          {/* FILE UPLOAD */}
          <Form>

            <Form.Group className="mb-3">
              <Form.Label>Gift Deed (PDF)</Form.Label>
              <Form.Control
                type="file"
                name="giftDeed"
                onChange={handleChange}
              />
            </Form.Group>

            <Row>
              <Col>
                <Form.Label>Buyer Photo</Form.Label>
                <Form.Control
                  type="file"
                  name="buyerPhoto"
                  onChange={handleChange}
                />
              </Col>

              <Col>
                <Form.Label>Donor Photo</Form.Label>
                <Form.Control
                  type="file"
                  name="donorPhoto"
                  onChange={handleChange}
                />
              </Col>
            </Row>

            <Row className="mt-3">
              <Col>
                <Form.Label>Buyer Fingerprint</Form.Label>
                <Form.Control
                  type="file"
                  name="buyerFingerprint"
                  onChange={handleChange}
                />
              </Col>

              <Col>
                <Form.Label>Donor Fingerprint</Form.Label>
                <Form.Control
                  type="file"
                  name="donorFingerprint"
                  onChange={handleChange}
                />
              </Col>
            </Row>

            <Form.Group className="mt-3">
              <Form.Label>Remarks</Form.Label>
              <Form.Control
                as="textarea"
                name="remarks"
                onChange={handleChange}
              />
            </Form.Group>

            {/* ACTION BUTTONS */}
            <div className="d-flex gap-3 mt-4">

              <Button
                variant="success"
                onClick={() => handleSubmit(true)}
              >
                ✅ Approve & Register
              </Button>

              <Button
                variant="danger"
                onClick={() => handleSubmit(false)}
              >
                ❌ Reject
              </Button>

            </div>

          </Form>

        </Card>
      )}
    </Container>
  );
};

export default VerifyGift;