import React, { useEffect, useState, useContext, createContext } from "react";
import { Container, Row, Col, Navbar, Dropdown, Badge } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import "bootstrap-icons/font/bootstrap-icons.css";

/* ================= THEME CONTEXT ================= */
const ThemeContext = createContext({ darkMode: false, toggleTheme: () => {} });
const useTheme = () => useContext(ThemeContext);

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

/* ================= MAIN DASHBOARD ================= */
const UserDashboard = () => {
  const { darkMode, toggleTheme } = useTheme();
  const navigate = useNavigate();
  const [userName, setUserName] = useState("");

  useEffect(() => {
    const user = localStorage.getItem("user");
    if (!user) {
      navigate("/login");
      return;
    }
    setUserName(JSON.parse(user).name);
  }, [navigate]);

  const logout = () => {
    localStorage.clear();
    navigate("/login");
  };

  return (
    <>
      {/* NAVBAR */}
      <Navbar className="px-4 py-3 glass-nav">
        <span className="navbar-brand fw-bold fs-4">
          <i className="bi bi-building me-2" style={{ color: "#6b46c1" }}></i>
          BhoomiDarpan
        </span>

        <div className="ms-auto d-flex align-items-center gap-3">
          <Badge bg="success" className="px-3 py-2">
            ⛓ Verified on Blockchain
          </Badge>

          <button onClick={toggleTheme} className="theme-toggle">
            {darkMode ? "Light" : "Dark"}
          </button>

          <Dropdown.Item onClick={() => navigate("/scan-qr")}>
            📷 Scan QR & Verify
          </Dropdown.Item>

          <Dropdown align="end">
            <Dropdown.Toggle className="profile-btn">
              👤
            </Dropdown.Toggle>

            <Dropdown.Menu>
              <Dropdown.Item onClick={() => navigate("/transactions")}>
                📄 See Transactions
              </Dropdown.Item>
              <Dropdown.Item onClick={() => navigate("/change-password")}>
                🔑 Change Password
              </Dropdown.Item>
              <Dropdown.Divider />
              <Dropdown.Item onClick={logout} className="text-danger">
                🚪 Logout
              </Dropdown.Item>
            </Dropdown.Menu>
          </Dropdown>
        </div>
      </Navbar>

      <Container className="mt-5">
        {/* HERO */}
        <div className="hero-blockchain">
          <h3>🔐 Secure Land Records</h3>
          <p>All transactions are blockchain verified.</p>
          <div className="hash-box">TX HASH: 0xA92F...98F23C</div>
        </div>

        {/* MODULES */}
        <Row className="g-4 mt-2">
          <ModuleCard icon="bi-search" title="Buy Property" link="/buy-property" />
          <ModuleCard icon="bi-house-check" title="My Properties" link="/my-properties" />
          <ModuleCard icon="bi-calendar-check" title="Appointments" link="/my-appointments" />
          <ModuleCard icon="bi-person-check" title="Owner Requests" link="/owner-requests" />
          <ModuleCard icon="bi-exclamation-triangle" title="Disputes" link="/dispute-status" />
          <ModuleCard icon="bi-hourglass-split" title="My Requests" link="/my-request" />
          <ModuleCard icon="bi-shield-exclamation" title="Raise Dispute" link="/dispute-request" />
        </Row>
      </Container>

      {/* FOOTER */}
      <div className="dashboard-footer text-center mt-5">
        © 2026 BhoomiDarpan
      </div>
    </>
  );
};

/* MODULE CARD */
const ModuleCard = ({ icon, title, link }) => {
  const navigate = useNavigate();
  return (
    <Col md={4}>
      <div className="module-card-web3" onClick={() => navigate(link)}>
        <i className={`bi ${icon}`}></i>
        <h6>{title}</h6>
      </div>
    </Col>
  );
};

/* EXPORT */
const UserDashboardWithTheme = () => (
  <ThemeProvider>
    <UserDashboard />
  </ThemeProvider>
);

export default UserDashboardWithTheme;