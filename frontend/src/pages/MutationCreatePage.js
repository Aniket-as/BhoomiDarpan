import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Container,
  Button,
  Form,
  Spinner,
  Card,
  Row,
  Col,
  Alert,
  Badge
} from "react-bootstrap";
import { toast } from "react-hot-toast";

const API_BASE = "https://bhoomidarpan-5.onrender.com/api";

const MutationCreatePage = () => {
  const { mutationId } = useParams();
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const [mutation, setMutation] = useState(null);

  const [sevenTwelve, setSevenTwelve] = useState(null);
  const [eightA, setEightA] = useState(null);

  const [remarks, setRemarks] = useState("");

  const [documentsUploaded, setDocumentsUploaded] = useState(false);

  

  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(true);
  const [error, setError] = useState(null);

  /* ================= LOAD DETAILS ================= */

  const fetchDetails = async () => {
    try {
      const res = await fetch(
        `${API_BASE}/mutation/details/${mutationId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (!res.ok) throw new Error("Failed to load mutation details");

      const data = await res.json();
      setMutation(data);

      if (data.sevenTwelvePath && data.eightAPath) {
        setDocumentsUploaded(true);
      }

    } catch (err) {
      setError(err.message);
      toast.error(err.message);
    } finally {
      setLoadingData(false);
    }
  };

  useEffect(() => {
    if (!mutationId) return;
    fetchDetails();
  }, [mutationId]);

  /* ================= OCR CHECK ================= */

  

  /* ================= PROCESS DOCUMENTS ================= */

  const handleProcess = async (e) => {
    e.preventDefault();

    if (!sevenTwelve || !eightA) {
      toast.error("Both documents are required");
      return;
    }

 

    try {
      setLoading(true);

      const formData = new FormData();
      formData.append("sevenTwelve", sevenTwelve);
      formData.append("eightA", eightA);
      formData.append("remarks", remarks);

      const res = await fetch(
        `${API_BASE}/mutation/process/${mutationId}`,
        {
          method: "POST",
          headers: { Authorization: `Bearer ${token}` },
          body: formData,
        }
      );

      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || "Failed to process mutation");
      }

      toast.success("Documents uploaded successfully ✅");

      setDocumentsUploaded(true);
      fetchDetails();

    } catch (err) {
      toast.error(err.message);
    } finally {
      setLoading(false);
    }
  };

  /* ================= APPROVE ================= */

  const handleApprove = async () => {

    if (!documentsUploaded) {
      toast.error("Upload documents first");
      return;
    }

    try {
      setLoading(true);

      const res = await fetch(
        `${API_BASE}/mutation/approve/${mutationId}`,
        {
          method: "POST",
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || "Approval failed");
      }

      toast.success("Mutation approved successfully 🎉");

      navigate("/tehsil/dashboard");

    } catch (err) {
      toast.error(err.message);
    } finally {
      setLoading(false);
    }
  };

  /* ================= UI STATES ================= */

  if (loadingData) {
    return (
      <Container className="mt-5 text-center">
        <Spinner />
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="mt-5">
        <Alert variant="danger">{error}</Alert>
      </Container>
    );
  }

  /* ================= MAIN UI ================= */

  return (
    <Container className="mt-5">

      <h4 className="mb-4">
        Process & Approve Mutation #{mutationId}
      </h4>

      {mutation && (
        <Card className="mb-4 shadow-sm">
          <Card.Body>
            <Row>
              <Col md={6}>
                <h6>🏠 Property Details</h6>
                <p><strong>Property Code:</strong> {mutation.propertyCode}</p>
              </Col>

              <Col md={6}>
                <h6>🧍 Buyer</h6>
                <p><strong>Name:</strong> {mutation.buyerName}</p>
              </Col>

              <Col md={6}>
                <h6>🧍 Seller</h6>
                <p><strong>Name:</strong> {mutation.sellerName}</p>
                <p><strong>Aadhaar:</strong> {mutation.sellerAadhaar}</p>
                <p><strong>PAN:</strong> {mutation.sellerPan}</p>
              </Col>

              <Col md={6}>
                <h6>Status</h6>
                <Badge bg="warning">{mutation.status}</Badge>
              </Col>
            </Row>
          </Card.Body>
        </Card>
      )}

      <Card className="shadow-sm">
        <Card.Body>

          {!documentsUploaded && (
            <Form onSubmit={handleProcess}>

              <Form.Group className="mb-3">
                <Form.Label>Upload Updated 7/12 (PDF)</Form.Label>
                <Form.Control
                  type="file"
                  accept=".pdf"
                 onChange={(e) => {
  setSevenTwelve(e.target.files[0]);
}}
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Upload Updated 8A (PDF)</Form.Label>
                <Form.Control
                  type="file"
                  accept=".pdf"
                  onChange={(e) => {
  setEightA(e.target.files[0]);
}}
                />
              </Form.Group>

              <Button
                className="mb-3"
                variant="info"
                onClick={runOCRCheck}
                disabled={ocrLoading}
              >
                {ocrLoading ? <Spinner size="sm" /> : "🔍 Run OCR Check"}
              </Button>

              {ocrResult && (
                <Alert variant={ocrResult.isMatch ? "success" : "danger"}>
                  <div><strong>7/12 Owner:</strong> {ocrResult.owner1 || "Not Found"}</div>
                  <div><strong>8A Owner:</strong> {ocrResult.owner2 || "Not Found"}</div>
                  {ocrResult.isMatch ? "✅ Names Match" : "❌ Owner Mismatch"}
                </Alert>
              )}

              <Form.Group className="mb-3">
                <Form.Label>Remarks</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={3}
                  value={remarks}
                  onChange={(e) => setRemarks(e.target.value)}
                />
              </Form.Group>

              <Button type="submit" disabled={loading}>
                {loading ? <Spinner size="sm" /> : "Upload Documents"}
              </Button>

              <Button
                variant="secondary"
                className="ms-3"
                onClick={() => navigate("/tehsil/dashboard")}
              >
                Cancel
              </Button>
            </Form>
          )}

          {documentsUploaded && mutation.status !== "APPROVED" && (
            <>
              <Alert variant="success">
                Documents uploaded successfully. Ready for approval.
              </Alert>

              <Button
                variant="success"
                onClick={handleApprove}
                disabled={loading}
              >
                {loading ? <Spinner size="sm" /> : "Approve Mutation"}
              </Button>
            </>
          )}

          {mutation.status === "APPROVED" && (
            <Alert variant="success">
              This mutation has already been approved.
            </Alert>
          )}

        </Card.Body>
      </Card>

    </Container>
  );
};

export default MutationCreatePage;
