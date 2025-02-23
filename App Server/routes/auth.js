const express = require("express");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const Redis = require("ioredis");
const User = require("../models/User");

const router = express.Router();

// Initialize Redis
const redis = new Redis({
  host: "127.0.0.1",
  port: 6379,
});

const JWT_SECRET = process.env.JWT_SECRET;

// Signup Route
router.post("/signup", async (req, res) => {
  try {
    const { username, name, password, phoneno } = req.body;

    let user = await User.findOne({ username });
    if (user) return res.status(400).json({ message: "User already exists" });

    // Hash password with bcrypt
    const hashedPassword = await bcrypt.hash(password, 10);

    // Fix: Correct variable name for phoneno
    user = new User({ username, name, password: hashedPassword, phoneno });
    await user.save();

    // Store hashed password in Redis for quick access (Optional, expires in 1 hour)
    await redis.setex(`user:${user._id}:password`, 3600, hashedPassword);
    await redis.setex(`user:${user._id}:phoneno`, 3600, phoneno); // Store phone number in Redis (Optional)

    res.status(201).json({ message: "User registered successfully", id: user._id });
  } catch (error) {
    res.status(500).json({ message: "Server error", error: error.message });
  }
});

// Login Route (Compare Password Using `bcrypt`)
router.post("/login", async (req, res) => {
  try {
    const { username, password } = req.body;

    const user = await User.findOne({ username });
    if (!user) return res.status(400).json({ message: "Invalid credentials" });

    // Fetch hashed password from Redis
    let hashedPassword = await redis.get(`user:${user._id}:password`);
    if (!hashedPassword) {
      hashedPassword = user.password;
      await redis.setex(`user:${user._id}:password`, 3600, hashedPassword);
    }

    // Compare input password with hashed password
    const isMatch = await bcrypt.compare(password, hashedPassword);
    if (!isMatch) {
      return res.status(400).json({ message: "Invalid credentials" });
    }

    // Generate JWT token
    const token = jwt.sign(
      { id: user._id, username: user.username, phoneno: user.phoneno },
      JWT_SECRET,
      { expiresIn: "2h" }
    );

    res.json({ token, username: user.username, phoneno: user.phoneno });
  } catch (error) {
    res.status(500).json({ message: "Server error", error: error.message });
  }
});

// Logout Route (Blacklist Token)
router.post("/logout", async (req, res) => {
  try {
    const token = req.headers.authorization?.split(" ")[1];
    if (!token) return res.status(400).json({ message: "No token provided" });

    // Blacklist token in Redis (Expires in 2h to match JWT)
    await redis.setex(`blacklist:${token}`, 7200, "1");

    res.json({ message: "User logged out successfully" });
  } catch (error) {
    res.status(500).json({ message: "Logout failed", error: error.message });
  }
});

module.exports = router;