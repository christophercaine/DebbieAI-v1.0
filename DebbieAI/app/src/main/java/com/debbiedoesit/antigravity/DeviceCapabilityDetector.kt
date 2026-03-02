package com.debbiedoesit.antigravity

import android.app.ActivityManager
import android.content.Context
import org.tensorflow.lite.gpu.GpuDelegate

/**
 * Universal AI Suite — Architecture v2.0
 * Run this at app startup to gate features based on hardware capabilities.
 */
object DeviceCapabilityDetector {

    fun detect(context: Context): DeviceTier {
        val ramGB = getTotalRamGB(context)
        // val hasGPU = checkGPUDelegate() // We can use this for specific model optimizations
        
        return when {
            ramGB >= 8f -> DeviceTier.PRO
            ramGB >= 4f -> DeviceTier.STANDARD
            else        -> DeviceTier.LITE
        }
    }

    private fun getTotalRamGB(context: Context): Float {
        val mi = ActivityManager.MemoryInfo()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        return mi.totalMem / 1024f / 1024f / 1024f
    }

    /**
     * GPU Delegate — Always Wrap in Try/Catch
     */
    fun checkGPUDelegate(): Boolean = try {
        GpuDelegate().close()
        true
    } catch (e: Exception) {
        false
    }

    fun isFeatureAvailable(feature: DeviceFeature, tier: DeviceTier): Boolean =
        when (feature) {
            DeviceFeature.STABLE_DIFFUSION -> tier == DeviceTier.PRO
            DeviceFeature.DEPTH_MEASUREMENT -> tier >= DeviceTier.STANDARD
            DeviceFeature.AR_MEASUREMENT   -> tier == DeviceTier.PRO
            DeviceFeature.FLOOR_PLAN_AI    -> tier >= DeviceTier.STANDARD
            DeviceFeature.YOLO_DETECTION   -> true  // works on all tiers
            DeviceFeature.OCR              -> true  // works on all tiers
            DeviceFeature.FFMPEG_VIDEO     -> true  // works on all tiers
            DeviceFeature.WHISPER_TINY     -> true  // works on all tiers
            DeviceFeature.STYLE_TRANSFER   -> tier >= DeviceTier.STANDARD
        }
}

enum class DeviceFeature {
    STABLE_DIFFUSION, DEPTH_MEASUREMENT, AR_MEASUREMENT,
    FLOOR_PLAN_AI, YOLO_DETECTION, OCR, FFMPEG_VIDEO,
    WHISPER_TINY, STYLE_TRANSFER
}
