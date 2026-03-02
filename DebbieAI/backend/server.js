const express = require("express");
const cors = require("cors");
const { Pool } = require("pg");
require("dotenv").config();

const app = express();

app.use(cors());
app.use(express.json());

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: process.env.NODE_ENV === "production" ? { rejectUnauthorized: false } : false
});

app.get("/api/health", (req, res) => {
  res.json({
    status: "OK",
    timestamp: new Date().toISOString(),
    environment: process.env.NODE_ENV || "development"
  });
});

app.get("/api/contacts", async (req, res) => {
  try {
    const result = await pool.query("SELECT * FROM contacts ORDER BY name ASC LIMIT 100");
    res.json(result.rows);
  } catch (error) {
    console.error("Error fetching contacts:", error);
    res.status(500).json({ error: "Failed to fetch contacts" });
  }
});

app.get("/api/contacts/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const result = await pool.query("SELECT * FROM contacts WHERE id = $1", [id]);
    if (result.rows.length === 0) {
      return res.status(404).json({ error: "Contact not found" });
    }
    res.json(result.rows[0]);
  } catch (error) {
    console.error("Error fetching contact:", error);
    res.status(500).json({ error: "Failed to fetch contact" });
  }
});

app.post("/api/contacts", async (req, res) => {
  try {
    const { name, phones, emails, company, jobTitle, notes } = req.body;
    if (!name) {
      return res.status(400).json({ error: "Name is required" });
    }
    const result = await pool.query(
      "INSERT INTO contacts (name, phones, emails, company, job_title, notes) VALUES ($1, $2, $3, $4, $5, $6) RETURNING *",
      [name, phones || [], emails || [], company || "", jobTitle || "", notes || ""]
    );
    res.status(201).json(result.rows[0]);
  } catch (error) {
    console.error("Error creating contact:", error);
    res.status(500).json({ error: "Failed to create contact" });
  }
});

app.put("/api/contacts/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const { name, phones, emails, company, jobTitle, notes } = req.body;
    const result = await pool.query(
      "UPDATE contacts SET name=$1, phones=$2, emails=$3, company=$4, job_title=$5, notes=$6, updated_at=NOW() WHERE id=$7 RETURNING *",
      [name, phones || [], emails || [], company || "", jobTitle || "", notes || "", id]
    );
    if (result.rows.length === 0) {
      return res.status(404).json({ error: "Contact not found" });
    }
    res.json(result.rows[0]);
  } catch (error) {
    console.error("Error updating contact:", error);
    res.status(500).json({ error: "Failed to update contact" });
  }
});

app.delete("/api/contacts/:id", async (req, res) => {
  try {
    const { id } = req.params;
    const result = await pool.query("DELETE FROM contacts WHERE id = $1 RETURNING id", [id]);
    if (result.rows.length === 0) {
      return res.status(404).json({ error: "Contact not found" });
    }
    res.json({ message: "Contact deleted", id });
  } catch (error) {
    console.error("Error deleting contact:", error);
    res.status(500).json({ error: "Failed to delete contact" });
  }
});

async function initializeDatabase() {
  try {
    await pool.query(`
      CREATE TABLE IF NOT EXISTS contacts (
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        phones TEXT[] DEFAULT '{}',
        emails TEXT[] DEFAULT '{}',
        company VARCHAR(255),
        job_title VARCHAR(255),
        notes TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
      
      CREATE INDEX IF NOT EXISTS idx_contacts_name ON contacts(name);
      CREATE INDEX IF NOT EXISTS idx_contacts_created_at ON contacts(created_at);
    `);
    console.log("✅ Database tables initialized");
  } catch (error) {
    console.error("❌ Error initializing database:", error);
  }
}

const PORT = process.env.PORT || 3000;

app.listen(PORT, async () => {
  console.log(`🚀 Server running on port ${PORT}`);
  console.log(`📍 API: http://localhost:${PORT}/api`);
  console.log(`💚 Health check: http://localhost:${PORT}/api/health`);
  await initializeDatabase();
});

process.on("SIGTERM", async () => {
  console.log("SIGTERM signal received: closing HTTP server");
  await pool.end();
  process.exit(0);
});
