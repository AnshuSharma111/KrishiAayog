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
// Signup Route (Now also returns a token)
router.post("/signup", async (req, res) => {
  try {
    console.log("Received request to signup");
    const { username, name, password, phoneno } = req.body;

    console.log("Checking if user already exists...");
    let user = await User.findOne({ username });
    if (user) {
      console.log("User already exists...Exiting");
      return res.status(400).json({ message: "User already exists" });
    }
    console.log("User does not exist...");
    console.log("hashing password...");
    // Hash password with bcrypt
    const hashedPassword = await bcrypt.hash(password, 10);

    // Create user in database
    console.log("Registering user on database...");
    user = new User({ username, name, password: hashedPassword, phoneno });
    await user.save();
    console.log("Registered user on database...");

    console.log("Storing password and phone number in Redis...");
    // Store password and phone number in Redis (expires in 24 hours)
    await redis.setex(`user:${user._id}:password`, 86400, hashedPassword);
    await redis.setex(`user:${user._id}:phoneno`, 86400, phoneno);

    console.log("Generating JWT token...");
    // Generate JWT token immediately after signup
    const token = jwt.sign(
      { id: user._id, username: user.username, phoneno: user.phoneno },
      JWT_SECRET,
      { expiresIn: "24h" }
    );

    console.log("Returning response...");
    res.status(201).json({
      message: "User registered successfully",
      id: user._id,
      token,
      username: user.username,
      phoneno: String(user.phoneno), // convert to string
    });
  } catch (error) {
    res.status(500).json({ message: "Server error", error: error.message });
  }
});

// Login Route (Compare Password Using `bcrypt`)
router.post("/login", async (req, res) => {
  try {
    console.log("Received request to login");
    const { username, password } = req.body;

    console.log("Checking if user exists...");
    const user = await User.findOne({ username });
    if (!user) {
      console.log("User does not exist...Exiting"); 
      return res.status(400).json({ message: "Invalid credentials" });
    }
    console.log("User exists...");

    // Fetch hashed password from Redis
    console.log("Fetching hashed password from Redis...");
    let hashedPassword = await redis.get(`user:${user._id}:password`);
    if (!hashedPassword) {
      hashedPassword = user.password;
      await redis.setex(`user:${user._id}:password`, 86400, hashedPassword);
    }

    console.log("Comparing passwords...");
    // Compare input password with hashed password
    const isMatch = await bcrypt.compare(password, hashedPassword);
    if (!isMatch) {
      return res.status(400).json({ message: "Invalid credentials" });
    }

    console.log("Generating JWT token...");
    // Generate JWT token
    const token = jwt.sign(
      { id: user._id, username: user.username, phoneno: user.phoneno },
      JWT_SECRET,
      { expiresIn: "24h" }
    );

    console.log("Returning response...");
    res.json({ token, username: user.username, phoneno: user.phoneno });
  } catch (error) {
    res.status(500).json({ message: "Server error", error: error.message });
  }
});

// Logout Route (Blacklist Token)
router.post("/logout", async (req, res) => {
  try {
    console.log("Received request to logout");
    const token = req.headers.authorization?.split(" ")[1];
    if (!token) {
      console.log("Invalid token...Exiting");
      return res.status(400).json({ message: "No token provided" });
    }

    // Blacklist token in Redis (Expires in 24h to match JWT)
    console.log("Blacklisting token in Redis...");
    await redis.setex(`blacklist:${token}`, 86400, "1");

    console.log("Returning response...");
    res.json({ message: "User logged out successfully" });
  } catch (error) {
    res.status(500).json({ message: "Logout failed", error: error.message });
  }
});

module.exports = router;