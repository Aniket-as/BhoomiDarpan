import React, { useEffect, useState } from "react";
import { Container, Navbar, Form, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const API_BASE = "http://localhost:8080/api";

const GiftDeedRequest = () => {
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const [properties, setProperties] = useState([]);
  const [ownerName, setOwnerName] = useState("");

  const [form, setForm] = useState({
    propertyId: "",
    relationship: "",
    childName: "",
    childAadhaar: "",
    childPan: "",
    transferReason: "",
    giftDeedDocument: null,
    declaration: false,
  });

  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }

    fetch(`${API_BASE}/properties/my-properties`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => res.json())
      .then(setProperties);
  }, [navigate, token]);

  const handleChange = async (e) => {
    const { name, value, files, type, checked } = e.target;

    if (name === "propertyId") {
      const selectedProperty = properties.find(p => p.id == value);

      if (selectedProperty) {
        const res = await fetch(
          `${API_BASE}/properties/${selectedProperty.propertyCode}/owner`,
          { headers: { Authorization: `Bearer ${token}` } }
        );

        const data = await res.text();
        setOwnerName(data);

        setForm(prev => ({
          ...prev,
          propertyId: value
        }));
      }
    } else {
      setForm(prev => ({
        ...prev,
        [name]:
          type === "file"
            ? files[0]
            : type === "checkbox"
            ? checked
            : value
      }));
    }
  };

  const handleSubmit = async () => {
    try {
      const data = new FormData();

      data.append("propertyId", form.propertyId);
      data.append("relationship", form.relationship);
      data.append("childName", form.childName);
      data.append("childAadhaar", form.childAadhaar);
      data.append("childPan", form.childPan);
      data.append("transferReason", form.transferReason);
      data.append("giftDeedDocument", form.giftDeedDocument);
      data.append("declaration", form.declaration);

      const res = await fetch(`${API_BASE}/gift-deed/request`, {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
        body: data
      });

      const text = await res.text();

      if (!res.ok) throw new Error(text);

      alert("Gift Deed submitted");
      navigate("/dashboard");

    } catch (err) {
      alert(err.message);
    }
  };

  return (
    <>
      <Navbar className="px-4 py-3">
        <span className="navbar-brand">🎁 Gift Deed Transfer</span>
      </Navbar>

      <Container className="mt-5">

        <Form.Select name="propertyId" onChange={handleChange}>
          <option>Select Property</option>
          {properties.map(p => (
            <option key={p.id} value={p.id}>
              {p.propertyCode} - {p.location}
            </option>
          ))}
        </Form.Select>

        <Form.Control value={ownerName} readOnly className="mt-3" />

        <Form.Control name="relationship" placeholder="Relationship" className="mt-3" onChange={handleChange}/>
        <Form.Control name="childName" placeholder="Child Name" className="mt-2" onChange={handleChange}/>
        <Form.Control name="childAadhaar" placeholder="Aadhaar" className="mt-2" onChange={handleChange}/>
        <Form.Control name="childPan" placeholder="PAN" className="mt-2" onChange={handleChange}/>

        <Form.Control type="file" name="giftDeedDocument" className="mt-3" onChange={handleChange}/>

        <Form.Check name="declaration" label="Confirm transfer" className="mt-3" onChange={handleChange}/>

        <Button className="w-100 mt-4" onClick={handleSubmit}>
          Submit
        </Button>
      </Container>
    </>
  );
};

export default GiftDeedRequest;