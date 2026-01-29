package com.debbiedoesit.debbieai.jobs.data.repository

import com.debbiedoesit.debbieai.jobs.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class JobRepository(
    private val jobDao: JobDao
) {
    
    // ===== CREATE =====
    
    suspend fun createJob(job: Job): Long {
        val jobNumber = if (job.jobNumber.isEmpty()) {
            generateJobNumber()
        } else {
            job.jobNumber
        }
        return jobDao.insert(job.copy(jobNumber = jobNumber))
    }
    
    suspend fun createJob(
        title: String,
        contactId: Long? = null,
        addressId: Long? = null,
        jobType: JobType = JobType.GENERAL,
        description: String = "",
        estimatedCost: Double = 0.0,
        source: LeadSource = LeadSource.DIRECT
    ): Long {
        val job = Job(
            title = title,
            contactId = contactId,
            addressId = addressId,
            jobType = jobType,
            description = description,
            estimatedCost = estimatedCost,
            source = source,
            jobNumber = generateJobNumber()
        )
        return jobDao.insert(job)
    }
    
    // ===== READ =====
    
    fun getAllJobs(): Flow<List<Job>> = jobDao.getAllJobs()
    
    fun getOpenJobs(): Flow<List<Job>> = jobDao.getOpenJobs()
    
    fun getActiveJobs(): Flow<List<Job>> = jobDao.getActiveJobs()
    
    fun getClosedJobs(): Flow<List<Job>> = jobDao.getClosedJobs()
    
    fun getJobsByStatus(status: JobStatus): Flow<List<Job>> = jobDao.getJobsByStatus(status)
    
    fun getJobsByType(type: JobType): Flow<List<Job>> = jobDao.getJobsByType(type)
    
    fun getJobsByContact(contactId: Long): Flow<List<Job>> = jobDao.getJobsByContact(contactId)
    
    fun getJobsForDate(date: LocalDate): Flow<List<Job>> = jobDao.getJobsForDate(date)
    
    fun getJobsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Job>> = 
        jobDao.getJobsInRange(startDate, endDate)
    
    fun getUpcomingJobs(limit: Int = 10): Flow<List<Job>> = 
        jobDao.getUpcomingJobs(LocalDate.now(), limit)
    
    fun getUrgentJobs(): Flow<List<Job>> = jobDao.getUrgentJobs()
    
    fun searchJobs(query: String): Flow<List<Job>> = jobDao.searchJobs(query)
    
    fun getUnpaidJobs(): Flow<List<Job>> = jobDao.getUnpaidJobs()
    
    suspend fun getJobById(jobId: Long): Job? = jobDao.getById(jobId)
    
    fun getJobByIdFlow(jobId: Long): Flow<Job?> = jobDao.getByIdFlow(jobId)
    
    suspend fun getJobWithCounts(jobId: Long): JobWithCounts? = jobDao.getJobWithCounts(jobId)
    
    // ===== UPDATE =====
    
    suspend fun updateJob(job: Job) {
        jobDao.update(job.copy(updatedAt = LocalDateTime.now()))
    }
    
    suspend fun updateStatus(jobId: Long, status: JobStatus) {
        jobDao.updateStatus(jobId, status, LocalDateTime.now())
    }
    
    suspend fun updatePriority(jobId: Long, priority: JobPriority) {
        jobDao.updatePriority(jobId, priority, LocalDateTime.now())
    }
    
    suspend fun assignCrew(jobId: Long, crewMembers: List<String>) {
        jobDao.updateAssignedCrew(jobId, crewMembers, LocalDateTime.now())
    }
    
    suspend fun scheduleJob(jobId: Long, date: LocalDate, time: String = "") {
        jobDao.scheduleJob(jobId, date, time, JobStatus.SCHEDULED, LocalDateTime.now())
    }
    
    suspend fun startJob(jobId: Long) {
        jobDao.updateStatus(jobId, JobStatus.IN_PROGRESS, LocalDateTime.now())
    }
    
    suspend fun completeJob(jobId: Long, actualHours: Double = 0.0) {
        jobDao.completeJob(
            jobId = jobId,
            completedDate = LocalDate.now(),
            status = JobStatus.COMPLETED,
            hours = actualHours,
            updatedAt = LocalDateTime.now()
        )
    }
    
    suspend fun markDepositPaid(jobId: Long, paid: Boolean = true) {
        jobDao.updateDepositPaid(jobId, paid, LocalDateTime.now())
    }
    
    suspend fun markFinalPaid(jobId: Long, paid: Boolean = true) {
        val status = if (paid) JobStatus.PAID else JobStatus.INVOICED
        jobDao.updateFinalPaid(jobId, paid, status, LocalDateTime.now())
    }
    
    suspend fun putOnHold(jobId: Long) {
        jobDao.updateStatus(jobId, JobStatus.ON_HOLD, LocalDateTime.now())
    }
    
    suspend fun cancelJob(jobId: Long) {
        jobDao.updateStatus(jobId, JobStatus.CANCELLED, LocalDateTime.now())
    }
    
    // ===== DELETE =====
    
    suspend fun deleteJob(jobId: Long) {
        jobDao.deleteById(jobId)
    }
    
    // ===== STATS & SUMMARY =====
    
    suspend fun getJobSummary(): JobSummary {
        val today = LocalDate.now()
        val monthStart = today.withDayOfMonth(1)
        val monthEnd = YearMonth.from(today).atEndOfMonth()
        
        val totalJobs = jobDao.getTotalJobCount()
        val activeJobs = jobDao.getJobCountByStatus(JobStatus.IN_PROGRESS) + 
                        jobDao.getJobCountByStatus(JobStatus.SCHEDULED)
        val completedThisMonth = jobDao.getCompletedCountInRange(monthStart, monthEnd)
        val totalRevenue = jobDao.getTotalPaidRevenue() ?: 0.0
        val pendingRevenue = jobDao.getTotalEstimatedRevenue() ?: 0.0
        
        val jobsByStatus = JobStatus.values().associateWith { status ->
            jobDao.getJobCountByStatus(status)
        }.filter { it.value > 0 }
        
        val jobsByType = JobType.values().associateWith { type ->
            jobDao.getJobCountByType(type)
        }.filter { it.value > 0 }
        
        return JobSummary(
            totalJobs = totalJobs,
            activeJobs = activeJobs,
            completedThisMonth = completedThisMonth,
            totalRevenue = totalRevenue,
            pendingRevenue = pendingRevenue - totalRevenue,
            jobsByStatus = jobsByStatus,
            jobsByType = jobsByType
        )
    }
    
    suspend fun getJobCountForContact(contactId: Long): Int = 
        jobDao.getJobCountForContact(contactId)
    
    // ===== UTILITY =====
    
    private suspend fun generateJobNumber(): String {
        val year = LocalDate.now().year
        val prefix = "$year-"
        val maxNumber = jobDao.getMaxJobNumberForPrefix(prefix) ?: 0
        val nextNumber = maxNumber + 1
        return "$prefix${nextNumber.toString().padStart(4, '0')}"
    }
    
    /**
     * Get jobs grouped by status for kanban-style view
     */
    fun getJobsGroupedByStatus(): Flow<Map<JobStatus, List<Job>>> {
        return jobDao.getAllJobs().map { jobs ->
            jobs.groupBy { it.status }
        }
    }
    
    /**
     * Get jobs for week view
     */
    fun getJobsForWeek(weekStart: LocalDate): Flow<List<Job>> {
        val weekEnd = weekStart.plusDays(6)
        return jobDao.getJobsInRange(weekStart, weekEnd)
    }
    
    /**
     * Get jobs for month view
     */
    fun getJobsForMonth(yearMonth: YearMonth): Flow<List<Job>> {
        val monthStart = yearMonth.atDay(1)
        val monthEnd = yearMonth.atEndOfMonth()
        return jobDao.getJobsInRange(monthStart, monthEnd)
    }
}
