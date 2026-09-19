import React, { useState, useEffect } from 'react';
import {
  Box, Typography, Paper, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, TextField, MenuItem, Chip,
  InputAdornment, Button, Avatar, Tooltip, Zoom, Stack, Grid
} from '@mui/material';
import {
  Search as SearchIcon,
  FilterList as FilterIcon,
  Download as DownloadIcon,
  VerifiedUser as VerifiedIcon,
  HelpOutline as PendingIcon
} from '@mui/icons-material';
import { db } from '../firebaseConfig';
import { ref, onValue } from 'firebase/database';

const Users = () => {
  const [users, setUsers] = useState([]);
  const [searchTerm, setSearchName] = useState('');
  const [filterState, setFilterState] = useState('All');

  useEffect(() => {
    onValue(ref(db, 'users'), (snapshot) => {
      const data = snapshot.val();
      if (data) {
        setUsers(Object.entries(data).map(([id, val]) => ({ id, ...val })));
      }
    });
  }, []);

  const filteredUsers = users.filter(u => {
    const matchesSearch = u.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          u.email?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesState = filterState === 'All' || u.state === filterState;
    return matchesSearch && matchesState;
  });

  const uniqueStates = ['All', ...new Set(users.map(u => u.state).filter(Boolean))];

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 900, color: '#0F172A', letterSpacing: -1 }}>
            Electorate Roll
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Manage and verify registered citizens across all jurisdictions
          </Typography>
        </Box>
        <Button variant="outlined" startIcon={<DownloadIcon />} sx={{ borderRadius: 2 }}>
          Export Roll
        </Button>
      </Box>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={6}>
          <TextField
            fullWidth
            placeholder="Search by name, email or ID..."
            variant="outlined"
            value={searchTerm}
            onChange={(e) => setSearchName(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon color="primary" />
                </InputAdornment>
              ),
              sx: { borderRadius: 3, bgcolor: 'white' }
            }}
          />
        </Grid>
        <Grid item xs={12} md={3}>
           <TextField
             select fullWidth label="Filter by State"
             value={filterState}
             onChange={(e) => setFilterState(e.target.value)}
             sx={{ '& .MuiOutlinedInput-root': { borderRadius: 3, bgcolor: 'white' } }}
           >
             {uniqueStates.map(s => <MenuItem key={s} value={s}>{s}</MenuItem>)}
           </TextField>
        </Grid>
        <Grid item xs={12} md={3}>
           <Button fullWidth variant="contained" startIcon={<FilterIcon />} sx={{ height: '100%', borderRadius: 3, bgcolor: '#0F172A' }}>
             Advanced Search
           </Button>
        </Grid>
      </Grid>

      <TableContainer component={Paper} sx={{ borderRadius: 5, boxShadow: '0 4px 20px 0 rgba(0,0,0,0.03)', border: '1px solid rgba(0,0,0,0.05)', overflow: 'hidden' }}>
        <Table sx={{ minWidth: 650 }}>
          <TableHead sx={{ bgcolor: 'rgba(15, 23, 42, 0.02)' }}>
            <TableRow>
              <TableCell sx={{ fontWeight: 800, color: '#475569' }}>Citizen</TableCell>
              <TableCell sx={{ fontWeight: 800, color: '#475569' }}>Contact Info</TableCell>
              <TableCell sx={{ fontWeight: 800, color: '#475569' }}>Access Role</TableCell>
              <TableCell sx={{ fontWeight: 800, color: '#475569' }}>Jurisdiction</TableCell>
              <TableCell sx={{ fontWeight: 800, color: '#475569' }}>Poll Status</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredUsers.map((u) => (
              <TableRow key={u.id} hover sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                <TableCell>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <Avatar sx={{ bgcolor: 'primary.light', color: 'primary.main', fontWeight: 'bold' }}>{u.name?.charAt(0)}</Avatar>
                    <Box>
                      <Typography variant="subtitle2" sx={{ fontWeight: 800 }}>{u.name}</Typography>
                      <Typography variant="caption" color="text.secondary">UID: {u.id.substring(0,12).toUpperCase()}</Typography>
                    </Box>
                  </Box>
                </TableCell>
                <TableCell>
                  <Typography variant="body2">{u.email}</Typography>
                  <Typography variant="caption" color="text.secondary">{u.phone || 'No phone recorded'}</Typography>
                </TableCell>
                <TableCell>
                  <Chip
                    label={u.role || 'Voter'}
                    size="small"
                    sx={{
                      fontWeight: 'bold',
                      borderRadius: 1.5,
                      bgcolor: u.role === 'admin' ? 'secondary.light' : 'grey.100',
                      color: u.role === 'admin' ? 'secondary.dark' : 'text.secondary'
                    }}
                  />
                </TableCell>
                <TableCell>
                  <Typography variant="body2" sx={{ fontWeight: 'bold' }}>{u.state || 'National'}</Typography>
                  <Typography variant="caption" color="text.secondary">{u.constituency || 'General'}</Typography>
                </TableCell>
                <TableCell>
                  <Chip
                    icon={u.hasVoted ? <VerifiedIcon /> : <PendingIcon />}
                    label={u.hasVoted ? 'VOTED' : 'NOT VOTED'}
                    size="small"
                    variant={u.hasVoted ? "filled" : "outlined"}
                    color={u.hasVoted ? 'success' : 'error'}
                    sx={{ fontWeight: 900, borderRadius: 1.5, px: 1 }}
                  />
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};

export default Users;
