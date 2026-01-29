package com.debbiedoesit.debbieai.tasks.data.repository

import com.debbiedoesit.debbieai.tasks.data.local.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TaskRepository(
    private val taskDao: TaskDao
) {
    
    // ===== CREATE =====
    
    suspend fun createTask(task: Task): Long {
        return taskDao.insert(task)
    }
    
    suspend fun createTask(
        title: String,
        description: String = "",
        category: TaskCategory = TaskCategory.GENERAL,
        priority: TaskPriority = TaskPriority.NORMAL,
        dueDate: LocalDate? = null,
        dueTime: LocalTime? = null,
        contactId: Long? = null,
        jobId: Long? = null,
        reminderMinutesBefore: Int? = null
    ): Long {
        val reminderDateTime = if (reminderMinutesBefore != null && dueDate != null) {
            val dueDateTime = LocalDateTime.of(dueDate, dueTime ?: LocalTime.of(9, 0))
            dueDateTime.minusMinutes(reminderMinutesBefore.toLong())
        } else null
        
        val task = Task(
            title = title,
            description = description,
            category = category,
            priority = priority,
            dueDate = dueDate,
            dueTime = dueTime,
            allDay = dueTime == null,
            contactId = contactId,
            jobId = jobId,
            reminderEnabled = reminderDateTime != null,
            reminderDateTime = reminderDateTime,
            reminderMinutesBefore = reminderMinutesBefore ?: 30
        )
        return taskDao.insert(task)
    }
    
    /**
     * Create task from quick template
     */
    suspend fun createFromTemplate(
        template: QuickTaskTemplate,
        contactId: Long? = null,
        jobId: Long? = null,
        dueDate: LocalDate? = null
    ): Long {
        return createTask(
            title = template.title,
            category = template.category,
            dueDate = dueDate,
            contactId = contactId,
            jobId = jobId
        )
    }
    
    // ===== READ =====
    
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    
    fun getActiveTasks(): Flow<List<Task>> = taskDao.getActiveTasks()
    
    fun getCompletedTasks(): Flow<List<Task>> = taskDao.getCompletedTasks()
    
    fun getRecentlyCompleted(limit: Int = 10): Flow<List<Task>> = 
        taskDao.getRecentlyCompleted(limit)
    
    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>> = 
        taskDao.getTasksByStatus(status)
    
    fun getTasksByCategory(category: TaskCategory): Flow<List<Task>> = 
        taskDao.getTasksByCategory(category)
    
    fun getTasksByPriority(priority: TaskPriority): Flow<List<Task>> = 
        taskDao.getTasksByPriority(priority)
    
    fun getHighPriorityTasks(): Flow<List<Task>> = taskDao.getHighPriorityTasks()
    
    fun getOverdueTasks(): Flow<List<Task>> = taskDao.getOverdueTasks(LocalDate.now())
    
    fun getTasksDueToday(): Flow<List<Task>> = taskDao.getTasksDueToday(LocalDate.now())
    
    fun getTasksDueThisWeek(): Flow<List<Task>> = 
        taskDao.getTasksDueThisWeek(LocalDate.now(), LocalDate.now().plusDays(7))
    
    fun getTasksNoDueDate(): Flow<List<Task>> = taskDao.getTasksNoDueDate()
    
    fun getTasksForDate(date: LocalDate): Flow<List<Task>> = taskDao.getTasksForDate(date)
    
    fun getTasksInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Task>> = 
        taskDao.getTasksInRange(startDate, endDate)
    
    fun getTasksByContact(contactId: Long): Flow<List<Task>> = 
        taskDao.getTasksByContact(contactId)
    
    fun getActiveTasksByContact(contactId: Long): Flow<List<Task>> = 
        taskDao.getActiveTasksByContact(contactId)
    
    fun getTasksByJob(jobId: Long): Flow<List<Task>> = taskDao.getTasksByJob(jobId)
    
    fun getActiveTasksByJob(jobId: Long): Flow<List<Task>> = 
        taskDao.getActiveTasksByJob(jobId)
    
    fun getTasksByAssignee(assignee: String): Flow<List<Task>> = 
        taskDao.getTasksByAssignee(assignee)
    
    fun searchTasks(query: String): Flow<List<Task>> = taskDao.searchTasks(query)
    
    suspend fun getTaskById(taskId: Long): Task? = taskDao.getById(taskId)
    
    fun getTaskByIdFlow(taskId: Long): Flow<Task?> = taskDao.getByIdFlow(taskId)
    
    suspend fun getTaskWithDetails(taskId: Long): TaskWithDetails? = 
        taskDao.getTaskWithDetails(taskId)
    
    // ===== UPDATE =====
    
    suspend fun updateTask(task: Task) {
        taskDao.update(task.copy(updatedAt = LocalDateTime.now()))
    }
    
    suspend fun updateStatus(taskId: Long, status: TaskStatus) {
        taskDao.updateStatus(taskId, status, LocalDateTime.now())
    }
    
    suspend fun completeTask(taskId: Long, notes: String = "") {
        taskDao.completeTask(
            taskId = taskId,
            completedAt = LocalDateTime.now(),
            notes = notes,
            updatedAt = LocalDateTime.now()
        )
    }
    
    suspend fun reopenTask(taskId: Long) {
        val task = taskDao.getById(taskId)
        if (task != null) {
            taskDao.update(task.copy(
                status = TaskStatus.PENDING,
                completedAt = null,
                completionNotes = "",
                updatedAt = LocalDateTime.now()
            ))
        }
    }
    
    suspend fun updatePriority(taskId: Long, priority: TaskPriority) {
        taskDao.updatePriority(taskId, priority, LocalDateTime.now())
    }
    
    suspend fun updateDueDate(taskId: Long, dueDate: LocalDate?, dueTime: LocalTime? = null) {
        taskDao.updateDueDate(taskId, dueDate, dueTime, LocalDateTime.now())
    }
    
    suspend fun assignTask(taskId: Long, assignee: String) {
        taskDao.updateAssignment(taskId, assignee, LocalDateTime.now())
    }
    
    suspend fun setReminder(taskId: Long, reminderDateTime: LocalDateTime?) {
        taskDao.updateReminder(
            taskId = taskId,
            enabled = reminderDateTime != null,
            reminderTime = reminderDateTime,
            updatedAt = LocalDateTime.now()
        )
    }
    
    suspend fun snoozeReminder(taskId: Long, minutes: Int) {
        val newReminderTime = LocalDateTime.now().plusMinutes(minutes.toLong())
        setReminder(taskId, newReminderTime)
    }
    
    // ===== DELETE =====
    
    suspend fun deleteTask(taskId: Long) {
        taskDao.deleteById(taskId)
    }
    
    suspend fun deleteOldCompletedTasks(olderThanDays: Int = 30) {
        val cutoff = LocalDateTime.now().minusDays(olderThanDays.toLong())
        taskDao.deleteOldCompleted(cutoff)
    }
    
    // ===== STATS =====
    
    suspend fun getTaskSummary(): TaskSummary {
        val today = LocalDate.now()
        val startOfDay = LocalDateTime.of(today, LocalTime.MIN)
        
        val totalTasks = taskDao.getTotalCount()
        val pendingTasks = taskDao.getCountByStatus(TaskStatus.PENDING) + 
                          taskDao.getCountByStatus(TaskStatus.IN_PROGRESS) +
                          taskDao.getCountByStatus(TaskStatus.WAITING)
        val completedToday = taskDao.getCompletedCountSince(startOfDay)
        val overdueTasks = taskDao.getOverdueCount(today)
        val dueTodayTasks = taskDao.getDueTodayCount(today)
        
        val tasksByCategory = TaskCategory.values().associateWith { category ->
            taskDao.getCountByCategory(category)
        }.filter { it.value > 0 }
        
        val tasksByPriority = TaskPriority.values().associateWith { priority ->
            taskDao.getCountByPriority(priority)
        }.filter { it.value > 0 }
        
        return TaskSummary(
            totalTasks = totalTasks,
            pendingTasks = pendingTasks,
            completedToday = completedToday,
            overdueTasks = overdueTasks,
            dueTodayTasks = dueTodayTasks,
            dueThisWeekTasks = 0, // Would need another query
            tasksByCategory = tasksByCategory,
            tasksByPriority = tasksByPriority
        )
    }
    
    suspend fun getTaskCountForContact(contactId: Long): Int = 
        taskDao.getActiveTaskCountForContact(contactId)
    
    suspend fun getTaskCountForJob(jobId: Long): Int = 
        taskDao.getTaskCountForJob(jobId)
    
    suspend fun getCompletedTaskCountForJob(jobId: Long): Int = 
        taskDao.getCompletedTaskCountForJob(jobId)
    
    // ===== REMINDERS =====
    
    suspend fun getTasksWithDueReminders(): List<Task> {
        return taskDao.getTasksWithDueReminders(LocalDateTime.now())
    }
    
    fun getUpcomingReminders(hoursAhead: Int = 24): Flow<List<Task>> {
        return taskDao.getUpcomingReminders(
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(hoursAhead.toLong())
        )
    }
}
