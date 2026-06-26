import React, { useEffect, useRef, useState } from "react";
import { Container, Card, Button, Spinner, Alert } from "react-bootstrap";
import { Html5Qrcode, Html5QrcodeScanner } from "html5-qrcode";
import { toast } from "react-hot-toast";

const QrScannerPage = () => {
  const scannerRef = useRef(null);

  const [isScanning, setIsScanning] = useState(true);
  const [scanResult, setScanResult] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    startScanner();

    return () => {
      if (scannerRef.current) {
        scannerRef.current.clear().catch(() => {});
      }
    };
  }, []);

  const startScanner = () => {
    setIsScanning(true);

    const scanner = new Html5QrcodeScanner(
      "reader",
      {
        fps: 10,
        qrbox: {
          width: 260,
          height: 260,
        },
        rememberLastUsedCamera: true,
      },
      false
    );

    scannerRef.current = scanner;

    scanner.render(
      (decodedText) => {
        handleResult(decodedText);

        scanner.clear().catch(() => {});
      },
      () => {}
    );
  };

  const handleResult = (decodedText) => {
    setLoading(true);
    setIsScanning(false);
    setScanResult(decodedText);

    toast.success("QR Code Scanned Successfully");

    setTimeout(() => {
      window.location.href = decodedText;
    }, 1200);
  };

  const handleFileUpload = async (event) => {
    const file = event.target.files[0];

    if (!file) return;

    setLoading(true);

    try {
      const html5QrCode = new Html5Qrcode("reader");

      const decodedText = await html5QrCode.scanFile(file, true);

      handleResult(decodedText);
    } catch (err) {
      setLoading(false);
      toast.error("Invalid QR Code");
    }
  };

  const restartScanner = () => {
    setLoading(false);
    setScanResult("");

    const reader = document.getElementById("reader");

    if (reader) {
      reader.innerHTML = "";
    }

    startScanner();
  };

  return (
    <Container className="py-5">

      <Card
        className="shadow-lg border-0 mx-auto"
        style={{
          maxWidth: "700px",
          borderRadius: "20px",
          overflow: "hidden",
        }}
      >
        <Card.Body className="p-4">

          <div className="text-center mb-4">

            <div style={{ fontSize: "55px" }}>
              🏛️
            </div>

            <h2 className="fw-bold text-primary">
              BhoomiDarpan
            </h2>

            <p className="text-muted">
              Blockchain Property QR Verification
            </p>

          </div>

          {loading && (
            <div className="text-center mb-4">

              <Spinner animation="border" />

              <p className="mt-3">
                Verifying Property...
              </p>

            </div>
          )}

          <div
            id="reader"
            style={{
              width: "100%",
              border: "3px solid #6f42c1",
              borderRadius: "15px",
              padding: "10px",
              minHeight: "320px",
            }}
          ></div>

          <div className="text-center mt-4">

            <label
              className="btn btn-outline-primary"
              style={{
                borderRadius: "30px",
                padding: "10px 25px",
              }}
            >
              📁 Upload QR Image

              <input
                type="file"
                accept="image/*"
                hidden
                onChange={handleFileUpload}
              />
            </label>

          </div>

          {!isScanning && scanResult && (
            <Alert
              variant="success"
              className="mt-4 text-center"
            >
              <h5>✅ QR Code Verified</h5>

              <small>
                Redirecting to Property Verification...
              </small>

              <hr />

              <Button
                variant="success"
                onClick={restartScanner}
              >
                Scan Another QR
              </Button>
            </Alert>
          )}

          <Card
            className="mt-4 border-0"
            style={{
              background: "#f8f9fa",
            }}
          >
            <Card.Body>

              <h5 className="fw-bold">
                🔒 Secure Blockchain Verification
              </h5>

              <ul className="mb-0">
                <li>Verify Property Ownership</li>
                <li>Validate Blockchain Record</li>
                <li>Check Property Certificate</li>
                <li>View Property Verification Status</li>
              </ul>

            </Card.Body>
          </Card>

          <div
            className="text-center mt-4"
            style={{
              color: "#6c757d",
              fontSize: "14px",
            }}
          >
            Powered by Blockchain • AI Verification • BhoomiDarpan
          </div>

        </Card.Body>
      </Card>

    </Container>
  );
};

export default QrScannerPage;
