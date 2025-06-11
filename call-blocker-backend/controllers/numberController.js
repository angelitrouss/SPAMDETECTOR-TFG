const mongoose = require('mongoose');

const blockedSchema = new mongoose.Schema({
    number: String,
});

const BlockedNumber = mongoose.model('BlockedNumber', blockedSchema);

exports.checkNumber = async (req, res) => {
    const { number } = req.body;

    try {
        const found = await BlockedNumber.findOne({ number });
        if (found) {
            res.json({ blocked: true });
        } else {
            res.json({ blocked: false });
        }
    } catch (error) {
        res.status(500).json({ error: 'Server error' });
    }
};
