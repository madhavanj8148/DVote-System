import React, { useState, useEffect } from 'react';
import {
  Box, Grid, Card, CardContent, Typography, LinearProgress,
  Avatar, useTheme, Chip, IconButton, Button
} from '@mui/material';
import {
  People, HowToVote, Person, Public,
  TrendingUp, EventNote, MoreVert, ArrowUpward
} from '@mui/icons-material';
import { ref, onValue } from 'firebase/database';
import { db } from '../firebaseConfig';
import { motion } from 'framer-motion';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar } from 'recharts';
import CountUp from 'react-countup';

const StatCard = ({ title, value, icon, color, trend }) => {
  const theme = useTheme();
  return (
    <Card sx={{ height: '100%', position: 'relative', overflow: 'hidden' }}>
      <Box sx={{
        position: 'absolute', top: -10, right: -10,
        width: 100, height: 100, borderRadius: '50%',
        background: `${color}15`, zIndex: 0
      }} />
      <CardContent sx={{ position: 'relative', zIndex: 1 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
          <Avatar sx={{ bgcolor: `${color}20`, color: color, width: 50, height: 50 }}>
            {icon}
          </Avatar>
          {trend && (
            <Chip
              icon={<ArrowUpward style={{ fontSize: 14, color: '#00C853' }} />}
              label={`${trend}%`}
              size="small"
              sx={{ bgcolor: '#00C85310', color: '#00C853', fontWeight: 'bold' }}
            />
          )}
        </Box>
        <Typography variant="body2" color="text.secondary" gutterBottom sx={{ fontWeight: 600, letterSpacing: 0.5 }}>
          {title.toUpperCase()}
        </Typography>
        <Typography variant="h4" sx={{ fontWeight: 800 }}>
          <CountUp end={value} duration={2} separator="," />
        </Typography>
      </CardContent>
    </Card>
  );
};

const Dashboard = () => {
  const [stats, setStats] = useState({
    users: 0,
    candidates: 0,
    elections: 0,
    votes: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const usersRef = ref(db, 'users');
    const candRef = ref(db, 'candidates');
    const elecRef = ref(db, 'elections');

    // Simulating aggregation since we can't easily count in RTDB without fetching all
    onValue(usersRef, (snapshot) => {
      setStats(prev => ({ ...prev, users: snapshot.exists() ? Object.keys(snapshot.val()).length : 0 }));
    });
    onValue(candRef, (snapshot) => {
      setStats(prev => ({ ...prev, candidates: snapshot.exists() ? Object.keys(snapshot.val()).length : 0 }));
    });
    onValue(elecRef, (snapshot) => {
      setStats(prev => ({ ...prev, elections: snapshot.exists() ? Object.keys(snapshot.val()).length : 0 }));
    });

    setLoading(false);
  }, []);

  const chartData = [
    { name: 'Mon', votes: 4000 },
    { name: 'Tue', votes: 3000 },
    { name: 'Wed', votes: 5000 },
    { name: 'Thu', votes: 2780 },
    { name: 'Fri', votes: 1890 },
    { name: 'Sat', votes: 2390 },
    { name: 'Sun', votes: 3490 },
  ];

  if (loading) return <LinearProgress />;

  return (
    <Box>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800, color: 'primary.main' }}>
            Election Dashboard
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Welcome back, Admin. Here's what's happening today.
          </Typography>
        </Box>
        <Button variant="contained" startIcon={<TrendingUp />}>Generate Report</Button>
      </Box>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard title="Total Voters" value={stats.users} icon={<People />} color="#1565C0" trend={12} />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard title="Active Elections" value={stats.elections} icon={<HowToVote />} color="#00C853" trend={5} />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard title="Candidates" value={stats.candidates} icon={<Person />} color="#FFC107" trend={8} />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard title="States Covered" value={28} icon={<Public />} color="#673ab7" trend={0} />
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard;
