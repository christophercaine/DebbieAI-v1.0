package com.debbiedoesit.antigravity.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.debbiedoesit.antigravity.DeviceCapabilityDetector
import com.debbiedoesit.antigravity.DeviceTier
import com.debbiedoesit.antigravity.ai.MeasurementEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MeasurementViewModel(application: Application) : AndroidViewModel(application) {

    private val tier = DeviceCapabilityDetector.detect(application)
    private val engine = MeasurementEngine(application, tier)

    private val _result = MutableStateFlow<Float?>(null)
    val result = _result.asStateFlow()

    private val _arDistance = MutableStateFlow<Float?>(null)
    val arDistance = _arDistance.asStateFlow()

    val isArSupported = tier == DeviceTier.PRO

    fun calculateReferenceMeasurement(refReal: Float, refPixels: Float, targetPixels: Float) {
        val calculated =
                engine.measureFromReference(
                        bitmap = null, // Not needed for manual pixel input
                        referenceObjectPixelHeight = refPixels,
                        referenceRealInches = refReal,
                        targetObjectPixelHeight = targetPixels
                )
        _result.value = calculated
    }

    fun updateArDistance(distanceInches: Float) {
        _arDistance.value = if (distanceInches > 0) distanceInches else null
    }

    fun calculateArDistance(pose1: com.google.ar.core.Pose, pose2: com.google.ar.core.Pose): Float {
        return engine.calculateDistanceInches(pose1, pose2)
    }
}
