import pg from 'pg';
const { Pool } = pg;

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: process.env.NODE_ENV === 'production' ? { rejectUnauthorized: false } : false,
});

async function init() {
  await pool.query(`
    CREATE TABLE IF NOT EXISTS users (
      id SERIAL PRIMARY KEY,
      username TEXT UNIQUE NOT NULL,
      email TEXT UNIQUE NOT NULL,
      password TEXT NOT NULL,
      created_at TIMESTAMPTZ DEFAULT NOW()
    );
    CREATE TABLE IF NOT EXISTS posts (
      id SERIAL PRIMARY KEY,
      user_id INTEGER NOT NULL REFERENCES users(id),
      title TEXT NOT NULL,
      description TEXT,
      make TEXT NOT NULL,
      model TEXT NOT NULL,
      year INTEGER NOT NULL,
      price NUMERIC NOT NULL,
      mileage INTEGER,
      image_url TEXT,
      created_at TIMESTAMPTZ DEFAULT NOW()
    );
  `);
}

const db = {
  init,

  // Users
  async createUser({ username, email, password }) {
    const { rows } = await pool.query(
      'INSERT INTO users (username, email, password) VALUES ($1, $2, $3) RETURNING id, username, email, created_at',
      [username, email, password]
    );
    return rows[0];
  },

  async getUserByEmail(email) {
    const { rows } = await pool.query('SELECT * FROM users WHERE email = $1', [email]);
    return rows[0] ?? null;
  },

  // Posts
  async createPost({ user_id, title, description, make, model, year, price, mileage, image_url }) {
    const { rows } = await pool.query(
      `INSERT INTO posts (user_id, title, description, make, model, year, price, mileage, image_url)
       VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9) RETURNING *`,
      [user_id, title, description ?? null, make, model, year, price, mileage ?? null, image_url ?? null]
    );
    return rows[0];
  },

  async getAllPosts() {
    const { rows } = await pool.query(
      `SELECT posts.*, users.username
       FROM posts JOIN users ON posts.user_id = users.id
       ORDER BY posts.created_at DESC`
    );
    return rows;
  },

  async getPostById(id) {
    const { rows } = await pool.query(
      `SELECT posts.*, users.username
       FROM posts JOIN users ON posts.user_id = users.id
       WHERE posts.id = $1`,
      [id]
    );
    return rows[0] ?? null;
  },

  async updatePost(id, { title, description, make, model, year, price, mileage, image_url }) {
    const { rows } = await pool.query(
      `UPDATE posts SET title=$1, description=$2, make=$3, model=$4,
       year=$5, price=$6, mileage=$7, image_url=$8 WHERE id=$9 RETURNING *`,
      [title, description, make, model, year, price, mileage, image_url, id]
    );
    return rows[0];
  },

  async deletePost(id) {
    await pool.query('DELETE FROM posts WHERE id = $1', [id]);
  },
};

export default db;
