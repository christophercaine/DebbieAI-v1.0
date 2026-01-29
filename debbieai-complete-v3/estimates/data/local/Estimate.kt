package com.debbiedoesit.debbieai.estimates.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.debbiedoesit.debbieai.contacts.data.local.Contact
import com.debbiedoesit.debbieai.jobs.data.local.Job
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "estimates",
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
        Index(value = ["createdAt"])
    ]
)
data class Estimate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val contactId: Long? = null,
    val jobId: Long? = null,
    
    val estimateNumber: String = "",
    val title: String,
    val description: String = "",
    
    val status: EstimateStatus = EstimateStatus.DRAFT,
    
    val lineItems: List<LineItem> = emptyList(),
    
    val subtotal: Double = 0.0,
    val taxRate: Double = 0.0,
    val taxAmount: Double = 0.0,
    val discountType: DiscountType = DiscountType.NONE,
    val discountValue: Double = 0.0,
    val discountAmount: Double = 0.0,
    val total: Double = 0.0,
    
    val depositRequired: Boolean = false,
    val depositPercent: Double = 50.0,
    val depositAmount: Double = 0.0,
    
    val validUntil: LocalDate? = null,
    val sentAt: LocalDateTime? = null,
    val viewedAt: LocalDateTime? = null,
    val acceptedAt: LocalDateTime? = null,
    val declinedAt: LocalDateTime? = null,
    
    val customerNotes: String = "",
    val internalNotes: String = "",
    val terms: String = "",
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class LineItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val category: LineItemCategory = LineItemCategory.LABOR,
    val description: String,
    val quantity: Double = 1.0,
    val unit: String = "ea",
    val unitPrice: Double = 0.0,
    val total: Double = 0.0,
    val taxable: Boolean = true,
    val sortOrder: Int = 0
)

enum class EstimateStatus(val displayName: String) {
    DRAFT("Draft"),
    SENT("Sent"),
    VIEWED("Viewed"),
    ACCEPTED("Accepted"),
    DECLINED("Declined"),
    EXPIRED("Expired"),
    CONVERTED("Converted to Job")
}

enum class LineItemCategory(val displayName: String) {
    LABOR("Labor"),
    MATERIALS("Materials"),
    EQUIPMENT("Equipment"),
    SUBCONTRACTOR("Subcontractor"),
    PERMITS("Permits & Fees"),
    DISPOSAL("Disposal"),
    TRAVEL("Travel"),
    OTHER("Other")
}

enum class DiscountType { NONE, PERCENTAGE, FIXED }

data class EstimateWithDetails(
    val estimate: Estimate,
    val contactName: String? = null,
    val jobTitle: String? = null,
    val photoCount: Int = 0
)

data class EstimateSummary(
    val totalEstimates: Int = 0,
    val draftCount: Int = 0,
    val sentCount: Int = 0,
    val acceptedCount: Int = 0,
    val totalValue: Double = 0.0,
    val acceptedValue: Double = 0.0,
    val conversionRate: Double = 0.0
)