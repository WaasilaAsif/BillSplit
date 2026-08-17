const express = require('express');
const authenticateToken = require('../middleware/authenticateToken');
const { listFriends, getFriendBalance, getSharedExpenses } = require('../controllers/friendController');

const router = express.Router();
router.use(authenticateToken);

router.get('/', listFriends);
router.get('/:userId/balance', getFriendBalance);
router.get('/:userId/expenses', getSharedExpenses);

module.exports = router;
