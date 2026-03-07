# DebbieAI Architecture

This document provides an overview of the technical architecture of the DebbieAI Android application, now expanded into an all-inclusive personal assistant for contractors.

## 6-App Ecosystem

1.  **Debbie Does Photos**: Metadata-rich photo management with Google Photos integration.
2.  **Debbie Does Contacts**: CRM with social media scraping and automated outreach.
3.  **Debbie Does Drafting**: Floor plan generation with automatic customer filing.
4.  **Debbie Does Measurements**: On-site sensor-based distance measuring and bubble level.
5.  **Debbie Does Details**: Task and schedule management with voice command support.
6.  **Debbie Does Data**: Business intelligence and detailed customer dossiers.

## Input Monitoring Layer (The "Advisor")

DebbieAI works by monitoring every available input to assess the owner's life and business:
-   **Microphone**: Real-time listening for job-site context and voice commands.
-   **Messages & Phone Calls**: Monitoring communication for scheduling and follow-ups.
-   **Emails**: Scanning for proposals, invoices, and customer inquiries.
-   **Sensors**: Utilizing IMU and ARCore for field measurements.

## Core AI Engine (DebbieAI Core)

-   **Standalone & Offline-First**: Built on MediaPipe LLM Inference (Gemma 2B) for zero-latency, private, and offline operation.
-   **Personality Module**: Quirky, flirty, and humorous ("Deb Jokes") to improve engagement.
-   **Expert Knowledge Core**: Embedded construction codes, legal standards, and business calculations.

## Four-Tier Memory System

To ensure DebbieAI remembers everything and acts as a true advisor, she uses a four-tier memory architecture:
1.  **Sensory Memory (Tier 1)**: Real-time, transient buffers for microphone input, camera feed, and sensor data (level, measurement) used for immediate context awareness.
2.  **Episodic Memory (Tier 2)**: A persistent "Interaction Journal" that logs every call, text, email, and job-site event in chronological order.
3.  **Semantic Memory (Tier 3)**: The static "Expert Knowledge Base" containing building codes, construction math, and professional standards.
4.  **Strategic Memory (Tier 4)**: The high-level "Business & Life Profile" where Debbie tracks the owner's financial health and long-term business goals.

## Duplicate Detection (Existing)

The duplicate detection mechanism is designed to prevent multiple entries for the same contact in the local database.

### Workflow

1.  **Sync Initiation**: The process begins when the user initiates a device contact sync from the application.
2.  **Contact Reading**: The `ContactSyncService` reads all contacts from the device's contact provider.
3.  **Duplicate Check**: For each contact read from the device, the service checks if a contact with the same `name` already exists in the local Room database.
    - This is done by calling the `findContactByName` function in the `ContactRepository`.
4.  **Flagging Duplicates**:
    - If a contact with the same name is found, the new contact being synced is marked as a duplicate by setting its `isDuplicate` flag to `true` and its `isDuplicateOf` property to the ID of the existing contact.
    - If no contact with the same name is found, the new contact is inserted as a normal entry.
5.  **UI Display**: The UI observes the list of duplicates from the `ContactRepository` and displays them to the user, who can then choose to merge or dismiss them.

### Key Components

-   `ContactSyncService.kt`: Orchestrates the contact syncing process.
-   `ContactRepository.kt`: Provides an interface to the `ContactDao` and exposes the `findContactByName` function.
-   `ContactDao.kt`: Contains the Room database queries, including `findByName`.
-   `Contact.kt`: The data entity that includes the `isDuplicate` and `isDuplicateOf` fields.
