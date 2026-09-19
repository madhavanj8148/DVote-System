import React, { useState, useEffect } from 'react';
import {
  Box, Typography, Button, Paper, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Dialog, DialogTitle,
  DialogContent, DialogActions, TextField, MenuItem, Chip, IconButton,
  Card, CardContent, Grid, InputAdornment, Tooltip, Zoom, Fade
} from '@mui/material';
import {
  Add as AddIcon,
  Delete as DeleteIcon,
  Search as SearchIcon,
  FilterList as FilterIcon,
  CalendarToday as CalendarIcon,
  Public as NationalIcon,
  LocationOn as StateIcon
} from '@mui/icons-material';
import { db } from '../firebaseConfig';
import { ref, onValue, push, set, remove, get } from 'firebase/database';

const states = ["Andhra Pradesh", "Telangana"];

const Elections = () => {
  const [elections, setElections] = useState([]);
  const [constituencies, setConstituencies] = useState([]);
  const [open, setOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState({
    title: '',
    type: 'National',
    state: '',
    constituency: '',
    startDate: '',
    endDate: '',
    status: 'Upcoming'
  });

  useEffect(() => {
    const electionsRef = ref(db, 'Elections');
    onValue(electionsRef, (snapshot) => {
      const data = snapshot.val();
      const list = [];
      if (data) {
        if (data.National) {
          Object.entries(data.National).forEach(([id, val]) => list.push({ id, ...val, type: 'National' }));
        }
        if (data.State) {
          Object.entries(data.State).forEach(([stateName, stateElections]) => {
            Object.entries(stateElections).forEach(([id, val]) => {
              list.push({ id, ...val, type: 'State', state: stateName });
            });
          });
        }
      }
      setElections(list);
    });
  }, []);

  const handleStateChange = async (state) => {
    setFormData({ ...formData, state, constituency: '' });
    if (state) {
      const consRef = ref(db, `States/${state}/Constituencies`);
      const snapshot = await get(consRef);
      if (snapshot.exists()) {
        setConstituencies(snapshot.val());
      }
    }
  };

  const handleOpen = () => setOpen(true);
  const handleClose = () => {
    setOpen(false);
    setFormData({ title: '', type: 'National', state: '', constituency: '', startDate: '', endDate: '', status: 'Upcoming' });
  };

  const handleSave = async () => {
    const path = formData.type === 'National'
      ? `Elections/National`
      : `Elections/State/${formData.state}`;

    const newElectionRef = push(ref(db, path));
    const electionId = newElectionRef.key;

    await set(newElectionRef, {
      id: electionId,
      title: formData.title,
      status: formData.status,
      type: formData.type,
      state: formData.type === 'State' ? formData.state : null,
      constituency: formData.type === 'State' ? formData.constituency : null,
      date: `${formData.startDate} to ${formData.endDate}`
    });
    handleClose();
  };

  const handleDelete = async (election) => {
    const path = election.type === 'National'
      ? `Elections/National/${election.id}`
      : `Elections/State/${election.state}/${election.id}`;
    if (window.confirm('Are you sure you want to delete this election? This action cannot be undone.')) {
      await remove(ref(db, path));
    }
  };

  const filteredElections = elections.filter(e =>
    e.title.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 900, color: '#0F172A', letterSpacing: -1 }}>
            Election Management
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Schedule and configure national and regional polls
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={handleOpen}
          sx={{ borderRadius: 2, py: 1.2, px: 3, fontWeight: 'bold', boxShadow: '0 8px 16px rgba(21, 101, 192, 0.2)' }}
        >
          Create New Election
        </Button>
      </Box>

      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={8}>
          <TextField
            fullWidth
            placeholder="Search by election title..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
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
        <Grid item xs={12} md={4}>
           <Button fullWidth variant="outlined" startIcon={<FilterIcon />} sx={{ height: '100%', borderRadius: 3, bgcolor: 'white' }}>
             Advanced Filters
           </Button>
        </Grid>
      </Grid>

      <TableContainer component={Paper} sx={{ borderRadius: 5, boxShadow: '0 4px 20px 0 rgba(0,0,0,0.03)', border: '1px solid rgba(0,0,0,0.05)', overflow: 'hidden' }}>
        <Table sx={{ minWidth: 650 }}>
          <TableHead sx={{ bgcolor: 'rgba(15, 23, 42, 0.02)' }}>
            <TableRow>
              <TableCell sx={{ fontWeight: 800, color: '#475569' }}>Election Title</TableCell>
              <TableCell sx={{ fontWeight: 800, color: '#475569' }}>Classification</TableCell>
              <TableCell sx={{ fontWeight: 800, color: '#475569' }}>Geographic Region</TableCell>
              <TableCell sx={{ fontWeight: 800, color: '#475569' }}>Poll Schedule</TableCell>
              <TableCell sx={{ fontWeight: 800, color: '#475569' }}>Status</TableCell>
              <TableCell sx={{ fontWeight: 800, color: '#475569' }} align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredElections.map((election) => (
              <TableRow key={election.id} hover sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                <TableCell>
                  <Typography variant="subtitle2" sx={{ fontWeight: 800 }}>{election.title}</Typography>
                </TableCell>
                <TableCell>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    {election.type === 'National' ? <NationalIcon fontSize="small" color="primary" /> : <StateIcon fontSize="small" color="success" />}
                    <Typography variant="body2">{election.type}</Typography>
                  </Box>
                </TableCell>
                <TableCell>
                  <Typography variant="body2" color="text.secondary">
                    {election.type === 'National' ? 'Whole Nation' :
                     `${election.state} ${election.constituency ? `(${election.constituency})` : '(General)'}`}
                  </Typography>
                </TableCell>
                <TableCell>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <CalendarIcon fontSize="small" sx={{ opacity: 0.3 }} />
                    <Typography variant="caption" sx={{ fontWeight: 'bold' }}>{election.startDate} - {election.endDate}</Typography>
                  </Box>
                </TableCell>
                <TableCell>
                  <Chip
                    label={election.status}
                    size="small"
                    sx={{
                      fontWeight: 'bold',
                      borderRadius: 1.5,
                      bgcolor: election.status === 'Active' ? 'success.light' : 'grey.100',
                      color: election.status === 'Active' ? 'success.dark' : 'text.secondary'
                    }}
                  />
                </TableCell>
                <TableCell align="right">
                  <Tooltip title="Delete Election" TransitionComponent={Zoom}>
                    <IconButton color="error" onClick={() => handleDelete(election)} sx={{ '&:hover': { bgcolor: 'error.light', color: 'error.dark' } }}>
                      <DeleteIcon />
                    </IconButton>
                  </Tooltip>
                </TableCell>
              </TableRow>
            ))}
            {filteredElections.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} align="center" sx={{ py: 10 }}>
                  <Typography variant="body1" color="text.secondary">No elections match your search criteria.</Typography>
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm" TransitionComponent={Zoom}>
        <DialogTitle sx={{ fontWeight: 900, pb: 0 }}>Configure New Election</DialogTitle>
        <DialogContent sx={{ pt: 2 }}>
          <Typography variant="caption" color="text.secondary" sx={{ mb: 2, display: 'block' }}>Provide the essential details to schedule a new national or state election cycle.</Typography>

          <TextField
            fullWidth label="Election Title" margin="normal" variant="outlined"
            placeholder="e.g. 2024 General Assembly Elections"
            value={formData.title} onChange={(e) => setFormData({...formData, title: e.target.value})}
            sx={{ '& .MuiOutlinedInput-root': { borderRadius: 2 } }}
          />

          <Grid container spacing={2}>
            <Grid item xs={6}>
              <TextField
                select fullWidth label="Election Classification" margin="normal"
                value={formData.type} onChange={(e) => setFormData({...formData, type: e.target.value})}
                sx={{ '& .MuiOutlinedInput-root': { borderRadius: 2 } }}
              >
                <MenuItem value="National">National Level</MenuItem>
                <MenuItem value="State">State Level</MenuItem>
              </TextField>
            </Grid>
            <Grid item xs={6}>
              <TextField
                select fullWidth label="Initial Status" margin="normal"
                value={formData.status} onChange={(e) => setFormData({...formData, status: e.target.value})}
                sx={{ '& .MuiOutlinedInput-root': { borderRadius: 2 } }}
              >
                <MenuItem value="Upcoming">Upcoming</MenuItem>
                <MenuItem value="Active">Live / Active</MenuItem>
              </TextField>
            </Grid>
          </Grid>

          {formData.type === 'State' && (
            <Fade in>
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <TextField
                    select fullWidth label="State" margin="normal"
                    value={formData.state} onChange={(e) => handleStateChange(e.target.value)}
                    sx={{ '& .MuiOutlinedInput-root': { borderRadius: 2 } }}
                  >
                    {states.map(s => <MenuItem key={s} value={s}>{s}</MenuItem>)}
                  </TextField>
                </Grid>
                <Grid item xs={6}>
                  <TextField
                    select fullWidth label="Constituency (Optional)" margin="normal"
                    value={formData.constituency} onChange={(e) => setFormData({...formData, constituency: e.target.value})}
                    disabled={!formData.state}
                    sx={{ '& .MuiOutlinedInput-root': { borderRadius: 2 } }}
                  >
                    <MenuItem value="">Full State</MenuItem>
                    {constituencies.map(c => <MenuItem key={c} value={c}>{c}</MenuItem>)}
                  </TextField>
                </Grid>
              </Grid>
            </Fade>
          )}

          <Grid container spacing={2}>
            <Grid item xs={6}>
              <TextField
                fullWidth label="Poll Start Date" type="date" margin="normal"
                InputLabelProps={{ shrink: true }}
                value={formData.startDate} onChange={(e) => setFormData({...formData, startDate: e.target.value})}
                sx={{ '& .MuiOutlinedInput-root': { borderRadius: 2 } }}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                fullWidth label="Poll End Date" type="date" margin="normal"
                InputLabelProps={{ shrink: true }}
                value={formData.endDate} onChange={(e) => setFormData({...formData, endDate: e.target.value})}
                sx={{ '& .MuiOutlinedInput-root': { borderRadius: 2 } }}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 3 }}>
          <Button onClick={handleClose} sx={{ fontWeight: 'bold' }}>Cancel</Button>
          <Button
            onClick={handleSave}
            variant="contained"
            disabled={!formData.title || (formData.type === 'State' && !formData.state)}
            sx={{ px: 4, borderRadius: 2, fontWeight: 'bold' }}
          >
            Create Election Cycle
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Elections;
