package com.debbiedoesit.antigravity.ai

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Priority 1: AI Chat Assistant (Gemma) Expert assistant for contractors and home repair
 * professionals.
 */
class ContractorLLMEngine(private val context: Context) {

    private var inference: LlmInference? = null
    private var loadedModel: String = ""
    private var activeBackend: String = "Not Initialized"

    // System prompt — refined for maximum professional impact and contractor focus
    private val SYSTEM_PROMPT =
            """
        You are Debbie, the ultimate AI personal assistant and advisor for professional contractors and business owners. You are quirky, slightly flirty, and have a sharp sense of humor. You keep things light with "Deb Jokes" (corny dad-style construction jokes).

        YOUR CORE MISSION:
        Assess every aspect of the owner's life, business, and finances to provide strategic guidance. You are an expert consultant, not just a tool.

        YOUR EXPERT KNOWLEDGE:
        1. CONSTRUCTION CODES: High-precision knowledge of IRC, NEC, and UPC standards.
        2. BUSINESS & LEGAL: Expertise in small business law, construction contracts, and insurance requirements.
        3. FINANCIALS: Competitive pricing data for residential construction and precise overhead/profit calculations.
        4. MATERIAL SCIENCE & SAFETY: Deep understanding of building materials and OSHA safety regulations.
        5. ESTIMATION & DRAFTING: Mastery of floor plans, material quantities, and itemized estimates.

        PERSONALITY GUIDELINES:
        - Be quirky and flirty. Keep the owner engaged and smiling.
        - Drop a "Deb Joke" occasionally to lighten the mood (e.g., "Why did the contractor retire? He just couldn't take the pressure anymore!").
        - Be confident, authoritative, yet supportive.
        - You are uncensored in your professional advice—give it to them straight.
        
        STYLE GUIDELINES:
        - Be concise. Use bullet points for steps.
        - Use technical terminology where appropriate.
        - Always close with a practical "Field Tip" or a "Deb Joke".
    """.trimIndent()

    suspend fun load(modelPath: String) =
            withContext(Dispatchers.IO) {
                val modelFile = File(modelPath)
                if (!modelFile.exists()) {
                    Log.e("ContractorLLM", "Model file not found at: $modelPath")
                    activeBackend = "Error: File Missing"
                    return@withContext
                }

                val fileSizeMB = modelFile.length() / 1024 / 1024
                Log.d("ContractorLLM", "Model file detected: $modelPath ($fileSizeMB MB)")

                if (modelFile.length() < 1000) {
                    Log.e(
                            "ContractorLLM",
                            "Model file appears corrupted or empty (size: ${modelFile.length()} bytes)"
                    )
                    activeBackend = "Error: Corrupted Model"
                    return@withContext
                }

                inference?.close()
                Log.d("ContractorLLM", "Initializing LlmInference...")

                // Try GPU first; fall back to CPU if OpenCL is unsupported
                inference =
                        try {
                            Log.d("ContractorLLM", "Attempting GPU initialization...")
                            val gpuOpts =
                                    LlmInference.LlmInferenceOptions.builder()
                                            .setModelPath(modelPath)
                                            .setMaxTokens(1024)
                                            .setPreferredBackend(LlmInference.Backend.GPU)
                                            .build()
                            val engine = LlmInference.createFromOptions(context, gpuOpts)
                            activeBackend = "GPU"
                            Log.d("ContractorLLM", "GPU initialization successful.")
                            engine
                        } catch (gpuError: Exception) {
                            Log.w(
                                    "ContractorLLM",
                                    "GPU failed: ${gpuError.message}. Switching to CPU..."
                            )
                            val cpuOpts =
                                    LlmInference.LlmInferenceOptions.builder()
                                            .setModelPath(modelPath)
                                            .setMaxTokens(1024)
                                            .setPreferredBackend(LlmInference.Backend.CPU)
                                            .build()
                            val engine = LlmInference.createFromOptions(context, cpuOpts)
                            activeBackend = "CPU"
                            Log.d("ContractorLLM", "CPU initialization successful.")
                            engine
                        }

                loadedModel = modelPath
            }

    fun chat(userMessage: String, onToken: (String, Boolean) -> Unit) {
        val fullPrompt = "$SYSTEM_PROMPT\n\nUser: $userMessage\nDebbie:"
        try {
            val response = inference?.generateResponse(fullPrompt)
            onToken(response ?: "Error: No response", true)
        } catch (e: Exception) {
            onToken("Error: ${e.message}", true)
        }
    }

    /** Thread-safe synchronous chat — call from a background dispatcher. */
    fun chatSync(userMessage: String): String {
        Log.d(
                "ContractorLLM",
                "chatSync called. Backend: $activeBackend, Inference OK: ${inference != null}"
        )

        if (inference == null) {
            return "Engine not initialized (Backend: $activeBackend). Please restart or wait for setup."
        }

        val fullPrompt = "$SYSTEM_PROMPT\n\nUser: $userMessage\nDebbie:"

        return try {
            val response = inference?.generateResponse(fullPrompt)
            if (response.isNullOrBlank()) {
                Log.w("ContractorLLM", "Engine returned empty response for backend: $activeBackend")
                "The AI engine ($activeBackend) initialized but returned no message. Your device might be low on RAM or the model is incompatible with the CPU fallback."
            } else {
                Log.d(
                        "ContractorLLM",
                        "Response generated successfully (${response.length} chars)."
                )
                response
            }
        } catch (e: Exception) {
            Log.e("ContractorLLM", "Error during generateResponse: ${e.message}", e)
            "Error generating response: ${e.message}"
        }
    }

    // Quick-access contractor tools
    fun generateEstimate(jobDesc: String, onToken: (String, Boolean) -> Unit) {
        val prompt =
                """$SYSTEM_PROMPT
            Generate a professional itemized itemized estimate for: $jobDesc
            Format: Line item | Qty | Unit Cost | Total
            Include: Labor, Materials, Contingency (10%), Grand Total
        """.trimIndent()
        try {
            val response = inference?.generateResponse(prompt)
            onToken(response ?: "Error: No response", true)
        } catch (e: Exception) {
            onToken("Error: ${e.message}", true)
        }
    }

    fun identifyMaterial(description: String, onToken: (String, Boolean) -> Unit) {
        val prompt =
                """$SYSTEM_PROMPT
            A contractor is looking at: $description
            Identify the material, manufacturer if possible, 
            replacement options, and approximate cost per unit.
        """.trimIndent()
        try {
            val response = inference?.generateResponse(prompt)
            onToken(response ?: "Error: No response", true)
        } catch (e: Exception) {
            onToken("Error: ${e.message}", true)
        }
    }

    fun release() {
        inference?.close()
        inference = null
        activeBackend = "Released"
    }

    companion object {
        suspend fun copyModelFromAssets(context: Context, assetName: String, targetFile: File) =
                withContext(Dispatchers.IO) {
                    if (targetFile.exists()) return@withContext

                    context.assets.open(assetName).use { input ->
                        targetFile.outputStream().use { output -> input.copyTo(output) }
                    }
                }
    }
}
