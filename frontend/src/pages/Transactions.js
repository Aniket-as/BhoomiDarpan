import React, { useEffect, useState } from "react";
import { Container, Table, Badge, Navbar } from "react-bootstrap";
import { Link } from "react-router-dom";
import "bootstrap-icons/font/bootstrap-icons.css";

const API_BASE = "http://localhost:8080/api";

const Transactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");

    fetch(`${API_BASE}/transactions/my`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed");
        return res.json();
      })
      .then((data) => {
        setTransactions(data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  const badgeVariant = (status) => {
    if (status === "FINALIZED") return "success";
    if (status === "ON_HOLD") return "danger";
    return "warning";
  };

  return (
    <>
      <Navbar className="px-4 py-3">
        <span className="navbar-brand">📑 BhoomiDarpan – Transactions</span>
      </Navbar>

      <Container className="mt-5">
        <div className="glass p-4 mb-4">
          <h4 style={{ fontFamily: "Questrial" }}>My Transactions</h4>
          <p className="text-muted mb-0">
            Track the complete lifecycle of your property transactions.
          </p>
        </div>

        <div className="glass p-4">
          {loading ? (
            <p>Loading transactions...</p>
          ) : (
            <Table hover responsive alignMiddle>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Property</th>
                  <th>Buyer</th>
                  <th>Seller</th>
                  <th>Stage</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>

              <tbody>
                {transactions.map((t) => (
                  <tr key={t.transactionId}>
                    <td>TXN-{t.transactionId}</td>
                    <td>{t.propertyLocation}</td>
                    <td>{t.buyerName}</td>
                    <td>{t.sellerName}</td>
                    <td>{t.currentStage}</td>
                    <td>
                      <Badge bg={badgeVariant(t.status)}>
                        {t.status.replace("_", " ")}
                      </Badge>
                    </td>
                    <td>
                      <Link
                        to={`/transaction/${t.transactionId}`}
                        className="btn btn-violet btn-sm"
                      >
                        View
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          )}
        </div>
      </Container>

      <div className="footer mt-5 text-center">
        © 2026 BhoomiDarpan • Transparent • Trackable • Secure
      </div>
    </>
  );
};

export default Transactions;
