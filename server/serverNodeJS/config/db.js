const mongoose = require("mongoose");

const local = "mongodb://localhost:27017/bai13";

const connect = async () => {
  try {
    await mongoose.connect(local);
    console.log("Connect success");
  } catch (error) {
    console.error("Connection to MongoDB failed:", error);
  }
};

module.exports = { connect };
