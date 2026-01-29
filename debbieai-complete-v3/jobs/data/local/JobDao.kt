package com.debbiedoesit.debbieai.jobs.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface JobDao {
    
    // ===== INSERT =====
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: Job): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(jobs: List<Job>): List<Long>
    
    // ===== UPDATE =====
    
    @Update
    suspend fun update(job: Job)
    
    @Query("UPDATE jobs SET status = :status, updatedAt = :updatedAt WHERE id = :jobId")
    suspend fun updateStatus(jobId: Long, status: JobStatus, updatedAt: java.time.LocalDateTime)
    
    @Query("UPDATE jobs SET priority = :priority, updatedAt = :updatedAt WHERE id = :jobId")
    suspend fun updatePriority(jobId: Long, priority: JobPriority, updatedAt: java.time.LocalDateTime)
    
    @Query("UPDATE jobs SET assignedTo = :crew, updatedAt = :updatedAt WHERE id = :jobId")
    suspend fun updateAssignedCrew(jobId: Long, crew: List<String>, updatedAt: java.time.LocalDateTime)
    
    @Query("UPDATE jobs SET startDate = :startDate, scheduledTime = :time, status = :status, updatedAt = :updatedAt WHERE id = :jobId")
    suspend fun scheduleJob(jobId: Long, startDate: LocalDate, time: String, status: JobStatus, updatedAt: java.time.LocalDateTime)
    
    @Query("UPDATE jobs SET completedDate = :completedDate, status = :status, actualHours = :hours, updatedAt = :updatedAt WHERE id = :jobId")
    suspend fun completeJob(jobId: Long, completedDate: LocalDate, status: JobStatus, hours: Double, updatedAt: java.time.LocalDateTime)
    
    @Query("UPDATE jobs SET depositPaid = :paid, updatedAt = :updatedAt WHERE id = :jobId")
    suspend fun updateDepositPaid(jobId: Long, paid: Boolean, updatedAt: java.time.LocalDateTime)
    
    @Query("UPDATE jobs SET finalPaid = :paid, status = :status, updatedAt = :updatedAt WHERE id = :jobId")
    suspend fun updateFinalPaid(jobId: Long, paid: Boolean, status: JobStatus, updatedAt: java.time.LocalDateTime)
    
    // ===== DELETE =====
    
    @Delete
    suspend fun delete(job: Job)
    
    @Query("DELETE FROM jobs WHERE id = :jobId")
    suspend fun deleteById(jobId: Long)
    
    // ===== QUERIES - Single =====
    
    @Query("SELECT * FROM jobs WHERE id = :jobId")
    suspend fun getById(jobId: Long): Job?
    
    @Query("SELECT * FROM jobs WHERE id = :jobId")
    fun getByIdFlow(jobId: Long): Flow<Job?>
    
    @Query("SELECT * FROM jobs WHERE jobNumber = :jobNumber LIMIT 1")
    suspend fun getByJobNumber(jobNumber: String): Job?
    
    // ===== QUERIES - All Jobs =====
    
    @Query("SELECT * FROM jobs ORDER BY updatedAt DESC")
    fun getAllJobs(): Flow<List<Job>>
    
    @Query("SELECT * FROM jobs ORDER BY createdAt DESC")
    suspend fun getAllJobsSnapshot(): List<Job>
    
    @Query("SELECT * FROM jobs WHERE status IN (:statuses) ORDER BY startDate ASC, priority DESC")
    fun getJobsByStatuses(statuses: List<JobStatus>): Flow<List<Job>>
    
    // ===== QUERIES - By Status =====
    
    @Query("SELECT * FROM jobs WHERE status = :status ORDER BY updatedAt DESC")
    fun getJobsByStatus(status: JobStatus): Flow<List<Job>>
    
    @Query("""
        SELECT * FROM jobs 
        WHERE status IN ('LEAD', 'QUOTED', 'APPROVED', 'SCHEDULED', 'IN_PROGRESS', 'ON_HOLD')
        ORDER BY priority DESC, startDate ASC
    """)
    fun getOpenJobs(): Flow<List<Job>>
    
    @Query("""
        SELECT * FROM jobs 
        WHERE status IN ('SCHEDULED', 'IN_PROGRESS')
        ORDER BY startDate ASC, scheduledTime ASC
    """)
    fun getActiveJobs(): Flow<List<Job>>
    
    @Query("""
        SELECT * FROM jobs 
        WHERE status IN ('COMPLETED', 'INVOICED', 'PAID', 'CANCELLED')
        ORDER BY completedDate DESC
    """)
    fun getClosedJobs(): Flow<List<Job>>
    
    // ===== QUERIES - By Type =====
    
    @Query("SELECT * FROM jobs WHERE jobType = :type ORDER BY updatedAt DESC")
    fun getJobsByType(type: JobType): Flow<List<Job>>
    
    // ===== QUERIES - By Contact =====
    
    @Query("SELECT * FROM jobs WHERE contactId = :contactId ORDER BY createdAt DESC")
    fun getJobsByContact(contactId: Long): Flow<List<Job>>
    
    @Query("SELECT COUNT(*) FROM jobs WHERE contactId = :contactId")
    suspend fun getJobCountForContact(contactId: Long): Int
    
    // ===== QUERIES - By Date =====
    
    @Query("SELECT * FROM jobs WHERE startDate = :date ORDER BY scheduledTime ASC")
    fun getJobsForDate(date: LocalDate): Flow<List<Job>>
    
    @Query("SELECT * FROM jobs WHERE startDate BETWEEN :startDate AND :endDate ORDER BY startDate ASC")
    fun getJobsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Job>>
    
    @Query("""
        SELECT * FROM jobs 
        WHERE startDate >= :today 
        AND status IN ('SCHEDULED', 'IN_PROGRESS')
        ORDER BY startDate ASC, scheduledTime ASC
        LIMIT :limit
    """)
    fun getUpcomingJobs(today: LocalDate, limit: Int = 10): Flow<List<Job>>
    
    @Query("""
        SELECT * FROM jobs 
        WHERE completedDate BETWEEN :startDate AND :endDate
        ORDER BY completedDate DESC
    """)
    fun getCompletedInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Job>>
    
    // ===== QUERIES - By Priority =====
    
    @Query("""
        SELECT * FROM jobs 
        WHERE priority = :priority 
        AND status NOT IN ('COMPLETED', 'PAID', 'CANCELLED')
        ORDER BY startDate ASC
    """)
    fun getJobsByPriority(priority: JobPriority): Flow<List<Job>>
    
    @Query("""
        SELECT * FROM jobs 
        WHERE priority = 'URGENT' 
        AND status NOT IN ('COMPLETED', 'PAID', 'CANCELLED')
        ORDER BY startDate ASC
    """)
    fun getUrgentJobs(): Flow<List<Job>>
    
    // ===== QUERIES - Search =====
    
    @Query("""
        SELECT * FROM jobs 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%'
        OR jobNumber LIKE '%' || :query || '%'
        OR notes LIKE '%' || :query || '%'
        ORDER BY updatedAt DESC
    """)
    fun searchJobs(query: String): Flow<List<Job>>
    
    // ===== QUERIES - Financial =====
    
    @Query("""
        SELECT * FROM jobs 
        WHERE status IN ('COMPLETED', 'INVOICED') 
        AND finalPaid = 0
        ORDER BY completedDate ASC
    """)
    fun getUnpaidJobs(): Flow<List<Job>>
    
    @Query("""
        SELECT * FROM jobs 
        WHERE depositPaid = 0 
        AND depositAmount > 0
        AND status IN ('APPROVED', 'SCHEDULED', 'IN_PROGRESS')
        ORDER BY startDate ASC
    """)
    fun getJobsAwaitingDeposit(): Flow<List<Job>>
    
    @Query("SELECT SUM(estimatedCost) FROM jobs WHERE status NOT IN ('CANCELLED')")
    suspend fun getTotalEstimatedRevenue(): Double?
    
    @Query("SELECT SUM(actualCost) FROM jobs WHERE finalPaid = 1")
    suspend fun getTotalPaidRevenue(): Double?
    
    // ===== QUERIES - Stats =====
    
    @Query("SELECT COUNT(*) FROM jobs")
    suspend fun getTotalJobCount(): Int
    
    @Query("SELECT COUNT(*) FROM jobs WHERE status = :status")
    suspend fun getJobCountByStatus(status: JobStatus): Int
    
    @Query("SELECT COUNT(*) FROM jobs WHERE jobType = :type")
    suspend fun getJobCountByType(type: JobType): Int
    
    @Query("""
        SELECT COUNT(*) FROM jobs 
        WHERE completedDate BETWEEN :startDate AND :endDate
    """)
    suspend fun getCompletedCountInRange(startDate: LocalDate, endDate: LocalDate): Int
    
    // ===== QUERIES - With Related Data =====
    
    @Query("""
        SELECT j.*, 
               c.name as contactName,
               a.street || ', ' || a.city as addressLine,
               (SELECT COUNT(*) FROM photos WHERE jobId = j.id) as photoCount,
               (SELECT COUNT(*) FROM estimates WHERE jobId = j.id) as estimateCount,
               (SELECT COUNT(*) FROM tasks WHERE jobId = j.id) as taskCount,
               (SELECT COUNT(*) FROM tasks WHERE jobId = j.id AND status = 'COMPLETED') as completedTaskCount
        FROM jobs j
        LEFT JOIN contacts c ON j.contactId = c.id
        LEFT JOIN addresses a ON j.addressId = a.id
        WHERE j.id = :jobId
    """)
    suspend fun getJobWithCounts(jobId: Long): JobWithCounts?
    
    // ===== UTILITY =====
    
    @Query("SELECT MAX(CAST(SUBSTR(jobNumber, 6) AS INTEGER)) FROM jobs WHERE jobNumber LIKE :prefix || '%'")
    suspend fun getMaxJobNumberForPrefix(prefix: String): Int?
}
