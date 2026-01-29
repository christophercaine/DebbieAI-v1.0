package com.debbiedoesit.debbieai.jobs.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.debbiedoesit.debbieai.contacts.data.local.Contact
import com.debbiedoesit.debbieai.contacts.data.local.Address
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Job entity - the central hub linking contacts, photos, estimates, and tasks
 */
@Entity(
    tableName = "jobs",
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Address::class,
            parentColumns = ["id"],
            childColumns = ["addressId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["contactId"]),
        Index(value = ["addressId"]),
        Index(value = ["status"]),
        Index(value = ["jobType"]),
        Index(value = ["startDate"]),
        Index(value = ["createdAt"])
    ]
)
data class Job(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Customer & Location
    val contactId: Long? = null,
    val addressId: Long? = null,
    
    // Job Info
    val title: String,
    val description: String = "",
    val jobNumber: String = "",  // User-defined job number (e.g., "2026-001")
    
    // Type & Status
    val jobType: JobType = JobType.GENERAL,
    val status: JobStatus = JobStatus.LEAD,
    val priority: JobPriority = JobPriority.NORMAL,
    
    // Dates
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val completedDate: LocalDate? = null,
    val scheduledTime: String = "",  // e.g., "9:00 AM"
    
    // Financials
    val estimatedCost: Double = 0.0,
    val actualCost: Double = 0.0,
    val depositAmount: Double = 0.0,
    val depositPaid: Boolean = false,
    val finalPaid: Boolean = false,
    
    // Crew
    val assignedTo: List<String> = emptyList(),  // Crew member names
    val estimatedHours: Double = 0.0,
    val actualHours: Double = 0.0,
    
    // Notes & Tags
    val notes: String = "",
    val tags: List<String> = emptyList(),
    
    // Source tracking
    val source: LeadSource = LeadSource.DIRECT,
    val referredBy: String = "",
    
    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Job status progression
 */
enum class JobStatus(val displayName: String, val order: Int) {
    LEAD("Lead", 0),
    QUOTED("Quoted", 1),
    APPROVED("Approved", 2),
    SCHEDULED("Scheduled", 3),
    IN_PROGRESS("In Progress", 4),
    ON_HOLD("On Hold", 5),
    COMPLETED("Completed", 6),
    INVOICED("Invoiced", 7),
    PAID("Paid", 8),
    CANCELLED("Cancelled", 9);
    
    fun isActive(): Boolean = this in listOf(SCHEDULED, IN_PROGRESS)
    fun isOpen(): Boolean = this in listOf(LEAD, QUOTED, APPROVED, SCHEDULED, IN_PROGRESS, ON_HOLD)
    fun isClosed(): Boolean = this in listOf(COMPLETED, INVOICED, PAID, CANCELLED)
}

/**
 * Job types for contractors
 */
enum class JobType(val displayName: String) {
    GENERAL("General"),
    FLOORING("Flooring"),
    KITCHEN("Kitchen"),
    BATHROOM("Bathroom"),
    BASEMENT("Basement"),
    ROOFING("Roofing"),
    SIDING("Siding"),
    PAINTING("Painting"),
    DECK("Deck/Patio"),
    ELECTRICAL("Electrical"),
    PLUMBING("Plumbing"),
    HVAC("HVAC"),
    DRYWALL("Drywall"),
    TILE("Tile"),
    CARPENTRY("Carpentry"),
    WINDOWS("Windows/Doors"),
    LANDSCAPING("Landscaping"),
    INSPECTION("Inspection"),
    ESTIMATE_ONLY("Estimate Only"),
    WARRANTY("Warranty Work"),
    OTHER("Other")
}

/**
 * Job priority levels
 */
enum class JobPriority(val displayName: String, val level: Int) {
    LOW("Low", 0),
    NORMAL("Normal", 1),
    HIGH("High", 2),
    URGENT("Urgent", 3)
}

/**
 * Lead source tracking
 */
enum class LeadSource(val displayName: String) {
    DIRECT("Direct Contact"),
    REFERRAL("Referral"),
    WEBSITE("Website"),
    GOOGLE("Google"),
    SOCIAL_MEDIA("Social Media"),
    HOME_ADVISOR("HomeAdvisor"),
    ANGIES_LIST("Angi's List"),
    THUMBTACK("Thumbtack"),
    NEXTDOOR("Nextdoor"),
    YELP("Yelp"),
    REPEAT_CUSTOMER("Repeat Customer"),
    DRIVE_BY("Drive By"),
    FLYER("Flyer/Mailer"),
    OTHER("Other")
}

/**
 * Job with related counts for display
 */
data class JobWithCounts(
    val job: Job,
    val contactName: String? = null,
    val addressLine: String? = null,
    val photoCount: Int = 0,
    val estimateCount: Int = 0,
    val taskCount: Int = 0,
    val completedTaskCount: Int = 0
)

/**
 * Job summary for dashboard/quick views
 */
data class JobSummary(
    val totalJobs: Int = 0,
    val activeJobs: Int = 0,
    val completedThisMonth: Int = 0,
    val totalRevenue: Double = 0.0,
    val pendingRevenue: Double = 0.0,
    val jobsByStatus: Map<JobStatus, Int> = emptyMap(),
    val jobsByType: Map<JobType, Int> = emptyMap()
)
