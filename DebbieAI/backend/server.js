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
    // Expand contacts table
    await pool.query(`
      CREATE TABLE IF NOT EXISTS contacts (
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        phones TEXT[] DEFAULT '{}',
        emails TEXT[] DEFAULT '{}',
        company VARCHAR(255),
        job_title VARCHAR(255),
        notes TEXT,
        address TEXT,
        social_media JSONB DEFAULT '{}',
        occupation VARCHAR(255),
        income_level VARCHAR(255),
        assets TEXT,
        last_ai_source_at TIMESTAMP,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
      
      CREATE INDEX IF NOT EXISTS idx_contacts_name ON contacts(name);
      CREATE INDEX IF NOT EXISTS idx_contacts_created_at ON contacts(created_at);

      CREATE TABLE IF NOT EXISTS interactions (
        id BIGSERIAL PRIMARY KEY,
        contact_id BIGINT REFERENCES contacts(id) ON DELETE CASCADE,
        timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        type VARCHAR(50),
        summary TEXT,
        sentiment VARCHAR(20),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );

      CREATE INDEX IF NOT EXISTS idx_interactions_contact_id ON interactions(contact_id);
      CREATE INDEX IF NOT EXISTS idx_interactions_timestamp ON interactions(timestamp);
    `);
    console.log("✅ Database tables initialized");
  } catch (error) {
    console.error("❌ Error initializing database:", error);
  }
}

// Interaction Endpoints
app.get("/api/contacts/:id/interactions", async (req, res) => {
  try {
    const { id } = req.params;
    const result = await pool.query(
      "SELECT * FROM interactions WHERE contact_id = $1 ORDER BY timestamp DESC",
      [id]
    );
    res.json(result.rows);
  } catch (error) {
    console.error("Error fetching interactions:", error);
    res.status(500).json({ error: "Failed to fetch interactions" });
  }
});

app.post("/api/contacts/:id/interactions", async (req, res) => {
  try {
    const { id } = req.params;
    const { type, summary, sentiment } = req.body;
    const result = await pool.query(
      "INSERT INTO interactions (contact_id, type, summary, sentiment) VALUES ($1, $2, $3, $4) RETURNING *",
      [id, type, summary, sentiment || "neutral"]
    );
    res.status(201).json(result.rows[0]);
  } catch (error) {
    console.error("Error creating interaction:", error);
    res.status(500).json({ error: "Failed to create interaction" });
  }
});

// Sourcing Stub
app.post("/api/contacts/:id/source-info", async (req, res) => {
  try {
    const { id } = req.params;
    // For now, this is a stub that simulates finding information.
    // In a final version, this could trigger a background worker that searches the web.
    const mockUpdate = {
      social_media: { linkedin: "https://linkedin.com/in/profile", twitter: "@contractor_pro" },
      occupation: "Freelance Developer",
      income_level: "$75k - $120k",
      assets: "Owns professional equipment, local workshop",
      last_ai_source_at: new Date()
    };

    const result = await pool.query(
      "UPDATE contacts SET social_media=$1, occupation=$2, income_level=$3, assets=$4, last_ai_source_at=$5, updated_at=NOW() WHERE id=$6 RETURNING *",
      [mockUpdate.social_media, mockUpdate.occupation, mockUpdate.income_level, mockUpdate.assets, mockUpdate.last_ai_source_at, id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: "Contact not found" });
    }
    res.json({ message: "Information sourced (Mock)", contact: result.rows[0] });
  } catch (error) {
    console.error("Error sourcing info:", error);
    res.status(500).json({ error: "Failed to source info" });
  }
});

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
