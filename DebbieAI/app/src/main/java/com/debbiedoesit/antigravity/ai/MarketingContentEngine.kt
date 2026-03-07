package com.debbiedoesit.antigravity.ai

import android.graphics.*
import com.debbiedoesit.antigravity.DeviceTier

/** Marketing Content Engine — Creating polished marketing content from job site photos. */
class MarketingContentEngine(
        private val tier: DeviceTier,
        private val videoEngine: JobSiteVideoEngine,
        private val styleTransfer: StyleTransfer?, // null on LITE
        private val imageGenerator: ImageGenerator?, // null on LITE/STANDARD
        private val llmEngine: ContractorLLMEngine
) {

    // NEW: Generate social media copy for a project
    suspend fun generateSocialCaption(projectName: String, keyFeatures: List<String>): String {
        val featuresString =
                if (keyFeatures.isNotEmpty()) "featuring ${keyFeatures.joinToString(", ")}" else ""
        val prompt =
                """
            Write a high-energy, professional Instagram and Facebook caption for a construction project called "$projectName" $featuresString.
            
            Guidelines:
            - Focus on the quality of craftsmanship and the "Debbie Does It" standard.
            - Include 3-5 relevant hashtags (e.g., #HomeRenovation, #ContractorLife).
            - Add a professional call to action at the end.
            - Keep the total length under 280 characters.
            - Do not use generic placeholders like [Phone Number].
            
            Response format: Just the caption text.
        """.trimIndent()

        return try {
            llmEngine.chatSync(prompt)
        } catch (e: Exception) {
            "Another successful project by the team! $projectName is looking incredible. DM us for a quote! #Construction #BuildDebbie"
        }
    }

    // Create a social media ready video from job photos
    suspend fun createMarketingReel(
            jobPhotos: List<String>,
            reelStyle: ReelStyle,
            outputPath: String
    ) {
        when (reelStyle) {
            ReelStyle.BEFORE_AFTER ->
                    videoEngine.createBeforeAfterReel(
                            jobPhotos.take(jobPhotos.size / 2),
                            jobPhotos.drop(jobPhotos.size / 2),
                            outputPath,
                            "Project Showcase"
                    )
            ReelStyle.TIMELAPSE ->
                    videoEngine.createTimelapse(
                            jobPhotos.first().substringBeforeLast("/"),
                            outputPath
                    )
            ReelStyle.SLIDESHOW -> createBrandedSlideshow(jobPhotos, outputPath)
        }
    }

    // Logo variant generator — works differently by tier
    suspend fun generateLogoVariant(logoBitmap: Bitmap, variationType: LogoVariation): Bitmap =
            when {
                tier == DeviceTier.PRO && imageGenerator != null -> {
                    // Full Stable Diffusion img2img
                    imageGenerator.imageToImage(logoBitmap, variationType.sdPrompt, 0.65f)
                }
                tier >= DeviceTier.STANDARD && styleTransfer != null -> {
                    // Neural style transfer
                    styleTransfer.transferStyle(logoBitmap, variationType.styleReference)
                }
                else -> {
                    // LITE: color palette swap via Palette API — no AI model needed
                    applyColorPaletteSwap(logoBitmap, variationType.primaryColor)
                }
            }

    private fun applyColorPaletteSwap(bitmap: Bitmap, newColor: Int): Bitmap {
        val paint =
                Paint().apply { colorFilter = ColorMatrixColorFilter(hueRotationMatrix(newColor)) }
        val result =
                Bitmap.createBitmap(
                        bitmap.width,
                        bitmap.height,
                        bitmap.config ?: Bitmap.Config.ARGB_8888
                )
        Canvas(result).drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }

    private fun hueRotationMatrix(color: Int): ColorMatrix {
        val matrix = ColorMatrix()
        val r = (color shr 16 and 0xFF) / 255f
        val g = (color shr 8 and 0xFF) / 255f
        val b = (color and 0xFF) / 255f

        // Simple approximation: Rotate hue by adjusting color channels
        // For a more precise rotation, one would convert to HSV and back.
        // This matrix shifts colors towards the target primary color.
        matrix.set(
                floatArrayOf(
                        r,
                        0f,
                        0f,
                        0f,
                        0f,
                        0f,
                        g,
                        0f,
                        0f,
                        0f,
                        0f,
                        0f,
                        b,
                        0f,
                        0f,
                        0f,
                        0f,
                        0f,
                        1f,
                        0f
                )
        )
        return matrix
    }

    private suspend fun createBrandedSlideshow(photos: List<String>, output: String) {
        // Concatenate photos into a branded slideshow with a steady pace
        // The JobSiteVideoEngine already handles scaling and padding.
        videoEngine.createBeforeAfterReel(
                beforePhotos = photos.take(photos.size / 2),
                afterPhotos = photos.drop(photos.size / 2),
                outputPath = output,
                projectName = "Recent Project Highlights"
        )
    }
}

enum class ReelStyle {
    BEFORE_AFTER,
    TIMELAPSE,
    SLIDESHOW
}

enum class LogoVariation(val sdPrompt: String, val styleReference: Any?, val primaryColor: Int) {
    PROFESSIONAL("clean corporate logo, blue tones, minimal", null, Color.BLUE),
    BOLD("bold construction logo, high contrast, strong", null, Color.RED),
    MODERN("modern flat design logo, gradient", null, Color.CYAN),
    CLASSIC("classic tradesman logo, gold and navy", null, Color.YELLOW)
}

/** Neural Style Transfer — STANDARD Tier */
class StyleTransfer(private val context: android.content.Context) {
    fun transferStyle(bitmap: Bitmap, styleRef: Any?): Bitmap {
        // Placeholder for TFLite style transfer
        return bitmap
    }
}

/** Image Generator (Stable Diffusion) — PRO Tier */
class ImageGenerator(private val context: android.content.Context) {

    private var isModelLoaded = false

    fun loadModel(modelPath: String) {
        // MediaPipe Stable Diffusion model loading logic
        // if (DeviceCapabilityDetector.detect(context) == DeviceTier.PRO) { ... }
        isModelLoaded = true
    }

    fun imageToImage(bitmap: Bitmap, prompt: String, strength: Float): Bitmap {
        if (!isModelLoaded) return bitmap
        // Placeholder for MediaPipe Stable Diffusion img2img
        return bitmap
    }
}
