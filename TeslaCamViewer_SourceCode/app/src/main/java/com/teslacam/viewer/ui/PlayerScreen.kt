package com.teslacam.viewer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teslacam.viewer.model.TelemetryData
import com.teslacam.viewer.model.TeslaEventClip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    clip: TeslaEventClip,
    onBack: () -> Unit,
    onExport: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var hudEnabled by remember { mutableStateOf(true) }
    var currentProgress by remember { mutableStateOf(0.35f) } // 0.0 ~ 1.0
    val eventMarkerPosition = 0.5f // 타임라인 중앙 부근 이벤트 발생 지점 (🔴 마커)
    
    // A-B Loop States
    var loopA by remember { mutableStateOf<Float?>(null) }
    var loopB by remember { mutableStateOf<Float?>(null) }
    var isLooping by remember { mutableStateOf(false) }

    // Numeric Seek Dialog
    var showSeekDialog by remember { mutableStateOf(false) }
    var inputMinutes by remember { mutableStateOf("") }
    var inputSeconds by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(text = clip.folderName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = "이벤트: ${clip.eventReason} (${clip.category})", fontSize = 12.sp, color = Color.LightGray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // HUD Toggle
                    FilterChip(
                        selected = hudEnabled,
                        onClick = { hudEnabled = !hudEnabled },
                        label = { Text("HUD ON") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Export/Share (NO DELETION BUTTON)
                    Button(
                        onClick = onExport,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("안전 백업/공유")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
        ) {
            // 4-Channel Grid Video Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.weight(1f)) {
                        VideoViewPlaceholder("FRONT VIEW", Modifier.weight(1f).fillMaxHeight(), Color(0xFF1A1A1A))
                        VideoViewPlaceholder("REAR VIEW", Modifier.weight(1f).fillMaxHeight(), Color(0xFF222222))
                    }
                    Row(modifier = Modifier.weight(1f)) {
                        VideoViewPlaceholder("LEFT REPEATER", Modifier.weight(1f).fillMaxHeight(), Color(0xFF222222))
                        VideoViewPlaceholder("RIGHT REPEATER", Modifier.weight(1f).fillMaxHeight(), Color(0xFF1A1A1A))
                    }
                }

                // HUD Overlay
                if (hudEnabled) {
                    TelemetryHudOverlay(
                        telemetry = TelemetryData(),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }

            // Quick Jump & Event Notice Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A1A))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📯 클락션/이벤트 감지됨", color = Color(0xFFFF5252), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("(로딩 시 1분 전 자동 이동 완료)", color = Color.LightGray, fontSize = 11.sp)
                }
                Row {
                    Button(
                        onClick = { currentProgress = maxOf(0f, eventMarkerPosition - 0.1f) }, // -1 min jump
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("⏪ 이벤트 -1분", fontSize = 12.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { currentProgress = eventMarkerPosition }, // direct jump
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("🔴 이벤트 시점", fontSize = 12.sp, color = Color.White)
                    }
                }
            }

            // Custom Timeline with Red Event Marker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Slider(
                    value = currentProgress,
                    onValueChange = { currentProgress = it },
                    modifier = Modifier.fillMaxWidth()
                )
                // Red Event Marker Pin overlay on Slider
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth(eventMarkerPosition)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterRight)
                            .size(width = 4.dp, height = 24.dp)
                            .background(Color.Red)
                    )
                }
            }

            // Transport Controller Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Numeric Seek Trigger
                OutlinedButton(
                    onClick = { showSeekDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("이동: 04:15")
                }

                // A-B Repeat Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { loopA = currentProgress },
                        colors = ButtonDefaults.buttonColors(containerColor = if (loopA != null) Color(0xFF4CAF50) else Color.DarkGray)
                    ) { Text("A") }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = { loopB = currentProgress },
                        colors = ButtonDefaults.buttonColors(containerColor = if (loopB != null) Color(0xFF4CAF50) else Color.DarkGray)
                    ) { Text("B") }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = { isLooping = !isLooping }) {
                        Icon(
                            Icons.Default.Repeat,
                            contentDescription = "Loop",
                            tint = if (isLooping) Color(0xFF4CAF50) else Color.Gray
                        )
                    }
                }

                // Play / Pause
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.Black
                    )
                }

                // Granular Speed Control Slider (0.25x ~ 4.0x)
                var showSpeedMenu by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(
                        onClick = { showSpeedMenu = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("${String.format("%.1f", playbackSpeed)}x 배속")
                    }
                    DropdownMenu(
                        expanded = showSpeedMenu,
                        onDismissRequest = { showSpeedMenu = false },
                        modifier = Modifier.background(Color(0xFF222222))
                    ) {
                        listOf(0.25f, 0.5f, 0.8f, 1.0f, 1.2f, 1.5f, 2.0f, 4.0f).forEach { speed ->
                            DropdownMenuItem(
                                text = { Text("${speed}x ${if (speed == 0.25f) "(슬로모션)" else ""}", color = Color.White) },
                                onClick = {
                                    playbackSpeed = speed
                                    showSpeedMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSeekDialog) {
        AlertDialog(
            onDismissRequest = { showSeekDialog = false },
            title = { Text("숫자 직접 입력 이동") },
            text = {
                Column {
                    Text("이동할 시간을 입력하세요 (분 : 초)")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        OutlinedTextField(
                            value = inputMinutes,
                            onValueChange = { inputMinutes = it },
                            label = { Text("분") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = inputSeconds,
                            onValueChange = { inputSeconds = it },
                            label = { Text("초") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showSeekDialog = false }) { Text("점프") }
            },
            dismissButton = {
                TextButton(onClick = { showSeekDialog = false }) { Text("취소") }
            }
        )
    }
}

@Composable
fun VideoViewPlaceholder(label: String, modifier: Modifier = Modifier, bgColor: Color) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(1.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
