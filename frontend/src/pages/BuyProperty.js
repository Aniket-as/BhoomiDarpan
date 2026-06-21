import React, { useEffect, useState } from "react";
import { Container, Row, Col, Navbar, Badge, Form } from "react-bootstrap";
import { Link } from "react-router-dom";
import "bootstrap-icons/font/bootstrap-icons.css";

const API_BASE = "https://bhoomidarpan-5.onrender.com/api";

const BuyProperty = () => {
  const [properties, setProperties] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(false);

  // 🔁 FETCH PROPERTIES (with optional search)
  const fetchProperties = async (searchValue = "") => {
  setLoading(true);

  try {
    const token = localStorage.getItem("token");

    const url =
      searchValue.trim() === ""
        ? `${API_BASE}/properties/available`
        : `${API_BASE}/properties/available?search=${encodeURIComponent(
            searchValue
          )}`;

    const res = await fetch(url, {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    if (!res.ok) throw new Error("Failed to fetch properties");

    const data = await res.json();
    setProperties(data);
  } catch (err) {
    console.error(err);
    setProperties([]);
  } finally {
    setLoading(false);
  }
};


  // 🔁 INITIAL LOAD
  useEffect(() => {
    fetchProperties();
  }, []);

  // 🔍 SEARCH HANDLER
  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearch(value);
    fetchProperties(value);
  };

  return (
    <>
      {/* NAVBAR */}
      <Navbar className="px-4 py-3">
        <span className="navbar-brand">🏛️ BhoomiDarpan</span>
        <div className="ms-auto d-flex gap-4">
          <i className="bi bi-bell-fill fs-5"></i>
          <i className="bi bi-person-circle fs-4"></i>
        </div>
      </Navbar>

      <Container className="mt-5">
        {/* HEADER + SEARCH */}
        <div className="glass p-4 mb-4">
          <h4 style={{ fontFamily: "Questrial" }}>
            Buy Blockchain-Verified Property
          </h4>
          <p className="text-muted mb-3">
            Search by village / locality / city
          </p>

          <Form.Control
            type="text"
            placeholder="Search area (e.g. Wakad, Baner, Hinjewadi)"
            value={search}
            onChange={handleSearchChange}
          />
        </div>

        {/* CONTENT */}
        {loading ? (
          <div className="glass p-4 text-center">Loading properties...</div>
        ) : (
          <Row className="g-4">
            {properties.length === 0 ? (
              <p className="text-muted text-center">
                No properties found.
              </p>
            ) : (
              properties.map((property) => (
                <PropertyCard key={property.id} property={property} />
              ))
            )}
          </Row>
        )}
      </Container>

      <div className="footer mt-5 text-center">
        © 2026 BhoomiDarpan • Verified Property Marketplace
      </div>
    </>
  );
};

/* ===== PROPERTY CARD ===== */

const PropertyCard = ({ property }) => (
  <Col md={4}>
    <div className="glass p-4 card-hover">
      <Badge bg="success" className="mb-2">
        Blockchain Verified
      </Badge>

      <h6 className="mb-1">{property.propertyCode}</h6>

      <p className="text-muted small mb-1">
        📍 Location: {property.location}
      </p>

      <p className="text-muted small mb-1">
        🧾 Land Type: {property.landType}
      </p>

      <p className="fw-bold">
        📐 Area: {property.area} sq.ft
      </p>

      <div className="d-grid mt-3">
        <Link
          to={`/property/${property.propertyCode}`}
          className="btn btn-orange"
        >
          View Details
        </Link>
      </div>
    </div>
  </Col>
);

export default BuyProperty;
