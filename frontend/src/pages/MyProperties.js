import React, { useEffect, useState } from "react";
import { Container, Row, Col, Navbar, Badge } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import "bootstrap-icons/font/bootstrap-icons.css";

const API_BASE = "https://bhoomidarpan-5.onrender.com/api";

const MyProperties = () => {
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token) {
      navigate("/login");
      return;
    }

    fetch(`${API_BASE}/properties/my-properties`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Unauthorized");
        return res.json();
      })
      .then((data) => {
        setProperties(data);
        setLoading(false);
      })
      .catch(() => {
        localStorage.removeItem("token");
        navigate("/login");
      });
  }, [navigate]);

  return (
    <>
      <Navbar className="px-4 py-3">
        <span className="navbar-brand">🏛️ BhoomiDarpan</span>
      </Navbar>

      <Container className="mt-5">
        <div className="glass p-4 mb-4">
          <h4 style={{ fontFamily: "Questrial" }}>My Properties</h4>
        </div>

        {loading ? (
          <div className="glass p-4 text-center">Loading...</div>
        ) : properties.length === 0 ? (
          <div className="glass p-4 text-center">
            You do not own any properties yet.
          </div>
        ) : (
          <Row className="g-4">
            {properties.map((property) => (
              <PropertyCard key={property.id} property={property} />
            ))}
          </Row>
        )}
      </Container>
    </>
  );
};

const PropertyCard = ({ property }) => {
  const isClear = property.status === "CLEAR";

  return (
    <Col md={4}>
      <div className="glass p-3 card-hover">
        <h6>{property.location}</h6>
        <p className="small">Code: {property.propertyCode}</p>

        <Badge bg={isClear ? "success" : "warning"}>
          {isClear ? "Finalized" : "Mutation Pending"}
        </Badge>

        <ul className="small mt-2">
          <li>Land Type: {property.landType}</li>
          <li>Area: {property.area} sq.ft</li>
        </ul>

        {/* 🔥 Updated Link */}
        <Link
          to={`/my-property-details/${property.propertyCode}`}
          className="btn btn-violet w-100 mt-2"
        >
          View Details
        </Link>
      </div>
    </Col>
  );
};

export default MyProperties;