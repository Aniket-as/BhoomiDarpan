import React, { useEffect, useState } from "react";
import {
  Container,
  Card,
  Badge,
  Row,
  Col,
  Alert,
  Button,
  Spinner,
} from "react-bootstrap";
import { useParams, useNavigate } from "react-router-dom";
import { toast } from "react-hot-toast";

const API_BASE = "https://bhoomidarpan-5.onrender.com/api";
const FILE_BASE = "https://bhoomidarpan-5.onrender.com/api/property-documents";

const PropertyDocuments = () => {
  const { propertyCode } = useParams();
  const navigate = useNavigate();

  const [property, setProperty] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token) {
      navigate("/login");
      return;
    }

    fetch(`${API_BASE}/properties/${propertyCode}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(async (res) => {
        if (!res.ok) throw new Error("Failed to fetch property details");
        return res.json();
      })
      .then(setProperty)
      .catch((err) => {
        setError(err.message);
        toast.error(err.message);
      })
      .finally(() => setLoading(false));
  }, [propertyCode, navigate]);

  if (loading)
    return (
      <Container className="mt-5 text-center">
        <Spinner animation="border" />
        <p className="mt-3">Loading property details...</p>
      </Container>
    );

  if (error)
    return (
      <Container className="mt-5">
        <Alert variant="danger">
          {error} <Button onClick={() => window.location.reload()}>Retry</Button>
        </Alert>
      </Container>
    );

  if (!property) return null;

  const isClear = property.status === "CLEAR";

  return (
    <Container className="mt-5">
      <Card className="p-4 shadow-lg mb-4">
        <h4>Property Code: {property.propertyCode}</h4>
        <Badge bg={isClear ? "success" : "warning"} className="mb-3">
          {property.status}
        </Badge>
        <Row>
          <Col md={6}>
            <p>
              <strong>Location:</strong> {property.location}
            </p>
            <p>
              <strong>Survey Number:</strong> {property.surveyNumber}
            </p>
            <p>
              <strong>Gat Number:</strong> {property.gatNumber || "N/A"}
            </p>
          </Col>
          <Col md={6}>
            <p>
              <strong>Land Type:</strong> {property.landType}
            </p>
            <p>
              <strong>Area:</strong> {property.area} sq.ft
            </p>
            <p>
              <strong>Created At:</strong> {property.createdAt}
            </p>
          </Col>
        </Row>
      </Card>

      <Card className="p-4 shadow-lg mb-4">
        <h5>Owners</h5>
        {property.owners?.length > 0 ? (
          property.owners.map((owner, index) => (
            <div key={index} className="border-bottom pb-2 mb-2">
              <p>
                <strong>Name:</strong> {owner.name}
              </p>
              <p>
                <strong>Ownership %:</strong> {owner.ownershipPercentage}%
              </p>
              <p>
                <strong>Type:</strong> {owner.ownershipType}
              </p>
            </div>
          ))
        ) : (
          <p className="text-muted">No owners found</p>
        )}
      </Card>

      <Card className="p-4 shadow-lg">
        <h5>Documents</h5>
        {property.documents?.length > 0 ? (
          property.documents.map((doc) => {
            if (!doc.fileUrl) return null;

            // Clean path
            let cleanPath = doc.fileUrl
              .replace("uploads/property-documents/", "")
              .replace("property-documents/", "");

            const fileUrl = `${FILE_BASE}/${encodeURIComponent(cleanPath)}`;

            return (
              <div key={doc.id} className="border-bottom pb-3 mb-3">
                <Row className="align-items-center">
                  <Col md={7}>
                    <p>
                      <strong>Type:</strong> {doc.documentType}
                    </p>
                    <Badge bg={doc.verified ? "success" : "secondary"}>
                      {doc.verified ? "Verified" : "Not Verified"}
                    </Badge>
                  </Col>
                  <Col md={5} className="text-end">
                    <a
                      href={fileUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="btn btn-sm btn-primary me-2"
                    >
                      Open
                    </a>
                    <a
                      href={fileUrl}
                      download
                      className="btn btn-sm btn-outline-dark"
                    >
                      Download
                    </a>
                  </Col>
                </Row>
              </div>
            );
          })
        ) : (
          <p className="text-muted">No documents uploaded for this property.</p>
        )}
      </Card>

      <Button className="mt-4" variant="secondary" onClick={() => navigate(-1)}>
        Back
      </Button>
    </Container>
  );
};

export default PropertyDocuments;