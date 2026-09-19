import React, { useState } from 'react';
import {
  Box, Drawer, AppBar, Toolbar, List, Typography, Divider, IconButton,
  ListItem, ListItemButton, ListItemIcon, ListItemText, Avatar,
  Tooltip, Badge, InputBase, Menu, MenuItem, useTheme, useMediaQuery
} from '@mui/material';
import {
  Menu as MenuIcon, Dashboard, People, HowToVote, Map, Assessment,
  Settings, ExitToApp, Search, Notifications, DarkMode, LightMode,
  Language, ChevronLeft, Person
} from '@mui/icons-material';
import { useNavigate, useLocation } from 'react-router-dom';
import { signOut } from 'firebase/auth';
import { auth } from '../firebaseConfig';
import { useThemeMode } from '../context/ThemeContext';
import { motion, AnimatePresence } from 'framer-motion';

const drawerWidth = 280;

const DashboardLayout = ({ children }) => {
  const [open, setOpen] = useState(true);
  const [anchorEl, setAnchorEl] = useState(null);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const navigate = useNavigate();
  const location = useLocation();
  const { mode, toggleTheme } = useThemeMode();

  const handleDrawerToggle = () => setOpen(!open);
  const handleProfileMenuOpen = (event) => setAnchorEl(event.currentTarget);
  const handleProfileMenuClose = () => setAnchorEl(null);

  const handleLogout = async () => {
    await signOut(auth);
    navigate('/login');
  };

  const menuItems = [
    { text: 'Dashboard', icon: <Dashboard />, path: '/' },
    { text: 'Users', icon: <People />, path: '/users' },
    { text: 'Candidates', icon: <Person />, path: '/candidates' },
    { text: 'Elections', icon: <HowToVote />, path: '/elections' },
    { text: 'Locations', icon: <Map />, path: '/locations' },
    { text: 'Results', icon: <Assessment />, path: '/results' },
    { text: 'Settings', icon: <Settings />, path: '/settings' },
  ];

  const drawerContent = (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column', bgcolor: 'background.sidebar', color: '#fff' }}>
      <Toolbar sx={{ px: [1], display: 'flex', alignItems: 'center', justifyContent: 'center', py: 2 }}>
        <motion.div initial={{ scale: 0.8 }} animate={{ scale: 1 }} transition={{ duration: 0.5 }}>
           <Typography variant="h5" sx={{ fontWeight: 800, letterSpacing: 1, color: 'primary.light' }}>
             DVOTE<span style={{ color: '#fff' }}>ADMIN</span>
           </Typography>
        </motion.div>
      </Toolbar>
      <Divider sx={{ bgcolor: 'rgba(255,255,255,0.1)' }} />
      <List sx={{ px: 2, py: 2, flexGrow: 1 }}>
        {menuItems.map((item) => (
          <ListItem key={item.text} disablePadding sx={{ mb: 1 }}>
            <ListItemButton
              onClick={() => navigate(item.path)}
              selected={location.pathname === item.path}
              sx={{
                borderRadius: 2,
                '&.Mui-selected': {
                  bgcolor: 'primary.main',
                  '&:hover': { bgcolor: 'primary.dark' },
                },
                '&:hover': { bgcolor: 'rgba(255,255,255,0.05)' },
              }}
            >
              <ListItemIcon sx={{ color: location.pathname === item.path ? '#fff' : 'rgba(255,255,255,0.7)', minWidth: 40 }}>
                {item.icon}
              </ListItemIcon>
              <ListItemText
                primary={item.text}
                primaryTypographyProps={{
                  fontWeight: location.pathname === item.path ? 600 : 400,
                  fontSize: '0.9rem'
                }}
              />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
      <Box sx={{ p: 2 }}>
        <ListItemButton
          onClick={handleLogout}
          sx={{ borderRadius: 2, color: '#ff4d4d', '&:hover': { bgcolor: 'rgba(255,77,77,0.1)' } }}
        >
          <ListItemIcon sx={{ color: 'inherit', minWidth: 40 }}><ExitToApp /></ListItemIcon>
          <ListItemText primary="Logout" />
        </ListItemButton>
      </Box>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: 'background.default' }}>
      <AppBar
        position="fixed"
        sx={{
          zIndex: (theme) => theme.zIndex.drawer + 1,
          width: { md: `calc(100% - ${open ? drawerWidth : 0}px)` },
          ml: { md: `${open ? drawerWidth : 0}px)` },
          transition: theme.transitions.create(['width', 'margin'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.enteringScreen,
          }),
        }}
      >
        <Toolbar sx={{ justifyContent: 'space-between' }}>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <IconButton edge="start" color="inherit" onClick={handleDrawerToggle} sx={{ mr: 2 }}>
              {open ? <ChevronLeft /> : <MenuIcon />}
            </IconButton>
            <Box sx={{
              display: { xs: 'none', sm: 'flex' },
              alignItems: 'center',
              bgcolor: mode === 'light' ? '#f0f2f5' : 'rgba(255,255,255,0.05)',
              px: 2, py: 0.5, borderRadius: 2, width: 300
            }}>
              <Search sx={{ color: 'text.secondary', mr: 1, fontSize: 20 }} />
              <InputBase placeholder="Search anything..." sx={{ fontSize: '0.9rem', width: '100%' }} />
            </Box>
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Typography variant="body2" sx={{ display: { xs: 'none', lg: 'block' }, color: 'text.secondary', mr: 2 }}>
              {new Date().toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
            </Typography>
            <Tooltip title="Language">
              <IconButton color="inherit"><Language /></IconButton>
            </Tooltip>
            <Tooltip title="Toggle Theme">
              <IconButton color="inherit" onClick={toggleTheme}>
                {mode === 'light' ? <DarkMode /> : <LightMode />}
              </IconButton>
            </Tooltip>
            <Tooltip title="Notifications">
              <IconButton color="inherit">
                <Badge badgeContent={4} color="error"><Notifications /></Badge>
              </IconButton>
            </Tooltip>
            <IconButton onClick={handleProfileMenuOpen} sx={{ p: 0.5, ml: 1 }}>
              <Avatar sx={{ width: 35, height: 35, bgcolor: 'primary.main' }}>A</Avatar>
            </IconButton>
            <Menu
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={handleProfileMenuClose}
              transformOrigin={{ horizontal: 'right', vertical: 'top' }}
              anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
            >
              <MenuItem onClick={handleProfileMenuClose}><Person sx={{ mr: 1 }} /> Profile</MenuItem>
              <MenuItem onClick={handleLogout}><ExitToApp sx={{ mr: 1 }} /> Logout</MenuItem>
            </Menu>
          </Box>
        </Toolbar>
      </AppBar>

      <Box
        component="nav"
        sx={{ width: { md: open ? drawerWidth : 0 }, flexShrink: { md: 0 }, transition: 'width 0.3s' }}
      >
        <Drawer
          variant={isMobile ? "temporary" : "persistent"}
          open={open}
          onClose={handleDrawerToggle}
          sx={{
            '& .MuiDrawer-paper': {
              width: drawerWidth,
              boxSizing: 'border-box',
              borderRight: 'none',
              boxShadow: '4px 0 10px rgba(0,0,0,0.05)'
            },
          }}
        >
          {drawerContent}
        </Drawer>
      </Box>

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: { md: `calc(100% - ${open ? drawerWidth : 0}px)` },
          mt: 8,
          transition: 'all 0.3s'
        }}
      >
        <AnimatePresence mode="wait">
          <motion.div
            key={location.pathname}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.3 }}
          >
            {children}
          </motion.div>
        </AnimatePresence>
      </Box>
    </Box>
  );
};

export default DashboardLayout;
