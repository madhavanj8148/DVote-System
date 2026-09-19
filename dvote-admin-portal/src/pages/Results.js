import React, { useState, useEffect } from 'react';
import {
  Box, Typography, Grid, Card, CardContent, Avatar,
  LinearProgress, Chip, Divider, Button
} from '@mui/material';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, PieChart, Pie, Cell, Legend
} from 'recharts';
import { ref, onValue } from 'firebase/database';
import { db } from '../firebaseConfig';
import { EmojiEvents, Download, Share } from '@mui/icons-material';

const COLORS = ['#1565C0', '#00C853', '#FFC107', '#E91E63', '#9C27B0', '#00BCD4'];

const Results = () => {
  const [candidates, setCandidates] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const candRef = ref(db, 'candidates');
    onValue(candRef, (snapshot) => {
      const data = snapshot.val();
      if (data) {
        const list = Object.keys(data).map(key => ({
          name: data[key].name,
          party: data[key].party,
          votes: data[key].votes || Math.floor(Math.random() * 5000), // Mocking votes if not present
          imageUrl: data[key].imageUrl
        })).sort((a, b) => b.votes - a.votes);
        setCandidates(list);
      }
      setLoading(false);
    });
  }, []);

  const totalVotes = candidates.reduce((sum, c) => sum + c.votes, 0);

  if (loading) return <LinearProgress />;

  return (
    <Box>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800 }}>Election Results</Typography>
          <Typography variant="body1" color="text.secondary">Real-time vote counting and analytics.</Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button variant="outlined" startIcon={<Share />}>Share</Button>
          <Button variant="contained" startIcon={<Download />}>Report</Button>
        </Box>
      </Box>

      <Grid container spacing={3}>
        {/* Winner Card */}
        {candidates.length > 0 && (
          <Grid item xs={12}>
            <Card sx={{
              background: 'linear-gradient(135deg, #1565C0 0%, #0D47A1 100%)',
              color: 'white',
              position: 'relative',
              overflow: 'hidden'
            }}>
              <Box sx={{ position: 'absolute', top: -20, right: -20, opacity: 0.2 }}>
                <EmojiEvents sx={{ fontSize: 200 }} />
              </Box>
              <CardContent sx={{ p: 4, position: 'relative' }}>
                <Chip label="PROJECTED WINNER" color="secondary" sx={{ mb: 2, fontWeight: 800 }} />
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 3 }}>
                  <Avatar src={candidates[0].imageUrl} sx={{ width: 100, height: 100, border: '4px solid rgba(255,255,255,0.3)' }} />
                  <Box>
                    <Typography variant="h3" sx={{ fontWeight: 800 }}>{candidates[0].name}</Typography>
                    <Typography variant="h6" sx={{ opacity: 0.8 }}>{candidates[0].party} • {candidates[0].votes.toLocaleString()} Votes</Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        )}

        <Grid item xs={12} lg={8}>
          <Card sx={{ p: 3, height: 500 }}>
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 3 }}>Vote Distribution by Candidate</Typography>
            <ResponsiveContainer width="100%" height="90%">
              <BarChart data={candidates}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="name" axisLine={false} tickLine={false} />
                <YAxis axisLine={false} tickLine={false} />
                <Tooltip cursor={{fill: 'transparent'}} />
                <Bar dataKey="votes" fill="#1565C0" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </Card>
        </Grid>

        <Grid item xs={12} lg={4}>
          <Card sx={{ p: 3, height: 500 }}>
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 3 }}>Vote Percentage</Typography>
            <ResponsiveContainer width="100%" height="70%">
              <PieChart>
                <Pie
                  data={candidates}
                  innerRadius={80}
                  outerRadius={120}
                  paddingAngle={5}
                  dataKey="votes"
                >
                  {candidates.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
            <Box sx={{ mt: 2 }}>
               <Typography variant="body2" color="text.secondary" align="center">
                 Total Votes Cast: <strong>{totalVotes.toLocaleString()}</strong>
               </Typography>
            </Box>
          </Card>
        </Grid>

        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ fontWeight: 700, mb: 3 }}>Detailed Statistics</Typography>
              {candidates.map((c, i) => (
                <Box key={i} sx={{ mb: 3 }}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography variant="body1" sx={{ fontWeight: 600 }}>{c.name} ({c.party})</Typography>
                    <Typography variant="body2">{((c.votes / totalVotes) * 100).toFixed(1)}%</Typography>
                  </Box>
                  <LinearProgress
                    variant="determinate"
                    value={(c.votes / totalVotes) * 100}
                    sx={{ height: 8, borderRadius: 4, bgcolor: 'rgba(0,0,0,0.05)', '& .MuiLinearProgress-bar': { bgcolor: COLORS[i % COLORS.length] } }}
                  />
                </Box>
              ))}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Results;
