package com.debbiedoesdetails.app.data.ai

import com.debbiedoesdetails.app.data.local.AIContactAnalysis
import com.debbiedoesdetails.app.data.local.Contact
import com.debbiedoesdetails.app.data.local.ContactCategory
import com.debbiedoesdetails.app.data.local.ContactTags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * AI Service for smart contact features
 * Uses Claude API for intelligent analysis
 */
class AIService(
    private val apiKey: String = "" // Set your Claude API key
) {
    
    companion object {
        private const val CLAUDE_API_URL = "https://api.anthropic.com/v1/messages"
        private const val MODEL = "claude-sonnet-4-20250514"
    }
    
    /**
     * Analyze a contact and suggest category, tags, and insights
     */
    suspend fun analyzeContact(contact: Contact): AIContactAnalysis = withContext(Dispatchers.IO) {
        // If no API key, use local heuristics
        if (apiKey.isEmpty()) {
            return@withContext analyzeContactLocally(contact)
        }
        
        try {
            val prompt = buildAnalysisPrompt(contact)
            val response = callClaudeAPI(prompt)
            parseAnalysisResponse(response)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to local analysis
            analyzeContactLocally(contact)
        }
    }
    
    /**
     * Smart search - convert natural language to search query
     */
    suspend fun parseSearchQuery(query: String, contacts: List<Contact>): List<Contact> = withContext(Dispatchers.IO) {
        val lowerQuery = query.lowercase()
        
        // Natural language patterns
        when {
            // Category searches
            lowerQuery.contains("customer") || lowerQuery.contains("client") -> 
                contacts.filter { it.category == ContactCategory.CUSTOMER || it.contactType == ContactCategory.CUSTOMER }
            
            lowerQuery.contains("lead") -> 
                contacts.filter { it.category == ContactCategory.LEAD }
            
            lowerQuery.contains("vendor") || lowerQuery.contains("supplier") -> 
                contacts.filter { it.category == ContactCategory.VENDOR }
            
            lowerQuery.contains("subcontractor") || lowerQuery.contains("sub") -> 
                contacts.filter { it.category == ContactCategory.SUBCONTRACTOR }
            
            lowerQuery.contains("employee") || lowerQuery.contains("staff") || lowerQuery.contains("team") -> 
                contacts.filter { it.category == ContactCategory.EMPLOYEE }
            
            // Tag searches
            lowerQuery.contains("vip") || lowerQuery.contains("important") -> 
                contacts.filter { ContactTags.VIP in it.tags }
            
            lowerQuery.contains("follow up") || lowerQuery.contains("callback") -> 
                contacts.filter { ContactTags.FOLLOW_UP in it.tags }
            
            lowerQuery.contains("pending") || lowerQuery.contains("estimate") || lowerQuery.contains("quote") -> 
                contacts.filter { ContactTags.PENDING_ESTIMATE in it.tags }
            
            lowerQuery.contains("active") || lowerQuery.contains("current job") -> 
                contacts.filter { ContactTags.ACTIVE_JOB in it.tags }
            
            // Attribute searches
            lowerQuery.contains("no email") || lowerQuery.contains("without email") || lowerQuery.contains("missing email") -> 
                contacts.filter { it.emails.isEmpty() }
            
            lowerQuery.contains("no phone") || lowerQuery.contains("without phone") || lowerQuery.contains("missing phone") -> 
                contacts.filter { it.phones.isEmpty() }
            
            lowerQuery.contains("no company") || lowerQuery.contains("without company") -> 
                contacts.filter { it.company.isEmpty() }
            
            lowerQuery.contains("has notes") || lowerQuery.contains("with notes") -> 
                contacts.filter { it.notes.isNotEmpty() }
            
            // Location searches
            lowerQuery.contains("in ") -> {
                val location = lowerQuery.substringAfter("in ").trim()
                contacts.filter { 
                    it.city.lowercase().contains(location) || 
                    it.state.lowercase().contains(location) ||
                    it.zipCode.contains(location)
                }
            }
            
            // Company searches
            lowerQuery.contains("from ") || lowerQuery.contains("at ") -> {
                val company = lowerQuery.substringAfter(if ("from " in lowerQuery) "from " else "at ").trim()
                contacts.filter { it.company.lowercase().contains(company) }
            }
            
            // Recent searches
            lowerQuery.contains("recent") || lowerQuery.contains("new") || lowerQuery.contains("latest") -> 
                contacts.sortedByDescending { it.createdAt }.take(20)
            
            lowerQuery.contains("oldest") || lowerQuery.contains("first") -> 
                contacts.sortedBy { it.createdAt }.take(20)
            
            // Duplicate searches
            lowerQuery.contains("duplicate") || lowerQuery.contains("dupe") -> 
                contacts.filter { it.isDuplicate }
            
            // Default: standard text search
            else -> {
                contacts.filter { contact ->
                    contact.name.lowercase().contains(lowerQuery) ||
                    contact.company.lowercase().contains(lowerQuery) ||
                    contact.phones.any { it.contains(lowerQuery) } ||
                    contact.emails.any { it.lowercase().contains(lowerQuery) } ||
                    contact.notes.lowercase().contains(lowerQuery) ||
                    contact.tags.any { it.lowercase().contains(lowerQuery) } ||
                    contact.searchKeywords.any { it.lowercase().contains(lowerQuery) }
                }
            }
        }
    }
    
    /**
     * Find potential duplicates using fuzzy matching
     */
    suspend fun findDuplicates(contact: Contact, allContacts: List<Contact>): List<Pair<Contact, Float>> = withContext(Dispatchers.IO) {
        val duplicates = mutableListOf<Pair<Contact, Float>>()
        
        for (other in allContacts) {
            if (other.id == contact.id) continue
            
            val confidence = calculateDuplicateConfidence(contact, other)
            if (confidence > 0.5f) {
                duplicates.add(other to confidence)
            }
        }
        
        duplicates.sortedByDescending { it.second }
    }
    
    /**
     * Calculate duplicate confidence between two contacts
     */
    private fun calculateDuplicateConfidence(a: Contact, b: Contact): Float {
        var score = 0f
        var checks = 0
        
        // Name similarity (fuzzy)
        val nameSimilarity = calculateStringSimilarity(a.name.lowercase(), b.name.lowercase())
        if (nameSimilarity > 0.8f) {
            score += nameSimilarity * 0.4f  // Name is 40% of score
        }
        checks++
        
        // Phone match (normalized)
        val aPhonesNorm = a.phones.map { normalizePhone(it) }.filter { it.isNotEmpty() }
        val bPhonesNorm = b.phones.map { normalizePhone(it) }.filter { it.isNotEmpty() }
        if (aPhonesNorm.any { it in bPhonesNorm }) {
            score += 0.35f  // Phone match is 35% of score
        }
        checks++
        
        // Email match (normalized)
        val aEmailsNorm = a.emails.map { it.lowercase().trim() }.filter { it.isNotEmpty() }
        val bEmailsNorm = b.emails.map { it.lowercase().trim() }.filter { it.isNotEmpty() }
        if (aEmailsNorm.any { it in bEmailsNorm }) {
            score += 0.35f  // Email match is 35% of score
        }
        checks++
        
        // Company similarity (bonus)
        if (a.company.isNotEmpty() && b.company.isNotEmpty()) {
            val companySimilarity = calculateStringSimilarity(a.company.lowercase(), b.company.lowercase())
            if (companySimilarity > 0.8f) {
                score += 0.1f  // Company is bonus 10%
            }
        }
        
        // First + Last name match
        if (a.firstName.isNotEmpty() && b.firstName.isNotEmpty() && 
            a.lastName.isNotEmpty() && b.lastName.isNotEmpty()) {
            if (a.firstName.equals(b.firstName, ignoreCase = true) && 
                a.lastName.equals(b.lastName, ignoreCase = true)) {
                score += 0.2f
            }
        }
        
        // Nickname to name match (Bob = Robert)
        if (isNicknameMatch(a.name, b.name) || isNicknameMatch(a.firstName, b.firstName)) {
            score += 0.15f
        }
        
        return score.coerceIn(0f, 1f)
    }
    
    /**
     * Calculate string similarity using Levenshtein distance
     */
    private fun calculateStringSimilarity(s1: String, s2: String): Float {
        if (s1 == s2) return 1f
        if (s1.isEmpty() || s2.isEmpty()) return 0f
        
        val distance = levenshteinDistance(s1, s2)
        val maxLength = maxOf(s1.length, s2.length)
        return 1f - (distance.toFloat() / maxLength)
    }
    
    /**
     * Levenshtein distance algorithm
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1,      // insertion
                    dp[i - 1][j - 1] + cost // substitution
                )
            }
        }
        
        return dp[s1.length][s2.length]
    }
    
    /**
     * Common nickname mappings
     */
    private val nicknameMap = mapOf(
        "bob" to listOf("robert", "bobby", "rob"),
        "robert" to listOf("bob", "bobby", "rob"),
        "bill" to listOf("william", "will", "billy"),
        "william" to listOf("bill", "will", "billy"),
        "mike" to listOf("michael", "mick", "mikey"),
        "michael" to listOf("mike", "mick", "mikey"),
        "jim" to listOf("james", "jimmy", "jamie"),
        "james" to listOf("jim", "jimmy", "jamie"),
        "joe" to listOf("joseph", "joey"),
        "joseph" to listOf("joe", "joey"),
        "tom" to listOf("thomas", "tommy"),
        "thomas" to listOf("tom", "tommy"),
        "dick" to listOf("richard", "rick", "ricky"),
        "richard" to listOf("dick", "rick", "ricky"),
        "dave" to listOf("david", "davey"),
        "david" to listOf("dave", "davey"),
        "steve" to listOf("stephen", "steven", "stevie"),
        "stephen" to listOf("steve", "steven", "stevie"),
        "chris" to listOf("christopher", "christy"),
        "christopher" to listOf("chris", "christy"),
        "dan" to listOf("daniel", "danny"),
        "daniel" to listOf("dan", "danny"),
        "matt" to listOf("matthew", "matty"),
        "matthew" to listOf("matt", "matty"),
        "jen" to listOf("jennifer", "jenny"),
        "jennifer" to listOf("jen", "jenny"),
        "kate" to listOf("katherine", "kathy", "katie"),
        "katherine" to listOf("kate", "kathy", "katie"),
        "liz" to listOf("elizabeth", "beth", "lizzy"),
        "elizabeth" to listOf("liz", "beth", "lizzy"),
        "meg" to listOf("margaret", "maggie", "peggy"),
        "margaret" to listOf("meg", "maggie", "peggy"),
        "sam" to listOf("samuel", "samantha", "sammy"),
        "alex" to listOf("alexander", "alexandra", "alexa")
    )
    
    private fun isNicknameMatch(name1: String, name2: String): Boolean {
        val n1 = name1.lowercase().split(" ").firstOrNull() ?: return false
        val n2 = name2.lowercase().split(" ").firstOrNull() ?: return false
        
        if (n1 == n2) return true
        
        val n1Nicknames = nicknameMap[n1] ?: emptyList()
        val n2Nicknames = nicknameMap[n2] ?: emptyList()
        
        return n2 in n1Nicknames || n1 in n2Nicknames
    }
    
    private fun normalizePhone(phone: String): String {
        return phone.filter { it.isDigit() }.takeLast(10)
    }
    
    /**
     * Local analysis without API (rule-based)
     */
    private fun analyzeContactLocally(contact: Contact): AIContactAnalysis {
        val category = guessCategory(contact)
        val tags = guessTags(contact)
        val keywords = generateKeywords(contact)
        
        return AIContactAnalysis(
            suggestedCategory = category,
            categoryConfidence = 0.7f,  // Local analysis is less confident
            suggestedTags = tags,
            insights = generateInsights(contact, category, tags),
            searchKeywords = keywords
        )
    }
    
    private fun guessCategory(contact: Contact): String {
        val jobTitle = contact.jobTitle.lowercase()
        val company = contact.company.lowercase()
        val notes = contact.notes.lowercase()
        
        return when {
            // Vendor indicators
            jobTitle.contains("sales") || jobTitle.contains("rep") ||
            company.contains("supply") || company.contains("lumber") ||
            company.contains("hardware") || company.contains("wholesale") ||
            notes.contains("vendor") || notes.contains("supplier") -> ContactCategory.VENDOR
            
            // Subcontractor indicators
            jobTitle.contains("contractor") || jobTitle.contains("plumber") ||
            jobTitle.contains("electrician") || jobTitle.contains("hvac") ||
            jobTitle.contains("painter") || jobTitle.contains("roofer") ||
            company.contains("plumbing") || company.contains("electric") ||
            company.contains("roofing") || company.contains("painting") ||
            notes.contains("subcontractor") || notes.contains("sub ") -> ContactCategory.SUBCONTRACTOR
            
            // Employee indicators
            jobTitle.contains("employee") || jobTitle.contains("worker") ||
            jobTitle.contains("foreman") || jobTitle.contains("crew") ||
            notes.contains("employee") || notes.contains("crew member") -> ContactCategory.EMPLOYEE
            
            // Lead indicators
            notes.contains("lead") || notes.contains("estimate") ||
            notes.contains("quote") || notes.contains("interested") ||
            notes.contains("callback") || notes.contains("potential") -> ContactCategory.LEAD
            
            // Customer indicators
            notes.contains("customer") || notes.contains("client") ||
            notes.contains("job") || notes.contains("project") ||
            notes.contains("invoice") || notes.contains("paid") -> ContactCategory.CUSTOMER
            
            // Default based on contact type or personal
            contact.contactType != ContactCategory.PERSONAL -> contact.contactType
            
            else -> ContactCategory.PERSONAL
        }
    }
    
    private fun guessTags(contact: Contact): List<String> {
        val tags = mutableListOf<String>()
        val notes = contact.notes.lowercase()
        
        if (notes.contains("vip") || notes.contains("important") || notes.contains("priority")) {
            tags.add(ContactTags.VIP)
        }
        if (notes.contains("follow up") || notes.contains("call back") || notes.contains("callback")) {
            tags.add(ContactTags.FOLLOW_UP)
        }
        if (notes.contains("estimate") || notes.contains("quote") || notes.contains("bid")) {
            tags.add(ContactTags.PENDING_ESTIMATE)
        }
        if (notes.contains("active") || notes.contains("current") || notes.contains("ongoing")) {
            tags.add(ContactTags.ACTIVE_JOB)
        }
        if (notes.contains("referral") || notes.contains("referred")) {
            tags.add(ContactTags.REFERRAL)
        }
        if (notes.contains("do not") || notes.contains("don't call") || notes.contains("blocked")) {
            tags.add(ContactTags.DO_NOT_CONTACT)
        }
        
        return tags
    }
    
    private fun generateKeywords(contact: Contact): List<String> {
        val keywords = mutableListOf<String>()
        
        // Add name parts
        keywords.addAll(contact.name.lowercase().split(" "))
        if (contact.firstName.isNotEmpty()) keywords.add(contact.firstName.lowercase())
        if (contact.lastName.isNotEmpty()) keywords.add(contact.lastName.lowercase())
        if (contact.nickname.isNotEmpty()) keywords.add(contact.nickname.lowercase())
        
        // Add company words
        if (contact.company.isNotEmpty()) {
            keywords.addAll(contact.company.lowercase().split(" "))
        }
        
        // Add job title words
        if (contact.jobTitle.isNotEmpty()) {
            keywords.addAll(contact.jobTitle.lowercase().split(" "))
        }
        
        // Add location
        if (contact.city.isNotEmpty()) keywords.add(contact.city.lowercase())
        if (contact.state.isNotEmpty()) keywords.add(contact.state.lowercase())
        
        return keywords.distinct().filter { it.length > 2 }
    }
    
    private fun generateInsights(contact: Contact, category: String, tags: List<String>): String {
        val insights = mutableListOf<String>()
        
        if (contact.emails.isEmpty()) {
            insights.add("Missing email address")
        }
        if (contact.phones.isEmpty()) {
            insights.add("Missing phone number")
        }
        if (contact.company.isEmpty() && category in listOf(ContactCategory.VENDOR, ContactCategory.SUBCONTRACTOR)) {
            insights.add("Consider adding company name")
        }
        if (tags.contains(ContactTags.FOLLOW_UP)) {
            insights.add("Needs follow-up")
        }
        
        return insights.joinToString(". ")
    }
    
    /**
     * Build prompt for Claude API
     */
    private fun buildAnalysisPrompt(contact: Contact): String {
        return """
Analyze this contact for a contractor's CRM and respond with JSON only:

Name: ${contact.name}
Company: ${contact.company}
Job Title: ${contact.jobTitle}
Notes: ${contact.notes}
Phones: ${contact.phones.joinToString(", ")}
Emails: ${contact.emails.joinToString(", ")}
Location: ${contact.city}, ${contact.state}

Categories: Customer, Lead, Vendor, Subcontractor, Employee, Personal, Other
Tags: VIP, Follow Up, Pending Estimate, Active Job, Past Customer, Referral, Do Not Contact, Needs Review

Respond with this exact JSON format:
{
  "category": "suggested category",
  "confidence": 0.0 to 1.0,
  "tags": ["tag1", "tag2"],
  "insights": "brief insight about this contact",
  "keywords": ["search", "keywords"]
}
        """.trimIndent()
    }
    
    /**
     * Call Claude API
     */
    private fun callClaudeAPI(prompt: String): String {
        val url = URL(CLAUDE_API_URL)
        val connection = url.openConnection() as HttpURLConnection
        
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("x-api-key", apiKey)
        connection.setRequestProperty("anthropic-version", "2023-06-01")
        connection.doOutput = true
        
        val requestBody = JSONObject().apply {
            put("model", MODEL)
            put("max_tokens", 500)
            put("messages", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
        }
        
        connection.outputStream.bufferedWriter().use { it.write(requestBody.toString()) }
        
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        connection.disconnect()
        
        return response
    }
    
    /**
     * Parse Claude API response
     */
    private fun parseAnalysisResponse(response: String): AIContactAnalysis {
        return try {
            val json = JSONObject(response)
            val content = json.getJSONArray("content").getJSONObject(0).getString("text")
            val analysisJson = JSONObject(content)
            
            AIContactAnalysis(
                suggestedCategory = analysisJson.optString("category", ContactCategory.PERSONAL),
                categoryConfidence = analysisJson.optDouble("confidence", 0.8).toFloat(),
                suggestedTags = analysisJson.optJSONArray("tags")?.let { arr ->
                    (0 until arr.length()).map { arr.getString(it) }
                } ?: emptyList(),
                insights = analysisJson.optString("insights", ""),
                searchKeywords = analysisJson.optJSONArray("keywords")?.let { arr ->
                    (0 until arr.length()).map { arr.getString(it) }
                } ?: emptyList()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            AIContactAnalysis(
                suggestedCategory = ContactCategory.PERSONAL,
                categoryConfidence = 0.5f,
                suggestedTags = emptyList(),
                insights = "Analysis failed",
                searchKeywords = emptyList()
            )
        }
    }
}
