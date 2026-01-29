package com.debbiedoesit.debbieai.tasks.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.debbiedoesit.debbieai.contacts.data.local.Contact
import com.debbiedoesit.debbieai.jobs.data.local.Job
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Task entity - todos, reminders, follow-ups
 */
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
        )
    ],
    indices = [
        Index(value = ["contactId"]),
        Index(value = ["jobId"]),
        Index(value = ["status"]),
        Index(value = ["dueDate"]),
        Index(value = ["priority"]),
        Index(value = ["category"])
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Core fields
    val title: String,
    val description: String = "",
    val status: TaskStatus = TaskStatus.PENDING,
    val priority: TaskPriority = TaskPriority.NORMAL,
    val category: TaskCategory = TaskCategory.GENERAL,
    
    // Linking
    val contactId: Long? = null,
    val jobId: Long? = null,
    
    // Scheduling
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val allDay: Boolean = true,
    
    // Reminders
    val reminderEnabled: Boolean = false,
    val reminderDateTime: LocalDateTime? = null,
    val reminderMinutesBefore: Int = 30,  // For quick setup
    
    // Recurrence
    val isRecurring: Boolean = false,
    val recurrencePattern: RecurrencePattern? = null,
    val recurrenceEndDate: LocalDate? = null,
    val parentTaskId: Long? = null,  // For recurring instances
    
    // Assignment
    val assignedTo: String = "",  // Crew member name
    
    // Completion
    val completedAt: LocalDateTime? = null,
    val completionNotes: String = "",
    
    // Location (for site visits)
    val location: String = "",
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    
    // Effort tracking
    val estimatedMinutes: Int = 0,
    val actualMinutes: Int = 0,
    
    // Tags
    val tags: List<String> = emptyList(),
    
    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    val isOverdue: Boolean
        get() = status != TaskStatus.COMPLETED && 
                dueDate != null && 
                dueDate < LocalDate.now()
    
    val isDueToday: Boolean
        get() = dueDate == LocalDate.now()
    
    val isDueTomorrow: Boolean
        get() = dueDate == LocalDate.now().plusDays(1)
    
    val isDueThisWeek: Boolean
        get() = dueDate != null && 
                dueDate >= LocalDate.now() && 
                dueDate <= LocalDate.now().plusDays(7)
}

/**
 * Task status
 */
enum class TaskStatus(val displayName: String) {
    PENDING("To Do"),
    IN_PROGRESS("In Progress"),
    WAITING("Waiting"),
    COMPLETED("Done"),
    CANCELLED("Cancelled")
}

/**
 * Task priority
 */
enum class TaskPriority(val displayName: String, val level: Int) {
    LOW("Low", 0),
    NORMAL("Normal", 1),
    HIGH("High", 2),
    URGENT("Urgent", 3)
}

/**
 * Task categories for contractors
 */
enum class TaskCategory(val displayName: String, val icon: String) {
    GENERAL("General", "task"),
    CALL("Phone Call", "phone"),
    EMAIL("Email", "email"),
    MEETING("Meeting", "people"),
    SITE_VISIT("Site Visit", "location"),
    ESTIMATE("Estimate", "calculator"),
    FOLLOW_UP("Follow Up", "reply"),
    PURCHASE("Purchase", "shopping"),
    INSPECTION("Inspection", "search"),
    SCHEDULING("Scheduling", "calendar"),
    PAPERWORK("Paperwork", "document"),
    PAYMENT("Payment", "payment"),
    WARRANTY("Warranty", "shield"),
    OTHER("Other", "more")
}

/**
 * Recurrence patterns
 */
enum class RecurrencePattern(val displayName: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    BIWEEKLY("Every 2 Weeks"),
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly"),
    YEARLY("Yearly")
}

/**
 * Task with related info for display
 */
data class TaskWithDetails(
    val task: Task,
    val contactName: String? = null,
    val jobTitle: String? = null
)

/**
 * Quick task templates for common contractor tasks
 */
enum class QuickTaskTemplate(
    val title: String,
    val category: TaskCategory,
    val estimatedMinutes: Int = 0
) {
    CALL_CUSTOMER("Call customer", TaskCategory.CALL, 15),
    SEND_ESTIMATE("Send estimate", TaskCategory.ESTIMATE, 30),
    FOLLOW_UP_ESTIMATE("Follow up on estimate", TaskCategory.FOLLOW_UP, 10),
    SCHEDULE_JOB("Schedule job", TaskCategory.SCHEDULING, 15),
    SITE_VISIT("Site visit", TaskCategory.SITE_VISIT, 60),
    ORDER_MATERIALS("Order materials", TaskCategory.PURCHASE, 30),
    SEND_INVOICE("Send invoice", TaskCategory.PAYMENT, 15),
    COLLECT_PAYMENT("Collect payment", TaskCategory.PAYMENT, 10),
    FINAL_WALKTHROUGH("Final walkthrough", TaskCategory.INSPECTION, 30),
    WARRANTY_CHECK("Warranty check", TaskCategory.WARRANTY, 30)
}

/**
 * Task summary stats
 */
data class TaskSummary(
    val totalTasks: Int = 0,
    val pendingTasks: Int = 0,
    val completedToday: Int = 0,
    val overdueTasks: Int = 0,
    val dueTodayTasks: Int = 0,
    val dueThisWeekTasks: Int = 0,
    val tasksByCategory: Map<TaskCategory, Int> = emptyMap(),
    val tasksByPriority: Map<TaskPriority, Int> = emptyMap()
)
