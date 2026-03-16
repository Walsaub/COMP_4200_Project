import express from 'express';
import bcrypt from 'bcryptjs';
import db from '../db.js';

const router = express.Router();

// POST /api/auth/register
router.post('/register', async (req, res) => {
  const { username, email, password } = req.body;
  if (!username || !email || !password) {
    return res.status(400).json({ error: 'username, email, and password are required' });
  }

  const hashedPassword = bcrypt.hashSync(password, 10);
  try {
    const user = await db.createUser({ username, email, password: hashedPassword });
    res.status(201).json({ user: { id: user.id, username, email } });
  } catch (err) {
    if (err.message.includes('unique') || err.message.includes('duplicate')) {
      return res.status(409).json({ error: 'Username or email already taken' });
    }
    res.status(500).json({ error: 'Server error' });
  }
});

// POST /api/auth/login
router.post('/login', async (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) {
    return res.status(400).json({ error: 'email and password are required' });
  }

  const user = await db.getUserByEmail(email);
  if (!user || !bcrypt.compareSync(password, user.password)) {
    return res.status(401).json({ error: 'Invalid credentials' });
  }

  res.json({ user: { id: user.id, username: user.username, email: user.email } });
});

export default router;
