const pool = require('../db/pool');
const { equalSplit, percentageSplit, exactSplit, itemizedSplit } = require('../utils/splitCalculator');


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
    let finalAmount = amount;

    try {
        switch (split_type) {
            case 'equal':
                if (!amount || !participant_ids || participant_ids.length === 0) {
                    return res.status(400).json({ status: 'error', message: 'amount and participant_ids are required for equal splits' });
                }
                computedSplits = equalSplit(amount, participant_ids, effectivePaidBy);
                break;

            case 'percentage':
                if (!amount || !splits || splits.length === 0) {
                    return res.status(400).json({ status: 'error', message: 'amount and splits (with percentage) are required' });
                }
                computedSplits = percentageSplit(amount, splits, effectivePaidBy);
                break;

            case 'exact':
                if (!amount || !splits || splits.length === 0) {
                    return res.status(400).json({ status: 'error', message: 'amount and splits (with share_amount) are required' });
                }
                computedSplits = exactSplit(amount, splits);
                break;

            case 'itemized': {
                if (!items || items.length === 0) {
                    return res.status(400).json({ status: 'error', message: 'items are required for itemized splits' });
                }
                const result = itemizedSplit(items, taxAmount, effectivePaidBy);
                computedSplits = result.splits;
                finalAmount = result.totalAmount; // subtotal + tax, not client-supplied
                break;
            }

            default:
                return res.status(400).json({ status: 'error', message: `Unknown split_type: ${split_type}` });
        }
    } catch (err) {
        // exactSplit throws if the client's numbers don't add up — a 400, not a 500.
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
            'SELECT * FROM expenses WHERE group_id = $1 ORDER BY created_at DESC',
            [groupId]
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
        const expenseResult = await pool.query('SELECT * FROM expenses WHERE id = $1', [id]);
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

module.exports = { createExpense, listExpensesForGroup, getExpenseDetail };