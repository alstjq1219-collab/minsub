package com.teslacam.viewer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.teslacam.viewer.model.TeslaEventClip
import com.teslacam.viewer.ui.HomeScreen
import com.teslacam.viewer.ui.PlayerScreen
import com.teslacam.viewer.ui.theme.TeslaCamViewerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Mock sample clips representing parsed TeslaCam OTG folder
        val mockClips = listOf(
            TeslaEventClip(
                folderName = "2026-06-29_14-22-10",
                timestamp = System.currentTimeMillis(),
                category = "Sentry",
                eventReason = "sentry_aware_object_detection",
                frontUri = null, rearUri = null, leftUri = null, rightUri = null
            ),
            TeslaEventClip(
                folderName = "2026-06-29_10-15-33",
                timestamp = System.currentTimeMillis() - 3600000,
                category = "Saved",
                eventReason = "user_interaction_honk (클락션)",
                frontUri = null, rearUri = null, leftUri = null, rightUri = null
            ),
            TeslaEventClip(
                folderName = "2026-06-28_19-05-12",
                timestamp = System.currentTimeMillis() - 86400000,
                category = "Recent",
                eventReason = "circular_drive_record",
                frontUri = null, rearUri = null, leftUri = null, rightUri = null
            )
        )

        setContent {
            TeslaCamViewerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var selectedClip by remember { mutableStateOf<TeslaEventClip?>(null) }
                    var hasSafPermission by remember { mutableStateOf(false) }

                    // Storage Access Framework Launcher (READ ONLY)
                    val safLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.OpenDocumentTree()
                    ) { uri: Uri? ->
                        uri?.let {
                            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            contentResolver.takePersistableUriPermission(it, takeFlags)
                            hasSafPermission = true
                            Toast.makeText(this, "TeslaCam 읽기 전용 권한 승인됨", Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (selectedClip != null) {
                        PlayerScreen(
                            clip = selectedClip!!,
                            onBack = { selectedClip = null },
                            onExport = {
                                Toast.makeText(this, "안전 백업 모드: 원본 훼손 없이 압축 백업합니다.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        HomeScreen(
                            clips = mockClips,
                            onSelectClip = { selectedClip = it },
                            onRequestSafPermission = { safLauncher.launch(null) }
                        )
                    }
                }
            }
        }
    }
}
