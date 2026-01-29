package com.debbiedoesit.debbieai.core.database

import android.content.Context
import androidx.room.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

// ============================================================================
// TYPE CONVERTERS
// ============================================================================

class Converters {
    // String List
    @TypeConverter
    fun fromStringList(value: List<String>?): String = value?.joinToString("|||") ?: ""
    
    @TypeConverter
    fun toStringList(value: String): List<String> = 
        if (value.isEmpty()) emptyList() else value.split("|||")
    
    // Long List
    @TypeConverter
    fun fromLongList(value: List<Long>?): String = value?.joinToString(",") ?: ""
    
    @TypeConverter
    fun toLongList(value: String): List<Long> = 
        if (value.isEmpty()) emptyList() else value.split(",").map { it.toLong() }
    
    // Int List
    @TypeConverter
    fun fromIntList(value: List<Int>?): String = value?.joinToString(",") ?: ""
    
    @TypeConverter
    fun toIntList(value: String): List<Int> = 
        if (value.isEmpty()) emptyList() else value.split(",").map { it.toInt() }
    
    // Map<String, String>
    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String =
        value?.entries?.joinToString("|||") { "${it.key}::${it.value}" } ?: ""
    
    @TypeConverter
    fun toStringMap(value: String): Map<String, String> =
        if (value.isEmpty()) emptyMap()
        else value.split("|||").associate {
            val parts = it.split("::", limit = 2)
            parts[0] to (parts.getOrNull(1) ?: "")
        }
    
    // LocalDateTime
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString()
    
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = 
        value?.let { LocalDateTime.parse(it) }
    
    // LocalDate
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()
    
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = 
        value?.let { LocalDate.parse(it) }
    
    // LocalTime
    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.toString()
    
    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = 
        value?.let { LocalTime.parse(it) }
    
    // RoomMeasurement List (JSON-like)
    @TypeConverter
    fun fromRoomMeasurements(value: List<RoomMeasurement>?): String {
        if (value == null || value.isEmpty()) return ""
        return value.joinToString(";;;") { room ->
            "${room.name}||${room.length}||${room.width}||${room.height}||${room.squareFeet}||${room.notes}||${room.photoIds.joinToString(",")}"
        }
    }
    
    @TypeConverter
    fun toRoomMeasurements(value: String): List<RoomMeasurement> {
        if (value.isEmpty()) return emptyList()
        return value.split(";;;").map { roomStr ->
            val parts = roomStr.split("||")
            RoomMeasurement(
                name = parts.getOrElse(0) { "" },
                length = parts.getOrElse(1) { "0" }.toDoubleOrNull() ?: 0.0,
                width = parts.getOrElse(2) { "0" }.toDoubleOrNull() ?: 0.0,
                height = parts.getOrElse(3) { "8" }.toDoubleOrNull() ?: 8.0,
                squareFeet = parts.getOrElse(4) { "0" }.toDoubleOrNull() ?: 0.0,
                notes = parts.getOrElse(5) { "" },
                photoIds = parts.getOrElse(6) { "" }.split(",").mapNotNull { it.toLongOrNull() }
            )
        }
    }
}

// ============================================================================
// ENUMS
// ============================================================================

// Contact enums
enum class AddressType { HOME, WORK, BILLING, PROPERTY, OTHER }
enum class CustomerType { LEAD, PROSPECT, ACTIVE, PAST, VENDOR, OTHER }

// Photo enums
enum class PhotoCategory {
    BEFORE, DURING, AFTER, DAMAGE, MATERIALS,
    MEASUREMENT, RECEIPT, REFERENCE, GENERAL
}
enum class AlbumType { JOB, PROJECT, CUSTOMER, DATE, LOCATION, CUSTOM, SMART }
enum class SyncStatus { LOCAL, SYNCING, SYNCED, ERROR }

// Job enums
enum class JobStatus {
    LEAD, QUOTED, SCHEDULED, IN_PROGRESS, ON_HOLD,
    COMPLETED, INVOICED, PAID, CANCELLED
}

// Task enums
enum class TaskStatus { TODO, IN_PROGRESS, WAITING, COMPLETED, CANCELLED }
enum class TaskPriority { LOW, NORMAL, HIGH, URGENT }
enum class TaskCategory {
    GENERAL, CALL, MEETING, SITE_VISIT, FOLLOW_UP,
    ESTIMATE, PURCHASE, DELIVERY, WORK, INVOICE
}

// Event enums
enum class EventStatus { TENTATIVE, CONFIRMED, CANCELLED }

// Estimate enums
enum class EstimateStatus { DRAFT, SENT, VIEWED, ACCEPTED, DECLINED, EXPIRED, REVISED }
enum class LineItemCategory {
    MATERIALS, LABOR, EQUIPMENT, SUBCONTRACTOR,
    PERMIT, DISPOSAL, DELIVERY, OTHER
}

// AI enums
enum class AnalysisType {
    OBJECT_DETECTION, FACE_DETECTION, OCR,
    DAMAGE_ASSESSMENT, MEASUREMENT, MATERIAL_ID
}
enum class AnalysisStatus { PENDING, PROCESSING, COMPLETED, FAILED }

// ============================================================================
// ENTITIES - CONTACTS MODULE
// ============================================================================

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    // Name
    val name: String,
    val firstName: String = "",
    val lastName: String = "",
    
    // Contact info (multiple)
    val phones: List<String> = emptyList(),
    val emails: List<String> = emptyList(),
    
    // Work
    val company: String = "",
    val jobTitle: String = "",
    
    // Social & notes
    val socialMedia: Map<String, String> = emptyMap(),
    val notes: String = "",
    
    // Classification
    val customerType: String = CustomerType.LEAD.name,
    val source: String = "",
    val rating: Int = 0,
    val tags: List<String> = emptyList(),
    
    // Calculated (updated by triggers/queries)
    val totalRevenue: Double = 0.0,
    val jobCount: Int = 0,
    
    // Duplicate handling
    val isDuplicate: Boolean = false,
    val isDuplicateOf: Long? = null,
    val isPlaceholder: Boolean = false,
    
    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val syncedAt: LocalDateTime? = null
)

@Entity(
    tableName = "addresses",
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("contactId")]
)
data class Address(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactId: Long,
    
    val type: String = AddressType.HOME.name,
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

// ============================================================================
// ENTITIES - JOBS MODULE (Central linking entity)
// ============================================================================

@Entity(
    tableName = "jobs",
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Address::class,
            parentColumns = ["id"],
            childColumns = ["addressId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("contactId"), Index("addressId"), Index("status")]
)
data class Job(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    // Basic info
    val title: String,
    val description: String = "",
    val jobNumber: String? = null,
    
    // Relationships
    val contactId: Long,
    val addressId: Long? = null,
    
    // Status
    val status: String = JobStatus.LEAD.name,
    val priority: Int = 0,
    
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
    val workType: String = "",
    val tags: List<String> = emptyList(),
    val notes: String = "",
    
    // Cached counts
    val photoCount: Int = 0,
    val taskCount: Int = 0,
    val estimateCount: Int = 0,
    
    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

// ============================================================================
// ENTITIES - PHOTOS MODULE
// ============================================================================

@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = Album::class,
            parentColumns = ["id"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Job::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("albumId"), Index("contactId"), Index("jobId"),
        Index("category"), Index("takenAt"), Index("isFavorite"), Index("isDeleted")
    ]
)
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
    val category: String = PhotoCategory.GENERAL.name,
    val tags: List<String> = emptyList(),
    
    // EXIF metadata
    val takenAt: LocalDateTime? = null,
    val deviceMake: String? = null,
    val deviceModel: String? = null,
    val gpsLatitude: Double? = null,
    val gpsLongitude: Double? = null,
    val gpsAltitude: Double? = null,
    val orientation: Int = 0,
    
    // Location (derived or manual)
    val locationName: String? = null,
    val locationCity: String? = null,
    val locationState: String? = null,
    val locationZip: String? = null,
    
    // Before/After linking
    val beforePhotoId: Long? = null,
    val afterPhotoId: Long? = null,
    val relatedPhotoIds: List<Long> = emptyList(),
    
    // AI analysis results (populated by Data module)
    val aiAnalyzed: Boolean = false,
    val aiDescription: String? = null,
    val aiObjects: List<String> = emptyList(),
    val aiFaces: List<String> = emptyList(),
    val aiText: String? = null,
    val aiConfidence: Float? = null,
    val aiTags: List<String> = emptyList(),
    val aiAnalyzedAt: LocalDateTime? = null,
    
    // Status
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val isDeleted: Boolean = false,
    val syncStatus: String = SyncStatus.LOCAL.name,
    val cloudUrl: String? = null,
    
    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val syncedAt: LocalDateTime? = null
)

@Entity(
    tableName = "albums",
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Job::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("contactId"), Index("jobId"), Index("albumType")]
)
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    val name: String,
    val description: String = "",
    val albumType: String = AlbumType.CUSTOM.name,
    
    // Relationships
    val contactId: Long? = null,
    val jobId: Long? = null,
    val parentAlbumId: Long? = null,
    
    // Cover
    val coverPhotoId: Long? = null,
    val coverPhotoPath: String? = null,
    
    // Location
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val gpsLatitude: Double? = null,
    val gpsLongitude: Double? = null,
    
    // Dates
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    
    // Cached counts
    val photoCount: Int = 0,
    val beforeCount: Int = 0,
    val afterCount: Int = 0,
    
    // Display
    val tags: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
    val color: String? = null,
    val icon: String? = null,
    val sortOrder: Int = 0,
    
    // Status
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val isAutoGenerated: Boolean = false,
    
    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

// ============================================================================
// ENTITIES - DETAILS MODULE (Tasks, Events, Crew)
// ============================================================================

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Job::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = CrewMember::class,
            parentColumns = ["id"],
            childColumns = ["assignedToId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("contactId"), Index("jobId"), Index("assignedToId"),
        Index("status"), Index("dueDate"), Index("priority")
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    val title: String,
    val description: String = "",
    
    // Relationships
    val contactId: Long? = null,
    val jobId: Long? = null,
    val assignedToId: Long? = null,
    val parentTaskId: Long? = null,
    
    // Scheduling
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val reminderAt: LocalDateTime? = null,
    val estimatedMinutes: Int? = null,
    val actualMinutes: Int? = null,
    
    // Recurrence
    val isRecurring: Boolean = false,
    val recurrenceRule: String? = null,
    
    // Status
    val status: String = TaskStatus.TODO.name,
    val priority: String = TaskPriority.NORMAL.name,
    val category: String = TaskCategory.GENERAL.name,
    
    // Completion
    val completedAt: LocalDateTime? = null,
    val completedBy: String? = null,
    val completionNotes: String = "",
    
    // Metadata
    val tags: List<String> = emptyList(),
    
    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Job::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Address::class,
            parentColumns = ["id"],
            childColumns = ["addressId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("contactId"), Index("jobId"), Index("startDateTime")]
)
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
    val reminderMinutes: List<Int> = listOf(30),
    
    // Recurrence
    val isRecurring: Boolean = false,
    val recurrenceRule: String? = null,
    
    // External sync
    val externalCalendarId: String? = null,
    val syncedAt: LocalDateTime? = null,
    
    // Status
    val status: String = EventStatus.CONFIRMED.name,
    val color: String? = null,
    
    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

@Entity(tableName = "crew_members")
data class CrewMember(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    val name: String,
    val phone: String = "",
    val email: String = "",
    val role: String = "",
    
    val hourlyRate: Double = 0.0,
    val isActive: Boolean = true,
    val color: String? = null,
    
    val notes: String = "",
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    // What this reminder is for
    val taskId: Long? = null,
    val eventId: Long? = null,
    val contactId: Long? = null,
    val jobId: Long? = null,
    
    val title: String,
    val message: String = "",
    
    // When
    val reminderAt: LocalDateTime,
    val repeatInterval: String? = null,  // daily, weekly, monthly
    
    // Status
    val isFired: Boolean = false,
    val isDismissed: Boolean = false,
    val firedAt: LocalDateTime? = null,
    
    val createdAt: LocalDateTime = LocalDateTime.now()
)

// ============================================================================
// ENTITIES - DRAFTING MODULE (Estimates, Materials)
// ============================================================================

// Helper data class for room measurements (stored as JSON-like string)
data class RoomMeasurement(
    val name: String,
    val length: Double,
    val width: Double,
    val height: Double = 8.0,
    val squareFeet: Double,
    val notes: String = "",
    val photoIds: List<Long> = emptyList()
)

@Entity(
    tableName = "estimates",
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Job::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Address::class,
            parentColumns = ["id"],
            childColumns = ["addressId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("contactId"), Index("jobId"), Index("status"), Index("estimateNumber")]
)
data class Estimate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    // Identification
    val estimateNumber: String,
    val title: String,
    val description: String = "",
    
    // Relationships
    val contactId: Long,
    val jobId: Long? = null,
    val addressId: Long? = null,
    val templateId: Long? = null,
    
    // Status
    val status: String = EstimateStatus.DRAFT.name,
    val validUntil: LocalDate? = null,
    
    // Measurements
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

@Entity(
    tableName = "estimate_line_items",
    foreignKeys = [
        ForeignKey(
            entity = Estimate::class,
            parentColumns = ["id"],
            childColumns = ["estimateId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Material::class,
            parentColumns = ["id"],
            childColumns = ["materialId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("estimateId"), Index("materialId")]
)
data class EstimateLineItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    val estimateId: Long,
    val sortOrder: Int = 0,
    
    // Item details
    val category: String = LineItemCategory.MATERIALS.name,
    val description: String,
    val notes: String = "",
    
    // Quantity
    val quantity: Double = 1.0,
    val unit: String = "each",
    
    // Pricing
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0,
    val isTaxable: Boolean = true,
    
    // Material reference
    val materialId: Long? = null,
    val wastePercent: Double = 0.0,
    
    // Labor
    val laborHours: Double = 0.0,
    val laborRate: Double = 0.0,
    
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity(tableName = "materials")
data class Material(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    val name: String,
    val description: String = "",
    val category: String = "",
    val brand: String = "",
    val sku: String = "",
    
    // Pricing
    val unitPrice: Double = 0.0,
    val unit: String = "each",
    val priceUpdatedAt: LocalDate? = null,
    
    // Supplier
    val supplier: String = "",
    val supplierUrl: String = "",
    
    // Coverage calculations
    val coveragePerUnit: Double? = null,
    val defaultWastePercent: Double = 10.0,
    
    val isActive: Boolean = true,
    val isFavorite: Boolean = false,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

@Entity(tableName = "estimate_templates")
data class EstimateTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    val name: String,
    val description: String = "",
    val workType: String = "",
    
    // Default values
    val defaultTaxRate: Double = 0.06,
    val defaultPaymentTerms: String = "",
    val defaultNotes: String = "",
    
    // Template line items stored as JSON
    val lineItemsJson: String = "[]",
    
    val isActive: Boolean = true,
    val usageCount: Int = 0,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

// ============================================================================
// ENTITIES - DATA/AI MODULE
// ============================================================================

@Entity(
    tableName = "ai_analyses",
    foreignKeys = [
        ForeignKey(
            entity = Photo::class,
            parentColumns = ["id"],
            childColumns = ["photoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("photoId"), Index("status"), Index("analysisType")]
)
data class AiAnalysis(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    // What was analyzed
    val photoId: Long? = null,
    val documentPath: String? = null,
    
    // Analysis type
    val analysisType: String = AnalysisType.OBJECT_DETECTION.name,
    
    // Status
    val status: String = AnalysisStatus.PENDING.name,
    val confidence: Float = 0f,
    
    // Results (JSON blobs)
    val resultsJson: String = "{}",
    val detectedObjectsJson: String = "[]",
    val detectedFacesJson: String = "[]",
    val extractedText: String? = null,
    val suggestedTags: List<String> = emptyList(),
    val suggestedCategory: String? = null,
    val measurementsJson: String? = null,
    
    // Error handling
    val errorMessage: String? = null,
    val retryCount: Int = 0,
    
    // API tracking
    val apiProvider: String = "google_vision",
    val apiCost: Double = 0.0,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null
)

@Entity(tableName = "business_insights")
data class BusinessInsight(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    val insightType: String,  // revenue_trend, material_cost, seasonal_pattern
    val title: String,
    val description: String,
    val dataJson: String = "{}",
    
    val period: String? = null,  // 2026-Q1, 2026-01, etc.
    val value: Double? = null,
    val previousValue: Double? = null,
    val changePercent: Double? = null,
    
    val isRead: Boolean = false,
    val isDismissed: Boolean = false,
    
    val createdAt: LocalDateTime = LocalDateTime.now()
)

// ============================================================================
// DATABASE
// ============================================================================

@Database(
    entities = [
        // Contacts
        Contact::class,
        Address::class,
        // Jobs
        Job::class,
        // Photos
        Photo::class,
        Album::class,
        // Details
        Task::class,
        Event::class,
        CrewMember::class,
        Reminder::class,
        // Drafting
        Estimate::class,
        EstimateLineItem::class,
        Material::class,
        EstimateTemplate::class,
        // AI/Data
        AiAnalysis::class,
        BusinessInsight::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class DebbieDatabase : RoomDatabase() {
    
    // DAOs - Contacts
    abstract fun contactDao(): ContactDao
    abstract fun addressDao(): AddressDao
    
    // DAOs - Jobs
    abstract fun jobDao(): JobDao
    
    // DAOs - Photos
    abstract fun photoDao(): PhotoDao
    abstract fun albumDao(): AlbumDao
    
    // DAOs - Details
    abstract fun taskDao(): TaskDao
    abstract fun eventDao(): EventDao
    abstract fun crewMemberDao(): CrewMemberDao
    abstract fun reminderDao(): ReminderDao
    
    // DAOs - Drafting
    abstract fun estimateDao(): EstimateDao
    abstract fun estimateLineItemDao(): EstimateLineItemDao
    abstract fun materialDao(): MaterialDao
    abstract fun estimateTemplateDao(): EstimateTemplateDao
    
    // DAOs - AI/Data
    abstract fun aiAnalysisDao(): AiAnalysisDao
    abstract fun businessInsightDao(): BusinessInsightDao
    
    companion object {
        @Volatile
        private var INSTANCE: DebbieDatabase? = null
        
        fun getDatabase(context: Context): DebbieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DebbieDatabase::class.java,
                    "debbie_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// ============================================================================
// DAO INTERFACES (Stubs - will be fully implemented)
// ============================================================================

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllContacts(): kotlinx.coroutines.flow.Flow<List<Contact>>
    
    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Long): Contact?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact): Long
    
    @Update
    suspend fun update(contact: Contact)
    
    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses WHERE contactId = :contactId")
    fun getAddressesForContact(contactId: Long): kotlinx.coroutines.flow.Flow<List<Address>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(address: Address): Long
    
    @Update
    suspend fun update(address: Address)
    
    @Query("DELETE FROM addresses WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs ORDER BY createdAt DESC")
    fun getAllJobs(): kotlinx.coroutines.flow.Flow<List<Job>>
    
    @Query("SELECT * FROM jobs WHERE contactId = :contactId")
    fun getJobsForContact(contactId: Long): kotlinx.coroutines.flow.Flow<List<Job>>
    
    @Query("SELECT * FROM jobs WHERE id = :id")
    suspend fun getJobById(id: Long): Job?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: Job): Long
    
    @Update
    suspend fun update(job: Job)
    
    @Query("DELETE FROM jobs WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllPhotos(): kotlinx.coroutines.flow.Flow<List<Photo>>
    
    @Query("SELECT * FROM photos WHERE albumId = :albumId AND isDeleted = 0")
    fun getPhotosInAlbum(albumId: Long): kotlinx.coroutines.flow.Flow<List<Photo>>
    
    @Query("SELECT * FROM photos WHERE contactId = :contactId AND isDeleted = 0")
    fun getPhotosForContact(contactId: Long): kotlinx.coroutines.flow.Flow<List<Photo>>
    
    @Query("SELECT * FROM photos WHERE jobId = :jobId AND isDeleted = 0")
    fun getPhotosForJob(jobId: Long): kotlinx.coroutines.flow.Flow<List<Photo>>
    
    @Query("SELECT * FROM photos WHERE id = :id")
    suspend fun getPhotoById(id: Long): Photo?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: Photo): Long
    
    @Update
    suspend fun update(photo: Photo)
    
    @Query("UPDATE photos SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)
}

@Dao
interface AlbumDao {
    @Query("SELECT * FROM albums ORDER BY name ASC")
    fun getAllAlbums(): kotlinx.coroutines.flow.Flow<List<Album>>
    
    @Query("SELECT * FROM albums WHERE contactId = :contactId")
    fun getAlbumsForContact(contactId: Long): kotlinx.coroutines.flow.Flow<List<Album>>
    
    @Query("SELECT * FROM albums WHERE jobId = :jobId")
    fun getAlbumsForJob(jobId: Long): kotlinx.coroutines.flow.Flow<List<Album>>
    
    @Query("SELECT * FROM albums WHERE id = :id")
    suspend fun getAlbumById(id: Long): Album?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(album: Album): Long
    
    @Update
    suspend fun update(album: Album)
    
    @Query("DELETE FROM albums WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE status != 'CANCELLED' ORDER BY dueDate ASC")
    fun getAllTasks(): kotlinx.coroutines.flow.Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE contactId = :contactId")
    fun getTasksForContact(contactId: Long): kotlinx.coroutines.flow.Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE jobId = :jobId")
    fun getTasksForJob(jobId: Long): kotlinx.coroutines.flow.Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long
    
    @Update
    suspend fun update(task: Task)
    
    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY startDateTime ASC")
    fun getAllEvents(): kotlinx.coroutines.flow.Flow<List<Event>>
    
    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): Event?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event): Long
    
    @Update
    suspend fun update(event: Event)
    
    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface CrewMemberDao {
    @Query("SELECT * FROM crew_members WHERE isActive = 1 ORDER BY name ASC")
    fun getAllCrewMembers(): kotlinx.coroutines.flow.Flow<List<CrewMember>>
    
    @Query("SELECT * FROM crew_members WHERE id = :id")
    suspend fun getCrewMemberById(id: Long): CrewMember?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(crewMember: CrewMember): Long
    
    @Update
    suspend fun update(crewMember: CrewMember)
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE isDismissed = 0 ORDER BY reminderAt ASC")
    fun getActiveReminders(): kotlinx.coroutines.flow.Flow<List<Reminder>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder): Long
    
    @Update
    suspend fun update(reminder: Reminder)
}

@Dao
interface EstimateDao {
    @Query("SELECT * FROM estimates ORDER BY createdAt DESC")
    fun getAllEstimates(): kotlinx.coroutines.flow.Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates WHERE contactId = :contactId")
    fun getEstimatesForContact(contactId: Long): kotlinx.coroutines.flow.Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates WHERE jobId = :jobId")
    fun getEstimatesForJob(jobId: Long): kotlinx.coroutines.flow.Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates WHERE id = :id")
    suspend fun getEstimateById(id: Long): Estimate?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(estimate: Estimate): Long
    
    @Update
    suspend fun update(estimate: Estimate)
    
    @Query("DELETE FROM estimates WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface EstimateLineItemDao {
    @Query("SELECT * FROM estimate_line_items WHERE estimateId = :estimateId ORDER BY sortOrder")
    fun getLineItemsForEstimate(estimateId: Long): kotlinx.coroutines.flow.Flow<List<EstimateLineItem>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lineItem: EstimateLineItem): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lineItems: List<EstimateLineItem>)
    
    @Update
    suspend fun update(lineItem: EstimateLineItem)
    
    @Query("DELETE FROM estimate_line_items WHERE estimateId = :estimateId")
    suspend fun deleteAllForEstimate(estimateId: Long)
}

@Dao
interface MaterialDao {
    @Query("SELECT * FROM materials WHERE isActive = 1 ORDER BY name ASC")
    fun getAllMaterials(): kotlinx.coroutines.flow.Flow<List<Material>>
    
    @Query("SELECT * FROM materials WHERE category = :category AND isActive = 1")
    fun getMaterialsByCategory(category: String): kotlinx.coroutines.flow.Flow<List<Material>>
    
    @Query("SELECT * FROM materials WHERE id = :id")
    suspend fun getMaterialById(id: Long): Material?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(material: Material): Long
    
    @Update
    suspend fun update(material: Material)
}

@Dao
interface EstimateTemplateDao {
    @Query("SELECT * FROM estimate_templates WHERE isActive = 1 ORDER BY name ASC")
    fun getAllTemplates(): kotlinx.coroutines.flow.Flow<List<EstimateTemplate>>
    
    @Query("SELECT * FROM estimate_templates WHERE id = :id")
    suspend fun getTemplateById(id: Long): EstimateTemplate?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: EstimateTemplate): Long
    
    @Update
    suspend fun update(template: EstimateTemplate)
}

@Dao
interface AiAnalysisDao {
    @Query("SELECT * FROM ai_analyses WHERE photoId = :photoId")
    fun getAnalysesForPhoto(photoId: Long): kotlinx.coroutines.flow.Flow<List<AiAnalysis>>
    
    @Query("SELECT * FROM ai_analyses WHERE status = 'PENDING' ORDER BY createdAt ASC")
    fun getPendingAnalyses(): kotlinx.coroutines.flow.Flow<List<AiAnalysis>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(analysis: AiAnalysis): Long
    
    @Update
    suspend fun update(analysis: AiAnalysis)
}

@Dao
interface BusinessInsightDao {
    @Query("SELECT * FROM business_insights WHERE isDismissed = 0 ORDER BY createdAt DESC")
    fun getActiveInsights(): kotlinx.coroutines.flow.Flow<List<BusinessInsight>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(insight: BusinessInsight): Long
    
    @Update
    suspend fun update(insight: BusinessInsight)
}
