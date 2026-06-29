package com.teslacam.viewer.model

import android.net.Uri

data class TeslaEventClip(
    val folderName: String,
    val timestamp: Long,
    val category: String, // "Sentry", "Saved", "Recent"
    val eventReason: String, // "sentry_aware_object_detection", "user_interaction_honk", etc.
    val frontUri: Uri?,
    val rearUri: Uri?,
    val leftUri: Uri?,
    val rightUri: Uri?,
    val eventOffsetMs: Long = 60000L // 기본 1분 지점에 이벤트 마커 위치
)

data class TelemetryData(
    val speedKmh: Int = 65,
    val steeringAngle: Float = -12f,
    val throttlePercent: Float = 0.25f,
    val brakeActive: Boolean = false,
    val timestampText: String = "2026-06-29 14:22:15.420"
)
