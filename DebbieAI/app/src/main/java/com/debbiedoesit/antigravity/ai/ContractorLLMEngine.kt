package com.debbiedoesit.antigravity.ai

import android.content.Context
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

    // System prompt — baked into every conversation
    private val SYSTEM_PROMPT =
            """
        You are Debbie, an expert AI assistant for contractors and 
        home repair professionals. You specialize in:
        - Writing professional estimates and material lists
        - Identifying building codes and permit requirements
        - Diagnosing construction and repair problems
        - Recommending materials, tools, and techniques
        - Calculating quantities (tile, flooring, paint, lumber)
        Keep answers practical, concise, and field-ready.
        Always include cost ranges when discussing materials.
    """.trimIndent()

    suspend fun load(modelPath: String) =
            withContext(Dispatchers.IO) {
                inference?.close()

                // Try GPU first; fall back to CPU if OpenCL is unsupported on this device
                inference =
                        try {
                            val gpuOpts =
                                    LlmInference.LlmInferenceOptions.builder()
                                            .setModelPath(modelPath)
                                            .setMaxTokens(1024)
                                            .setPreferredBackend(LlmInference.Backend.GPU)
                                            .build()
                            LlmInference.createFromOptions(context, gpuOpts)
                        } catch (gpuError: Exception) {
                            // GPU failed (e.g. missing clSetPerfHintQ) — fall back to CPU
                            val cpuOpts =
                                    LlmInference.LlmInferenceOptions.builder()
                                            .setModelPath(modelPath)
                                            .setMaxTokens(1024)
                                            .setPreferredBackend(LlmInference.Backend.CPU)
                                            .build()
                            LlmInference.createFromOptions(context, cpuOpts)
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
        val fullPrompt = "$SYSTEM_PROMPT\n\nUser: $userMessage\nDebbie:"
        val response = inference?.generateResponse(fullPrompt)
        return response ?: "I'm sorry, I wasn't able to generate a response. Please try again."
    }

    // Quick-access contractor tools
    fun generateEstimate(jobDesc: String, onToken: (String, Boolean) -> Unit) {
        val prompt =
                """$SYSTEM_PROMPT
            Generate a professional itemized estimate for: $jobDesc
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
