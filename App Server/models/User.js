const mongoose = require("mongoose");

const UserSchema = new mongoose.Schema({
  username: { type: String, required: true, unique: true }, // Added username
  name: { type: String, required: true },
  email: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  encryptedData: { type: String } // Encrypted sensitive user data
});

module.exports = mongoose.model("User", UserSchema);
