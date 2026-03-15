import 'dotenv/config';
import express from 'express';
import cors from 'cors';
import db from './db.js';
import authRoutes from './routes/auth.js';
import postRoutes from './routes/posts.js';

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

app.use('/api/auth', authRoutes);
app.use('/api/posts', postRoutes);

db.init()
  .then(() => {
    app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
  })
  .catch(err => {
    console.error('Failed to connect to database:', err.message);
    process.exit(1);
  });
