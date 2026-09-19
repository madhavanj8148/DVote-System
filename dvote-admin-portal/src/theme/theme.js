import { createTheme } from '@mui/material/styles';

const getDesignTokens = (mode) => ({
  palette: {
    mode,
    primary: {
      main: '#1565C0',
      light: '#42a5f5',
      dark: '#0d47a1',
      contrastText: '#ffffff',
    },
    secondary: {
      main: '#00C853',
      light: '#69f0ae',
      dark: '#00c853',
      contrastText: '#ffffff',
    },
    accent: {
      main: '#FFC107',
    },
    background: {
      default: mode === 'light' ? '#F5F7FA' : '#0a1929',
      paper: mode === 'light' ? '#ffffff' : '#001e3c',
      sidebar: mode === 'light' ? '#0F172A' : '#000000',
    },
    text: {
      primary: mode === 'light' ? '#212121' : '#f3f6f9',
      secondary: mode === 'light' ? '#616161' : '#b2bac2',
    },
  },
  typography: {
    fontFamily: '"Plus Jakarta Sans", "Inter", "Roboto", sans-serif',
    h1: { fontWeight: 800 },
    h2: { fontWeight: 700 },
    h3: { fontWeight: 700 },
    h4: { fontWeight: 700 },
    h5: { fontWeight: 600 },
    h6: { fontWeight: 600 },
    button: { textTransform: 'none', fontWeight: 600 },
  },
  shape: {
    borderRadius: 12,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          padding: '8px 20px',
          boxShadow: 'none',
          '&:hover': {
            boxShadow: '0px 4px 12px rgba(0, 0, 0, 0.1)',
          },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          boxShadow: mode === 'light'
            ? '0px 2px 4px rgba(0, 0, 0, 0.05), 0px 1px 2px rgba(0, 0, 0, 0.1)'
            : '0px 2px 4px rgba(0, 0, 0, 0.2)',
          border: mode === 'light' ? '1px solid #f0f0f0' : '1px solid #1e4976',
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundColor: mode === 'light' ? 'rgba(255, 255, 255, 0.8)' : 'rgba(10, 25, 41, 0.8)',
          backdropFilter: 'blur(12px)',
          color: mode === 'light' ? '#212121' : '#ffffff',
          boxShadow: 'none',
          borderBottom: mode === 'light' ? '1px solid #f0f0f0' : '1px solid #1e4976',
        },
      },
    },
  },
});

export const theme = (mode) => createTheme(getDesignTokens(mode));
