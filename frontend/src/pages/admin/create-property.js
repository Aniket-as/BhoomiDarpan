import React, { useState, useEffect } from "react";
import { Container, Row, Col, Form, Button, Card, Alert } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const API_BASE = "http://localhost:8080/api";

const CreateProperty = () => {

  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    propertyCode: "",
    location: "",
    surveyNumber: "",
    gatNumber: "",
    landType: "AGRICULTURAL",
    area: "",
    ownerAadhaar: ""
  });

  const [sevenTwelve, setSevenTwelve] = useState(null);
  const [saleDeed, setSaleDeed] = useState(null);
  const [otherDocuments, setOtherDocuments] = useState([]);

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  // 🔐 Role Protection
  useEffect(() => {
    const token = localStorage.getItem("token");
    const user = JSON.parse(localStorage.getItem("user"));

    if (!token || !user ||
        (user.role !== "ADMIN" && user.role !== "SUB_REGISTRAR")) {
      navigate("/login");
    }
  }, [navigate]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const validatePDF = (file, label) => {
    if (file && file.type !== "application/pdf") {
      setError(`${label} must be a PDF file`);
      return false;
    }
    return true;
  };

  const handleSevenTwelveChange = (e) => {
    const file = e.target.files[0];
    if (!validatePDF(file, "7/12")) return;
    setSevenTwelve(file);
  };

  const handleSaleDeedChange = (e) => {
    const file = e.target.files[0];
    if (!validatePDF(file, "Sale Deed")) return;
    setSaleDeed(file);
  };

  const handleOtherFilesChange = (e) => {
    const files = Array.from(e.target.files);

    for (let file of files) {
      if (!validatePDF(file, "All documents")) return;
    }

    setOtherDocuments(files);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setMessage("");

    if (!sevenTwelve) {
      setError("7/12 document is mandatory");
      return;
    }

    if (!saleDeed) {
      setError("Sale Deed document is mandatory");
      return;
    }

    if (!/^\d{12}$/.test(formData.ownerAadhaar)) {
      setError("Owner Aadhaar must be 12 digits");
      return;
    }

    const token = localStorage.getItem("token");

    const data = new FormData();

    data.append("property", new Blob(
      [JSON.stringify(formData)],
      { type: "application/json" }
    ));

    data.append("sevenTwelve", sevenTwelve);
    data.append("saleDeed", saleDeed);

    otherDocuments.forEach(file => {
      data.append("otherDocuments", file);
    });

    try {
      const res = await fetch(`${API_BASE}/properties/admin-create`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`
        },
        body: data
      });

      if (!res.ok) {
        const err = await res.text();
        throw new Error(err);
      }

      const result = await res.text();
      setMessage(result);

      // Reset
      setFormData({
        propertyCode: "",
        location: "",
        surveyNumber: "",
        gatNumber: "",
        landType: "AGRICULTURAL",
        area: "",
        ownerAadhaar: ""
      });

      setSevenTwelve(null);
      setSaleDeed(null);
      setOtherDocuments([]);

    } catch (err) {
      setError(err.message || "Failed to create property");
    }
  };

  return (
    <Container className="mt-5">
      <Card className="p-4 shadow-lg">
        <h4 className="mb-4">🏠 Register Property (7/12 + Sale Deed Required)</h4>

        {message && <Alert variant="success">{message}</Alert>}
        {error && <Alert variant="danger">{error}</Alert>}

        <Form onSubmit={handleSubmit}>
          <Row className="g-3">

            <Col md={6}>
              <Form.Control
                name="propertyCode"
                placeholder="Property Code"
                value={formData.propertyCode}
                onChange={handleChange}
                required
              />
            </Col>

            <Col md={6}>
              <Form.Control
                name="ownerAadhaar"
                placeholder="Owner Aadhaar (12 digits)"
                value={formData.ownerAadhaar}
                onChange={handleChange}
                required
              />
            </Col>

            <Col md={12}>
              <Form.Control
                name="location"
                placeholder="Location"
                value={formData.location}
                onChange={handleChange}
                required
              />
            </Col>

            <Col md={6}>
              <Form.Control
                name="surveyNumber"
                placeholder="Survey Number"
                value={formData.surveyNumber}
                onChange={handleChange}
                required
              />
            </Col>

            <Col md={6}>
              <Form.Control
                name="gatNumber"
                placeholder="Gat Number"
                value={formData.gatNumber}
                onChange={handleChange}
              />
            </Col>

            <Col md={6}>
              <Form.Select
                name="landType"
                value={formData.landType}
                onChange={handleChange}
              >
                <option value="AGRICULTURAL">Agricultural</option>
                <option value="RESIDENTIAL">Residential</option>
                <option value="COMMERCIAL">Commercial</option>
                <option value="INDUSTRIAL">Industrial</option>
              </Form.Select>
            </Col>

            <Col md={6}>
              <Form.Control
                name="area"
                type="number"
                placeholder="Area (sqft)"
                value={formData.area}
                onChange={handleChange}
                required
              />
            </Col>

            {/* 7/12 */}
            <Col md={12}>
              <Form.Label>Upload 7/12 (Mandatory)</Form.Label>
              <Form.Control
                type="file"
                accept="application/pdf"
                onChange={handleSevenTwelveChange}
                required
              />
              {sevenTwelve && (
                <small className="text-success">
                  Selected: {sevenTwelve.name}
                </small>
              )}
            </Col>

            {/* Sale Deed */}
            <Col md={12}>
              <Form.Label>Upload Sale Deed (Mandatory)</Form.Label>
              <Form.Control
                type="file"
                accept="application/pdf"
                onChange={handleSaleDeedChange}
                required
              />
              {saleDeed && (
                <small className="text-success">
                  Selected: {saleDeed.name}
                </small>
              )}
            </Col>

            {/* Optional Documents */}
            <Col md={12}>
              <Form.Label>Upload Other Documents (Optional)</Form.Label>
              <Form.Control
                type="file"
                multiple
                accept="application/pdf"
                onChange={handleOtherFilesChange}
              />
              {otherDocuments.length > 0 && (
                <ul className="small mt-2">
                  {otherDocuments.map((file, index) => (
                    <li key={index}>{file.name}</li>
                  ))}
                </ul>
              )}
            </Col>

          </Row>

          <Button
            type="submit"
            className="mt-4 w-100"
            variant="success"
          >
            Create Property
          </Button>
        </Form>
      </Card>
    </Container>
  );
};

export default CreateProperty;
