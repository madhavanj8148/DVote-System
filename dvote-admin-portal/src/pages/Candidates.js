import React, { useState, useEffect } from 'react';
import {
  Box, Typography, Button, Card, TextField, InputAdornment,
  Dialog, DialogTitle, DialogContent, DialogActions, Grid,
  Avatar, IconButton, Chip, LinearProgress, MenuItem
} from '@mui/material';
import {
  Add, Search, Edit, Delete, PhotoCamera,
  FilterList, Download, Person, AccountBalance
} from '@mui/icons-material';
import { DataGrid, GridActionsCellItem } from '@mui/x-data-grid';
import { ref, onValue, push, set, remove, update } from 'firebase/database';
import { ref as sRef, uploadBytesResumable, getDownloadURL } from 'firebase/storage';
import { db, storage } from '../firebaseConfig';

const states = ["Andhra Pradesh", "Telangana"];

const Candidates = () => {
  const [candidates, setCandidates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [formData, setFormData] = useState({
    candidateId: '',
    candidateName: '',
    partyName: '',
    photoUrl: '',
    partySymbol: '',
    state: '',
    constituency: '',
    voteCount: 0
  });
  const [uploadProgress, setUploadProgress] = useState(0);

  useEffect(() => {
    const candRef = ref(db, 'candidates');
    onValue(candRef, (snapshot) => {
      const data = snapshot.val();
      if (data) {
        const list = Object.keys(data).map(key => ({ id: key, ...data[key] }));
        setCandidates(list);
      } else {
        setCandidates([]);
      }
      setLoading(false);
    });
  }, []);

  const handleOpen = () => {
    setEditMode(false);
    setFormData({
      candidateId: '',
      candidateName: '',
      partyName: '',
      photoUrl: '',
      partySymbol: '',
      state: '',
      constituency: '',
      voteCount: 0
    });
    setOpen(true);
  };

  const handleEdit = (candidate) => {
    setEditMode(true);
    setFormData(candidate);
    setOpen(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this candidate?')) {
      await remove(ref(db, `candidates/${id}`));
    }
  };

  const handleFileUpload = (file, field) => {
    const storageRef = sRef(storage, `candidates/${Date.now()}_${file.name}`);
    const uploadTask = uploadBytesResumable(storageRef, file);

    uploadTask.on('state_changed',
      (snapshot) => {
        const progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
        setUploadProgress(progress);
      },
      (error) => console.error(error),
      () => {
        getDownloadURL(uploadTask.snapshot.ref).then((downloadURL) => {
          setFormData(prev => ({ ...prev, [field]: downloadURL }));
          setUploadProgress(0);
        });
      }
    );
  };

  const handleSubmit = async () => {
    if (editMode) {
      const { id, ...updateData } = formData;
      await update(ref(db, `candidates/${id}`), updateData);
    } else {
      const newRef = push(ref(db, 'candidates'));
      const candidateId = newRef.key;
      await set(newRef, { ...formData, candidateId });
    }
    setOpen(false);
  };

  const columns = [
    {
      field: 'photoUrl',
      headerName: 'Photo',
      width: 70,
      renderCell: (params) => <Avatar src={params.value} sx={{ bgcolor: 'primary.light' }}><Person /></Avatar>
    },
    { field: 'candidateName', headerName: 'Full Name', flex: 1, fontWeight: 'bold' },
    { field: 'partyName', headerName: 'Political Party', width: 180, renderCell: (params) => (
      <Chip label={params.value} size="small" variant="outlined" color="primary" sx={{ fontWeight: 600 }} />
    )},
    { field: 'state', headerName: 'State', width: 150 },
    { field: 'constituency', headerName: 'Constituency', width: 150 },
    { field: 'voteCount', headerName: 'Votes', width: 100, type: 'number' },
    {
      field: 'actions',
      type: 'actions',
      headerName: 'Actions',
      width: 100,
      getActions: (params) => [
        <GridActionsCellItem icon={<Edit color="primary" />} label="Edit" onClick={() => handleEdit(params.row)} />,
        <GridActionsCellItem icon={<Delete color="error" />} label="Delete" onClick={() => handleDelete(params.id)} />,
      ],
    },
  ];

  return (
    <Box>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800 }}>Candidate Management</Typography>
          <Typography variant="body1" color="text.secondary">Register and manage verified election candidates.</Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button variant="outlined" startIcon={<Download />}>Export</Button>
          <Button variant="contained" startIcon={<Add />} onClick={handleOpen}>Add Candidate</Button>
        </Box>
      </Box>

      <Card sx={{ mb: 4, p: 2 }}>
        <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
          <TextField
            placeholder="Search candidates..."
            size="small"
            fullWidth
            InputProps={{ startAdornment: <InputAdornment position="start"><Search /></InputAdornment> }}
          />
          <IconButton><FilterList /></IconButton>
        </Box>
        <Box sx={{ height: 600, width: '100%' }}>
          <DataGrid
            rows={candidates}
            columns={columns}
            loading={loading}
            pageSizeOptions={[10, 25, 50]}
            initialState={{ pagination: { paginationModel: { pageSize: 10 } } }}
            disableRowSelectionOnClick
            sx={{ border: 'none' }}
          />
        </Box>
      </Card>

      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 800 }}>{editMode ? 'Edit Candidate' : 'Register New Candidate'}</DialogTitle>
        <DialogContent dividers>
          <Grid container spacing={2}>
            <Grid item xs={6} sx={{ textAlign: 'center' }}>
              <Typography variant="caption" display="block" gutterBottom>CANDIDATE PHOTO</Typography>
              <Box sx={{ position: 'relative', display: 'inline-block' }}>
                <Avatar
                  src={formData.photoUrl}
                  sx={{ width: 80, height: 80, mx: 'auto', mb: 1, border: '2px solid #eee' }}
                />
                <IconButton
                  component="label"
                  size="small"
                  sx={{ position: 'absolute', bottom: 5, right: -5, bgcolor: 'primary.main', color: 'white', '&:hover': { bgcolor: 'primary.dark' } }}
                >
                  <input type="file" hidden onChange={(e) => handleFileUpload(e.target.files[0], 'photoUrl')} />
                  <PhotoCamera fontSize="inherit" />
                </IconButton>
              </Box>
            </Grid>
            <Grid item xs={6} sx={{ textAlign: 'center' }}>
              <Typography variant="caption" display="block" gutterBottom>PARTY SYMBOL</Typography>
              <Box sx={{ position: 'relative', display: 'inline-block' }}>
                <Avatar
                  src={formData.partySymbol}
                  variant="rounded"
                  sx={{ width: 80, height: 80, mx: 'auto', mb: 1, border: '2px solid #eee', bgcolor: 'white' }}
                >
                  <AccountBalance color="disabled" />
                </Avatar>
                <IconButton
                  component="label"
                  size="small"
                  sx={{ position: 'absolute', bottom: 5, right: -5, bgcolor: 'secondary.main', color: 'white', '&:hover': { bgcolor: 'secondary.dark' } }}
                >
                  <input type="file" hidden onChange={(e) => handleFileUpload(e.target.files[0], 'partySymbol')} />
                  <PhotoCamera fontSize="inherit" />
                </IconButton>
              </Box>
            </Grid>
            {uploadProgress > 0 && <Grid item xs={12}><LinearProgress variant="determinate" value={uploadProgress} /></Grid>}

            <Grid item xs={12}>
              <TextField fullWidth label="Full Name" size="small" value={formData.candidateName} onChange={(e) => setFormData({...formData, candidateName: e.target.value})} />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Political Party" size="small" value={formData.partyName} onChange={(e) => setFormData({...formData, partyName: e.target.value})} />
            </Grid>
            <Grid item xs={6}>
              <TextField select fullWidth label="State" size="small" value={formData.state} onChange={(e) => setFormData({...formData, state: e.target.value})}>
                {states.map(s => <MenuItem key={s} value={s}>{s}</MenuItem>)}
              </TextField>
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth label="Constituency" size="small" value={formData.constituency} onChange={(e) => setFormData({...formData, constituency: e.target.value})} />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleSubmit}>Save Candidate</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Candidates;
