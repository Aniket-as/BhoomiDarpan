import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
Container,
Button,
Spinner,
Alert,
Badge,
Form,
Card,
Row,
Col,
ProgressBar,
} from "react-bootstrap";
import { toast } from "react-hot-toast";

const API_BASE = "https://bhoomidarpan-5.onrender.com/api";

function PropertyDetails() {
const { propertyCode } = useParams();
const navigate = useNavigate();

const [property, setProperty] = useState(null);
const [aiData, setAiData] = useState(null);
const [loading, setLoading] = useState(true);
const [aiLoading, setAiLoading] = useState(true);
const [error, setError] = useState("");
const [offeredPrice, setOfferedPrice] = useState("");
const [requesting, setRequesting] = useState(false);
const [offerSubmitted, setOfferSubmitted] = useState(false);

const token = localStorage.getItem("token");

useEffect(() => {
if (!token) {
navigate("/login");
return;
}

fetch(`${API_BASE}/properties/${propertyCode}`, {
  headers: { Authorization: `Bearer ${token}` },
})
  .then((res) => {
    if (!res.ok) throw new Error("Property not found");
    return res.json();
  })
  .then(setProperty)
  .catch((err) => {
    setError(err.message);
    toast.error(err.message);
  })
  .finally(() => setLoading(false));

fetch(`${API_BASE}/ai/property/${propertyCode}/analysis`, {
  headers: { Authorization: `Bearer ${token}` },
})
  .then((res) => {
    if (!res.ok) throw new Error();
    return res.json();
  })
  .then(setAiData)
  .catch(() => toast.error("AI analysis unavailable"))
  .finally(() => setAiLoading(false));


}, [propertyCode, token, navigate]);

const handleBuyRequest = async () => {
if (!offeredPrice || offeredPrice <= 0) {
toast.error("Please enter a valid offer amount");
return;
}
setRequesting(true);
try {
const res = await fetch(`${API_BASE}/buy/request`, {
method: "POST",
headers: {
Authorization: `Bearer ${token}`,
"Content-Type": "application/json",
},
body: JSON.stringify({
propertyCode: property.propertyCode,
offeredPrice: parseFloat(offeredPrice),
}),
});
const text = await res.text();
if (!res.ok) throw new Error(text);
toast.success("Buy request submitted successfully!");
setOfferSubmitted(true);
setOfferedPrice("");
} catch (err) {
toast.error(err.message);
} finally {
setRequesting(false);
}
};

const getRiskGradient = (score) => {
if (score < 30) return "linear-gradient(90deg, #28a745, #8bc34a)";
if (score < 60) return "linear-gradient(90deg, #ffc107, #fd7e14)";
return "linear-gradient(90deg, #dc3545, #ff6b6b)";
};

const getCorrectFileUrl = (url) => {
if (!url) return "";
return url.replace("/image/upload/", "/raw/upload/");
};

if (loading)
return ( <div className="glass p-4 text-center"> <Spinner animation="border" /> <p className="mt-2">Loading property details...</p> </div>
);

if (error)
return ( <Alert variant="danger" className="m-4">
{error} </Alert>
);

return ( <Container className="mt-5"> <Card className="p-4 shadow-lg glass">

```
    {/* HEADER */}
    <div className="d-flex justify-content-between align-items-center flex-wrap">
      <h4>🏠 Property Code: {property?.propertyCode}</h4>
      <div>
        <Badge bg="success" className="me-2">
          CLEAR TITLE
        </Badge>
        {property?.availableForSale ? (
          <Badge bg="info">Listed For Sale</Badge>
        ) : (
          <Badge bg="secondary">Not Available</Badge>
        )}
      </div>
    </div>

    <hr />

    {/* PROPERTY INFO */}
    <Row>
      <Col md={6}>
        <p><b>📍 Location:</b> {property?.location}</p>
        <p><b>📐 Area:</b> {property?.area} sq.ft</p>
        <p><b>🏗 Land Type:</b> {property?.landType}</p>
      </Col>
      <Col md={6}>
        <p><b>🧾 Survey No:</b> {property?.surveyNumber}</p>
        <p><b>📜 Gat No:</b> {property?.gatNumber}</p>
        <p>
          <b>Status:</b>{" "}
          <Badge bg={property?.status === "CLEAR" ? "success" : "warning"}>
            {property?.status}
          </Badge>
        </p>
      </Col>
    </Row>

    <hr />

    {/* OWNER INFO */}
    <h6>👥 Registered Owners</h6>
    {property?.owners?.length > 0 ? (
      property.owners.map((o, i) => (
        <div key={i}>
          {o.name} ({o.ownershipPercentage}%)
        </div>
      ))
    ) : (
      <p className="text-muted">No ownership data available</p>
    )}

    <hr />


<pre style={{ color: "white", fontSize: "12px" }}>
  {JSON.stringify(property?.documents, null, 2)}
</pre>
    {/* 📄 DOCUMENT SECTION (PREVIEW REMOVED) */}
    <h6>📄 Verified Documents</h6>
    {property?.documents?.filter(d => d.verified)?.length > 0 ? (
      property.documents
        .filter(d => d.verified)
        .map((doc, i) => (
          <div key={i} className="mb-3">
            <a
              href={getCorrectFileUrl(doc.fileUrl)}
              target="_blank"
              rel="noreferrer"
              style={{ textDecoration: "none", fontWeight: "600" }}
            >
              📄 {doc.documentType}
            </a>
          </div>
        ))
    ) : (
      <p className="text-muted">No verified documents available</p>
    )}

    <hr />

    {/* AI INSIGHTS */}
    <h5>🤖 AI Property Insights</h5>
    {aiLoading ? (
      <Spinner animation="border" size="sm" />
    ) : aiData ? (
      <>
        <Row className="g-3 mb-3">
          <Col md={6}>
            <Card className="p-3 bg-soft">
              <p><b>💰 Predicted Market Price</b></p>
              <h4>₹ {aiData?.predictedPrice?.toLocaleString()}</h4>
              <small>Confidence: {aiData?.confidenceScore}%</small>
            </Card>
          </Col>

          <Col md={6}>
            <Card className="p-3 bg-soft">
              <p><b>📑 Document anomaly</b></p>
              {aiData?.documentAnomaly ? (
                <Badge bg="danger">Suspicious</Badge>
              ) : (
                <Badge bg="success">Clean</Badge>
              )}
            </Card>
          </Col>
        </Row>

        <Card className="p-3 mb-3">
          <b>⚠️ Risk Score</b>
          <ProgressBar now={aiData?.riskScore || 0} />
          <Badge className="mt-2">
            {aiData?.riskLevel} RISK
          </Badge>
        </Card>
      </>
    ) : (
      <p className="text-muted">AI analysis unavailable</p>
    )}

    <hr />

    {/* BUY SECTION */}
    {property?.availableForSale && property?.status === "CLEAR" ? (
      <>
        <h5>💰 Make an Offer</h5>

        {offerSubmitted ? (
          <Alert variant="success">Offer submitted successfully</Alert>
        ) : (
          <>
            <Form.Control
              type="number"
              value={offeredPrice}
              onChange={(e) => setOfferedPrice(e.target.value)}
            />
            <Button
              className="mt-2"
              onClick={handleBuyRequest}
              disabled={requesting}
            >
              {requesting ? "Submitting..." : "Submit"}
            </Button>
          </>
        )}
      </>
    ) : (
      <Alert variant="warning">Not available</Alert>
    )}
  </Card>
</Container>


);
}

export default PropertyDetails;
