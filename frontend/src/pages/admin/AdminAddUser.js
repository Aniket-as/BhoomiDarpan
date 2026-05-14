import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Container, Navbar, Form, Button, Alert, Row, Col } from "react-bootstrap";
import "bootstrap-icons/font/bootstrap-icons.css";

const API_BASE = "http://localhost:8080/api";

const AdminAddUser = () => {
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const [form, setForm] = useState({
    name: "",
    email: "",
    mobile: "",
    password: "",
    confirmPassword: "",
    aadhaarNumber: "",
    pan: "",
    role: "USER",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!token || user?.role !== "ADMIN") {
      navigate("/login");
    }
  }, [navigate, token]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    // Aadhaar numbers only
    if (name === "aadhaarNmber") {
      setForm({ ...form, [name]: value.replace(/\D/g, "") });
      return;
    }

    setForm({ ...form, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    if (!/^\d{12}$/.test(form.aadhaarNumber)) {
      setError("Aadhaar must be exactly 12 digits");
      return;
    }

    if (!/^[A-Z]{5}[0-9]{4}[A-Z]{1}$/.test(form.pan.toUpperCase())) {
      setError("Invalid PAN format (ABCDE1234F)");
      return;
    }

    try {
      const res = await fetch(`${API_BASE}/admin/add-user`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          ...form,
          pan: form.pan.toUpperCase(),
        }),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg);
      }

      setSuccess("User created successfully ✅");

      setForm({
        name: "",
        email: "",
        mobile: "",
        password: "",
        confirmPassword: "",
        aadhaarNumber: "",
        pan: "",
        role: "USER",
      });

    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <>
      {/* NAVBAR */}
      <Navbar bg="dark" variant="dark" className="px-4 py-3">
        <Navbar.Brand>🛡️ Admin Panel</Navbar.Brand>
      </Navbar>

      <Container className="mt-5">
        <div className="shadow-lg rounded-4 p-4 bg-white">

          <h4 className="mb-3 fw-bold text-primary">
            ➕ Create User / Officer
          </h4>

          <p className="text-muted">
            Admin can create USER, TEHSILDAR, SUB REGISTRAR
          </p>

          {error && <Alert variant="danger">{error}</Alert>}
          {success && <Alert variant="success">{success}</Alert>}

          <Form onSubmit={handleSubmit}>

            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Full Name</Form.Label>
                  <Form.Control
                    name="name"
                    value={form.name}
                    onChange={handleChange}
                    required
                  />
                </Form.Group>
              </Col>

              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Email</Form.Label>
                  <Form.Control
                    type="email"
                    name="email"
                    value={form.email}
                    onChange={handleChange}
                    required
                  />
                </Form.Group>
              </Col>
            </Row>

            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Mobile</Form.Label>
                  <Form.Control
                    name="mobile"
                    value={form.mobile}
                    onChange={handleChange}
                    required
                  />
                </Form.Group>
              </Col>

              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Aadhaar Number (Login ID)</Form.Label>
                  <Form.Control
                    name="aadhaarNumber"
                    maxLength="12"
                    value={form.aadhaarNumber}
                    onChange={handleChange}
                    required
                  />
                </Form.Group>
              </Col>
            </Row>

            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>PAN</Form.Label>
                  <Form.Control
                    name="pan"
                    value={form.pan}
                    onChange={handleChange}
                    required
                  />
                </Form.Group>
              </Col>

              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Role</Form.Label>
                  <Form.Select
                    name="role"
                    value={form.role}
                    onChange={handleChange}
                  >
                    <option value="USER">USER</option>
                    <option value="TEHSILDAR">TEHSILDAR</option>
                    <option value="SUB_REGISTRAR">SUB_REGISTRAR</option>
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>

            {/* PASSWORD SECTION */}
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3 position-relative">
                  <Form.Label>Password</Form.Label>
                  <Form.Control
                    type={showPassword ? "text" : "password"}
                    name="password"
                    value={form.password}
                    onChange={handleChange}
                    required
                  />
                  <i
                    className={`bi ${
                      showPassword ? "bi-eye-slash" : "bi-eye"
                    } position-absolute top-50 end-0 translate-middle-y me-3`}
                    onClick={() => setShowPassword(!showPassword)}
                    role="button"
                  ></i>
                </Form.Group>
              </Col>

              <Col md={6}>
                <Form.Group className="mb-4">
                  <Form.Label>Confirm Password</Form.Label>
                  <Form.Control
                    type="password"
                    name="confirmPassword"
                    value={form.confirmPassword}
                    onChange={handleChange}
                    required
                  />
                </Form.Group>
              </Col>
            </Row>

            <Button
              type="submit"
              variant="success"
              size="lg"
              className="w-100 rounded-3"
            >
              🚀 Create User
            </Button>

          </Form>
        </div>
      </Container>
    </>
  );
};

export default AdminAddUser;
