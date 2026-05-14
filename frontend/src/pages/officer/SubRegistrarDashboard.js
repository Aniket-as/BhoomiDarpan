import { useEffect, useState } from "react";
import {
  Container,
  Row,
  Col,
  Navbar,
  Table,
  Badge,
  Button,
  Spinner,
  Alert,
  Card
} from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { toast } from "react-hot-toast";

const API_BASE = "http://localhost:8080/api";

const SubRegistrarDashboard = () => {

  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const [appointments, setAppointments] = useState([]);
  const [pendingVerification, setPendingVerification] = useState([]);
  const [pendingDisputes, setPendingDisputes] = useState([]);
  const [activeDisputes, setActiveDisputes] = useState([]);
  const [inheritanceRequests, setInheritanceRequests] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [view, setView] = useState("dashboard");

  const headers = {
    Authorization: `Bearer ${token}`,
    "Content-Type": "application/json"
  };

  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      const [
        todayRes,
        pendingRes,
        pendingDisputeRes,
        activeDisputeRes,
        inheritanceRes
      ] = await Promise.all([
        fetch(`${API_BASE}/registration/todays-appointments`, { headers }),
        fetch(`${API_BASE}/registration/pending-verification`, { headers }),
        fetch(`${API_BASE}/dispute/pending`, { headers }),
        fetch(`${API_BASE}/dispute/active`, { headers }),
        fetch(`${API_BASE}/gift-deed/pending`, { headers })
      ]);

      if (
        !todayRes.ok ||
        !pendingRes.ok ||
        !pendingDisputeRes.ok ||
        !activeDisputeRes.ok ||
        !inheritanceRes.ok
      ) {
        throw new Error("Failed to load dashboard data");
      }

      setAppointments(await todayRes.json());
      setPendingVerification(await pendingRes.json());
      setPendingDisputes(await pendingDisputeRes.json());
      setActiveDisputes(await activeDisputeRes.json());
      setInheritanceRequests(await inheritanceRes.json());

    } catch (err) {
      setError(err.message);
      toast.error(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCloseClick = (disputeId) => {
    if (!window.confirm("Close this dispute?")) return;
    navigate(`/close-dispute/${disputeId}`);
  };

  return (
    <>
      <Navbar className="px-4 py-3 bg-dark text-white">
        <span className="navbar-brand text-white fw-bold">
          🔗 BhoomiDarpan – Sub-Registrar
        </span>
      </Navbar>

      <Container className="mt-4">

        {loading && <Spinner />}
        {error && <Alert variant="danger">{error}</Alert>}

        {!loading && !error && (
          <>
            {/* DASHBOARD CARDS */}
            {view === "dashboard" && (
              <Row className="g-4">

                <Web3Card
                  title="Today's Appointments"
                  count={appointments.length}
                  onClick={() => setView("appointments")}
                />

                <Web3Card
                  title="Pending Verification"
                  count={pendingVerification.length}
                  onClick={() => setView("verification")}
                />

                <Web3Card
                  title="Inheritance Requests"
                  count={inheritanceRequests.length}
                  onClick={() => setView("inheritance")}
                />

                <Web3Card
                  title="Pending Disputes"
                  count={pendingDisputes.length}
                  onClick={() => setView("pendingDisputes")}
                />

                <Web3Card
                  title="Active Disputes"
                  count={activeDisputes.length}
                  onClick={() => setView("activeDisputes")}
                />

              </Row>
            )}

            {/* TABLE VIEW */}
            {view !== "dashboard" && (
              <Card className="mt-3 shadow-sm">

                <Card.Header className="d-flex justify-content-between">
                  <strong>
                    {view === "appointments" && "📅 Today's Appointments"}
                    {view === "verification" && "⏳ Pending Verification"}
                    {view === "inheritance" && "🕊️ Inheritance Requests"}
                    {view === "pendingDisputes" && "⚖️ Pending Disputes"}
                    {view === "activeDisputes" && "🔥 Active Disputes"}
                  </strong>

                  <Button size="sm" onClick={() => setView("dashboard")}>
                    ⬅ Back
                  </Button>
                </Card.Header>

                <Card.Body>
                  <Table hover responsive>

                    <thead>

                      {/* APPOINTMENTS */}
                      {view === "appointments" && (
                        <tr>
                          <th>Property Code</th>
                          <th>Buyer</th>
                          <th>Date & Time</th>
                          <th>Status</th>
                          <th>Action</th>
                        </tr>
                      )}

                      {/* INHERITANCE */}
                      {view === "inheritance" && (
                        <tr>
                          <th>ID</th>
                          <th>Property</th>
                          <th>Donor</th>
                          <th>Child</th>
                          <th>Status</th>
                          <th>Action</th>
                        </tr>
                      )}

                      {/* DISPUTES */}
                      {(view === "pendingDisputes" || view === "activeDisputes") && (
                        <tr>
                          <th>Code</th>
                          <th>Property</th>
                          <th>Raised By</th>
                          <th>Case</th>
                          <th>Status</th>
                          {view === "activeDisputes" && <th>Action</th>}
                        </tr>
                      )}

                    </thead>

                    <tbody>

                      {/* APPOINTMENTS */}
                      {view === "appointments" &&
                        appointments.map((a) => (
                          <tr key={a.id}>
                            <td>{a.propertyCode}</td>
                            <td>{a.buyerName}</td>
                            <td>{new Date(a.appointmentDate).toLocaleString()}</td>
                            <td><Badge bg="success">{a.status}</Badge></td>

                            <td>
                              <Button
                                size="sm"
                                variant="primary"
                               onClick={() => navigate(`/registration-verify/${a.id}`)}
                              >
                                ✔ Verify
                              </Button>
                            </td>
                          </tr>
                        ))}

                      {/* INHERITANCE */}
                      {view === "inheritance" &&
                        inheritanceRequests.map(req => (
                          <tr key={req.id}>
                            <td>{req.id}</td>
                            <td>{req.propertyCode}</td>
                            <td>{req.donorName}</td>
                            <td>{req.childName}</td>
                            <td><Badge bg="info">{req.status}</Badge></td>
                            <td>
                              <Button
                                size="sm"
                                onClick={() => navigate(`/verify-gift/${req.id}`)}
                              >
                                Verify
                              </Button>
                            </td>
                          </tr>
                        ))}

                      {/* DISPUTES */}
                      {(view === "pendingDisputes" ? pendingDisputes :
                        view === "activeDisputes" ? activeDisputes : [])
                        .map(d => (
                          <tr key={d.id}>
                            <td>{d.disputeCode}</td>
                            <td>{d.propertyCode}</td>
                            <td>{d.raisedByName}</td>
                            <td>{d.caseNumber}</td>
                            <td>
                              <Badge bg={view === "pendingDisputes" ? "warning" : "danger"}>
                                {d.status}
                              </Badge>
                            </td>

                            {view === "activeDisputes" && (
                              <td>
                                <Button
                                  size="sm"
                                  variant="success"
                                  onClick={() => handleCloseClick(d.id)}
                                >
                                  Close
                                </Button>
                              </td>
                            )}
                          </tr>
                        ))}

                    </tbody>
                  </Table>
                </Card.Body>
              </Card>
            )}
          </>
        )}
      </Container>
    </>
  );
};

const Web3Card = ({ title, count, onClick }) => (
  <Col xs={12} md={6}>
    <div
      className="p-4 text-white bg-dark rounded"
      style={{ cursor: "pointer" }}
      onClick={onClick}
    >
      <h5>{title}</h5>
      <h2>{count}</h2>
    </div>
  </Col>
);

export default SubRegistrarDashboard;