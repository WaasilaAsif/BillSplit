const express = require('express');
const authenticateToken = require('../middleware/authenticateToken');
const { createExpense, listExpensesForGroup, getExpenseDetail, updateExpense, deleteExpense } = require('../controllers/expenseController');

const router = express.Router();
router.use(authenticateToken);

router.post('/', createExpense);
router.get('/:id', getExpenseDetail);
router.put('/:id', updateExpense);
router.delete('/:id', deleteExpense);

module.exports = router;
