import React from 'react';
import { BrowserRouter as Router, Switch, Route, Redirect } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import PrivateRoute from './components/PrivateRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import ClaimsList from './pages/ClaimsList';
import ClaimDetail from './pages/ClaimDetail';
import NewClaim from './pages/NewClaim';
import AdjusterQueue from './pages/AdjusterQueue';
import AdminDashboard from './pages/AdminDashboard';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Navbar />
        <Switch>
          <Route path="/login" component={Login} />
          <Route path="/register" component={Register} />
          <PrivateRoute path="/dashboard" component={Dashboard} />
          <PrivateRoute path="/claims/new" component={NewClaim} roles={['POLICYHOLDER']} />
          <PrivateRoute path="/claims/:id" component={ClaimDetail} />
          <PrivateRoute path="/claims" component={ClaimsList} />
          <PrivateRoute path="/adjuster/queue" component={AdjusterQueue} roles={['ADJUSTER','MANAGER','ADMIN']} />
          <PrivateRoute path="/admin" component={AdminDashboard} roles={['MANAGER','ADMIN']} />
          <Redirect from="/" to="/dashboard" exact />
          <Redirect to="/dashboard" />
        </Switch>
      </Router>
    </AuthProvider>
  );
}

export default App;
