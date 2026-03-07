package com.debbiedoesit.antigravity.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.debbiedoesit.antigravity.ai.ContractorLLMEngine
import com.debbiedoesit.antigravity.ai.JobSitePhotoEngine
import com.debbiedoesit.antigravity.ai.OCREngine
import com.debbiedoesit.antigravity.ai.YOLODetector
import com.debbiedoesit.antigravity.data.JobSitePhoto
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JobSitePhotoViewModel(application: Application) : AndroidViewModel(application) {

    private val yolo = YOLODetector(application)
    private val ocr = OCREngine()
    private val llm = ContractorLLMEngine(application) // Simplified instantiation
    private val engine = JobSitePhotoEngine(application, yolo, ocr, llm)

    private val _photos = MutableStateFlow<List<JobSitePhoto>>(emptyList())
    val photos = _photos.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    fun processCapturedPhotoFile(file: File) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                if (bitmap != null) {
                    val analyzedPhoto =
                            engine.analyzePhoto(bitmap, file.absolutePath, "DEFAULT_PROJECT")
                    _photos.value = _photos.value + analyzedPhoto
                }
            } catch (e: Exception) {
                Log.e("PhotoViewModel", "Error processing photo file: ${e.message}", e)
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun syncWithGooglePhotos() {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                Log.d("PhotoViewModel", "Syncing with Google Photos...")

                // Functional UX: Open Google Photos if available
                val intent =
                        getApplication<Application>()
                                .packageManager
                                .getLaunchIntentForPackage("com.google.android.apps.photos")
                intent?.let {
                    it.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    getApplication<Application>().startActivity(it)
                }

                kotlinx.coroutines.delay(2000)
            } catch (e: Exception) {
                Log.e("PhotoViewModel", "Google Photos Sync failed", e)
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun syncLocalMediaFolders() {
        viewModelScope.launch {
            // LOGIC: Scan DCIM, Pictures, etc. and add to comprehensive library
            Log.d("PhotoViewModel", "Scanning local media folders...")
        }
    }

    fun processCapturedPhoto(bitmap: Bitmap) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                // Save bitmap to file first
                val fileName = "job_photo_${System.currentTimeMillis()}.jpg"
                val file = File(getApplication<Application>().filesDir, fileName)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }

                val analyzedPhoto =
                        engine.analyzePhoto(bitmap, file.absolutePath, "DEFAULT_PROJECT")
                _photos.value = _photos.value + analyzedPhoto
            } catch (e: Exception) {
                Log.e("PhotoViewModel", "Error processing photo: ${e.message}", e)
            } finally {
                _isProcessing.value = false
            }
        }
    }
}
