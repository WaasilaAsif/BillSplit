const express = require('express');
const authenticateToken = require('../middleware/authenticateToken');
const { createSettlement } = require('../controllers/settlementController');

const router = express.Router();
router.use(authenticateToken);

router.post('/', createSettlement);

module.exports = router;