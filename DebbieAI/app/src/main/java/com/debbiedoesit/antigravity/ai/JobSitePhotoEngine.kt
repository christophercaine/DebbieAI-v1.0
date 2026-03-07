package com.debbiedoesit.antigravity.ai

import android.content.Context
import android.graphics.Bitmap
import com.debbiedoesit.antigravity.data.JobSitePhoto
import com.debbiedoesit.antigravity.data.PhotoType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

/** Combines YOLO detection + ML Kit OCR + AI tagging into a unified photo processing pipeline. */
class JobSitePhotoEngine(
        private val context: Context,
        private val yoloDetector: YOLODetector,
        private val ocrEngine: OCREngine,
        private val llmEngine: ContractorLLMEngine
) {

    // Full auto-analysis pipeline — runs on every photo saved
    suspend fun analyzePhoto(bitmap: Bitmap, filePath: String, projectId: String): JobSitePhoto =
            withContext(Dispatchers.IO) {

                // Run detection and OCR in parallel
                val detectionsDeferred = async { yoloDetector.detect(bitmap) }
                val ocrDeferred = async { ocrEngine.readText(bitmap) }

                val detections = detectionsDeferred.await()
                val ocrText = ocrDeferred.await()

                // Auto-classify photo type based on detections
                val photoType = classifyPhotoType(detections, ocrText)

                // NEW: Deep AI Analysis via LLM
                val analysisPrompt =
                        """
            Analyze this job site photo data:
            - Objects detected: ${detections.joinToString { it.label }}
            - Text found (OCR): $ocrText
            
            Provide a 1-sentence professional summary for a contractor's log. 
            Focus on what this means for the project.
        """.trimIndent()

                val aiSummary =
                        try {
                            llmEngine.chatSync(analysisPrompt)
                        } catch (e: Exception) {
                            "Analysis unavailable: ${e.message}"
                        }

                JobSitePhoto(
                        filePath = filePath,
                        projectId = projectId,
                        detectedObjects = detections.map { it.label },
                        extractedText =
                                listOf(aiSummary) + ocrText.lines().filter { it.isNotBlank() },
                        photoType = photoType
                )
            }

    // Auto-detect if this is a before/after/damage/material photo
    private fun classifyPhotoType(detections: List<Detection>, ocrText: String): PhotoType {
        val text = ocrText.lowercase()
        if (text.contains("before") || text.contains("existing")) return PhotoType.BEFORE
        if (text.contains("after") || text.contains("complete")) return PhotoType.AFTER

        val labels = detections.map { it.label.lowercase() }
        if (labels.any { it.contains("crack") || it.contains("damage") }) return PhotoType.DAMAGE
        if (labels.any { it.contains("tile") || it.contains("lumber") }) return PhotoType.MATERIAL

        return PhotoType.GENERAL
    }
}
