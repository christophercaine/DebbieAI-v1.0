package com.debbiedoesit.debbieai.estimates.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface EstimateDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(estimate: Estimate): Long
    
    @Update
    suspend fun update(estimate: Estimate)
    
    @Query("UPDATE estimates SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: EstimateStatus, updatedAt: LocalDateTime)
    
    @Query("UPDATE estimates SET sentAt = :sentAt, status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markSent(id: Long, sentAt: LocalDateTime, status: EstimateStatus, updatedAt: LocalDateTime)
    
    @Query("UPDATE estimates SET viewedAt = :viewedAt, status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markViewed(id: Long, viewedAt: LocalDateTime, status: EstimateStatus, updatedAt: LocalDateTime)
    
    @Query("UPDATE estimates SET acceptedAt = :acceptedAt, status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markAccepted(id: Long, acceptedAt: LocalDateTime, status: EstimateStatus, updatedAt: LocalDateTime)
    
    @Query("UPDATE estimates SET declinedAt = :declinedAt, status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markDeclined(id: Long, declinedAt: LocalDateTime, status: EstimateStatus, updatedAt: LocalDateTime)
    
    @Query("UPDATE estimates SET jobId = :jobId, status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun convertToJob(id: Long, jobId: Long, status: EstimateStatus, updatedAt: LocalDateTime)
    
    @Delete
    suspend fun delete(estimate: Estimate)
    
    @Query("DELETE FROM estimates WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("SELECT * FROM estimates WHERE id = :id")
    suspend fun getById(id: Long): Estimate?
    
    @Query("SELECT * FROM estimates WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<Estimate?>
    
    @Query("SELECT * FROM estimates WHERE estimateNumber = :number LIMIT 1")
    suspend fun getByNumber(number: String): Estimate?
    
    @Query("SELECT * FROM estimates ORDER BY updatedAt DESC")
    fun getAllEstimates(): Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates ORDER BY createdAt DESC")
    suspend fun getAllEstimatesSnapshot(): List<Estimate>
    
    @Query("SELECT * FROM estimates WHERE status = :status ORDER BY updatedAt DESC")
    fun getByStatus(status: EstimateStatus): Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates WHERE status = 'DRAFT' ORDER BY updatedAt DESC")
    fun getDrafts(): Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates WHERE status IN ('SENT', 'VIEWED') ORDER BY sentAt DESC")
    fun getPending(): Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates WHERE status = 'ACCEPTED' ORDER BY acceptedAt DESC")
    fun getAccepted(): Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates WHERE contactId = :contactId ORDER BY createdAt DESC")
    fun getByContact(contactId: Long): Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates WHERE jobId = :jobId ORDER BY createdAt DESC")
    fun getByJob(jobId: Long): Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates WHERE validUntil < :date AND status IN ('SENT', 'VIEWED') ORDER BY validUntil ASC")
    fun getExpiring(date: LocalDate): Flow<List<Estimate>>
    
    @Query("""
        SELECT * FROM estimates 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%'
        OR estimateNumber LIKE '%' || :query || '%'
        ORDER BY updatedAt DESC
    """)
    fun search(query: String): Flow<List<Estimate>>
    
    @Query("SELECT COUNT(*) FROM estimates")
    suspend fun getTotalCount(): Int
    
    @Query("SELECT COUNT(*) FROM estimates WHERE status = :status")
    suspend fun getCountByStatus(status: EstimateStatus): Int
    
    @Query("SELECT SUM(total) FROM estimates WHERE status NOT IN ('DECLINED', 'EXPIRED')")
    suspend fun getTotalValue(): Double?
    
    @Query("SELECT SUM(total) FROM estimates WHERE status = 'ACCEPTED'")
    suspend fun getAcceptedValue(): Double?
    
    @Query("SELECT COUNT(*) FROM estimates WHERE contactId = :contactId")
    suspend fun getCountForContact(contactId: Long): Int
    
    @Query("SELECT MAX(CAST(SUBSTR(estimateNumber, 5) AS INTEGER)) FROM estimates WHERE estimateNumber LIKE :prefix || '%'")
    suspend fun getMaxNumberForPrefix(prefix: String): Int?
    
    @Query("""
        SELECT e.*, c.name as contactName, j.title as jobTitle,
               (SELECT COUNT(*) FROM photos WHERE jobId = e.jobId) as photoCount
        FROM estimates e
        LEFT JOIN contacts c ON e.contactId = c.id
        LEFT JOIN jobs j ON e.jobId = j.id
        WHERE e.id = :id
    """)
    suspend fun getWithDetails(id: Long): EstimateWithDetails?
}
