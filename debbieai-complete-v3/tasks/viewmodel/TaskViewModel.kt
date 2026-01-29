package com.debbiedoesit.debbieai.tasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debbiedoesit.debbieai.tasks.data.local.*
import com.debbiedoesit.debbieai.tasks.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {
    
    // ===== UI STATE =====
    
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    // ===== FILTERS =====
    
    private val _statusFilter = MutableStateFlow<TaskStatus?>(null)
    val statusFilter: StateFlow<TaskStatus?> = _statusFilter.asStateFlow()
    
    private val _categoryFilter = MutableStateFlow<TaskCategory?>(null)
    val categoryFilter: StateFlow<TaskCategory?> = _categoryFilter.asStateFlow()
    
    private val _priorityFilter = MutableStateFlow<TaskPriority?>(null)
    val priorityFilter: StateFlow<TaskPriority?> = _priorityFilter.asStateFlow()
    
    private val _viewMode = MutableStateFlow(TaskViewMode.LIST)
    val viewMode: StateFlow<TaskViewMode> = _viewMode.asStateFlow()
    
    // ===== SEARCH =====
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<Task>>(emptyList())
    val searchResults: StateFlow<List<Task>> = _searchResults.asStateFlow()
    
    // ===== TASK LISTS =====
    
    val allTasks: Flow<List<Task>> = repository.getAllTasks()
    val activeTasks: Flow<List<Task>> = repository.getActiveTasks()
    val completedTasks: Flow<List<Task>> = repository.getCompletedTasks()
    val overdueTasks: Flow<List<Task>> = repository.getOverdueTasks()
    val todayTasks: Flow<List<Task>> = repository.getTasksDueToday()
    val thisWeekTasks: Flow<List<Task>> = repository.getTasksDueThisWeek()
    val highPriorityTasks: Flow<List<Task>> = repository.getHighPriorityTasks()
    val noDueDateTasks: Flow<List<Task>> = repository.getTasksNoDueDate()
    
    // Filtered tasks
    val filteredTasks: Flow<List<Task>> = combine(
        activeTasks,
        statusFilter,
        categoryFilter,
        priorityFilter
    ) { tasks, status, category, priority ->
        tasks.filter { task ->
            (status == null || task.status == status) &&
            (category == null || task.category == category) &&
            (priority == null || task.priority == priority)
        }
    }
    
    // ===== SELECTED TASK =====
    
    private val _selectedTaskId = MutableStateFlow<Long?>(null)
    val selectedTask: Flow<Task?> = _selectedTaskId.filterNotNull()
        .flatMapLatest { repository.getTaskByIdFlow(it) }
    
    // ===== SUMMARY =====
    
    private val _summary = MutableStateFlow(TaskSummary())
    val summary: StateFlow<TaskSummary> = _summary.asStateFlow()
    
    // ===== SELECTED DATE (for calendar view) =====
    
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()
    
    val tasksForSelectedDate: Flow<List<Task>> = selectedDate
        .flatMapLatest { date -> repository.getTasksForDate(date) }
    
    init {
        loadSummary()
    }
    
    // ===== ACTIONS - CREATE =====
    
    fun createTask(
        title: String,
        description: String = "",
        category: TaskCategory = TaskCategory.GENERAL,
        priority: TaskPriority = TaskPriority.NORMAL,
        dueDate: LocalDate? = null,
        dueTime: LocalTime? = null,
        contactId: Long? = null,
        jobId: Long? = null,
        reminderMinutesBefore: Int? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val taskId = repository.createTask(
                    title = title,
                    description = description,
                    category = category,
                    priority = priority,
                    dueDate = dueDate,
                    dueTime = dueTime,
                    contactId = contactId,
                    jobId = jobId,
                    reminderMinutesBefore = reminderMinutesBefore
                )
                _uiState.update { it.copy(
                    isLoading = false,
                    lastCreatedTaskId = taskId,
                    message = "Task created"
                )}
                loadSummary()
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message
                )}
            }
        }
    }
    
    fun createFromTemplate(
        template: QuickTaskTemplate,
        contactId: Long? = null,
        jobId: Long? = null,
        dueDate: LocalDate? = null
    ) {
        viewModelScope.launch {
            repository.createFromTemplate(template, contactId, jobId, dueDate)
            _uiState.update { it.copy(message = "Task created: ${template.title}") }
            loadSummary()
        }
    }
    
    // ===== ACTIONS - UPDATE =====
    
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
            loadSummary()
        }
    }
    
    fun updateStatus(taskId: Long, status: TaskStatus) {
        viewModelScope.launch {
            repository.updateStatus(taskId, status)
            loadSummary()
        }
    }
    
    fun completeTask(taskId: Long, notes: String = "") {
        viewModelScope.launch {
            repository.completeTask(taskId, notes)
            _uiState.update { it.copy(message = "Task completed!") }
            loadSummary()
        }
    }
    
    fun reopenTask(taskId: Long) {
        viewModelScope.launch {
            repository.reopenTask(taskId)
            _uiState.update { it.copy(message = "Task reopened") }
            loadSummary()
        }
    }
    
    fun updatePriority(taskId: Long, priority: TaskPriority) {
        viewModelScope.launch {
            repository.updatePriority(taskId, priority)
        }
    }
    
    fun updateDueDate(taskId: Long, dueDate: LocalDate?, dueTime: LocalTime? = null) {
        viewModelScope.launch {
            repository.updateDueDate(taskId, dueDate, dueTime)
            loadSummary()
        }
    }
    
    fun assignTask(taskId: Long, assignee: String) {
        viewModelScope.launch {
            repository.assignTask(taskId, assignee)
        }
    }
    
    fun setReminder(taskId: Long, reminderDateTime: LocalDateTime?) {
        viewModelScope.launch {
            repository.setReminder(taskId, reminderDateTime)
            val msg = if (reminderDateTime != null) "Reminder set" else "Reminder removed"
            _uiState.update { it.copy(message = msg) }
        }
    }
    
    fun snoozeReminder(taskId: Long, minutes: Int) {
        viewModelScope.launch {
            repository.snoozeReminder(taskId, minutes)
            _uiState.update { it.copy(message = "Snoozed for $minutes minutes") }
        }
    }
    
    // ===== ACTIONS - DELETE =====
    
    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            _uiState.update { it.copy(message = "Task deleted") }
            loadSummary()
        }
    }
    
    fun cleanupOldTasks(olderThanDays: Int = 30) {
        viewModelScope.launch {
            repository.deleteOldCompletedTasks(olderThanDays)
            _uiState.update { it.copy(message = "Old tasks cleaned up") }
            loadSummary()
        }
    }
    
    // ===== ACTIONS - FILTERS =====
    
    fun setStatusFilter(status: TaskStatus?) {
        _statusFilter.value = status
    }
    
    fun setCategoryFilter(category: TaskCategory?) {
        _categoryFilter.value = category
    }
    
    fun setPriorityFilter(priority: TaskPriority?) {
        _priorityFilter.value = priority
    }
    
    fun clearFilters() {
        _statusFilter.value = null
        _categoryFilter.value = null
        _priorityFilter.value = null
    }
    
    fun setViewMode(mode: TaskViewMode) {
        _viewMode.value = mode
    }
    
    // ===== ACTIONS - SEARCH =====
    
    fun search(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            repository.searchTasks(query).collect { results ->
                _searchResults.value = results
            }
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }
    
    // ===== ACTIONS - SELECTION =====
    
    fun selectTask(taskId: Long) {
        _selectedTaskId.value = taskId
    }
    
    fun clearSelection() {
        _selectedTaskId.value = null
    }
    
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }
    
    // ===== ACTIONS - LOAD DATA =====
    
    fun loadSummary() {
        viewModelScope.launch {
            val summary = repository.getTaskSummary()
            _summary.value = summary
        }
    }
    
    fun getTasksForDate(date: LocalDate): Flow<List<Task>> = 
        repository.getTasksForDate(date)
    
    fun getTasksInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Task>> = 
        repository.getTasksInRange(startDate, endDate)
    
    fun getTasksByContact(contactId: Long): Flow<List<Task>> = 
        repository.getTasksByContact(contactId)
    
    fun getActiveTasksByContact(contactId: Long): Flow<List<Task>> = 
        repository.getActiveTasksByContact(contactId)
    
    fun getTasksByJob(jobId: Long): Flow<List<Task>> = 
        repository.getTasksByJob(jobId)
    
    fun getActiveTasksByJob(jobId: Long): Flow<List<Task>> = 
        repository.getActiveTasksByJob(jobId)
    
    // ===== UTILITY =====
    
    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    suspend fun getTaskById(taskId: Long): Task? = repository.getTaskById(taskId)
    
    suspend fun getTaskWithDetails(taskId: Long): TaskWithDetails? = 
        repository.getTaskWithDetails(taskId)
}

/**
 * UI State for Tasks screen
 */
data class TaskUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val lastCreatedTaskId: Long? = null
)

/**
 * View modes for task list
 */
enum class TaskViewMode {
    LIST,       // Standard list grouped by status
    TODAY,      // Focus on today's tasks
    CALENDAR,   // Calendar view
    KANBAN      // Columns by status
}
