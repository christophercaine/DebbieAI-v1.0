package com.debbiedoesit.debbieai.jobs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debbiedoesit.debbieai.jobs.data.local.*
import com.debbiedoesit.debbieai.jobs.data.repository.JobRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class JobViewModel(
    private val repository: JobRepository
) : ViewModel() {
    
    // ===== UI STATE =====
    
    private val _uiState = MutableStateFlow(JobUiState())
    val uiState: StateFlow<JobUiState> = _uiState.asStateFlow()
    
    // ===== FILTERS =====
    
    private val _statusFilter = MutableStateFlow<JobStatus?>(null)
    val statusFilter: StateFlow<JobStatus?> = _statusFilter.asStateFlow()
    
    private val _typeFilter = MutableStateFlow<JobType?>(null)
    val typeFilter: StateFlow<JobType?> = _typeFilter.asStateFlow()
    
    private val _viewMode = MutableStateFlow(JobViewMode.LIST)
    val viewMode: StateFlow<JobViewMode> = _viewMode.asStateFlow()
    
    // ===== SEARCH =====
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<Job>>(emptyList())
    val searchResults: StateFlow<List<Job>> = _searchResults.asStateFlow()
    
    // ===== JOB LISTS =====
    
    val allJobs: Flow<List<Job>> = repository.getAllJobs()
    val openJobs: Flow<List<Job>> = repository.getOpenJobs()
    val activeJobs: Flow<List<Job>> = repository.getActiveJobs()
    val upcomingJobs: Flow<List<Job>> = repository.getUpcomingJobs(5)
    val urgentJobs: Flow<List<Job>> = repository.getUrgentJobs()
    val unpaidJobs: Flow<List<Job>> = repository.getUnpaidJobs()
    
    // Combined filtered jobs
    val filteredJobs: Flow<List<Job>> = combine(
        allJobs,
        statusFilter,
        typeFilter
    ) { jobs, status, type ->
        jobs.filter { job ->
            (status == null || job.status == status) &&
            (type == null || job.jobType == type)
        }
    }
    
    // Grouped by status for Kanban view
    val jobsByStatus: Flow<Map<JobStatus, List<Job>>> = repository.getJobsGroupedByStatus()
    
    // ===== SELECTED JOB =====
    
    private val _selectedJobId = MutableStateFlow<Long?>(null)
    val selectedJob: Flow<Job?> = _selectedJobId.filterNotNull()
        .flatMapLatest { repository.getJobByIdFlow(it) }
    
    // ===== SUMMARY =====
    
    private val _summary = MutableStateFlow(JobSummary())
    val summary: StateFlow<JobSummary> = _summary.asStateFlow()
    
    init {
        loadSummary()
    }
    
    // ===== ACTIONS - CREATE =====
    
    fun createJob(
        title: String,
        contactId: Long? = null,
        addressId: Long? = null,
        jobType: JobType = JobType.GENERAL,
        description: String = "",
        estimatedCost: Double = 0.0,
        source: LeadSource = LeadSource.DIRECT
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val jobId = repository.createJob(
                    title = title,
                    contactId = contactId,
                    addressId = addressId,
                    jobType = jobType,
                    description = description,
                    estimatedCost = estimatedCost,
                    source = source
                )
                _uiState.update { it.copy(
                    isLoading = false,
                    lastCreatedJobId = jobId,
                    message = "Job created successfully"
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
    
    // ===== ACTIONS - UPDATE =====
    
    fun updateJob(job: Job) {
        viewModelScope.launch {
            repository.updateJob(job)
            loadSummary()
        }
    }
    
    fun updateStatus(jobId: Long, status: JobStatus) {
        viewModelScope.launch {
            repository.updateStatus(jobId, status)
            _uiState.update { it.copy(message = "Status updated to ${status.displayName}") }
            loadSummary()
        }
    }
    
    fun updatePriority(jobId: Long, priority: JobPriority) {
        viewModelScope.launch {
            repository.updatePriority(jobId, priority)
        }
    }
    
    fun assignCrew(jobId: Long, crewMembers: List<String>) {
        viewModelScope.launch {
            repository.assignCrew(jobId, crewMembers)
        }
    }
    
    fun scheduleJob(jobId: Long, date: LocalDate, time: String = "") {
        viewModelScope.launch {
            repository.scheduleJob(jobId, date, time)
            _uiState.update { it.copy(message = "Job scheduled for $date") }
            loadSummary()
        }
    }
    
    fun startJob(jobId: Long) {
        viewModelScope.launch {
            repository.startJob(jobId)
            _uiState.update { it.copy(message = "Job started") }
            loadSummary()
        }
    }
    
    fun completeJob(jobId: Long, actualHours: Double = 0.0) {
        viewModelScope.launch {
            repository.completeJob(jobId, actualHours)
            _uiState.update { it.copy(message = "Job completed!") }
            loadSummary()
        }
    }
    
    fun markDepositPaid(jobId: Long) {
        viewModelScope.launch {
            repository.markDepositPaid(jobId, true)
            _uiState.update { it.copy(message = "Deposit marked as paid") }
        }
    }
    
    fun markFinalPaid(jobId: Long) {
        viewModelScope.launch {
            repository.markFinalPaid(jobId, true)
            _uiState.update { it.copy(message = "Payment received!") }
            loadSummary()
        }
    }
    
    fun putOnHold(jobId: Long) {
        viewModelScope.launch {
            repository.putOnHold(jobId)
            _uiState.update { it.copy(message = "Job put on hold") }
            loadSummary()
        }
    }
    
    fun cancelJob(jobId: Long) {
        viewModelScope.launch {
            repository.cancelJob(jobId)
            _uiState.update { it.copy(message = "Job cancelled") }
            loadSummary()
        }
    }
    
    // ===== ACTIONS - DELETE =====
    
    fun deleteJob(jobId: Long) {
        viewModelScope.launch {
            repository.deleteJob(jobId)
            _uiState.update { it.copy(message = "Job deleted") }
            loadSummary()
        }
    }
    
    // ===== ACTIONS - FILTERS =====
    
    fun setStatusFilter(status: JobStatus?) {
        _statusFilter.value = status
    }
    
    fun setTypeFilter(type: JobType?) {
        _typeFilter.value = type
    }
    
    fun clearFilters() {
        _statusFilter.value = null
        _typeFilter.value = null
    }
    
    fun setViewMode(mode: JobViewMode) {
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
            repository.searchJobs(query).collect { results ->
                _searchResults.value = results
            }
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }
    
    // ===== ACTIONS - SELECTION =====
    
    fun selectJob(jobId: Long) {
        _selectedJobId.value = jobId
    }
    
    fun clearSelection() {
        _selectedJobId.value = null
    }
    
    // ===== ACTIONS - LOAD DATA =====
    
    fun loadSummary() {
        viewModelScope.launch {
            val summary = repository.getJobSummary()
            _summary.value = summary
        }
    }
    
    fun getJobsForDate(date: LocalDate): Flow<List<Job>> = repository.getJobsForDate(date)
    
    fun getJobsForWeek(weekStart: LocalDate): Flow<List<Job>> = repository.getJobsForWeek(weekStart)
    
    fun getJobsForMonth(yearMonth: YearMonth): Flow<List<Job>> = repository.getJobsForMonth(yearMonth)
    
    fun getJobsByContact(contactId: Long): Flow<List<Job>> = repository.getJobsByContact(contactId)
    
    // ===== UTILITY =====
    
    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    suspend fun getJobById(jobId: Long): Job? = repository.getJobById(jobId)
    
    suspend fun getJobWithCounts(jobId: Long): JobWithCounts? = repository.getJobWithCounts(jobId)
}

/**
 * UI State for Jobs screen
 */
data class JobUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val lastCreatedJobId: Long? = null
)

/**
 * View modes for job list
 */
enum class JobViewMode {
    LIST,       // Standard list
    KANBAN,     // Columns by status
    CALENDAR,   // Calendar view
    MAP         // Map view (future)
}
