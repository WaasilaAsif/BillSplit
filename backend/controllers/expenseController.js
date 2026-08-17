const pool = require('../db/pool');
const { equalSplit, percentageSplit, exactSplit, itemizedSplit } = require('../utils/splitCalculator');


function computeSplitsForRequest({ split_type, amount, participant_ids, splits, items, taxAmount, effectivePaidBy }) {
    switch (split_type) {
        case 'equal':
            if (!amount || !participant_ids || participant_ids.length === 0) {
                throw new Error('amount and participant_ids are required for equal splits');
            }
            return { computedSplits: equalSplit(amount, participant_ids, effectivePaidBy), finalAmount: amount };

        case 'percentage':
            if (!amount || !splits || splits.length === 0) {
                throw new Error('amount and splits (with percentage) are required');
            }
            return { computedSplits: percentageSplit(amount, splits, effectivePaidBy), finalAmount: amount };

        case 'exact':
            if (!amount || !splits || splits.length === 0) {
                throw new Error('amount and splits (with share_amount) are required');
            }
            return { computedSplits: exactSplit(amount, splits), finalAmount: amount };

        case 'itemized': {
            if (!items || items.length === 0) {
                throw new Error('items are required for itemized splits');
            }
            const result = itemizedSplit(items, taxAmount, effectivePaidBy);
            return { computedSplits: result.splits, finalAmount: result.totalAmount }; // subtotal + tax, not client-supplied
        }

        default:
            throw new Error(`Unknown split_type: ${split_type}`);
    }
}

async function createExpense(req, res) {
    const {
        group_id, paid_by, description, category, split_type,
        receipt_url, tax_amount,
        amount, participant_ids, splits, items,
    } = req.body;

    const effectivePaidBy = paid_by || req.userId; // usually the current user; allow override for "logged on someone else's behalf" later
    const taxAmount = tax_amount || 0;

    if (!description || !split_type) {
        return res.status(400).json({ status: 'error', message: 'description and split_type are required' });
    }

    let computedSplits;
    let finalAmount;
    try {
        ({ computedSplits, finalAmount } = computeSplitsForRequest({
            split_type, amount, participant_ids, splits, items, taxAmount, effectivePaidBy,
        }));
    } catch (err) {
        return res.status(400).json({ status: 'error', message: err.message });
    }

    try {
        const result = await pool.query(
            `SELECT fn_create_expense_with_splits($1, $2, $3, $4, $5, $6, $7, $8, $9) AS expense_id`,
            [
                group_id || null,
                effectivePaidBy,
                finalAmount,
                description,
                category || null,
                split_type,
                receipt_url || null,
                taxAmount,
                JSON.stringify(computedSplits),
            ]
        );
        const expenseId = result.rows[0].expense_id;

        if (split_type === 'itemized') {
            for (const item of items) {
                await pool.query(
                    `INSERT INTO expense_items (expense_id, item_name, item_price, assigned_to_user_id)
                     VALUES ($1, $2, $3, $4)`,
                    [expenseId, item.item_name, item.item_price, item.assigned_to_user_id]
                );
            }
        }

        res.status(201).json({ status: 'success', data: { id: expenseId, amount: finalAmount, description, split_type } });
    } catch (err) {
        console.error('createExpense failed', err);
        res.status(500).json({ status: 'error', message: 'Could not create expense' });
    }
}


async function listExpensesForGroup(req, res) {
    const { groupId } = req.params;

    try {
        const membership = await pool.query(
            'SELECT 1 FROM group_members WHERE group_id = $1 AND user_id = $2',
            [groupId, req.userId]
        );
        if (membership.rows.length === 0) {
            return res.status(403).json({ status: 'error', message: 'You are not a member of this group' });
        }

        const result = await pool.query(
            `SELECT e.*,
                    (SELECT share_amount FROM expense_splits WHERE expense_id = e.id AND user_id = $2) AS your_share
             FROM expenses e
             WHERE e.group_id = $1 AND e.is_voided = false
             ORDER BY e.created_at DESC`,
            [groupId, req.userId]
        );
        res.json({ status: 'success', data: result.rows });
    } catch (err) {
        console.error('listExpensesForGroup failed', err);
        res.status(500).json({ status: 'error', message: 'Could not fetch expenses' });
    }
}

//get expense/:id
async function getExpenseDetail(req, res) {
    const { id } = req.params;

    try {
        const expenseResult = await pool.query('SELECT * FROM expenses WHERE id = $1 AND is_voided = false', [id]);
        if (expenseResult.rows.length === 0) {
            return res.status(404).json({ status: 'error', message: 'Expense not found' });
        }
        const expense = expenseResult.rows[0];


        if (expense.group_id) {
            const membership = await pool.query(
                'SELECT 1 FROM group_members WHERE group_id = $1 AND user_id = $2',
                [expense.group_id, req.userId]
            );
            if (membership.rows.length === 0) {
                return res.status(403).json({ status: 'error', message: 'You do not have access to this expense' });
            }
        }

        const splitsResult = await pool.query(
            `SELECT es.user_id, es.share_amount, u.username
             FROM expense_splits es JOIN users u ON u.id = es.user_id
             WHERE es.expense_id = $1`,
            [id]
        );

        let items = [];
        if (expense.split_type === 'itemized') {
            const itemsResult = await pool.query('SELECT * FROM expense_items WHERE expense_id = $1', [id]);
            items = itemsResult.rows;
        }

        res.json({ status: 'success', data: { ...expense, splits: splitsResult.rows, items } });
    } catch (err) {
        console.error('getExpenseDetail failed', err);
        res.status(500).json({ status: 'error', message: 'Could not fetch expense' });
    }
}


async function updateExpense(req, res) {
    const { id } = req.params;
    const {
        group_id, paid_by, description, category, split_type,
        receipt_url, tax_amount,
        amount, participant_ids, splits, items,
    } = req.body;

    const effectivePaidBy = paid_by || req.userId;
    const taxAmount = tax_amount || 0;

    if (!description || !split_type) {
        return res.status(400).json({ status: 'error', message: 'description and split_type are required' });
    }

    let computedSplits;
    let finalAmount;
    try {
        ({ computedSplits, finalAmount } = computeSplitsForRequest({
            split_type, amount, participant_ids, splits, items, taxAmount, effectivePaidBy,
        }));
    } catch (err) {
        return res.status(400).json({ status: 'error', message: err.message });
    }

    try {
        const existing = await pool.query('SELECT group_id FROM expenses WHERE id = $1 AND is_voided = false', [id]);
        if (existing.rows.length === 0) {
            return res.status(404).json({ status: 'error', message: 'Expense not found' });
        }
        const existingGroupId = existing.rows[0].group_id;
        if (existingGroupId) {
            const membership = await pool.query(
                'SELECT 1 FROM group_members WHERE group_id = $1 AND user_id = $2',
                [existingGroupId, req.userId]
            );
            if (membership.rows.length === 0) {
                return res.status(403).json({ status: 'error', message: 'You do not have access to this expense' });
            }
        }

        const voidResult = await pool.query('SELECT fn_void_expense($1) AS voided', [id]);
        if (!voidResult.rows[0].voided) {
            return res.status(404).json({ status: 'error', message: 'Expense not found' });
        }

        const createResult = await pool.query(
            `SELECT fn_create_expense_with_splits($1, $2, $3, $4, $5, $6, $7, $8, $9) AS expense_id`,
            [
                group_id || existingGroupId || null,
                effectivePaidBy,
                finalAmount,
                description,
                category || null,
                split_type,
                receipt_url || null,
                taxAmount,
                JSON.stringify(computedSplits),
            ]
        );
        const newExpenseId = createResult.rows[0].expense_id;

        if (split_type === 'itemized') {
            for (const item of items) {
                await pool.query(
                    `INSERT INTO expense_items (expense_id, item_name, item_price, assigned_to_user_id)
                     VALUES ($1, $2, $3, $4)`,
                    [newExpenseId, item.item_name, item.item_price, item.assigned_to_user_id]
                );
            }
        }

        res.json({ status: 'success', data: { id: newExpenseId, amount: finalAmount, description, split_type } });
    } catch (err) {
        console.error('updateExpense failed', err);
        res.status(500).json({ status: 'error', message: 'Could not update expense' });
    }
}

async function deleteExpense(req, res) {
    const { id } = req.params;

    try {
        const existing = await pool.query('SELECT group_id FROM expenses WHERE id = $1 AND is_voided = false', [id]);
        if (existing.rows.length === 0) {
            return res.status(404).json({ status: 'error', message: 'Expense not found' });
        }
        const groupId = existing.rows[0].group_id;
        if (groupId) {
            const membership = await pool.query(
                'SELECT 1 FROM group_members WHERE group_id = $1 AND user_id = $2',
                [groupId, req.userId]
            );
            if (membership.rows.length === 0) {
                return res.status(403).json({ status: 'error', message: 'You do not have access to this expense' });
            }
        }

        const voidResult = await pool.query('SELECT fn_void_expense($1) AS voided', [id]);
        if (!voidResult.rows[0].voided) {
            return res.status(404).json({ status: 'error', message: 'Expense not found' });
        }

        res.json({ status: 'success', data: { id, voided: true } });
    } catch (err) {
        console.error('deleteExpense failed', err);
        res.status(500).json({ status: 'error', message: 'Could not delete expense' });
    }
}

module.exports = { createExpense, listExpensesForGroup, getExpenseDetail, updateExpense, deleteExpense };
