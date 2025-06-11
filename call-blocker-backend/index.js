require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const checkNumberRoute = require('./routes/checkNumber');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

mongoose.connect(process.env.MONGO_URI)
    .then(() => console.log('MongoDB connected'))
    .catch(err => console.error(err));

app.use('/api', checkNumberRoute);

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});
