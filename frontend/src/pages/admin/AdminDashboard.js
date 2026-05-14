import React, { useEffect, useState } from "react";
import { Container, Row, Col, Navbar, Badge } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import "bootstrap-icons/font/bootstrap-icons.css";

const API_BASE = "http://localhost:8080/api";

const AdminDashboard = () => {
  const navigate = useNavigate();
  const [adminName, setAdminName] = useState("");
  const [onHoldCount, setOnHoldCount] = useState(0);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const user = JSON.parse(localStorage.getItem("user"));

    if (!token || !user || user.role !== "ADMIN") {
      navigate("/login");
      return;
    }

    setAdminName(user.name);
    fetchOnHoldCount(token);
  }, [navigate]);

  const fetchOnHoldCount = async (token) => {
    try {
      const res = await fetch(`${API_BASE}/admin/registrations/on-hold`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });

      if (!res.ok) return;

      const data = await res.json();
      setOnHoldCount(data.length);
    } catch (err) {
      console.error("Failed to fetch AI flagged cases", err);
    }
  };

  return (
    <>
      <Navbar className="px-4 py-3">
        <span className="navbar-brand">
          🛡️ BhoomiDarpan – Admin Panel
        </span>

        <div className="d-flex align-items-center gap-4 ms-auto">
          <span className="small text-muted">Admin</span>
          <i className="bi bi-person-shield fs-4"></i>
        </div>
      </Navbar>

      <Container className="mt-5">

        <div className="glass p-4 mb-4">
          <h4 style={{ fontFamily: "Questrial" }}>
            Welcome, {adminName} 👑
          </h4>
          <p className="text-muted mb-0">
            Manage system users, officers, and administrative controls.
          </p>
        </div>

        {/* AI ALERT BANNER */}
        {onHoldCount > 0 && (
          <div className="alert alert-danger mb-4">
            🚨 {onHoldCount} registration(s) flagged by AI for review.
          </div>
        )}

        <Row className="g-4">

          {/* 🔴 NEW AI REVIEW CARD */}
          <AdminCard
            icon="bi-robot"
            title="AI Flagged Registrations"
            desc="Review suspicious transactions detected by AI"
            btn="Review Cases"
            btnClass="btn-danger"
            link="/admin/ai-review"
            badge={onHoldCount}
          />

          <AdminCard
            icon="bi-person-plus"
            title="Add User / Officer"
            desc="Create USER, TEHSILDAR or SUB-REGISTRAR accounts"
            btn="Add User"
            btnClass="btn-green"
            link="/admin/add-user"
          />

          <AdminCard
            icon="bi-people"
            title="Manage Users"
            desc="View, activate or deactivate users"
            btn="Manage"
            btnClass="btn-violet"
            link="/admin/manage-users"
          />

          <AdminCard
            icon="bi-shield-check"
            title="Officers Control"
            desc="Assign or remove government roles"
            btn="Control"
            btnClass="btn-orange"
            link="/admin/manage-officers"
          />

          <AdminCard
            icon="bi-exclamation-triangle"
            title="Dispute Oversight"
            desc="Monitor disputes across the system"
            btn="View"
            btnClass="btn-red"
            link="/admin/disputes"
          />

          <AdminCard
            icon="bi-database-check"
            title="System Logs"
            desc="Audit actions & security logs"
            btn="Audit"
            btnClass="btn-blue"
            link="/admin/logs"
          />
          <AdminCard
            icon="bi-house-add"
            title="Create Property"
            desc="Register new land/property into system"
            btn="Create"
            btnClass="btn-success"
            link="/admin/create-property"
          />
          <AdminCard
            icon="bi-box-arrow-right"
            title="Logout"
            desc="Securely exit admin panel"
            btn="Logout"
            btnClass="btn-dark"
            link="/logout"
            isLogout
          />
        </Row>
      </Container>

      <div className="footer mt-5 text-center">
        © 2026 BhoomiDarpan • Admin Control Panel • Secure Governance
      </div>
    </>
  );
};

/* ===== ADMIN CARD ===== */

const AdminCard = ({
  icon,
  title,
  desc,
  btn,
  btnClass,
  link,
  isLogout,
  badge
}) => {
  const navigate = useNavigate();

  const handleClick = () => {
    if (isLogout) {
      localStorage.clear();
      navigate("/login");
    } else {
      navigate(link);
    }
  };

  return (
    <Col md={4}>
      <div className="glass p-4 text-center card-hover position-relative">

        {/* 🔔 Badge for AI Count */}
        {badge > 0 && (
          <Badge
            bg="danger"
            className="position-absolute top-0 end-0 m-2"
          >
            {badge}
          </Badge>
        )}

        <i className={`bi ${icon} fs-2`}></i>
        <h6 className="mt-3">{title}</h6>
        <p className="text-muted small">{desc}</p>
        <button
          className={`btn ${btnClass} mt-2`}
          onClick={handleClick}
        >
          {btn}
        </button>
      </div>
    </Col>
  );
};

export default AdminDashboard;
