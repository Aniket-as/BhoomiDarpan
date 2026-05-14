import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
Container,
Card,
Row,
Col,
Badge,
Spinner,
Alert,
Form,
} from "react-bootstrap";

const API_BASE = "http://localhost:8080/api";
const CERT_BASE = "http://localhost:8080/api/certificates";

const MyPropertyDetails = () => {
const { propertyCode } = useParams();
const navigate = useNavigate();

const [property, setProperty] = useState(null);
const [loading, setLoading] = useState(true);
const [error, setError] = useState("");
const [updating, setUpdating] = useState(false);
const [isForSale, setIsForSale] = useState(false);

// ✅ SAME FUNCTION AS PropertyDetails
const getCorrectFileUrl = (url) => {
if (!url) return "";
return url.replace("/image/upload/", "/raw/upload/");
};

useEffect(() => {
const token = localStorage.getItem("token");


if (!token) {
  navigate("/login");
  return;
}

fetch(`${API_BASE}/properties/${propertyCode}`, {
  headers: {
    Authorization: `Bearer ${token}`,
  },
})
  .then((res) => {
    if (!res.ok) throw new Error("Unauthorized");
    return res.json();
  })
  .then((data) => {
    setProperty(data);
    setIsForSale(data.availableForSale);
    setLoading(false);
  })
  .catch(() => {
    setError("Failed to load property details");
    setLoading(false);
  });


}, [propertyCode, navigate]);

// QR Download
const handleDownloadQR = async () => {
try {
const token = localStorage.getItem("token");


  const res = await fetch(
    `${CERT_BASE}/qr/${property.propertyCode}`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  if (!res.ok) throw new Error();

  const blob = await res.blob();
  const url = window.URL.createObjectURL(blob);

  const a = document.createElement("a");
  a.href = url;
  a.download = `${property.propertyCode}-qr.png`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  window.URL.revokeObjectURL(url);
} catch {
  alert("Failed to download QR code");
}


};

// Certificate Download
const handleDownloadCertificate = async () => {
try {
const token = localStorage.getItem("token");


  const res = await fetch(
    `${CERT_BASE}/${property.propertyCode}/download`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  if (!res.ok) throw new Error();

  const blob = await res.blob();
  const url = window.URL.createObjectURL(blob);

  const a = document.createElement("a");
  a.href = url;
  a.download = `${property.propertyCode}-certificate.pdf`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  window.URL.revokeObjectURL(url);
} catch {
  alert("Failed to download certificate");
}


};

// Toggle Sale
const handleToggleSale = async () => {
const token = localStorage.getItem("token");
setUpdating(true);


try {
  const res = await fetch(
    `${API_BASE}/properties/${propertyCode}/toggle-sale`,
    {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  if (!res.ok) throw new Error();

  setIsForSale(!isForSale);
} catch {
  alert("Failed to update sale status");
}

setUpdating(false);


};

if (loading) {
return ( <Container className="mt-5 text-center"> <Spinner animation="border" /> </Container>
);
}

if (error) {
return ( <Container className="mt-5"> <Alert variant="danger">{error}</Alert> </Container>
);
}

const isClear = property.status === "CLEAR";

return ( <Container className="mt-5"> <Card className="p-4 shadow-lg border-0 rounded-4">


    <h4 className="mb-3">Property Details</h4>

    <Row>
      <Col md={6}>
        <p><strong>Property Code:</strong> {property.propertyCode}</p>
        <p><strong>Location:</strong> {property.location}</p>
        <p><strong>Survey Number:</strong> {property.surveyNumber}</p>
        <p><strong>Gat Number:</strong> {property.gatNumber}</p>
        <p><strong>Land Type:</strong> {property.landType}</p>
        <p><strong>Area:</strong> {property.area} sq.ft</p>

        <p>
          <strong>Status:</strong>{" "}
          <Badge bg={isClear ? "success" : "warning"}>
            {property.status}
          </Badge>
        </p>

        <div className="mt-4">
          <Form>
            <Form.Check
              type="switch"
              label={isForSale ? "Property Listed For Sale" : "Mark Property For Sale"}
              checked={isForSale}
              onChange={handleToggleSale}
              disabled={updating}
            />
          </Form>
        </div>
      </Col>
    </Row>

    {/* ================= DOCUMENTS ================= */}
    <hr />
    <h5>📄 Property Documents</h5>

    <Row className="mt-3">
      {property.documents && property.documents.length > 0 ? (
        property.documents.map((doc) => (
          <Col md={4} key={doc.id} className="mb-3">
            <Card className="p-3 shadow-sm border-0 rounded-3">

              <h6>{doc.documentType}</h6>

              <Badge bg={doc.verified ? "success" : "warning"}>
                {doc.verified ? "Verified" : "Not Verified"}
              </Badge>

              <div className="mt-3 d-flex flex-column gap-2">

                <a
                  href={getCorrectFileUrl(doc.fileUrl)}
                  target="_blank"
                  rel="noreferrer"
                  className="btn btn-outline-primary btn-sm"
                >
                  👁 View Document
                </a>

                <a
                  href={getCorrectFileUrl(doc.fileUrl)}
                  download
                  className="btn btn-dark btn-sm"
                >
                  ⬇ Download
                </a>

              </div>
            </Card>
          </Col>
        ))
      ) : (
        <p>No documents available</p>
      )}
    </Row>

    {/* ================= CERTIFICATE ================= */}
    <hr />
    <h5>Verification & Certificate</h5>

    <div className="d-flex align-items-center gap-4 flex-wrap">

      <img
        src={`${CERT_BASE}/qr/${property.propertyCode}`}
        alt="QR"
        width="150"
      />

      <div className="d-flex flex-column gap-2">
        <button className="btn btn-dark" onClick={handleDownloadQR}>
          📱 Download QR Code
        </button>

        <button className="btn btn-success" onClick={handleDownloadCertificate}>
          📄 Download Certificate
        </button>

        <a
          href={`${CERT_BASE}/verify/${property.propertyCode}`}
          target="_blank"
          rel="noreferrer"
          className="btn btn-outline-primary"
        >
          🔍 Verify Property
        </a>
      </div>
    </div>

  </Card>
</Container>


);
};

export default MyPropertyDetails;
