import { useState, useContext, createContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "bootstrap-icons/font/bootstrap-icons.css";

// Theme Context (reuse from Home or define separately)
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

const Login = () => {
  const { darkMode, toggleTheme } = useTheme();
  const [aadhaarNumber, setAadhaarNumber] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    if (!/^\d{12}$/.test(aadhaarNumber)) {
      setError("Aadhaar number must be exactly 12 digits");
      return;
    }

    try {
      const response = await fetch("https://bhoomidarpan-5.onrender.com/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ aadhaarNumber, password }),
      });

      if (!response.ok) {
        throw new Error("Invalid Aadhaar number or password");
      }

      const data = await response.json();

      localStorage.setItem("token", data.token);
      localStorage.setItem("user", JSON.stringify(data));

      switch (data.role) {
        case "ADMIN":
          navigate("/admin/dashboard");
          break;
        case "SUB_REGISTRAR":
          navigate("/sub-registrar/dashboard");
          break;
        case "TEHSILDAR":
          navigate("/tehsil/dashboard");
          break;
        default:
          navigate("/dashboard");
      }
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div
      className="d-flex align-items-center justify-content-center vh-100"
      style={{
        backgroundColor: "#6b46c1", // Solid violet background matching Home
        transition: "background-color 0.3s ease",
      }}
    >
      {/* Theme Toggle Button (position absolute) */}
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
        className="p-5 shadow-lg rounded-4"
        style={{
          width: "420px",
          background: "var(--bg-secondary)",
          backdropFilter: "blur(10px)",
          border: "1px solid var(--border-color)",
          transition: "all 0.3s ease",
        }}
        onSubmit={handleLogin}
      >
        <div className="text-center mb-4">
          <i className="bi bi-building fs-1" style={{ color: "var(--violet)" }}></i>
          <h3 className="mt-2 fw-bold" style={{ color: "var(--text-primary)" }}>
            BhoomiDarpan
          </h3>
          <p className="small" style={{ color: "var(--text-secondary)" }}>
            Secure Land Records Portal
          </p>
        </div>

        {/* Aadhaar Input */}
        <div className="mb-3">
          <label className="form-label fw-semibold" style={{ color: "var(--text-primary)" }}>
            <i className="bi bi-person-vcard me-2"></i>
            Aadhaar Number
          </label>
          <input
            type="text"
            maxLength="12"
            className="form-control form-control-lg"
            placeholder="Enter 12-digit Aadhaar"
            value={aadhaarNumber}
            onChange={(e) => setAadhaarNumber(e.target.value.replace(/\D/g, ""))}
            required
            style={{
              background: "var(--bg-primary)",
              color: "var(--text-primary)",
              borderColor: "var(--border-color)",
            }}
          />
        </div>

        {/* Password Input */}
        <div className="mb-3 position-relative">
          <label className="form-label fw-semibold" style={{ color: "var(--text-primary)" }}>
            <i className="bi bi-lock me-2"></i>
            Password
          </label>
          <input
            type={showPassword ? "text" : "password"}
            className="form-control form-control-lg pe-5"
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
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

        {/* Error Message */}
        {error && (
          <div className="alert alert-danger py-2 small">{error}</div>
        )}

        {/* Login Button */}
        <div className="d-grid mt-4">
          <button
            type="submit"
            className="btn btn-lg rounded-3"
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
            Login
          </button>
        </div>

        {/* Links */}
        <div className="text-center mt-3">
          <a href="/forgot-password" className="small" style={{ color: "var(--violet)" }}>
            Forgot Password?
          </a>
          <br />
          <a href="/signup" className="small" style={{ color: "var(--violet)" }}>
            Create new account
          </a>
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

        a {
          text-decoration: none;
          transition: color 0.2s;
        }
        a:hover {
          color: var(--violet-dark);
          text-decoration: underline;
        }
      `}</style>
    </div>
  );
};

// Wrap with ThemeProvider for standalone usage
const LoginWithTheme = () => (
  <ThemeProvider>
    <Login />
  </ThemeProvider>
);

export default LoginWithTheme;
