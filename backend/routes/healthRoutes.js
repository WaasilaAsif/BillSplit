const express = require('express');
const pool = require('../db/pool');

const router = express.Router();

router.get('/', (req, res) => {
  res.json({ status: 'ok' });
});

router.get('/db', async (req, res) => {
  try {
    const result = await pool.query('SELECT NOW()');
    res.json({ status: 'ok', db_time: result.rows[0].now });
  } catch (err) {
    console.error('Database health check failed', err);
    res.status(500).json({ status: 'error', message: 'Could not reach the database' });
  }
});

module.exports = router;