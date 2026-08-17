const express = require('express');
const authenticateToken = require('../middleware/authenticateToken');
const { createGroup, listGroups, getGroupDetail, addGroupMember, removeGroupMember, getGroupBalances} = require('../controllers/groupController');
const { listExpensesForGroup } = require('../controllers/expenseController');

const router = express.Router();

router.use(authenticateToken); //to endure logged in user

router.post('/', createGroup);
router.get('/', listGroups);
router.get('/:id', getGroupDetail);
router.post('/:id/members', addGroupMember);
router.delete('/:id/members/:userId', removeGroupMember);
router.get('/:groupId/expenses', listExpensesForGroup);
router.get('/:id/balances', getGroupBalances);

module.exports = router;