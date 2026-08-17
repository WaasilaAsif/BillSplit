const pool = require('../db/pool');


async function listFriends(req, res) {
    try {
        const result = await pool.query(
            `SELECT DISTINCT u.id AS user_id, u.username, u.email,
                    COALESCE(pb.net_balance, 0) AS net_balance
             FROM users u
             LEFT JOIN pairwise_balances pb ON pb.other_user_id = u.id AND pb.user_id = $1
             WHERE u.id != $1
               AND (
                   u.id IN (
                       SELECT gm2.user_id
                       FROM group_members gm1
                       JOIN group_members gm2 ON gm1.group_id = gm2.group_id
                       WHERE gm1.user_id = $1 AND gm2.user_id != $1
                   )
                   OR u.id IN (
                       SELECT CASE WHEN le.from_user_id = $1 THEN le.to_user_id ELSE le.from_user_id END
                       FROM ledger_entries le
                       WHERE le.from_user_id = $1 OR le.to_user_id = $1
                   )
               )`,
            [req.userId]
        );

        res.json({ status: 'success', data: result.rows });
    } catch (err) {
        console.error('listFriends failed', err);
        res.status(500).json({ status: 'error', message: 'Could not fetch friends' });
    }
}


async function getFriendBalance(req, res) {
    const otherUserId = req.params.userId;

    try {
        const result = await pool.query(
            `SELECT COALESCE(net_balance, 0) AS net_balance
             FROM pairwise_balances WHERE user_id = $1 AND other_user_id = $2`,
            [req.userId, otherUserId]
        );
        const netBalance = result.rows.length > 0 ? result.rows[0].net_balance : 0;
        res.json({ status: 'success', data: { user_id: otherUserId, net_balance: netBalance } });
    } catch (err) {
        console.error('getFriendBalance failed', err);
        res.status(500).json({ status: 'error', message: 'Could not fetch balance' });
    }
}

async function getSharedExpenses(req, res) {
    const otherUserId = req.params.userId;

    try {
        // NOTE: add "AND e.is_voided = false" here once migration
        
        const result = await pool.query(
            `SELECT DISTINCT e.*, g.name AS group_name
             FROM expenses e
             LEFT JOIN groups g ON g.id = e.group_id
             WHERE (e.paid_by = $1 OR EXISTS (
                   SELECT 1 FROM expense_splits es1 WHERE es1.expense_id = e.id AND es1.user_id = $1
               ))
               AND (e.paid_by = $2 OR EXISTS (
                   SELECT 1 FROM expense_splits es2 WHERE es2.expense_id = e.id AND es2.user_id = $2
               ))
             ORDER BY e.created_at DESC`,
            [req.userId, otherUserId]
        );
        res.json({ status: 'success', data: result.rows });
    } catch (err) {
        console.error('getSharedExpenses failed', err);
        res.status(500).json({ status: 'error', message: 'Could not fetch shared expenses' });
    }
}

module.exports = { listFriends, getFriendBalance, getSharedExpenses };