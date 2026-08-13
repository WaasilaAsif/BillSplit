const express = require('express');
const authenticateToken = require('../middleware/authenticateToken');
const { createExpense, listExpensesForGroup, getExpenseDetail } = require('../controllers/expenseController');

const router = express.Router();
router.use(authenticateToken);

router.post('/', createExpense);
router.get('/:id', getExpenseDetail);

module.exports = router;