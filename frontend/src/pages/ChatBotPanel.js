import React, { useState } from "react";
import axios from "axios";

const ChatBotPanel = ({ isOpen, onClose }) => {
  const [messages, setMessages] = useState([
    { text: "Hello 👋 I am BhoomiDarpan AI Assistant.", sender: "bot" }
  ]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);

  const suggestions = [
    "Show my properties",
    "Check PROP001",
    "What is mutation?",
    "Explain blockchain"
  ];

  const sendMessage = async (msgText) => {
    const messageToSend = msgText || input;

    if (!messageToSend.trim()) return;

    const userMsg = { text: messageToSend, sender: "user" };
    setMessages(prev => [...prev, userMsg]);
    setInput("");
    setLoading(true);

    try {
      const res = await axios.post(
        "http://localhost:8080/api/chat",
        { message: messageToSend },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
          }
        }
      );

      setMessages(prev => [
        ...prev,
        { text: res.data.reply, sender: "bot" }
      ]);
    } catch (error) {
      console.error(error);

      let errorMsg = "Something went wrong ❌";

      if (error.response?.status === 403) {
        errorMsg = "Unauthorized ❌ Please login again.";
      } else if (error.response?.status === 500) {
        errorMsg = "Server error ❌ Try later.";
      }

      setMessages(prev => [...prev, { text: errorMsg, sender: "bot" }]);
    }

    setLoading(false);
  };

  if (!isOpen) return null;

  return (
    <div style={styles.container}>
      {/* HEADER */}
      <div style={styles.header}>
        <span>💬 BhoomiDarpan Assistant</span>
        <button style={styles.closeBtn} onClick={onClose}>✖</button>
      </div>

      {/* CHAT AREA */}
      <div style={styles.chatBox}>
        {messages.map((msg, i) => (
          <div
            key={i}
            style={{
              ...styles.msg,
              alignSelf: msg.sender === "user" ? "flex-end" : "flex-start",
              background: msg.sender === "user" ? "#6b46c1" : "#eee",
              color: msg.sender === "user" ? "#fff" : "#000"
            }}
          >
            {msg.text}
          </div>
        ))}

        {loading && (
          <div style={styles.loading}>Typing...</div>
        )}
      </div>

      {/* SUGGESTIONS */}
      <div style={styles.suggestions}>
        {suggestions.map((s, i) => (
          <button
            key={i}
            style={styles.suggestionBtn}
            onClick={() => sendMessage(s)}
          >
            {s}
          </button>
        ))}
      </div>

      {/* INPUT */}
      <div style={styles.inputBox}>
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && sendMessage()}
          placeholder="Ask anything..."
          style={styles.input}
        />
        <button style={styles.sendBtn} onClick={() => sendMessage()}>
          Send
        </button>
      </div>
    </div>
  );
};

/* ================= STYLES ================= */
const styles = {
  container: {
    position: "fixed",
    right: "20px",
    bottom: "20px",
    width: "340px",
    height: "460px",
    background: "#fff",
    borderRadius: "12px",
    boxShadow: "0 0 20px rgba(0,0,0,0.3)",
    display: "flex",
    flexDirection: "column",
    zIndex: 9999
  },
  header: {
    background: "#6b46c1",
    color: "#fff",
    padding: "10px",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center"
  },
  closeBtn: {
    background: "transparent",
    border: "none",
    color: "#fff",
    cursor: "pointer"
  },
  chatBox: {
    flex: 1,
    padding: "10px",
    overflowY: "auto",
    display: "flex",
    flexDirection: "column"
  },
  msg: {
    padding: "8px",
    borderRadius: "10px",
    marginBottom: "8px",
    maxWidth: "80%",
    fontSize: "14px"
  },
  loading: {
    fontSize: "12px",
    color: "#888"
  },
  suggestions: {
    padding: "5px",
    display: "flex",
    flexWrap: "wrap",
    gap: "5px"
  },
  suggestionBtn: {
    fontSize: "12px",
    padding: "5px 8px",
    borderRadius: "8px",
    border: "1px solid #ccc",
    cursor: "pointer",
    background: "#f5f5f5"
  },
  inputBox: {
    display: "flex",
    borderTop: "1px solid #ccc"
  },
  input: {
    flex: 1,
    padding: "10px",
    border: "none",
    outline: "none"
  },
  sendBtn: {
    background: "#6b46c1",
    color: "#fff",
    border: "none",
    padding: "10px",
    cursor: "pointer"
  }
};

export default ChatBotPanel;