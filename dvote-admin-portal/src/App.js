import React, { useState, useEffect, useMemo } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider as MuiThemeProvider, CssBaseline } from '@mui/material';
import { onAuthStateChanged } from 'firebase/auth';
import { auth } from './firebaseConfig';

import { ThemeProvider, useThemeMode } from './context/ThemeContext';
import { theme } from './theme/theme';

import DashboardLayout from './components/DashboardLayout';
import LoginPage from './pages/LoginPage';
import Dashboard from './pages/Dashboard';
import Elections from './pages/Elections';
import Candidates from './pages/Candidates';
import Users from './pages/Users';
import Results from './pages/Results';

const AppContent = () => {
  const { mode } = useThemeMode();
  const currentTheme = useMemo(() => theme(mode), [mode]);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    return onAuthStateChanged(auth, (u) => {
      setUser(u);
      setLoading(false);
    });
  }, []);

  if (loading) return null;

  return (
    <MuiThemeProvider theme={currentTheme}>
      <CssBaseline />
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={!user ? <LoginPage /> : <Navigate to="/" />} />
          <Route path="/" element={user ? <DashboardLayout><Dashboard /></DashboardLayout> : <Navigate to="/login" />} />
          <Route path="/elections" element={user ? <DashboardLayout><Elections /></DashboardLayout> : <Navigate to="/login" />} />
          <Route path="/candidates" element={user ? <DashboardLayout><Candidates /></DashboardLayout> : <Navigate to="/login" />} />
          <Route path="/users" element={user ? <DashboardLayout><Users /></DashboardLayout> : <Navigate to="/login" />} />
          <Route path="/results" element={user ? <DashboardLayout><Results /></DashboardLayout> : <Navigate to="/login" />} />
        </Routes>
      </BrowserRouter>
    </MuiThemeProvider>
  );
};

function App() {
  return (
    <ThemeProvider>
      <AppContent />
    </ThemeProvider>
  );
}

export default App;
