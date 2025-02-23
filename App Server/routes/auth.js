const express = require("express");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const crypto = require("node:crypto");
const User = require("../models/User");

const router = express.Router();

const AES_SECRET_KEY = Buffer.from(process.env.AES_SECRET_KEY, "hex");
const JWT_SECRET = process.env.JWT_SECRET;

const blacklistedTokens = new Set(); 

// AES-256 Encryption with Random IV
const encryptData = (data) => {
  const iv = crypto.randomBytes(16);
  const cipher = crypto.createCipheriv("aes-256-cbc", AES_SECRET_KEY, iv);
  let encrypted = cipher.update(data, "utf-8", "hex");
  encrypted += cipher.final("hex");
  return iv.toString("hex") + ":" + encrypted;
};

// AES-256 Decryption
const decryptData = (encryptedData) => {
  const [ivHex, encryptedText] = encryptedData.split(":");
  const iv = Buffer.from(ivHex, "hex");
  const decipher = crypto.createDecipheriv("aes-256-cbc", AES_SECRET_KEY, iv);
  let decrypted = decipher.update(encryptedText, "hex", "utf-8");
  decrypted += decipher.final("utf-8");
  return decrypted;
};

// Middleware to check if token is blacklisted
const isTokenBlacklisted = (req, res, next) => {
  const token = req.headers.authorization?.split(" ")[1]; // Get token from Authorization header
  if (blacklistedTokens.has(token)) {
    return res.status(401).json({ message: "Token is blacklisted. Please log in again." });
  }
  next();
};

// Signup Route
router.post("/signup", async (req, res) => {
  try {
    const { username, name, email, password, secretData } = req.body;

    let user = await User.findOne({ email });
    if (user) return res.status(400).json({ message: "User already exists" });

    const hashedPassword = await bcrypt.hash(password, 10);
    const encryptedData = secretData ? encryptData(secretData) : "";

    user = new User({ username, name, email, password: hashedPassword, encryptedData });
    await user.save();

    res.status(201).json({ message: "User registered successfully" });
  } catch (error) {
    res.status(500).json({ message: "Server error", error: error.message });
  }
});

// Login Route
router.post("/login", async (req, res) => {
  try {
    const { username, password } = req.body;

    const user = await User.findOne({ username });
    if (!user) return res.status(400).json({ message: "Invalid credentials" });

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) return res.status(400).json({ message: "Invalid credentials" });

    const token = jwt.sign(
      { id: user._id, email: user.email, username: user.username },
      JWT_SECRET,
      { expiresIn: "1h" } // Optional: Set token expiration
    );

    res.json({ token, username: user.username, encryptedData: user.encryptedData });
  } catch (error) {
    res.status(500).json({ message: "Server error", error: error.message });
  }
});

// Logout Route (Token Blacklisting)
router.post("/logout", (req, res) => {
  try {
    const token = req.headers.authorization?.split(" ")[1]; // Get token from Authorization header
    if (!token) return res.status(400).json({ message: "No token provided" });

    blacklistedTokens.add(token); // Add token to blacklist

    res.json({ message: "User logged out successfully" });
  } catch (error) {
    res.status(500).json({ message: "Logout failed", error: error.message });
  }
});

// Decrypt Encrypted Data
router.post("/decrypt", (req, res) => {
  try {
    const { encryptedData } = req.body;
    const decryptedData = decryptData(encryptedData);
    res.json({ decryptedData });
  } catch (error) {
    res.status(500).json({ message: "Decryption error", error: error.message });
  }
});

module.exports = router;
