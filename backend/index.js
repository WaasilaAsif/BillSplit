require('dotenv').config();

const express = require('express');
const cors = require('cors');

const healthRoutes = require('./routes/healthRoutes');
const authRoutes = require('./routes/authRoutes');
const groupRoutes = require('./routes/groupRoutes');
const expenseRoutes = require('./routes/expenseRoutes');
const friendRoutes = require('./routes/friendRoutes');
const settlementRoutes = require('./routes/settlementRoutes');
const userRoutes = require('./routes/userRoutes');
const errorHandler = require('./middleware/errorHandler');

const app = express();
const PORT = process.env.PORT || 3000;
app.use(cors());          // allows the Android app (different origin) to call this API
app.use(express.json());  // parses JSON request bodies into req.body


app.use('/health', healthRoutes);
app.use('/auth', authRoutes);
app.use('/groups', groupRoutes);
app.use('/expenses', expenseRoutes);
app.use('/friends', friendRoutes);
app.use('/settlements', settlementRoutes);
app.use('/users', userRoutes);

app.use(errorHandler);

app.listen(PORT, () => {
  console.log(`SplitEase backend listening on port ${PORT}`);
});




