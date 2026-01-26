package com.debbiedoesdetails.app.data.remote

import com.debbiedoesdetails.app.data.local.Contact
import retrofit2.http.*

interface ApiService {
    @GET("health")
    suspend fun healthCheck(): HealthResponse

    @GET("api/contacts")
    suspend fun getAllContacts(): List<Contact>

    @GET("api/contacts/{id}")
    suspend fun getContact(@Path("id") id: Long): Contact

    @POST("api/contacts")
    suspend fun createContact(@Body contact: Contact): Contact

    @PATCH("api/contacts/{id}")
    suspend fun updateContact(@Path("id") id: Long, @Body contact: Contact): Contact

    @DELETE("api/contacts/{id}")
    suspend fun deleteContact(@Path("id") id: Long): DeleteResponse

    @POST("api/interactions")
    suspend fun logInteraction(@Body interaction: InteractionRequest): InteractionResponse

    @GET("api/contacts/{id}/stats")
    suspend fun getContactStats(@Path("id") id: String): ContactStats
}

data class HealthResponse(
    val status: String,
    val timestamp: String,
    val version: String
)

data class DeleteResponse(
    val message: String
)

data class InteractionRequest(
    val contact_id: Long,
    val interaction_type: String,
    val subject: String? = null,
    val content: String? = null,
    val sentiment: String? = null
)

data class InteractionResponse(
    val id: String,
    val contact_id: String,
    val interaction_type: String,
    val created_at: String
)

data class ContactStats(
    val contact: Contact,
    val interaction_count: Int,
    val last_updated: String
)