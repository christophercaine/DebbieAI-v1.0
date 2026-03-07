package com.debbiedoesit.antigravity.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.debbiedoesit.antigravity.DeviceCapabilityDetector
import com.debbiedoesit.antigravity.DeviceTier
import com.debbiedoesit.antigravity.ai.*
import com.debbiedoesit.antigravity.ai.ContractorLLMEngine
import com.debbiedoesit.antigravity.ai.JobSiteVideoEngine
import com.debbiedoesit.antigravity.ai.MarketingContentEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarketingViewModel(application: Application) : AndroidViewModel(application) {

    private val videoEngine = JobSiteVideoEngine(application.cacheDir)
    private val llm = ContractorLLMEngine(application)
    private val tier = DeviceCapabilityDetector.detect(application)
    private val styleTransfer =
            if (tier >= DeviceTier.STANDARD) StyleTransfer(application) else null
    private val imageGenerator = if (tier == DeviceTier.PRO) ImageGenerator(application) else null

    private val engine =
            MarketingContentEngine(
                    tier = tier,
                    videoEngine = videoEngine,
                    styleTransfer = styleTransfer,
                    imageGenerator = imageGenerator,
                    llmEngine = llm
            )

    private val _generatedCaption = MutableStateFlow("")
    val generatedCaption = _generatedCaption.asStateFlow()

    private val _generatedLogo = MutableStateFlow<android.graphics.Bitmap?>(null)
    val generatedLogo = _generatedLogo.asStateFlow()

    private val _videoExportStatus = MutableStateFlow("")
    val videoExportStatus = _videoExportStatus.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    fun generateCaption(projectName: String, features: List<String>) {
        viewModelScope.launch {
            _isGenerating.value = true
            _generatedCaption.value = engine.generateSocialCaption(projectName, features)
            _isGenerating.value = false
        }
    }

    fun generateLogoVariation(
            baseLogo: android.graphics.Bitmap,
            variation: com.debbiedoesit.antigravity.ai.LogoVariation
    ) {
        viewModelScope.launch {
            _isGenerating.value = true
            _generatedLogo.value = engine.generateLogoVariant(baseLogo, variation)
            _isGenerating.value = false
        }
    }

    fun createReel(
            photos: List<String>,
            style: com.debbiedoesit.antigravity.ai.ReelStyle,
            projectName: String
    ) {
        viewModelScope.launch {
            _isGenerating.value = true
            _videoExportStatus.value = "Creating reel..."
            try {
                val output =
                        java.io.File(getApplication<Application>().cacheDir, "marketing_reel.mp4")
                                .absolutePath
                engine.createMarketingReel(photos, style, output)
                _videoExportStatus.value = "Reel created: $output"
            } catch (e: Exception) {
                _videoExportStatus.value = "Failed: ${e.message}"
            }
            _isGenerating.value = false
        }
    }
}
