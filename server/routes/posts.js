const express = require('express');
const db = require('../db');
const requireAuth = require('../middleware/auth');

const router = express.Router();

// GET /api/posts — all listings, newest first
router.get('/', async (req, res) => {
  try {
    res.json(await db.getAllPosts());
  } catch { res.status(500).json({ error: 'Server error' }); }
});

// GET /api/posts/:id — single listing
router.get('/:id', async (req, res) => {
  try {
    const post = await db.getPostById(Number(req.params.id));
    if (!post) return res.status(404).json({ error: 'Post not found' });
    res.json(post);
  } catch { res.status(500).json({ error: 'Server error' }); }
});

// POST /api/posts — create listing (auth required)
router.post('/', requireAuth, async (req, res) => {
  const { title, description, make, model, year, price, mileage, image_url } = req.body;
  if (!title || !make || !model || !year || !price) {
    return res.status(400).json({ error: 'title, make, model, year, and price are required' });
  }
  try {
    const post = await db.createPost({
      user_id: req.user.id,
      title, description, make, model,
      year: Number(year),
      price: Number(price),
      mileage: mileage ? Number(mileage) : null,
      image_url,
    });
    res.status(201).json(post);
  } catch { res.status(500).json({ error: 'Server error' }); }
});

// PUT /api/posts/:id — update listing (auth required, must own post)
router.put('/:id', requireAuth, async (req, res) => {
  try {
    const post = await db.getPostById(Number(req.params.id));
    if (!post) return res.status(404).json({ error: 'Post not found' });
    if (post.user_id !== req.user.id) return res.status(403).json({ error: 'Forbidden' });

    const { title, description, make, model, year, price, mileage, image_url } = req.body;
    const updated = await db.updatePost(Number(req.params.id), {
      title: title ?? post.title,
      description: description ?? post.description,
      make: make ?? post.make,
      model: model ?? post.model,
      year: year ? Number(year) : post.year,
      price: price ? Number(price) : post.price,
      mileage: mileage !== undefined ? Number(mileage) : post.mileage,
      image_url: image_url ?? post.image_url,
    });
    res.json(updated);
  } catch { res.status(500).json({ error: 'Server error' }); }
});

// DELETE /api/posts/:id — delete listing (auth required, must own post)
router.delete('/:id', requireAuth, async (req, res) => {
  try {
    const post = await db.getPostById(Number(req.params.id));
    if (!post) return res.status(404).json({ error: 'Post not found' });
    if (post.user_id !== req.user.id) return res.status(403).json({ error: 'Forbidden' });

    await db.deletePost(Number(req.params.id));
    res.json({ message: 'Post deleted' });
  } catch { res.status(500).json({ error: 'Server error' }); }
});

module.exports = router;
