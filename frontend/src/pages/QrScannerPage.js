import React, { useEffect } from "react";
import { Html5Qrcode, Html5QrcodeScanner } from "html5-qrcode";

const QrScannerPage = () => {

  useEffect(() => {

    const scanner = new Html5QrcodeScanner(
      "reader",
      {
        fps: 10,
        qrbox: { width: 250, height: 250 },
        rememberLastUsedCamera: true
      },
      false
    );

    scanner.render(
      (decodedText) => {
        handleResult(decodedText);
        scanner.clear();
      },
      () => {}
    );

    return () => {
      scanner.clear().catch(() => {});
    };

  }, []);

  // 🔥 COMMON HANDLER
  const handleResult = (decodedText) => {
    console.log("QR Result:", decodedText);

    alert("✅ QR Scanned Successfully");

    window.location.href = decodedText;
  };

  // 🔥 FILE UPLOAD HANDLER
  const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    const html5QrCode = new Html5Qrcode("reader");

    try {
      const decodedText = await html5QrCode.scanFile(file, true);
      handleResult(decodedText);
    } catch (err) {
      alert("❌ Invalid QR Code");
    }
  };

  return (
    <div style={{ textAlign: "center", marginTop: "20px" }}>
      <h3>📷 Scan Property QR Code</h3>

      {/* CAMERA SCANNER */}
      <div
        id="reader"
        style={{
          width: "320px",
          margin: "auto",
          border: "2px solid #6b46c1",
          borderRadius: "10px",
          padding: "10px"
        }}
      ></div>

      {/* 🔥 UPLOAD BUTTON */}
      <div style={{ marginTop: "20px" }}>
        <label
          style={{
            padding: "10px 20px",
            background: "#6b46c1",
            color: "white",
            borderRadius: "6px",
            cursor: "pointer"
          }}
        >
          📁 Upload QR Image
          <input
            type="file"
            accept="image/*"
            onChange={handleFileUpload}
            style={{ display: "none" }}
          />
        </label>
      </div>

      <p style={{ marginTop: "10px", color: "gray" }}>
        👉 Use camera OR upload QR image
      </p>
    </div>
  );
};

export default QrScannerPage;