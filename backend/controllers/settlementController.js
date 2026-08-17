const pool = require('../db/pool');

//uses db procedure
async function createSettlement(req, res) {
    const { group_id, to_user_id, amount, idempotency_key } = req.body;
    const from_user_id = req.userId; // always the logged-in user — never client-supplied

    if (!group_id || !to_user_id || !amount || !idempotency_key) {
        return res.status(400).json({ status: 'error', message: 'group_id, to_user_id, amount, and idempotency_key are required' });
    }
    if (amount <= 0) {
        return res.status(400).json({ status: 'error', message: 'amount must be positive' });
    }
    if (to_user_id === from_user_id) {
        return res.status(400).json({ status: 'error', message: 'Cannot settle up with yourself' });
    }

    try {
        const membership = await pool.query(
            'SELECT 1 FROM group_members WHERE group_id = $1 AND user_id = $2',
            [group_id, from_user_id]
        );
        if (membership.rows.length === 0) {
            return res.status(403).json({ status: 'error', message: 'You are not a member of this group' });
        }

        const result = await pool.query(
            'SELECT * FROM fn_settle_up($1, $2, $3, $4, $5)',
            [group_id, from_user_id, to_user_id, amount, idempotency_key]
        );

        const { settlement_id, was_already_processed } = result.rows[0];
        res.status(was_already_processed ? 200 : 201).json({
            status: 'success',
            data: { settlement_id, was_already_processed },
        });
    } catch (err) {
        console.error('createSettlement failed', err);
        res.status(500).json({ status: 'error', message: 'Could not process settlement' });
    }
}

async function listSettlements(req, res) {
    try {
        const result = await pool.query(
            `SELECT id, group_id, from_user_id, to_user_id, amount, status, created_at
             FROM settlements
             WHERE from_user_id = $1 OR to_user_id = $1
             ORDER BY created_at DESC`,
            [req.userId]
        );
        res.json({ status: 'success', data: result.rows });
    } catch (err) {
        console.error('listSettlements failed', err);
        res.status(500).json({ status: 'error', message: 'Could not fetch settlements' });
    }
}

module.exports = { createSettlement, listSettlements };