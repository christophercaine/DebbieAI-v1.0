# DebbieAI - Unified Architecture

**Version:** 1.0  
**Date:** January 29, 2026  
**Status:** Foundation Design

---

## Overview

Single unified Android application encompassing all five Debbie modules:

| Module | Package | Status | Description |
|--------|---------|--------|-------------|
| **Contacts** | `contacts` | ✅ MVP Ready | CRM, customer management |
| **Photos** | `photos` | ✅ MVP Ready | Job site photo organization |
| **Details** | `details` | 🔲 Phase 5 | Tasks, calendar, reminders |
| **Data** | `data_ai` | 🔲 Phase 3 | AI enrichment engine |
| **Drafting** | `drafting` | 🔲 Phase 4 | Estimates, floor plans |

---

## Package Structure

```
com.debbiedoesit.debbieai/
│
├── MainActivity.kt                 # Single entry point with bottom nav
├── DebbieApplication.kt            # Application class for DI
│
├── core/                           # Shared infrastructure
│   ├── database/
│   │   ├── DebbieDatabase.kt       # Unified Room database
│   │   └── Converters.kt           # Type converters
│   ├── di/                         # Dependency injection (future: Hilt)
│   ├── util/
│   │   ├── DateUtils.kt
│   │   ├── FileUtils.kt
│   │   └── Extensions.kt
│   └── ui/
│       ├── theme/
│       │   ├── Color.kt
│       │   ├── Type.kt
│       │   └── Theme.kt
│       └── components/             # Shared UI components
│           ├── DebbieTopBar.kt
│           ├── DebbieBottomNav.kt
│           ├── LoadingIndicator.kt
│           └── EmptyState.kt
│
├── contacts/                       # 👥 Debbie Does Contacts
│   ├── data/
│   │   ├── model/
│   │   │   ├── Contact.kt
│   │   │   └── Address.kt
│   │   ├── dao/
│   │   │   ├── ContactDao.kt
│   │   │   └── AddressDao.kt
│   │   ├── repository/
│   │   │   └── ContactRepository.kt
│   │   └── sync/
│   │       └── ContactSyncService.kt
│   ├── ui/
│   │   ├── screens/
│   │   │   ├── ContactListScreen.kt
│   │   │   ├── ContactDetailScreen.kt
│   │   │   ├── AddContactScreen.kt
│   │   │   └── DuplicatesScreen.kt
│   │   └── components/
│   │       └── ContactListItem.kt
│   └── viewmodel/
│       ├── ContactViewModel.kt
│       └── ContactViewModelFactory.kt
│
├── photos/                         # 📸 Debbie Does Photos
│   ├── data/
│   │   ├── model/
│   │   │   ├── Photo.kt
│   │   │   └── Album.kt
│   │   ├── dao/
│   │   │   ├── PhotoDao.kt
│   │   │   └── AlbumDao.kt
│   │   ├── repository/
│   │   │   └── PhotoRepository.kt
│   │   └── sync/
│   │       └── PhotoImportService.kt
│   ├── ui/
│   │   ├── screens/
│   │   │   ├── PhotoGalleryScreen.kt
│   │   │   ├── PhotoDetailScreen.kt
│   │   │   ├── AlbumListScreen.kt
│   │   │   └── AlbumDetailScreen.kt
│   │   └── components/
│   │       ├── PhotoGridItem.kt
│   │       └── AlbumCard.kt
│   └── viewmodel/
│       ├── PhotoViewModel.kt
│       └── PhotoViewModelFactory.kt
│
├── details/                        # 📋 Debbie Does Details
│   ├── data/
│   │   ├── model/
│   │   │   ├── Task.kt
│   │   │   ├── Event.kt
│   │   │   ├── Reminder.kt
│   │   │   └── CrewMember.kt
│   │   ├── dao/
│   │   │   ├── TaskDao.kt
│   │   │   ├── EventDao.kt
│   │   │   └── ReminderDao.kt
│   │   ├── repository/
│   │   │   └── DetailsRepository.kt
│   │   └── service/
│   │       ├── NotificationService.kt
│   │       ├── CalendarSyncService.kt
│   │       └── VoiceCommandService.kt
│   ├── ui/
│   │   ├── screens/
│   │   │   ├── TaskListScreen.kt
│   │   │   ├── TaskDetailScreen.kt
│   │   │   ├── CalendarScreen.kt
│   │   │   ├── DailyBriefingScreen.kt
│   │   │   └── CrewScreen.kt
│   │   └── components/
│   │       ├── TaskItem.kt
│   │       ├── CalendarDay.kt
│   │       └── ReminderChip.kt
│   └── viewmodel/
│       ├── DetailsViewModel.kt
│       └── DetailsViewModelFactory.kt
│
├── drafting/                       # 📐 Debbie Does Drafting
│   ├── data/
│   │   ├── model/
│   │   │   ├── Estimate.kt
│   │   │   ├── EstimateLineItem.kt
│   │   │   ├── FloorPlan.kt
│   │   │   ├── Room.kt
│   │   │   ├── Material.kt
│   │   │   └── PriceTemplate.kt
│   │   ├── dao/
│   │   │   ├── EstimateDao.kt
│   │   │   ├── FloorPlanDao.kt
│   │   │   └── MaterialDao.kt
│   │   ├── repository/
│   │   │   └── DraftingRepository.kt
│   │   └── service/
│   │       ├── MeasurementService.kt
│   │       ├── FloorPlanGenerator.kt
│   │       └── PdfExportService.kt
│   ├── ui/
│   │   ├── screens/
│   │   │   ├── EstimateListScreen.kt
│   │   │   ├── EstimateDetailScreen.kt
│   │   │   ├── EstimateBuilderScreen.kt
│   │   │   ├── FloorPlanScreen.kt
│   │   │   ├── MaterialsScreen.kt
│   │   │   └── TemplatesScreen.kt
│   │   └── components/
│   │       ├── LineItemRow.kt
│   │       ├── RoomCard.kt
│   │       └── FloorPlanCanvas.kt
│   └── viewmodel/
│       ├── DraftingViewModel.kt
│       └── DraftingViewModelFactory.kt
│
├── data_ai/                        # 🔍 Debbie Does Data (AI Engine)
│   ├── data/
│   │   ├── model/
│   │   │   ├── AiAnalysis.kt
│   │   │   ├── DetectedObject.kt
│   │   │   ├── DetectedFace.kt
│   │   │   ├── ExtractedText.kt
│   │   │   └── BusinessInsight.kt
│   │   ├── dao/
│   │   │   └── AiAnalysisDao.kt
│   │   ├── repository/
│   │   │   └── AiRepository.kt
│   │   └── service/
│   │       ├── VisionService.kt        # Google Cloud Vision
│   │       ├── OcrService.kt           # Text extraction
│   │       ├── FaceRecognitionService.kt
│   │       ├── ObjectDetectionService.kt
│   │       └── PatternAnalysisService.kt
│   ├── ui/
│   │   ├── screens/
│   │   │   ├── InsightsScreen.kt
│   │   │   ├── AnalysisQueueScreen.kt
│   │   │   └── PatternReportScreen.kt
│   │   └── components/
│   │       ├── InsightCard.kt
│   │       └── AnalysisProgress.kt
│   └── viewmodel/
│       ├── AiViewModel.kt
│       └── AiViewModelFactory.kt
│
├── jobs/                           # 🔗 Central Job/Project entity
│   ├── data/
│   │   ├── model/
│   │   │   ├── Job.kt
│   │   │   └── JobStatus.kt
│   │   ├── dao/
│   │   │   └── JobDao.kt
│   │   └── repository/
│   │       └── JobRepository.kt
│   ├── ui/
│   │   ├── screens/
│   │   │   ├── JobListScreen.kt
│   │   │   ├── JobDetailScreen.kt
│   │   │   └── JobDashboardScreen.kt
│   │   └── components/
│   │       └── JobCard.kt
│   └── viewmodel/
│       ├── JobViewModel.kt
│       └── JobViewModelFactory.kt
│
└── navigation/
    ├── DebbieNavigation.kt         # Main NavHost
    ├── BottomNavItems.kt           # Bottom nav definitions
    └── Routes.kt                   # All route constants
```

---

## Database Schema

### Entity Relationships

```
                    ┌─────────────┐
                    │    Job      │ (Central linking entity)
                    │─────────────│
                    │ id          │
                    │ contactId   │───────┐
                    │ addressId   │       │
                    │ title       │       │
                    │ status      │       │
                    └──────┬──────┘       │
                           │              │
       ┌───────────────────┼──────────────┼───────────────────┐
       │                   │              │                   │
       ▼                   ▼              ▼                   ▼
┌─────────────┐    ┌─────────────┐  ┌─────────────┐   ┌─────────────┐
│   Photo     │    │   Task      │  │  Contact    │   │  Estimate   │
│─────────────│    │─────────────│  │─────────────│   │─────────────│
│ id          │    │ id          │  │ id          │   │ id          │
│ jobId ──────│────│ jobId ──────│──│             │───│ jobId ──────│
│ contactId   │    │ contactId   │  │ name        │   │ contactId   │
│ albumId     │    │ assignedTo  │  │ phones[]    │   │ items[]     │
│ filePath    │    │ dueDate     │  │ emails[]    │   │ total       │
│ category    │    │ reminder    │  │ company     │   │ status      │
│ aiTags[]    │    │ status      │  └──────┬──────┘   └─────────────┘
└─────────────┘    └─────────────┘         │
       │                                   │
       ▼                                   ▼
┌─────────────┐                    ┌─────────────┐
│   Album     │                    │   Address   │
│─────────────│                    │─────────────│
│ id          │                    │ id          │
│ jobId       │                    │ contactId   │
│ contactId   │                    │ type        │
│ name        │                    │ street      │
│ coverPhoto  │                    │ city/state  │
└─────────────┘                    └─────────────┘
```

---

## Core Entities

### 1. Contact (Contacts Module) - ✅ EXISTS

```kotlin
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val firstName: String = "",
    val lastName: String = "",
    val phones: List<String> = emptyList(),
    val emails: List<String> = emptyList(),
    val company: String = "",
    val jobTitle: String = "",
    val notes: String = "",
    val socialMedia: Map<String, String> = emptyMap(),
    val customerType: String = "lead",  // lead, active, past, vendor
    val source: String = "",            // referral, website, etc.
    val rating: Int = 0,                // 1-5 stars
    val totalRevenue: Double = 0.0,     // calculated from jobs
    val isDuplicate: Boolean = false,
    val isDuplicateOf: Long? = null,
    val isPlaceholder: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val syncedAt: LocalDateTime? = null
)
```

### 2. Address (Contacts Module) - ✅ EXISTS

```kotlin
@Entity(tableName = "addresses")
data class Address(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactId: Long,
    val type: AddressType = AddressType.HOME,  // HOME, WORK, BILLING, PROPERTY
    val label: String = "",
    val street: String = "",
    val unit: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = "USA",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val notes: String = "",
    val isPrimary: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
```

### 3. Photo (Photos Module) - ✅ EXISTS

```kotlin
@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    // File info
    val fileName: String,
    val filePath: String,
    val thumbnailPath: String? = null,
    val fileSize: Long = 0,
    val mimeType: String = "image/jpeg",
    val width: Int = 0,
    val height: Int = 0,
    
    // Relationships
    val albumId: Long? = null,
    val contactId: Long? = null,
    val jobId: Long? = null,
    
    // Organization
    val category: PhotoCategory = PhotoCategory.GENERAL,
    val tags: List<String> = emptyList(),
    
    // EXIF data
    val takenAt: LocalDateTime? = null,
    val deviceMake: String? = null,
    val deviceModel: String? = null,
    val gpsLatitude: Double? = null,
    val gpsLongitude: Double? = null,
    val locationName: String? = null,
    val locationCity: String? = null,
    val locationState: String? = null,
    
    // Before/After linking
    val beforePhotoId: Long? = null,
    val afterPhotoId: Long? = null,
    
    // AI analysis (Data module populates)
    val aiAnalyzed: Boolean = false,
    val aiDescription: String? = null,
    val aiObjects: List<String> = emptyList(),
    val aiFaces: List<String> = emptyList(),
    val aiText: String? = null,
    val aiConfidence: Float? = null,
    
    // Status
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val isDeleted: Boolean = false,
    val syncStatus: SyncStatus = SyncStatus.LOCAL,
    
    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class PhotoCategory {
    BEFORE, DURING, AFTER, DAMAGE, MATERIALS,
    MEASUREMENT, RECEIPT, REFERENCE, GENERAL
}
```

### 4. Album (Photos Module) - ✅ EXISTS

```kotlin
@Entity(tableName = "albums")
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val albumType: AlbumType = AlbumType.CUSTOM,
    
    // Relationships
    val contactId: Long? = null,
    val jobId: Long? = null,
    val coverPhotoId: Long? = null,
    val coverPhotoPath: String? = null,
    
    // Location
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val gpsLatitude: Double? = null,
    val gpsLongitude: Double? = null,
    
    // Dates
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    
    // Cached counts
    val photoCount: Int = 0,
    val beforeCount: Int = 0,
    val afterCount: Int = 0,
    
    // Status
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class AlbumType {
    JOB, PROJECT, CUSTOMER, DATE, LOCATION, CUSTOM, SMART
}
```

### 5. Job (Jobs Module) - 🆕 NEW

```kotlin
@Entity(tableName = "jobs")
data class Job(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    // Basic info
    val title: String,
    val description: String = "",
    val jobNumber: String? = null,      // User's job numbering
    
    // Relationships
    val contactId: Long,                 // Required - who is this for?
    val addressId: Long? = null,         // Job site address
    
    // Status tracking
    val status: JobStatus = JobStatus.LEAD,
    val priority: Int = 0,               // 0=normal, 1=high, 2=urgent
    
    // Dates
    val createdDate: LocalDate = LocalDate.now(),
    val scheduledStart: LocalDate? = null,
    val scheduledEnd: LocalDate? = null,
    val actualStart: LocalDate? = null,
    val actualEnd: LocalDate? = null,
    
    // Financial
    val estimatedValue: Double = 0.0,
    val actualValue: Double = 0.0,
    val depositReceived: Double = 0.0,
    val balanceDue: Double = 0.0,
    
    // Work details
    val workType: String = "",           // Flooring, Kitchen, etc.
    val tags: List<String> = emptyList(),
    val notes: String = "",
    
    // Linked counts (cached)
    val photoCount: Int = 0,
    val taskCount: Int = 0,
    val estimateCount: Int = 0,
    
    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class JobStatus {
    LEAD,           // Initial inquiry
    QUOTED,         // Estimate sent
    SCHEDULED,      // Job scheduled
    IN_PROGRESS,    // Work ongoing
    ON_HOLD,        // Paused
    COMPLETED,      // Work done
    INVOICED,       // Invoice sent
    PAID,           // Payment received
    CANCELLED       // Job cancelled
}
```

### 6. Task (Details Module) - 🆕 NEW

```kotlin
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    // Basic info
    val title: String,
    val description: String = "",
    
    // Relationships
    val contactId: Long? = null,
    val jobId: Long? = null,
    val assignedToId: Long? = null,      // CrewMember id
    val parentTaskId: Long? = null,      // For subtasks
    
    // Scheduling
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val reminderAt: LocalDateTime? = null,
    val estimatedMinutes: Int? = null,
    val actualMinutes: Int? = null,
    
    // Recurrence
    val isRecurring: Boolean = false,
    val recurrenceRule: String? = null,  // iCal RRULE format
    
    // Status
    val status: TaskStatus = TaskStatus.TODO,
    val priority: TaskPriority = TaskPriority.NORMAL,
    
    // Completion
    val completedAt: LocalDateTime? = null,
    val completedBy: String? = null,
    val completionNotes: String = "",
    
    // Metadata
    val tags: List<String> = emptyList(),
    val category: TaskCategory = TaskCategory.GENERAL,
    
    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class TaskStatus {
    TODO, IN_PROGRESS, WAITING, COMPLETED, CANCELLED
}

enum class TaskPriority {
    LOW, NORMAL, HIGH, URGENT
}

enum class TaskCategory {
    GENERAL, CALL, MEETING, SITE_VISIT, FOLLOW_UP,
    ESTIMATE, PURCHASE, DELIVERY, WORK, INVOICE
}
```

### 7. Event (Details Module) - 🆕 NEW

```kotlin
@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    val title: String,
    val description: String = "",
    
    // Relationships
    val contactId: Long? = null,
    val jobId: Long? = null,
    val addressId: Long? = null,
    
    // Timing
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val isAllDay: Boolean = false,
    
    // Location
    val location: String = "",
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    
    // Reminders
    val reminderMinutes: List<Int> = listOf(30),  // 30 min before
    
    // Recurrence
    val isRecurring: Boolean = false,
    val recurrenceRule: String? = null,
    
    // Calendar sync
    val externalCalendarId: String? = null,
    val syncedAt: LocalDateTime? = null,
    
    // Status
    val status: EventStatus = EventStatus.CONFIRMED,
    val color: String? = null,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class EventStatus {
    TENTATIVE, CONFIRMED, CANCELLED
}
```

### 8. CrewMember (Details Module) - 🆕 NEW

```kotlin
@Entity(tableName = "crew_members")
data class CrewMember(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    val name: String,
    val phone: String = "",
    val email: String = "",
    val role: String = "",              // Lead, Helper, Subcontractor
    
    val hourlyRate: Double = 0.0,
    val isActive: Boolean = true,
    val color: String? = null,          // For calendar display
    
    val notes: String = "",
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
```

### 9. Estimate (Drafting Module) - 🆕 NEW

```kotlin
@Entity(tableName = "estimates")
data class Estimate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    // Identification
    val estimateNumber: String,         // "2026-0142"
    val title: String,
    val description: String = "",
    
    // Relationships
    val contactId: Long,
    val jobId: Long? = null,
    val addressId: Long? = null,
    val templateId: Long? = null,
    
    // Status
    val status: EstimateStatus = EstimateStatus.DRAFT,
    val validUntil: LocalDate? = null,
    
    // Room/measurement data (from AI or manual)
    val rooms: List<RoomMeasurement> = emptyList(),
    val totalSquareFeet: Double = 0.0,
    
    // Financial
    val subtotal: Double = 0.0,
    val taxRate: Double = 0.06,
    val taxAmount: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val depositRequired: Double = 0.0,
    
    // Terms
    val paymentTerms: String = "50% deposit, balance on completion",
    val estimatedDays: Int? = null,
    val notes: String = "",
    val internalNotes: String = "",
    
    // Versioning
    val version: Int = 1,
    val previousVersionId: Long? = null,
    
    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val sentAt: LocalDateTime? = null,
    val acceptedAt: LocalDateTime? = null
)

enum class EstimateStatus {
    DRAFT, SENT, VIEWED, ACCEPTED, DECLINED, EXPIRED, REVISED
}

data class RoomMeasurement(
    val name: String,
    val length: Double,        // feet
    val width: Double,
    val height: Double = 8.0,
    val squareFeet: Double,
    val notes: String = "",
    val photoIds: List<Long> = emptyList()
)
```

### 10. EstimateLineItem (Drafting Module) - 🆕 NEW

```kotlin
@Entity(tableName = "estimate_line_items")
data class EstimateLineItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    val estimateId: Long,
    val sortOrder: Int = 0,
    
    // Item details
    val category: LineItemCategory,
    val description: String,
    val notes: String = "",
    
    // Quantity
    val quantity: Double = 1.0,
    val unit: String = "each",          // each, sq ft, linear ft, hour
    
    // Pricing
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0,
    val isTaxable: Boolean = true,
    
    // For materials
    val materialId: Long? = null,
    val wastePercent: Double = 0.0,     // 10% waste factor
    
    // For labor
    val laborHours: Double = 0.0,
    val laborRate: Double = 0.0,
    
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class LineItemCategory {
    MATERIALS, LABOR, EQUIPMENT, SUBCONTRACTOR,
    PERMIT, DISPOSAL, DELIVERY, OTHER
}
```

### 11. Material (Drafting Module) - 🆕 NEW

```kotlin
@Entity(tableName = "materials")
data class Material(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    val name: String,
    val description: String = "",
    val category: String = "",          // Flooring, Paint, Lumber
    val brand: String = "",
    val sku: String = "",
    
    // Pricing
    val unitPrice: Double = 0.0,
    val unit: String = "each",
    val priceUpdatedAt: LocalDate? = null,
    
    // Supplier
    val supplier: String = "",
    val supplierUrl: String = "",
    
    // Coverage (for sq ft calculations)
    val coveragePerUnit: Double? = null,  // sq ft per unit
    val defaultWastePercent: Double = 10.0,
    
    val isActive: Boolean = true,
    val isFavorite: Boolean = false,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
```

### 12. AiAnalysis (Data Module) - 🆕 NEW

```kotlin
@Entity(tableName = "ai_analyses")
data class AiAnalysis(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    // What was analyzed
    val photoId: Long? = null,
    val documentPath: String? = null,
    
    // Analysis type
    val analysisType: AnalysisType,
    
    // Results
    val status: AnalysisStatus = AnalysisStatus.PENDING,
    val confidence: Float = 0f,
    val results: String = "",           // JSON blob
    
    // Specific extractions
    val detectedObjects: List<DetectedObject> = emptyList(),
    val detectedFaces: List<DetectedFace> = emptyList(),
    val extractedText: String? = null,
    val suggestedTags: List<String> = emptyList(),
    val suggestedCategory: String? = null,
    
    // Measurements (for Drafting)
    val measurements: List<Measurement>? = null,
    
    // Error handling
    val errorMessage: String? = null,
    val retryCount: Int = 0,
    
    // API tracking
    val apiProvider: String = "google_vision",
    val apiCost: Double = 0.0,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null
)

enum class AnalysisType {
    OBJECT_DETECTION, FACE_DETECTION, OCR,
    DAMAGE_ASSESSMENT, MEASUREMENT, MATERIAL_ID
}

enum class AnalysisStatus {
    PENDING, PROCESSING, COMPLETED, FAILED
}

data class DetectedObject(
    val label: String,
    val confidence: Float,
    val boundingBox: BoundingBox? = null
)

data class DetectedFace(
    val faceId: String,
    val personName: String? = null,     // If matched to crew
    val confidence: Float,
    val boundingBox: BoundingBox? = null
)

data class Measurement(
    val label: String,
    val value: Double,
    val unit: String,
    val confidence: Float
)

data class BoundingBox(
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float
)
```

---

## Navigation Structure

```kotlin
sealed class DebbieRoute(val route: String) {
    // Home/Dashboard
    object Home : DebbieRoute("home")
    
    // Contacts
    object ContactList : DebbieRoute("contacts")
    object ContactDetail : DebbieRoute("contacts/{contactId}")
    object AddContact : DebbieRoute("contacts/add")
    object Duplicates : DebbieRoute("contacts/duplicates")
    
    // Photos
    object PhotoGallery : DebbieRoute("photos")
    object PhotoDetail : DebbieRoute("photos/{photoId}")
    object Albums : DebbieRoute("photos/albums")
    object AlbumDetail : DebbieRoute("photos/albums/{albumId}")
    object PhotoSearch : DebbieRoute("photos/search")
    object Favorites : DebbieRoute("photos/favorites")
    object Trash : DebbieRoute("photos/trash")
    
    // Jobs
    object JobList : DebbieRoute("jobs")
    object JobDetail : DebbieRoute("jobs/{jobId}")
    object JobDashboard : DebbieRoute("jobs/{jobId}/dashboard")
    
    // Details (Tasks/Calendar)
    object TaskList : DebbieRoute("tasks")
    object TaskDetail : DebbieRoute("tasks/{taskId}")
    object Calendar : DebbieRoute("calendar")
    object DailyBriefing : DebbieRoute("briefing")
    object Crew : DebbieRoute("crew")
    
    // Drafting (Estimates)
    object EstimateList : DebbieRoute("estimates")
    object EstimateDetail : DebbieRoute("estimates/{estimateId}")
    object EstimateBuilder : DebbieRoute("estimates/builder")
    object Materials : DebbieRoute("materials")
    object Templates : DebbieRoute("templates")
    
    // Data/AI
    object Insights : DebbieRoute("insights")
    object AnalysisQueue : DebbieRoute("analysis")
    
    // Settings
    object Settings : DebbieRoute("settings")
    object Profile : DebbieRoute("profile")
}
```

### Bottom Navigation

```kotlin
enum class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    HOME("home", Icons.Filled.Home, "Home"),
    JOBS("jobs", Icons.Filled.Work, "Jobs"),
    PHOTOS("photos", Icons.Filled.PhotoLibrary, "Photos"),
    TASKS("tasks", Icons.Filled.CheckCircle, "Tasks"),
    MORE("more", Icons.Filled.Menu, "More")
}
```

---

## Cross-Module Integration Points

### Contact → Other Modules
- `contactId` on Photo, Album, Job, Task, Event, Estimate
- Contact detail shows: jobs, photos, estimates, tasks, timeline

### Job → Other Modules
- `jobId` on Photo, Album, Task, Estimate
- Job detail shows: all linked photos, tasks, estimates, timeline
- Job dashboard: progress, costs, schedule

### Photo → Other Modules
- `albumId`, `contactId`, `jobId` foreign keys
- AI analysis populates: `aiObjects`, `aiFaces`, `aiText`
- Measurements feed into Drafting estimates

### Task → Other Modules
- `contactId`, `jobId` foreign keys
- `assignedToId` links to CrewMember
- Creates reminders, syncs to calendar

### Estimate → Other Modules
- `contactId`, `jobId`, `addressId` foreign keys
- Uses photos for room measurements
- Line items reference Material pricing

---

## Implementation Priority

### Phase 1 (Now): Contacts + Photos MVP ✅
- Combine existing code
- Unified database
- Cross-linking working

### Phase 2: Jobs Module
- Job entity and CRUD
- Link jobs to contacts
- Job dashboard

### Phase 3: AI/Data Module  
- Google Vision integration
- Auto-tagging photos
- Object detection

### Phase 4: Drafting Module
- Estimate creation
- Material database
- PDF export

### Phase 5: Details Module
- Task management
- Calendar integration
- Notifications

---

## Next Steps

1. Create unified `DebbieDatabase.kt` with all entities
2. Create unified `MainActivity.kt` with bottom nav
3. Merge Contacts and Photos code
4. Add Job entity as linking layer
5. Stub out future modules

Ready to generate the code?
