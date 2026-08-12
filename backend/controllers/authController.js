const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const pool = require('../db/pool');

const BCRYPT_ROUNDS = 12; 
const JWT_EXPIRY = '2d';  


function signToken(user) {
    const payload = {
        sub: user.id,
        username: user.username,
    };
    return jwt.sign(payload, process.env.JWT_SECRET, { expiresIn: JWT_EXPIRY });
}
    

function toPublicUser(row) {
    return {
        id: row.id,
        username: row.username,
        email: row.email,
        created_at: row.created_at,
    };
}

async function register(req, res, next) {
    try {
        const { username, email, password } = req.body;

        if (!username || !email || !password) {
            return res.status(400).json({ status: 'error', message: 'username, email, and password are all required' });
        }
        if (password.length < 8) {
            return res.status(400).json({ status: 'error', message: 'Password must be at least 8 characters' });
        }

        
        const existing = await pool.query('SELECT id FROM users WHERE email = $1', [email.toLowerCase()]);
        if (existing.rows.length > 0) {
            return res.status(409).json({ status: 'error', message: 'An account with this email already exists' });
        }

        const passwordHash = await bcrypt.hash(password, BCRYPT_ROUNDS);

        const result = await pool.query(
            `INSERT INTO users (username, email, password_hash)
             VALUES ($1, $2, $3)
             RETURNING id, username, email, created_at`,
            [username, email.toLowerCase(), passwordHash]
        );

        const user = result.rows[0];
        const token = signToken(user);

        res.status(201).json({ token, user: toPublicUser(user) });
    } catch (err) {
        if (err.code === '23505') {
            return res.status(409).json({ status: 'error', message: 'An account with this email already exists' });
        }
        next(err);
    }
}

async function login(req, res, next) {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({ status: 'error', message: 'email and password are required' });
        }

        const result = await pool.query('SELECT * FROM users WHERE email = $1', [email.toLowerCase()]);
        const user = result.rows[0];

        const genericError = { status: 'error', message: 'Invalid email or password' };

        if (!user) {
            return res.status(401).json(genericError);
        }

        const passwordMatches = await bcrypt.compare(password, user.password_hash);
        if (!passwordMatches) {
            return res.status(401).json(genericError);
        }

        const token = signToken(user);
        res.json({ token, user: toPublicUser(user) });
    } catch (err) {
        next(err);
    }
}

module.exports = { register, login };