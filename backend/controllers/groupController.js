const pool = require('../db/pool');


async function createGroup(req, res) {
    const { name } = req.body;
    let is_temporary = req.body.is_temporary;
    if (is_temporary === undefined) {
        is_temporary = false;
    }

    if (!name) {
        return res.status(400).json({ status: 'error', message: 'name is required' });
    }

    const client = await pool.connect();
    try {
        await client.query('BEGIN');

        const groupResult = await client.query(
            `INSERT INTO groups (name, is_temporary, created_by)
             VALUES ($1, $2, $3)
             RETURNING id, name, is_temporary, is_archived, created_at`,
            [name, is_temporary, req.userId]
        );
        const group = groupResult.rows[0];

        await client.query(
            `INSERT INTO group_members (group_id, user_id)
             VALUES ($1, $2)`,
            [group.id, req.userId]
        );

        await client.query('COMMIT');
        res.status(201).json({ status: 'success', data: group });
    } catch (err) {
        await client.query('ROLLBACK');
        console.error('createGroup failed', err);
        res.status(500).json({ status: 'error', message: 'Could not create group' });
    } finally {
        client.release();
    }
}


async function listGroups(req, res) {
    try {
        const result = await pool.query(
            `SELECT
                g.id, g.name, g.is_temporary, g.is_archived, g.created_at,
                COALESCE(bal.balance, 0) AS current_user_balance
             FROM groups g
            
             JOIN group_members gm ON gm.group_id = g.id AND gm.user_id = $1
             LEFT JOIN (
                 SELECT
                     COALESCE(e.group_id, s.group_id) AS group_id,
                     SUM(CASE WHEN le.to_user_id = $1 THEN le.amount ELSE 0 END)
                     - SUM(CASE WHEN le.from_user_id = $1 THEN le.amount ELSE 0 END) AS balance
                 FROM ledger_entries le
                 LEFT JOIN expenses e ON e.id = le.expense_id
                 LEFT JOIN settlements s ON s.id = le.settlement_id
                 WHERE le.to_user_id = $1 OR le.from_user_id = $1
                 GROUP BY COALESCE(e.group_id, s.group_id)
             ) bal ON bal.group_id = g.id
             WHERE g.is_archived = false
             ORDER BY g.created_at DESC`,
            [req.userId]
        );
        res.json({ status: 'success', data: result.rows });
    } catch (err) {
        console.error('listGroups failed', err);
        res.status(500).json({ status: 'error', message: 'Could not fetch groups' });
    }
}


async function getGroupDetail(req, res) {
    const groupId = req.params.id;

    try {
        const membership = await pool.query(
            'SELECT 1 FROM group_members WHERE group_id = $1 AND user_id = $2',
            [groupId, req.userId]
        );
        if (membership.rows.length === 0) {
            return res.status(403).json({ status: 'error', message: 'You are not a member of this group' });
        }

        const groupResult = await pool.query('SELECT * FROM groups WHERE id = $1', [groupId]);
        if (groupResult.rows.length === 0) {
            return res.status(404).json({ status: 'error', message: 'Group not found' });
        }

        const membersResult = await pool.query(
            `SELECT gm.user_id, u.username, u.email, gm.joined_at, gm.invited_by
             FROM group_members gm
             JOIN users u ON u.id = gm.user_id
             WHERE gm.group_id = $1`,
            [groupId]
        );

        const expensesResult = await pool.query(
            'SELECT * FROM expenses WHERE group_id = $1 ORDER BY created_at DESC',
            [groupId]
        );

        res.json({
            status: 'success',
            data: {
                group: groupResult.rows[0],
                members: membersResult.rows,
                expenses: expensesResult.rows,
            },
        });
    } catch (err) {
        console.error('getGroupDetail failed', err);
        res.status(500).json({ status: 'error', message: 'Could not fetch group' });
    }
}


async function addGroupMember(req, res) {
    const groupId = req.params.id;
    const { user_id } = req.body;

    if (!user_id) {
        return res.status(400).json({ status: 'error', message: 'user_id is required' });
    }

    try {
        const membership = await pool.query(
            'SELECT 1 FROM group_members WHERE group_id = $1 AND user_id = $2',
            [groupId, req.userId]
        );
        if (membership.rows.length === 0) {
            return res.status(403).json({ status: 'error', message: 'You are not a member of this group' });
        }

        const result = await pool.query(
            `INSERT INTO group_members (group_id, user_id, invited_by)
             VALUES ($1, $2, $3)
             RETURNING group_id, user_id, invited_by, joined_at`,
            [groupId, user_id, req.userId]
        );

        res.status(201).json({ status: 'success', data: result.rows[0] });
    } catch (err) {
        if (err.code === '23505') {
           //fix dup issue
            return res.status(409).json({ status: 'error', message: 'That user is already a member of this group' });
        }
        if (err.code === '23503') {
            // foreign key violation  user_id doesn't correspond to a real user.
            return res.status(400).json({ status: 'error', message: 'No such user' });
        }
        console.error('addGroupMember failed', err);
        res.status(500).json({ status: 'error', message: 'Could not add member' });
    }
}

module.exports = { createGroup, listGroups, getGroupDetail, addGroupMember };