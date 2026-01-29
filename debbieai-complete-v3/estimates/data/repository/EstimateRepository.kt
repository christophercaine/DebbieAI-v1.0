package com.debbiedoesit.debbieai.estimates.data.repository

import com.debbiedoesit.debbieai.estimates.data.local.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

class EstimateRepository(private val estimateDao: EstimateDao) {
    
    suspend fun createEstimate(estimate: Estimate): Long {
        val number = if (estimate.estimateNumber.isEmpty()) generateEstimateNumber() else estimate.estimateNumber
        val calculated = calculateTotals(estimate)
        return estimateDao.insert(calculated.copy(estimateNumber = number))
    }
    
    suspend fun createEstimate(
        title: String,
        contactId: Long? = null,
        jobId: Long? = null,
        description: String = "",
        lineItems: List<LineItem> = emptyList(),
        taxRate: Double = 0.0,
        validDays: Int = 30
    ): Long {
        val estimate = Estimate(
            title = title,
            contactId = contactId,
            jobId = jobId,
            description = description,
            lineItems = lineItems,
            taxRate = taxRate,
            validUntil = LocalDate.now().plusDays(validDays.toLong()),
            estimateNumber = generateEstimateNumber()
        )
        return estimateDao.insert(calculateTotals(estimate))
    }
    
    fun getAllEstimates(): Flow<List<Estimate>> = estimateDao.getAllEstimates()
    fun getDrafts(): Flow<List<Estimate>> = estimateDao.getDrafts()
    fun getPending(): Flow<List<Estimate>> = estimateDao.getPending()
    fun getAccepted(): Flow<List<Estimate>> = estimateDao.getAccepted()
    fun getByStatus(status: EstimateStatus): Flow<List<Estimate>> = estimateDao.getByStatus(status)
    fun getByContact(contactId: Long): Flow<List<Estimate>> = estimateDao.getByContact(contactId)
    fun getByJob(jobId: Long): Flow<List<Estimate>> = estimateDao.getByJob(jobId)
    fun getExpiring(withinDays: Int = 7): Flow<List<Estimate>> = 
        estimateDao.getExpiring(LocalDate.now().plusDays(withinDays.toLong()))
    fun search(query: String): Flow<List<Estimate>> = estimateDao.search(query)
    
    suspend fun getById(id: Long): Estimate? = estimateDao.getById(id)
    fun getByIdFlow(id: Long): Flow<Estimate?> = estimateDao.getByIdFlow(id)
    suspend fun getWithDetails(id: Long): EstimateWithDetails? = estimateDao.getWithDetails(id)
    
    suspend fun updateEstimate(estimate: Estimate) {
        val calculated = calculateTotals(estimate)
        estimateDao.update(calculated.copy(updatedAt = LocalDateTime.now()))
    }
    
    suspend fun updateLineItems(estimateId: Long, lineItems: List<LineItem>) {
        val estimate = estimateDao.getById(estimateId) ?: return
        val updated = calculateTotals(estimate.copy(lineItems = lineItems))
        estimateDao.update(updated.copy(updatedAt = LocalDateTime.now()))
    }
    
    suspend fun addLineItem(estimateId: Long, item: LineItem) {
        val estimate = estimateDao.getById(estimateId) ?: return
        val newItems = estimate.lineItems + item.copy(sortOrder = estimate.lineItems.size)
        updateLineItems(estimateId, newItems)
    }
    
    suspend fun removeLineItem(estimateId: Long, itemId: String) {
        val estimate = estimateDao.getById(estimateId) ?: return
        val newItems = estimate.lineItems.filter { it.id != itemId }
        updateLineItems(estimateId, newItems)
    }
    
    suspend fun markSent(id: Long) {
        estimateDao.markSent(id, LocalDateTime.now(), EstimateStatus.SENT, LocalDateTime.now())
    }
    
    suspend fun markViewed(id: Long) {
        estimateDao.markViewed(id, LocalDateTime.now(), EstimateStatus.VIEWED, LocalDateTime.now())
    }
    
    suspend fun markAccepted(id: Long) {
        estimateDao.markAccepted(id, LocalDateTime.now(), EstimateStatus.ACCEPTED, LocalDateTime.now())
    }
    
    suspend fun markDeclined(id: Long) {
        estimateDao.markDeclined(id, LocalDateTime.now(), EstimateStatus.DECLINED, LocalDateTime.now())
    }
    
    suspend fun markExpired(id: Long) {
        estimateDao.updateStatus(id, EstimateStatus.EXPIRED, LocalDateTime.now())
    }
    
    suspend fun convertToJob(estimateId: Long, jobId: Long) {
        estimateDao.convertToJob(estimateId, jobId, EstimateStatus.CONVERTED, LocalDateTime.now())
    }
    
    suspend fun duplicate(id: Long): Long? {
        val original = estimateDao.getById(id) ?: return null
        val copy = original.copy(
            id = 0,
            estimateNumber = generateEstimateNumber(),
            status = EstimateStatus.DRAFT,
            sentAt = null,
            viewedAt = null,
            acceptedAt = null,
            declinedAt = null,
            validUntil = LocalDate.now().plusDays(30),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        return estimateDao.insert(copy)
    }
    
    suspend fun delete(id: Long) = estimateDao.deleteById(id)
    
    suspend fun getSummary(): EstimateSummary {
        val total = estimateDao.getTotalCount()
        val draft = estimateDao.getCountByStatus(EstimateStatus.DRAFT)
        val sent = estimateDao.getCountByStatus(EstimateStatus.SENT) + 
                   estimateDao.getCountByStatus(EstimateStatus.VIEWED)
        val accepted = estimateDao.getCountByStatus(EstimateStatus.ACCEPTED)
        val totalValue = estimateDao.getTotalValue() ?: 0.0
        val acceptedValue = estimateDao.getAcceptedValue() ?: 0.0
        val sentTotal = sent + accepted + estimateDao.getCountByStatus(EstimateStatus.DECLINED)
        val rate = if (sentTotal > 0) (accepted.toDouble() / sentTotal) * 100 else 0.0
        
        return EstimateSummary(total, draft, sent, accepted, totalValue, acceptedValue, rate)
    }
    
    private fun calculateTotals(estimate: Estimate): Estimate {
        val itemsWithTotals = estimate.lineItems.map { 
            it.copy(total = it.quantity * it.unitPrice) 
        }
        val subtotal = itemsWithTotals.sumOf { it.total }
        val taxableAmount = itemsWithTotals.filter { it.taxable }.sumOf { it.total }
        val taxAmount = taxableAmount * (estimate.taxRate / 100)
        val discountAmount = when (estimate.discountType) {
            DiscountType.PERCENTAGE -> subtotal * (estimate.discountValue / 100)
            DiscountType.FIXED -> estimate.discountValue
            DiscountType.NONE -> 0.0
        }
        val total = subtotal + taxAmount - discountAmount
        val depositAmount = if (estimate.depositRequired) total * (estimate.depositPercent / 100) else 0.0
        
        return estimate.copy(
            lineItems = itemsWithTotals,
            subtotal = subtotal,
            taxAmount = taxAmount,
            discountAmount = discountAmount,
            total = total,
            depositAmount = depositAmount
        )
    }
    
    private suspend fun generateEstimateNumber(): String {
        val year = LocalDate.now().year
        val prefix = "EST-"
        val max = estimateDao.getMaxNumberForPrefix(prefix) ?: 0
        return "EST-"
    }
}