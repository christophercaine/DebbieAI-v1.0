package com.debbiedoesit.debbieai.tasks.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface TaskDao {
    
    // ===== INSERT =====
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<Task>): List<Long>
    
    // ===== UPDATE =====
    
    @Update
    suspend fun update(task: Task)
    
    @Query("UPDATE tasks SET status = :status, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateStatus(taskId: Long, status: TaskStatus, updatedAt: LocalDateTime)
    
    @Query("UPDATE tasks SET status = 'COMPLETED', completedAt = :completedAt, completionNotes = :notes, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun completeTask(taskId: Long, completedAt: LocalDateTime, notes: String, updatedAt: LocalDateTime)
    
    @Query("UPDATE tasks SET priority = :priority, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updatePriority(taskId: Long, priority: TaskPriority, updatedAt: LocalDateTime)
    
    @Query("UPDATE tasks SET dueDate = :dueDate, dueTime = :dueTime, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateDueDate(taskId: Long, dueDate: LocalDate?, dueTime: java.time.LocalTime?, updatedAt: LocalDateTime)
    
    @Query("UPDATE tasks SET assignedTo = :assignee, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateAssignment(taskId: Long, assignee: String, updatedAt: LocalDateTime)
    
    @Query("UPDATE tasks SET reminderEnabled = :enabled, reminderDateTime = :reminderTime, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateReminder(taskId: Long, enabled: Boolean, reminderTime: LocalDateTime?, updatedAt: LocalDateTime)
    
    // ===== DELETE =====
    
    @Delete
    suspend fun delete(task: Task)
    
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteById(taskId: Long)
    
    @Query("DELETE FROM tasks WHERE status = 'COMPLETED' AND completedAt < :before")
    suspend fun deleteOldCompleted(before: LocalDateTime)
    
    // ===== QUERIES - Single =====
    
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getById(taskId: Long): Task?
    
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getByIdFlow(taskId: Long): Flow<Task?>
    
    // ===== QUERIES - All =====
    
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC, priority DESC, createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    suspend fun getAllTasksSnapshot(): List<Task>
    
    // ===== QUERIES - By Status =====
    
    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY dueDate ASC, priority DESC")
    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE status IN ('PENDING', 'IN_PROGRESS', 'WAITING') ORDER BY dueDate ASC, priority DESC")
    fun getActiveTasks(): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE status = 'COMPLETED' ORDER BY completedAt DESC")
    fun getCompletedTasks(): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE status = 'COMPLETED' ORDER BY completedAt DESC LIMIT :limit")
    fun getRecentlyCompleted(limit: Int): Flow<List<Task>>
    
    // ===== QUERIES - By Date =====
    
    @Query("SELECT * FROM tasks WHERE dueDate = :date AND status != 'COMPLETED' ORDER BY dueTime ASC, priority DESC")
    fun getTasksForDate(date: LocalDate): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE dueDate < :today AND status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY dueDate ASC, priority DESC")
    fun getOverdueTasks(today: LocalDate): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE dueDate = :today AND status != 'COMPLETED' ORDER BY dueTime ASC, priority DESC")
    fun getTasksDueToday(today: LocalDate): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startDate AND :endDate AND status != 'COMPLETED' ORDER BY dueDate ASC, dueTime ASC")
    fun getTasksInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :today AND :weekEnd AND status != 'COMPLETED' ORDER BY dueDate ASC, priority DESC")
    fun getTasksDueThisWeek(today: LocalDate, weekEnd: LocalDate): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE dueDate IS NULL AND status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY priority DESC, createdAt DESC")
    fun getTasksNoDueDate(): Flow<List<Task>>
    
    // ===== QUERIES - By Category =====
    
    @Query("SELECT * FROM tasks WHERE category = :category AND status != 'COMPLETED' ORDER BY dueDate ASC, priority DESC")
    fun getTasksByCategory(category: TaskCategory): Flow<List<Task>>
    
    // ===== QUERIES - By Priority =====
    
    @Query("SELECT * FROM tasks WHERE priority = :priority AND status != 'COMPLETED' ORDER BY dueDate ASC")
    fun getTasksByPriority(priority: TaskPriority): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE priority IN ('HIGH', 'URGENT') AND status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY priority DESC, dueDate ASC")
    fun getHighPriorityTasks(): Flow<List<Task>>
    
    // ===== QUERIES - By Contact =====
    
    @Query("SELECT * FROM tasks WHERE contactId = :contactId ORDER BY status ASC, dueDate ASC")
    fun getTasksByContact(contactId: Long): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE contactId = :contactId AND status != 'COMPLETED' ORDER BY dueDate ASC")
    fun getActiveTasksByContact(contactId: Long): Flow<List<Task>>
    
    @Query("SELECT COUNT(*) FROM tasks WHERE contactId = :contactId AND status != 'COMPLETED'")
    suspend fun getActiveTaskCountForContact(contactId: Long): Int
    
    // ===== QUERIES - By Job =====
    
    @Query("SELECT * FROM tasks WHERE jobId = :jobId ORDER BY status ASC, dueDate ASC")
    fun getTasksByJob(jobId: Long): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE jobId = :jobId AND status != 'COMPLETED' ORDER BY dueDate ASC")
    fun getActiveTasksByJob(jobId: Long): Flow<List<Task>>
    
    @Query("SELECT COUNT(*) FROM tasks WHERE jobId = :jobId")
    suspend fun getTaskCountForJob(jobId: Long): Int
    
    @Query("SELECT COUNT(*) FROM tasks WHERE jobId = :jobId AND status = 'COMPLETED'")
    suspend fun getCompletedTaskCountForJob(jobId: Long): Int
    
    // ===== QUERIES - By Assignment =====
    
    @Query("SELECT * FROM tasks WHERE assignedTo = :assignee AND status != 'COMPLETED' ORDER BY dueDate ASC, priority DESC")
    fun getTasksByAssignee(assignee: String): Flow<List<Task>>
    
    // ===== QUERIES - Reminders =====
    
    @Query("SELECT * FROM tasks WHERE reminderEnabled = 1 AND reminderDateTime <= :now AND status NOT IN ('COMPLETED', 'CANCELLED')")
    suspend fun getTasksWithDueReminders(now: LocalDateTime): List<Task>
    
    @Query("SELECT * FROM tasks WHERE reminderEnabled = 1 AND reminderDateTime BETWEEN :start AND :end")
    fun getUpcomingReminders(start: LocalDateTime, end: LocalDateTime): Flow<List<Task>>
    
    // ===== QUERIES - Search =====
    
    @Query("""
        SELECT * FROM tasks 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%'
        OR location LIKE '%' || :query || '%'
        ORDER BY status ASC, dueDate ASC
    """)
    fun searchTasks(query: String): Flow<List<Task>>
    
    // ===== QUERIES - Stats =====
    
    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTotalCount(): Int
    
    @Query("SELECT COUNT(*) FROM tasks WHERE status = :status")
    suspend fun getCountByStatus(status: TaskStatus): Int
    
    @Query("SELECT COUNT(*) FROM tasks WHERE category = :category AND status != 'COMPLETED'")
    suspend fun getCountByCategory(category: TaskCategory): Int
    
    @Query("SELECT COUNT(*) FROM tasks WHERE priority = :priority AND status != 'COMPLETED'")
    suspend fun getCountByPriority(priority: TaskPriority): Int
    
    @Query("SELECT COUNT(*) FROM tasks WHERE dueDate < :today AND status NOT IN ('COMPLETED', 'CANCELLED')")
    suspend fun getOverdueCount(today: LocalDate): Int
    
    @Query("SELECT COUNT(*) FROM tasks WHERE dueDate = :today AND status != 'COMPLETED'")
    suspend fun getDueTodayCount(today: LocalDate): Int
    
    @Query("SELECT COUNT(*) FROM tasks WHERE completedAt >= :since")
    suspend fun getCompletedCountSince(since: LocalDateTime): Int
    
    // ===== QUERIES - With Details =====
    
    @Query("""
        SELECT t.*, 
               c.name as contactName,
               j.title as jobTitle
        FROM tasks t
        LEFT JOIN contacts c ON t.contactId = c.id
        LEFT JOIN jobs j ON t.jobId = j.id
        WHERE t.id = :taskId
    """)
    suspend fun getTaskWithDetails(taskId: Long): TaskWithDetails?
}
