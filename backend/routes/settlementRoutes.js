const express = require('express');
const authenticateToken = require('../middleware/authenticateToken');
const { createSettlement, listSettlements } = require('../controllers/settlementController');

const router = express.Router();
router.use(authenticateToken);

router.post('/', createSettlement);
router.get('/', listSettlements);

module.exports = router;