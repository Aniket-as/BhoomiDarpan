import { useEffect, useState } from "react";
import {
  Container,
  Navbar,
  Button,
  Badge,
  Form,
  Row,
  Col,
  Spinner,
  Alert,
} from "react-bootstrap";
import { useParams, useNavigate } from "react-router-dom";
import { toast } from "react-hot-toast";

const API_BASE = "https://bhoomidarpan-5.onrender.com/api";

const RegistrationVerification = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const [data, setData] = useState(null);
  const [remarks, setRemarks] = useState("");

  const [saleDeed, setSaleDeed] = useState(null);
  const [buyerPhoto, setBuyerPhoto] = useState(null);
  const [sellerPhoto, setSellerPhoto] = useState(null);
  const [buyerFingerprint, setBuyerFingerprint] = useState(null);
  const [sellerFingerprint, setSellerFingerprint] = useState(null);

  const [ocrResult, setOcrResult] = useState(null);
  const [ocrLoading, setOcrLoading] = useState(false);

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  /* ================= LOAD REGISTRATION ================= */

  useEffect(() => {
    fetch(`${API_BASE}/registration/todays-appointments`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(async (res) => {
        if (!res.ok) throw new Error("Failed to load registration data");

        const list = await res.json();
        const reg = list.find((r) => r.id === Number(id));

        if (!reg) throw new Error("Registration not found");

        setData(reg);
      })
      .catch((err) => {
        setError(err.message);
        toast.error(err.message);
      })
      .finally(() => setLoading(false));
  }, [id, token]);

  /* ================= FILE VALIDATION ================= */

  const validateFile = (file, type) => {
    if (!file) return true;

    if (file.size > 5 * 1024 * 1024) {
      toast.error(`${type} must be less than 5MB`);
      return false;
    }

    return true;
  };

  /* ================= OCR CHECK ================= */


  /* ================= VERIFY REGISTRATION ================= */

  const verify = async (approve) => {
    if (approve) {
      if (!saleDeed) {
        toast.error("Sale deed is required");
        return;
      }

      if (!buyerPhoto || !sellerPhoto) {
        toast.error("Buyer and Seller photo required");
        return;
      }

      if (!buyerFingerprint || !sellerFingerprint) {
        toast.error("Buyer and Seller fingerprint required");
        return;
      }

      if (!ocrResult) {
        toast.error("Run OCR check before approval");
        return;
      }

      const ocrBuyer = ocrResult?.extractedFields?.buyerName;
      const ocrSeller = ocrResult?.extractedFields?.sellerName;

      const buyerMatch =
        ocrBuyer?.trim().toLowerCase() ===
        data.buyerName?.trim().toLowerCase();

      const sellerMatch =
        ocrSeller?.trim().toLowerCase() ===
        data.sellerName?.trim().toLowerCase();

      if (!buyerMatch || !sellerMatch) {
        toast.error("Cannot approve due to OCR mismatch");
        return;
      }
    }

    setSubmitting(true);
    setError(null);

    try {
      const formData = new FormData();

      formData.append("registrationId", String(id));
      formData.append("approve", approve ? "true" : "false");
      formData.append("remarks", remarks || "");

      if (saleDeed) formData.append("saleDeed", saleDeed);
      if (buyerPhoto) formData.append("buyerPhoto", buyerPhoto);
      if (sellerPhoto) formData.append("sellerPhoto", sellerPhoto);
      if (buyerFingerprint)
        formData.append("buyerFingerprint", buyerFingerprint);
      if (sellerFingerprint)
        formData.append("sellerFingerprint", sellerFingerprint);

      const response = await fetch(`${API_BASE}/registration/verify`, {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
        body: formData,
      });

      const text = await response.text();

      if (!response.ok) throw new Error(text);

      toast.success(text);
      navigate("/sub-registrar/dashboard");
    } catch (err) {
      setError(err.message);
      toast.error(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  /* ================= LOADING ================= */

  if (loading) {
    return (
      <div className="text-center mt-5">
        <Spinner />
      </div>
    );
  }

  if (!data) return <div className="m-5">Registration not found</div>;

  /* ================= OCR MATCH ================= */

  const ocrBuyer = ocrResult?.extractedFields?.buyerName;
  const ocrSeller = ocrResult?.extractedFields?.sellerName;

  const isMatch =
    ocrBuyer?.trim().toLowerCase() ===
      data.buyerName?.trim().toLowerCase() &&
    ocrSeller?.trim().toLowerCase() ===
      data.sellerName?.trim().toLowerCase();

  /* ================= UI ================= */

  return (
    <>
      <Navbar className="px-4 py-3 shadow-sm bg-white">
        <span className="navbar-brand fw-semibold">
          🏛️ Registration Verification (AI + Biometric Enabled)
        </span>
      </Navbar>

      <Container className="mt-5 mb-5">

        {error && <Alert variant="danger">{error}</Alert>}

        {/* Property Info */}

        <div className="p-4 mb-4 rounded-4 shadow-sm bg-white">
          <h6>🏠 Property Information</h6>
          <p>
            <b>Property Code:</b> {data.propertyCode}
          </p>
          <p>
            <b>Status:</b>{" "}
            <Badge bg="warning">{data.status}</Badge>
          </p>
        </div>

        {/* Buyer Seller */}

        <Row className="g-4 mb-4">
          <Col md={6}>
            <div className="p-4 rounded-4 shadow-sm bg-white">
              <h6>🧍 Buyer</h6>
              <p>
                <b>Name:</b> {data.buyerName}
              </p>
            </div>
          </Col>

          <Col md={6}>
            <div className="p-4 rounded-4 shadow-sm bg-white">
              <h6>🧍 Seller</h6>
              <p>
                <b>Name:</b> {data.sellerName}
              </p>
              <p>
                <b>Aadhaar:</b> {data.sellerAadhaar}
              </p>
              <p>
                <b>PAN:</b> {data.sellerPan}
              </p>
            </div>
          </Col>
        </Row>

        {/* Upload Section */}

        <div className="p-4 mb-4 rounded-4 shadow-sm bg-white">
          <h6>📂 Document & Biometric Upload</h6>

          <Form.Label>Sale Deed (PDF)</Form.Label>

          <Form.Control
            type="file"
            accept="application/pdf"
            onChange={(e) => {
              const file = e.target.files[0];
              if (validateFile(file, "Sale Deed")) {
                setSaleDeed(file);
                setOcrResult(null);
              }
            }}
          />

          <Button
            className="mt-3"
            variant="info"
            onClick={runOCRCheck}
            disabled={ocrLoading}
          >
            {ocrLoading ? <Spinner size="sm" /> : "🔍 Run OCR Check"}
          </Button>

          {ocrResult && (
            <div className="mt-4 p-3 bg-light rounded">
              <h6>🔎 OCR Result</h6>

              <p>
                <b>OCR Buyer:</b> {ocrBuyer || "Not Found"}
              </p>

              <p>
                <b>OCR Seller:</b> {ocrSeller || "Not Found"}
              </p>

              {isMatch ? (
                <Alert variant="success">✅ Names Matched</Alert>
              ) : (
                <Alert variant="danger">❌ Names Mismatch</Alert>
              )}
            </div>
          )}

          <hr />

          <Row className="g-3">

            <Col md={6}>
              <Form.Label>Buyer Photo</Form.Label>
              <Form.Control
                type="file"
                accept="image/*"
                onChange={(e) =>
                  setBuyerPhoto(e.target.files[0])
                }
              />
            </Col>

            <Col md={6}>
              <Form.Label>Seller Photo</Form.Label>
              <Form.Control
                type="file"
                accept="image/*"
                onChange={(e) =>
                  setSellerPhoto(e.target.files[0])
                }
              />
            </Col>

            <Col md={6}>
              <Form.Label>Buyer Fingerprint</Form.Label>
              <Form.Control
                type="file"
                accept="image/*"
                onChange={(e) =>
                  setBuyerFingerprint(e.target.files[0])
                }
              />
            </Col>

            <Col md={6}>
              <Form.Label>Seller Fingerprint</Form.Label>
              <Form.Control
                type="file"
                accept="image/*"
                onChange={(e) =>
                  setSellerFingerprint(e.target.files[0])
                }
              />
            </Col>

          </Row>
        </div>

        {/* Remarks */}

        <div className="p-4 mb-4 rounded-4 shadow-sm bg-white">
          <Form.Label>Officer Remarks</Form.Label>

          <Form.Control
            as="textarea"
            rows={3}
            value={remarks}
            onChange={(e) => setRemarks(e.target.value)}
          />
        </div>

        {/* Buttons */}

        <div className="d-flex gap-3">

          <Button
            variant="success"
            disabled={submitting}
            onClick={() => verify(true)}
          >
            {submitting ? <Spinner size="sm" /> : "✔ Approve"}
          </Button>

          <Button
            variant="danger"
            disabled={submitting}
            onClick={() => verify(false)}
          >
            ❌ Reject
          </Button>

        </div>
      </Container>
    </>
  );
};

export default RegistrationVerification;
