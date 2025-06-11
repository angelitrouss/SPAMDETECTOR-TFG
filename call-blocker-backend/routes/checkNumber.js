const express = require('express');
const router = express.Router();
const { checkNumber } = require('../controllers/numberController');

router.post('/check', checkNumber);

module.exports = router;
