const express = require('express');
const authenticateToken = require('../middleware/authenticateToken');
const { listFriends, getFriendBalance } = require('../controllers/friendController');

const router = express.Router();
router.use(authenticateToken);

router.get('/', listFriends);
router.get('/:userId/balance', getFriendBalance);

module.exports = router;