const express = require('express');
const authenticateToken = require('../middleware/authenticateToken');
const { searchUsers } = require('../controllers/userController');

const router = express.Router();
router.use(authenticateToken);

router.get('/', searchUsers);

module.exports = router;
