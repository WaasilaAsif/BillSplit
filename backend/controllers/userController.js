const pool = require('../db/pool');

async function searchUsers(req, res) {
    const query = (req.query.search || '').trim();

    if (!query) {
        return res.json({ status: 'success', data: [] });
    }

    try {
        const result = await pool.query(
            `SELECT id, username, email, created_at
             FROM users
             WHERE id != $1
               AND (username ILIKE '%' || $2 || '%' OR email ILIKE '%' || $2 || '%')
             ORDER BY username
             LIMIT 20`,
            [req.userId, query]
        );
        res.json({ status: 'success', data: result.rows });
    } catch (err) {
        console.error('searchUsers failed', err);
        res.status(500).json({ status: 'error', message: 'Could not search users' });
    }
}

module.exports = { searchUsers };
