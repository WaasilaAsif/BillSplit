const jwt = require('jsonwebtoken');

const EXPIRES_IN = '2d';

function generateToken(user) {
  return jwt.sign(
    { userId: user.id },
    process.env.JWT_SECRET,
    { expiresIn: EXPIRES_IN }
  );
}

function verifyToken(token) {
  return jwt.verify(token, process.env.JWT_SECRET); // throws if invalid/expired
}

module.exports = { generateToken, verifyToken };