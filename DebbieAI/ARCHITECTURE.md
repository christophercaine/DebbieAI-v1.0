# DebbieAI Architecture

This document provides an overview of the technical architecture of the DebbieAI Android application.

## Duplicate Detection

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
