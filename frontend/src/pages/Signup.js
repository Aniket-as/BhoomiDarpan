import { useState, useContext, createContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "bootstrap-icons/font/bootstrap-icons.css";

// Theme Context (reused from Login/Home)
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

const Signup = () => {
  const { darkMode, toggleTheme } = useTheme();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    email: "",
    mobile: "",
    password: "",
    confirmPassword: "",
    aadhaarNumber: "",
    pan: ""
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const API_URL = "https://bhoomidarpan-5.onrender.com/api/auth/register";

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "aadhaarNumber") {
      setForm({ ...form, [name]: value.replace(/\D/g, "") });
      return;
    }

    setForm({ ...form, [name]: value });
  };

  const handleSignup = async (e) => {
    e.preventDefault();
    setError("");

    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    if (!/^\d{12}$/.test(form.aadhaarNumber)) {
      setError("Aadhaar must be exactly 12 digits");
      return;
    }

    if (!/^\d{10}$/.test(form.mobile)) {
      setError("Mobile must be 10 digits");
      return;
    }

    if (!/^[A-Z]{5}[0-9]{4}[A-Z]$/.test(form.pan.toUpperCase())) {
      setError("Invalid PAN format (ABCDE1234F)");
      return;
    }

    setLoading(true);

    try {
      const response = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          name: form.name,
          email: form.email,
          password: form.password,
          mobile: form.mobile,
          aadhaarNumber: form.aadhaarNumber,
          pan: form.pan.toUpperCase()
        })
      });

      if (!response.ok) {
        const message = await response.text();
        throw new Error(message || "Registration failed");
      }

      alert("Registration successful! Please login with Aadhaar.");
      navigate("/login");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className="d-flex align-items-center justify-content-center"
      style={{
        minHeight: "100vh",
        backgroundColor: "#6b46c1", // Solid violet background
        transition: "background-color 0.3s ease",
        padding: "20px"
      }}
    >
      {/* Theme Toggle Button */}
      <button
        onClick={toggleTheme}
        className="position-absolute top-0 end-0 m-4 border-0 rounded-pill px-3 py-2"
        style={{
          background: "var(--bg-secondary)",
          color: "var(--text-primary)",
          border: "1px solid var(--border-color)",
          zIndex: 10,
        }}
      >
        <i className={`bi bi-${darkMode ? "sun" : "moon"}-fill me-1`}></i>
        {darkMode ? "Light Mode" : "Dark Mode"}
      </button>

      <form
        onSubmit={handleSignup}
        className="shadow-lg rounded-4"
        style={{
          width: "100%",
          maxWidth: "550px",
          padding: "40px",
          background: "var(--bg-secondary)",
          backdropFilter: "blur(10px)",
          border: "1px solid var(--border-color)",
          transition: "all 0.3s ease",
        }}
      >
        <div className="text-center mb-4">
          <i className="bi bi-building fs-1" style={{ color: "var(--violet)" }}></i>
          <h3 className="mt-2 fw-bold" style={{ color: "var(--text-primary)" }}>
            BhoomiDarpan
          </h3>
          <p className="small" style={{ color: "var(--text-secondary)" }}>
            Government Land Records Portal
          </p>
        </div>

        {error && (
          <div className="alert alert-danger py-2 text-center small">{error}</div>
        )}

        {/* Name */}
        <div className="mb-3">
          <label className="form-label fw-semibold" style={{ color: "var(--text-primary)" }}>
            Full Name
          </label>
          <input
            type="text"
            className="form-control form-control-lg"
            name="name"
            required
            value={form.name}
            onChange={handleChange}
            style={{
              background: "var(--bg-primary)",
              color: "var(--text-primary)",
              borderColor: "var(--border-color)",
            }}
          />
        </div>

        {/* Email */}
        <div className="mb-3">
          <label className="form-label fw-semibold" style={{ color: "var(--text-primary)" }}>
            Email
          </label>
          <input
            type="email"
            className="form-control form-control-lg"
            name="email"
            required
            value={form.email}
            onChange={handleChange}
            style={{
              background: "var(--bg-primary)",
              color: "var(--text-primary)",
              borderColor: "var(--border-color)",
            }}
          />
        </div>

        {/* Mobile */}
        <div className="mb-3">
          <label className="form-label fw-semibold" style={{ color: "var(--text-primary)" }}>
            Mobile
          </label>
          <input
            type="text"
            maxLength="10"
            className="form-control form-control-lg"
            name="mobile"
            required
            value={form.mobile}
            onChange={handleChange}
            style={{
              background: "var(--bg-primary)",
              color: "var(--text-primary)",
              borderColor: "var(--border-color)",
            }}
          />
        </div>

        {/* Password */}
        <div className="mb-3 position-relative">
          <label className="form-label fw-semibold" style={{ color: "var(--text-primary)" }}>
            Password
          </label>
          <input
            type={showPassword ? "text" : "password"}
            className="form-control form-control-lg pe-5"
            name="password"
            required
            value={form.password}
            onChange={handleChange}
            style={{
              background: "var(--bg-primary)",
              color: "var(--text-primary)",
              borderColor: "var(--border-color)",
            }}
          />
          <i
            className={`bi ${
              showPassword ? "bi-eye-slash" : "bi-eye"
            } position-absolute top-50 end-0 translate-middle-y me-3`}
            onClick={() => setShowPassword(!showPassword)}
            role="button"
            style={{ color: "var(--text-secondary)", cursor: "pointer" }}
          />
        </div>

        {/* Confirm Password */}
        <div className="mb-4">
          <label className="form-label fw-semibold" style={{ color: "var(--text-primary)" }}>
            Confirm Password
          </label>
          <input
            type="password"
            className="form-control form-control-lg"
            name="confirmPassword"
            required
            value={form.confirmPassword}
            onChange={handleChange}
            style={{
              background: "var(--bg-primary)",
              color: "var(--text-primary)",
              borderColor: "var(--border-color)",
            }}
          />
        </div>

        {/* KYC Section */}
        <div
          className="p-3 mb-4 rounded-3"
          style={{
            background: "var(--bg-primary)",
            border: "1px solid var(--border-color)",
          }}
        >
          <h6 className="fw-bold mb-3" style={{ color: "var(--text-primary)" }}>
            🪪 Identity Verification (KYC)
          </h6>

          <div className="mb-3">
            <label className="form-label fw-semibold" style={{ color: "var(--text-primary)" }}>
              Aadhaar Number (Login ID)
            </label>
            <input
              type="text"
              maxLength="12"
              className="form-control form-control-lg"
              name="aadhaarNumber"
              required
              value={form.aadhaarNumber}
              onChange={handleChange}
              style={{
                background: "var(--bg-secondary)",
                color: "var(--text-primary)",
                borderColor: "var(--border-color)",
              }}
            />
          </div>

          <div>
            <label className="form-label fw-semibold" style={{ color: "var(--text-primary)" }}>
              PAN Number
            </label>
            <input
              type="text"
              className="form-control form-control-lg"
              name="pan"
              required
              value={form.pan}
              onChange={handleChange}
              style={{
                background: "var(--bg-secondary)",
                color: "var(--text-primary)",
                borderColor: "var(--border-color)",
              }}
            />
          </div>
        </div>

        <div className="d-grid">
          <button
            type="submit"
            className="btn btn-lg rounded-3"
            disabled={loading}
            style={{
              background: "linear-gradient(135deg, var(--violet) 0%, var(--violet-dark) 100%)",
              border: "none",
              color: "white",
              fontWeight: 600,
              transition: "all 0.3s ease",
            }}
            onMouseEnter={(e) => e.target.style.transform = "scale(1.02)"}
            onMouseLeave={(e) => e.target.style.transform = "scale(1)"}
          >
            {loading ? "Creating Account..." : "Create Account"}
          </button>
        </div>

        <div className="text-center mt-3 small" style={{ color: "var(--text-secondary)" }}>
          Already registered?{" "}
          <span
            className="fw-semibold"
            role="button"
            onClick={() => navigate("/login")}
            style={{ color: "var(--violet)", cursor: "pointer" }}
          >
            Login here
          </span>
        </div>
      </form>

      <style>{`
        :root {
          --violet: #6b46c1;
          --violet-dark: #553c9a;
          --violet-light: #9f7aea;
          --orange: #ed8936;
          --green: #38a169;
          --red: #e53e3e;
        }

        [data-theme="light"] {
          --bg-primary: #f8f9fc;
          --bg-secondary: #ffffff;
          --text-primary: #1a202c;
          --text-secondary: #4a5568;
          --border-color: #e2e8f0;
        }

        [data-theme="dark"] {
          --bg-primary: #0f172a;
          --bg-secondary: #1e293b;
          --text-primary: #f1f5f9;
          --text-secondary: #cbd5e0;
          --border-color: #334155;
        }

        body {
          margin: 0;
          font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
          transition: background-color 0.3s ease;
        }

        .form-control:focus {
          border-color: var(--violet);
          box-shadow: 0 0 0 0.2rem rgba(107, 70, 193, 0.25);
        }
      `}</style>
    </div>
  );
};

// Wrap with ThemeProvider for standalone usage
const SignupWithTheme = () => (
  <ThemeProvider>
    <Signup />
  </ThemeProvider>
);

export default SignupWithTheme;