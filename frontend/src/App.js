import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Home from "./pages/Home";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import UserDashboard from "./pages/UserDashboard";
import BuyProperty from "./pages/BuyProperty";
import MyProperties from "./pages/MyProperties";
import PropertyDetails from "./pages/PropertyDetails";
import MutationTracking from "./pages/MutationTracking";
import Transactions from "./pages/Transactions";
import MyAppointments from "./pages/MyAppointments";
import DisputeStatus from "./pages/DisputeStatus";
import InheritanceRequest from "./pages/InheritanceRequest";
import MyRequests from "./pages/MyRequests";
import DisputeRequest from "./pages/DisputeRequest";
import DisputeClosureRequest from "./pages/officer/DisputeClosureRequest";
import AdminDashboard from "./pages/admin/AdminDashboard";
import AdminAddUser from "./pages/admin/AdminAddUser";
import AdminManageUsers from "./pages/admin/AdminManageUsers";
import AdminManageOfficers from "./pages/admin/AdminManageOfficers";
import AdminDisputes from "./pages/admin/AdminDisputes";
import AdminLogs from "./pages/admin/AdminLogs";
import SubRegistrarDashboard from "./pages/officer/SubRegistrarDashboard";
import TehsilDashboard from "./pages/tehsil/TehsilDashboard";
import RaiseDispute from "./pages/disputes/RaiseDispute";
import AdminAIReview from "./pages/admin/AdminAIReview";
import PropertyDocuments from "./pages/PropertyDocuments";
import CreateProperty from "./pages/admin/create-property";
import BuyerAppointment from "./pages/BuyerAppointment";
import OwnerAppointment from "./pages/OwnerAppointment";
import OwnerRequestDetails from "./pages/OwnerRequestDetails";
import OwnerRequests from "./pages/OwnerRequests";
import BuyerCounterResponse from "./pages/BuyerCounterResponse";
import RegistrationVerification from "./pages/RegistrationVerification";
import MutationCreatePage from "./pages/MutationCreatePage";
import MyPropertyDetails from "./pages/MyPropertyDetails";
import OwnerVisitDecision from "./pages/OwnerVisitDecision";
import VerifyGift from "./pages/officer/VerifyGift";
import QrScannerPage from "./pages//QrScannerPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />

        <Route path="/dashboard" element={
          <RequireAuth><UserDashboard /></RequireAuth>
        } />

        <Route path="/buy-property" element={
          <RequireAuth><BuyProperty /></RequireAuth>
        } />

        <Route path="/my-properties" element={
          <RequireAuth><MyProperties /></RequireAuth>
        } />

        <Route path="/property/:propertyCode" element={
          <RequireAuth><PropertyDetails /></RequireAuth>
        } />

        <Route path="/mutation-tracking" element={
          <RequireAuth><MutationTracking /></RequireAuth>
        } />

        <Route
          path="/transactions"
          element={
            <RequireAuth>
              <Transactions />
            </RequireAuth>
          }
        />


<Route
  path="/my-appointments"
  element={
    <RequireAuth>
      <MyAppointments />
    </RequireAuth>
  }
/>
<Route
  path="/dispute-status"
  element={
    <RequireAuth>
      <DisputeStatus />
    </RequireAuth>
  }
/>
<Route
  path="/inheritance-request"
  element={
    <RequireAuth>
      <InheritanceRequest />
    </RequireAuth>
  }
/>
<Route
  path="/my-request"
  element={
    <RequireAuth>
      <MyRequests />
    </RequireAuth>
  }
/>

<Route path="/scan-qr" element={<QrScannerPage />} />

<Route
          path="/buyer-counter-response/:requestId"
          element={<BuyerCounterResponse />}
        />
<Route
  path="/dispute-request"
  element={
    <RequireAuth>
      <DisputeRequest />
    </RequireAuth>
  }
/>
<Route
  path="/close-dispute/:id"
  element={
    <RequireAuth>
      <DisputeClosureRequest />
    </RequireAuth>
  }
/>

<Route path="/verify-gift/:id" element={<VerifyGift />} />

<Route
  path="/admin/dashboard"
  element={
    <RequireAuth>
      <AdminDashboard />
    </RequireAuth>
  }
/>

<Route path="/admin/add-user" element={<AdminAddUser />} />
<Route path="/admin/manage-users" element={<AdminManageUsers />} />
<Route
  path="/admin/manage-officers"
  element={<AdminManageOfficers />}
/>
<Route
  path="/admin/disputes"
  element={<AdminDisputes />}
/>
<Route
  path="/admin/logs"
  element={<AdminLogs />}
/>
<Route path="/sub-registrar/dashboard" element={<SubRegistrarDashboard />} />
<Route path="/tehsil/dashboard" element={<TehsilDashboard />} />
<Route path="/disputes/raise" element={<RaiseDispute />} />
<Route path="/admin/ai-review" element={<AdminAIReview />} />

<Route
  path="/property/:propertyCode/documents"
  element={
    <RequireAuth>
      <PropertyDocuments />
    </RequireAuth>
  }
/>
<Route path="/admin/create-property" element={<CreateProperty />} />

<Route
  path="/buyer-appointment/:requestId"
  element={<RequireAuth><BuyerAppointment /></RequireAuth>}
/>

<Route
  path="/owner-appointment/:requestId"
  element={<RequireAuth><OwnerAppointment /></RequireAuth>}
/>
<Route
  path="/owner-request/:requestId"
  element={
    <RequireAuth>
      <OwnerRequestDetails />
    </RequireAuth>
  }
/>
<Route
  path="/owner-requests"
  element={
    <RequireAuth>
      <OwnerRequests />
    </RequireAuth>
  }
/>

<Route
  path="/registration-verify/:id"
  element={
    <RequireAuth>
      <RegistrationVerification />
    </RequireAuth>
  }
/>
<Route
  path="/mutation-process/:mutationId"
  element={
    <RequireAuth>
      <MutationCreatePage />
    </RequireAuth>
  }
/>

<Route path="/my-property-details/:propertyCode" element={<MyPropertyDetails />} />

<Route
  path="/owner-visit/:requestId"
  element={<OwnerVisitDecision />}
/>

      </Routes>
     

    </BrowserRouter>
    
  );
}

function RequireAuth({ children }) {
  const token = localStorage.getItem("token");
  return token ? children : <Navigate to="/login" replace />;
}

export default App;
