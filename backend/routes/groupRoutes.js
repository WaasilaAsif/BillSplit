const express = require('express');
const authenticateToken = require('../middleware/authenticateToken');
const { createGroup, listGroups, getGroupDetail, addGroupMember } = require('../controllers/groupController');

const router = express.Router();

router.use(authenticateToken); //to endure logged in user

router.post('/', createGroup);
router.get('/', listGroups);
router.get('/:id', getGroupDetail);
router.post('/:id/members', addGroupMember);

module.exports = router;