import React, { useState, useEffect, createContext, useContext, useRef } from "react";
import { Navbar, Container, Button, Row, Col, Form, Badge, Spinner } from "react-bootstrap";
import "bootstrap-icons/font/bootstrap-icons.css";

// Theme Context
const ThemeContext = createContext({ darkMode: false, toggleTheme: () => {} });
const useTheme = () => useContext(ThemeContext);

// Theme Provider component
const ThemeProvider = ({ children }) => {
  const [darkMode, setDarkMode] = useState(() => {
    const saved = localStorage.getItem("theme");
    return saved === "dark";
  });

  useEffect(() => {
    localStorage.setItem("theme", darkMode ? "dark" : "light");
    document.body.setAttribute("data-theme", darkMode ? "dark" : "light");
  }, [darkMode]);

  const toggleTheme = () => setDarkMode(!darkMode);

  return (
    <ThemeContext.Provider value={{ darkMode, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  );
};

// Animated Counter Component
const AnimatedCounter = ({ target, suffix = "" }) => {
  const [count, setCount] = useState(0);
  const counterRef = useRef(null);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          let start = 0;
          const duration = 2000;
          const step = Math.ceil(target / (duration / 16));
          const timer = setInterval(() => {
            start += step;
            if (start >= target) {
              setCount(target);
              clearInterval(timer);
            } else {
              setCount(start);
            }
          }, 16);
          observer.disconnect();
        }
      },
      { threshold: 0.5 }
    );

    if (counterRef.current) observer.observe(counterRef.current);
    return () => observer.disconnect();
  }, [target]);

  return (
    <div ref={counterRef} className="fs-4 fw-bold gradient-text">
      {count.toLocaleString()}{suffix}
    </div>
  );
};

const Home = () => {
  const { darkMode, toggleTheme } = useTheme();
  
  const [blockchain, setBlockchain] = useState([
    {
      index: 45231,
      hash: "0x7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069",
      timestamp: "2026-03-30 10:32:15",
      data: "Genesis Block - BhoomiDarpan Initiated",
      shortHash: "0x7f83...9069"
    },
    {
      index: 45232,
      hash: "0x4e07408562bedb8b60ce05c1cfe0e0c4a2d6b1b3e8e1d1e3e2c9a8f7e6d5c4b3",
      timestamp: "2026-03-30 11:15:42",
      data: "Property: MH-01-1234-5678 | Owner: Sharma Family | Registration: 2024-02-15",
      shortHash: "0x4e07...c4b3"
    },
    {
      index: 45233,
      hash: "0x3a6eb0790f39ac87c94f3856b2dd2c5d110e681160226a3f5d4f1c3e4a5b6c7d",
      timestamp: "2026-03-30 14:22:07",
      data: "Property: DL-02-9876-5432 | Owner: Priya Constructions | Mutation: Approved",
      shortHash: "0x3a6e...6c7d"
    }
  ]);

  
    

  const handleFileUpload = (e) => {
    const file = e.target.files[0];
    scanQR(file);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setIsDragging(false);
    const file = e.dataTransfer.files[0];
    scanQR(file);
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  return (
    <>
      <style>{`
        :root {
          --olive: #6B8E23;
          --olive-dark: #556B2F;
          --olive-light: #8FBC6E;
          --dark-brown: #5D4037;
          --dark-brown-light: #795548;
          --light-brown: #D7C4A1;
        }

        [data-theme="light"] {
          --bg-primary: #FBF7F0;
          --bg-secondary: #FFFFFF;
          --text-primary: #3E2723;
          --text-secondary: #6D4C41;
          --border-color: #E0D3C0;
          --card-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
          --gradient-start: var(--olive);
          --gradient-end: var(--dark-brown);
          --hero-bg: linear-gradient(135deg, #FDF8F0 0%, #F5EDE0 100%);
        }

        [data-theme="dark"] {
          --bg-primary: #1E1A15;
          --bg-secondary: #2C261F;
          --text-primary: #F5EFE6;
          --text-secondary: #CBB99C;
          --border-color: #4A3F32;
          --card-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
          --gradient-start: #7B9C42;
          --gradient-end: #8B5A4B;
          --hero-bg: linear-gradient(135deg, #1E1A15 0%, #2C261F 100%);
        }

        * {
          transition: background-color 0.3s ease, color 0.3s ease, border-color 0.3s ease;
        }

        body {
          background: var(--hero-bg);
          font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
          margin: 0;
          min-height: 100vh;
          color: var(--text-primary);
        }

        /* Glass Morphism */
        .glass {
          background: var(--bg-secondary);
          backdrop-filter: blur(10px);
          border-radius: 28px;
          box-shadow: var(--card-shadow);
          border: 1px solid var(--border-color);
          transition: all 0.3s ease;
        }

        .glass:hover {
          transform: translateY(-4px);
          box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
        }

        /* Buttons */
        .btn-primary-custom {
          background: linear-gradient(135deg, var(--olive) 0%, var(--olive-dark) 100%);
          border: none;
          color: white !important;
          padding: 12px 32px;
          border-radius: 40px;
          font-weight: 600;
          transition: all 0.3s ease;
        }

        .btn-primary-custom:hover {
          transform: scale(1.05);
          box-shadow: 0 4px 15px rgba(107, 142, 35, 0.4);
        }

        .btn-outline-custom {
          background: transparent;
          border: 2px solid var(--olive);
          color: var(--olive);
          padding: 10px 28px;
          border-radius: 40px;
          font-weight: 600;
          transition: all 0.3s ease;
        }

        .btn-outline-custom:hover {
          background: var(--olive);
          color: white !important;
          transform: translateY(-2px);
        }

        /* Dropzone */
        .dropzone {
          border: 2px dashed var(--border-color);
          border-radius: 20px;
          padding: 2rem;
          text-align: center;
          cursor: pointer;
          transition: all 0.3s ease;
          background: var(--bg-primary);
        }

        .dropzone.dragging {
          border-color: var(--olive);
          background: rgba(107, 142, 35, 0.1);
          transform: scale(1.02);
        }

        .dropzone:hover {
          border-color: var(--olive);
        }

        /* Blockchain Cards */
        .blockchain-container {
          display: flex;
          gap: 1.5rem;
          overflow-x: auto;
          padding: 1rem 0.5rem;
          scrollbar-width: thin;
        }

        .blockchain-container::-webkit-scrollbar {
          height: 6px;
        }

        .blockchain-container::-webkit-scrollbar-track {
          background: rgba(107, 142, 35, 0.2);
          border-radius: 10px;
        }

        .blockchain-container::-webkit-scrollbar-thumb {
          background: var(--olive);
          border-radius: 10px;
        }

        .block-card {
          min-width: 280px;
          background: var(--bg-secondary);
          border-radius: 20px;
          padding: 1.25rem;
          border-left: 4px solid var(--olive);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          transition: all 0.3s ease;
          position: relative;
        }

        .block-card::after {
          content: '→';
          position: absolute;
          right: -20px;
          top: 50%;
          transform: translateY(-50%);
          font-size: 24px;
          color: var(--light-brown);
          font-weight: bold;
        }

        .block-card:last-child::after {
          display: none;
        }

        .block-card.new-block {
          border-left-color: var(--dark-brown);
          animation: glowPulse 0.5s ease-in-out;
          background: linear-gradient(135deg, rgba(93, 64, 55, 0.1) 0%, var(--bg-secondary) 100%);
        }

        @keyframes glowPulse {
          0% { box-shadow: 0 0 0 0 rgba(93, 64, 55, 0.7); }
          70% { box-shadow: 0 0 0 10px rgba(93, 64, 55, 0); }
          100% { box-shadow: 0 0 0 0 rgba(93, 64, 55, 0); }
        }

        .block-hash {
          font-family: 'Monaco', 'Courier New', monospace;
          font-size: 0.7rem;
          background: rgba(107, 142, 35, 0.1);
          padding: 4px 8px;
          border-radius: 8px;
        }

        /* Verification Result */
        .verification-result {
          background: rgba(107, 142, 35, 0.1);
          border-radius: 20px;
          padding: 1rem;
          border: 1px solid var(--olive);
          animation: slideUp 0.5s ease-out;
        }

        @keyframes slideUp {
          from {
            opacity: 0;
            transform: translateY(20px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }

        .detail-row {
          display: flex;
          justify-content: space-between;
          padding: 6px 0;
          font-size: 0.9rem;
          border-bottom: 1px solid var(--border-color);
        }

        .detail-label {
          color: var(--text-secondary);
          font-weight: 500;
        }

        .detail-value {
          color: var(--text-primary);
        }

        .tx-hash {
          word-break: break-all;
          font-size: 0.75rem;
        }

        /* Feature Cards */
        .feature-card {
          background: var(--bg-secondary);
          border-radius: 24px;
          padding: 1.75rem;
          transition: all 0.3s ease;
          border: 1px solid var(--border-color);
          text-align: center;
          height: 100%;
        }

        .feature-card:hover {
          transform: translateY(-8px);
          box-shadow: 0 20px 30px -10px rgba(0, 0, 0, 0.15);
        }

        .stat-card {
          background: var(--bg-secondary);
          border-radius: 20px;
          padding: 1rem 1.5rem;
          text-align: center;
          border: 1px solid var(--border-color);
          min-width: 120px;
        }

        .gradient-text {
          background: linear-gradient(135deg, var(--gradient-start) 0%, var(--gradient-end) 100%);
          -webkit-background-clip: text;
          background-clip: text;
          color: transparent;
        }

        /* Theme Toggle */
        .theme-toggle {
          background: var(--bg-secondary);
          border: 1px solid var(--border-color);
          border-radius: 40px;
          padding: 8px 16px;
          cursor: pointer;
          transition: all 0.3s ease;
          color: var(--text-primary);
        }

        .theme-toggle:hover {
          background: var(--olive-light);
          color: white;
        }

        /* Responsive */
        @media (max-width: 768px) {
          .block-card {
            min-width: 240px;
          }
          .block-card::after {
            display: none;
          }
          .stat-card {
            min-width: 100px;
          }
        }
      `}</style>

      {/* Navbar */}
      <Navbar className="px-4 py-3" expand="md" style={{ background: 'var(--bg-secondary)', borderBottom: '1px solid var(--border-color)' }}>
        <Container fluid>
          <Navbar.Brand className="fw-bold fs-4" style={{ color: 'var(--text-primary)' }}>
            <i className="bi bi-building me-2" style={{ color: 'var(--olive)' }}></i>
            BhoomiDarpan
          </Navbar.Brand>
          <div className="d-flex gap-3 align-items-center">
            <div className="theme-toggle" onClick={toggleTheme}>
              <i className={`bi bi-${darkMode ? 'sun' : 'moon'}-fill me-1`}></i>
              {darkMode ? 'Light' : 'Dark'} Mode
            </div>
            <Navbar.Toggle aria-controls="navbar-nav" />
            <Navbar.Collapse id="navbar-nav" className="justify-content-end">
              <div className="d-flex gap-3">
                <Button className="btn-primary-custom" href="/login">
                  <i className="bi bi-box-arrow-in-right me-2"></i>Login
                </Button>
                <Button className="btn-outline-custom" href="/signup">
                  <i className="bi bi-person-plus me-2"></i>Signup
                </Button>
              </div>
            </Navbar.Collapse>
          </div>
        </Container>
      </Navbar>

      <Container className="mt-5">
        {/* Hero Section */}
        <div className="glass p-5 text-center mb-5">
          <Badge style={{ backgroundColor: 'var(--olive)', border: 'none' }} className="mb-3 px-3 py-2 rounded-pill">
            <i className="bi bi-shield-check me-1"></i> Blockchain Powered
          </Badge>
          <h1 className="display-5 fw-bold gradient-text">
            Public Property Verification Portal
          </h1>
          <p className="mt-3 fs-5" style={{ color: 'var(--text-secondary)', maxWidth: '800px', margin: '0 auto' }}>
            Scan a BhoomiDarpan QR-based Property Certificate to verify ownership,
            registration, mutation status, and blockchain authenticity.
          </p>
          <div className="mt-4 d-flex justify-content-center gap-4 flex-wrap">
            <div><i className="bi bi-database-check me-2" style={{ color: 'var(--olive)' }}></i><span className="small text-uppercase fw-semibold">Immutable</span></div>
            <div><i className="bi bi-eye me-2" style={{ color: 'var(--dark-brown)' }}></i><span className="small text-uppercase fw-semibold">Transparent</span></div>
            <div><i className="bi bi-diagram-3 me-2" style={{ color: 'var(--olive)' }}></i><span className="small text-uppercase fw-semibold">Decentralized</span></div>
          </div>
        </div>

        

        {/* Blockchain Ledger */}
        <Row className="mt-5" id="blockchain-viz">
          <Col xs={12}>
            <div className="glass p-4">
              <div className="d-flex align-items-center mb-4 flex-wrap gap-2">
                <i className="bi bi-link-45deg fs-3 me-2" style={{ color: 'var(--olive)' }}></i>
                <h4 className="mb-0 fw-bold">Blockchain Ledger</h4>
                <Badge style={{ backgroundColor: 'var(--dark-brown)', border: 'none' }} className="ms-3 px-3 py-2">
                  <i className="bi bi-node-plus me-1"></i> Live Chain
                </Badge>
                <div className="ms-auto">
                  <span className="badge" style={{ background: 'var(--olive-light)', color: 'white' }}>
                    <i className="bi bi-database me-1"></i> Total Blocks: {blockchain.length}
                  </span>
                </div>
              </div>
              <p className="small mb-3" style={{ color: 'var(--text-secondary)' }}>
                Each block is cryptographically linked, ensuring tamper-proof property records.
                Upload a QR to add a new verified block to the chain.
              </p>
              <div className="blockchain-container">
                {blockchain.map((block, idx) => (
                  <div key={idx} className={`block-card ${block.isNew ? 'new-block' : ''}`}>
                    <div className="d-flex justify-content-between align-items-start mb-2">
                      <span className="fw-bold">
                        <i className="bi bi-cube me-1"></i> Block #{block.index}
                      </span>
                      {block.isNew && <Badge style={{ backgroundColor: 'var(--dark-brown)' }}>New ✓</Badge>}
                    </div>
                    <div className="block-hash mb-2 font-monospace small">
                      <i className="bi bi-hash me-1"></i> {block.shortHash}
                    </div>
                    <div className="small mb-2" style={{ color: 'var(--text-secondary)' }}>
                      <i className="bi bi-clock me-1"></i> {block.timestamp}
                    </div>
                    <div className="small" style={{ fontSize: '0.75rem', lineHeight: '1.4' }}>
                      <i className="bi bi-file-text me-1"></i> {block.data.length > 80 ? block.data.substring(0, 80) + '...' : block.data}
                    </div>
                  </div>
                ))}
              </div>
              <div className="text-center mt-3">
                <small className="text-muted">
                  <i className="bi bi-shield-lock me-1"></i> Blockchain Consensus: Proof-of-Authority | Network: BhoomiDarpan Mainnet
                </small>
              </div>
            </div>
          </Col>
        </Row>

        {/* Feature Cards */}
        <Row className="mt-5 g-4">
          <Col md={4}>
            <div className="feature-card">
              <i className="bi bi-shield-check fs-2 mb-3" style={{ color: "var(--olive)" }}></i>
              <h6 className="fw-bold">Blockchain Verified</h6>
              <p className="small text-muted mb-0">
                Property records are anchored on blockchain with cryptographic proof.
              </p>
            </div>
          </Col>
          <Col md={4}>
            <div className="feature-card">
              <i className="bi bi-eye fs-2 mb-3" style={{ color: "var(--dark-brown)" }}></i>
              <h6 className="fw-bold">Public Transparency</h6>
              <p className="small text-muted mb-0">
                Anyone can verify basic ownership details without login.
              </p>
            </div>
          </Col>
          <Col md={4}>
            <div className="feature-card">
              <i className="bi bi-lock-fill fs-2 mb-3" style={{ color: "var(--dark-brown-light)" }}></i>
              <h6 className="fw-bold">Privacy Protected</h6>
              <p className="small text-muted mb-0">
                Sensitive documents and personal data remain confidential.
              </p>
            </div>
          </Col>
        </Row>

        {/* Stats Section */}
        <Row className="mt-4">
          <Col xs={12}>
            <div className="glass p-3">
              <div className="d-flex justify-content-around align-items-center flex-wrap gap-3">
                <div className="stat-card">
                  <AnimatedCounter target={1200000} suffix="+" />
                  <small className="text-muted">Properties Verified</small>
                </div>
                <div className="stat-card">
                  <AnimatedCounter target={99.99} suffix="%" />
                  <small className="text-muted">Uptime</small>
                </div>
                <div className="stat-card">
                  <AnimatedCounter target={2.3} suffix="s" />
                  <small className="text-muted">Avg Verification</small>
                </div>
                <div className="stat-card">
                  <AnimatedCounter target={50} suffix="+" />
                  <small className="text-muted">Active Validators</small>
                </div>
              </div>
            </div>
          </Col>
        </Row>
      </Container>

      {/* Footer */}
      <div className="mt-5 py-4 text-center" style={{ background: 'var(--bg-secondary)', borderTop: '1px solid var(--border-color)', color: 'var(--text-secondary)' }}>
        <Container>
          <i className="bi bi-c-circle me-1"></i> 2026 BhoomiDarpan • Blockchain-Based Land Records • Prototype for Academic Use • No legal replacement
          <div className="mt-2">
            <a href="#" className="text-muted me-3"><i className="bi bi-twitter-x"></i></a>
            <a href="#" className="text-muted me-3"><i className="bi bi-github"></i></a>
            <a href="#" className="text-muted"><i className="bi bi-linkedin"></i></a>
          </div>
        </Container>
      </div>
    </>
  );
};

// Wrap the entire app with ThemeProvider
const HomeWithTheme = () => (
  <ThemeProvider>
    <Home />
  </ThemeProvider>
);

export default HomeWithTheme;
