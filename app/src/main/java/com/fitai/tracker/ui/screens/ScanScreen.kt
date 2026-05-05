package com.fitai.tracker.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.fitai.tracker.data.model.MealType
import com.fitai.tracker.data.model.NutritionInfo
import com.fitai.tracker.ui.components.*
import com.fitai.tracker.ui.theme.*
import com.fitai.tracker.viewmodel.ScanState
import com.fitai.tracker.viewmodel.ScanViewModel
import java.io.File
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onBack: () -> Unit,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val state by viewModel.scanState.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()
    val context = LocalContext.current

    var capturedUri by remember { mutableStateOf<Uri?>(null) }
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var selectedMealType by remember { mutableStateOf(MealType.LUNCH) }

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    val tempImageFile = remember {
        File.createTempFile("scan_", ".jpg", context.cacheDir).also {
            it.deleteOnExit()
        }
    }
    val tempUri = remember {
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempImageFile)
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            capturedUri = it
            viewModel.analyzeImage(it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            capturedUri = tempUri
            viewModel.analyzeImage(tempUri)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(DarkBackground, Color(0xFF0A0A20), DarkBackground))
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScanTopBar(onBack = onBack, onApiKey = { showApiKeyDialog = true })

            when (val currentState = state) {
                is ScanState.Idle -> {
                    IdleScanView(
                        hasCameraPermission = cameraPermission.status.isGranted,
                        onRequestCamera = { cameraPermission.launchPermissionRequest() },
                        onCameraClick = {
                            if (cameraPermission.status.isGranted) cameraLauncher.launch(tempUri)
                            else cameraPermission.launchPermissionRequest()
                        },
                        onGalleryClick = { galleryLauncher.launch("image/*") },
                        capturedUri = capturedUri
                    )
                }

                is ScanState.Analyzing -> {
                    AnalyzingView(imageUri = capturedUri)
                }

                is ScanState.Success -> {
                    SuccessView(
                        nutrition = currentState.nutrition,
                        imageUri = currentState.imageUri,
                        selectedMealType = selectedMealType,
                        onMealTypeChange = { selectedMealType = it },
                        onSave = {
                            viewModel.saveFoodEntry(currentState.nutrition, currentState.imageUri, selectedMealType)
                            onBack()
                        },
                        onRescan = {
                            capturedUri = null
                            viewModel.resetScan()
                        }
                    )
                }

                is ScanState.Error -> {
                    ErrorView(
                        message = currentState.message,
                        onRetry = {
                            capturedUri?.let { viewModel.analyzeImage(it) }
                                ?: viewModel.resetScan()
                        },
                        onApiKey = { showApiKeyDialog = true }
                    )
                }
            }
        }
    }

    if (showApiKeyDialog) {
        ApiKeyDialog(
            currentKey = apiKey,
            onDismiss = { showApiKeyDialog = false },
            onConfirm = { key ->
                viewModel.updateApiKey(key)
                showApiKeyDialog = false
            }
        )
    }
}

@Composable
private fun ScanTopBar(onBack: () -> Unit, onApiKey: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(GojoViolet.copy(0.2f), Color.Transparent)))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, "Back", tint = TextPrimary)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "∞ Infinity Scan",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text("AI Food Analysis", style = MaterialTheme.typography.labelSmall, color = GojoViolet)
        }
        IconButton(onClick = onApiKey) {
            Icon(Icons.Default.Key, "API Key", tint = TextSecondary)
        }
    }
}

@Composable
private fun IdleScanView(
    hasCameraPermission: Boolean,
    onRequestCamera: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    capturedUri: Uri?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        Box(modifier = Modifier.size(140.dp)) {
            GojoCharacter(modifier = Modifier.fillMaxSize(), animated = true)
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "\"Point it at your food and I'll\nanalyze it instantly — Infinity style\"",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Text(
            "— Gojo Satoru",
            style = MaterialTheme.typography.labelSmall,
            color = GojoViolet,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(24.dp))

        capturedUri?.let { uri ->
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Captured meal",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            ScanActionButton(
                icon = Icons.Default.CameraAlt,
                label = "Camera",
                subtitle = "Live photo",
                color = GojoViolet,
                onClick = onCameraClick,
                modifier = Modifier.weight(1f)
            )
            ScanActionButton(
                icon = Icons.Default.PhotoLibrary,
                label = "Gallery",
                subtitle = "Pick photo",
                color = DomainBlue,
                onClick = onGalleryClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, GojoViolet.copy(0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "⚡ What Infinity Scan detects:",
                    style = MaterialTheme.typography.labelLarge,
                    color = GojoViolet
                )
                Spacer(Modifier.height(8.dp))
                listOf(
                    "🔥 Total calories",
                    "🍚 Carbohydrates (g)",
                    "🥩 Protein (g)",
                    "🥑 Fats (g)",
                    "🌾 Fiber (g)",
                    "⚽ Football performance tips"
                ).forEach { item ->
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(item, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ScanActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(1.dp, color.copy(0.4f)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.radialGradient(listOf(color.copy(0.3f), color.copy(0.1f)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(label, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
private fun AnalyzingView(imageUri: Uri?) {
    val infiniteTransition = rememberInfiniteTransition(label = "analyzing")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "spin"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        imageUri?.let { uri ->
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(GojoViolet.copy(0.3f))
                )
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.Center)
                        .rotate(rotation),
                    color = GojoViolet,
                    strokeWidth = 4.dp
                )
            }
            Spacer(Modifier.height(24.dp))
        }

        Box(modifier = Modifier.size(100.dp)) {
            GojoCharacter(modifier = Modifier.fillMaxSize())
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "∞ Analyzing...",
            style = MaterialTheme.typography.headlineSmall,
            color = GojoViolet,
            modifier = Modifier.alpha(pulseAlpha)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Gojo is reading the cursed energy\nof your meal...",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        val scanSteps = listOf(
            "Identifying food items",
            "Calculating macronutrients",
            "Computing caloric content",
            "Generating athlete tips"
        )
        scanSteps.forEachIndexed { i, step ->
            Row(
                modifier = Modifier.padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(12.dp),
                    color = GojoViolet.copy(0.6f + i * 0.1f),
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(10.dp))
                Text(step, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun SuccessView(
    nutrition: NutritionInfo,
    imageUri: Uri,
    selectedMealType: MealType,
    onMealTypeChange: (MealType) -> Unit,
    onSave: () -> Unit,
    onRescan: () -> Unit
) {
    val totalNutrition = if (nutrition.additionalItems.isEmpty()) nutrition else {
        nutrition.copy(
            calories = nutrition.calories + nutrition.additionalItems.sumOf { it.calories.toDouble() }.toFloat(),
            carbs = nutrition.carbs + nutrition.additionalItems.sumOf { it.carbs.toDouble() }.toFloat(),
            protein = nutrition.protein + nutrition.additionalItems.sumOf { it.protein.toDouble() }.toFloat(),
            fat = nutrition.fat + nutrition.additionalItems.sumOf { it.fat.toDouble() }.toFloat(),
            fiber = nutrition.fiber + nutrition.additionalItems.sumOf { it.fiber.toDouble() }.toFloat()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Analysis Complete!", style = MaterialTheme.typography.labelMedium, color = SuccessGreen)
                    }
                }
                Box(modifier = Modifier.size(80.dp).align(Alignment.CenterHorizontally)) {
                    YujiCharacter(modifier = Modifier.fillMaxSize())
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        NutritionResultCard(
            foodName = if (nutrition.additionalItems.isEmpty()) nutrition.foodName
            else "${nutrition.foodName} + ${nutrition.additionalItems.size} more",
            calories = totalNutrition.calories,
            carbs = totalNutrition.carbs,
            protein = totalNutrition.protein,
            fat = totalNutrition.fat,
            fiber = totalNutrition.fiber,
            servingSize = nutrition.servingSize,
            tips = nutrition.tips
        )

        if (nutrition.additionalItems.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(
                "Detected Items",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            val allItems = listOf(nutrition) + nutrition.additionalItems
            allItems.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardElevated)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🍽️", fontSize = 16.sp)
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.foodName, style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                            Text(item.servingSize, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                        Text(
                            "${item.calories.toInt()} kcal",
                            style = MaterialTheme.typography.labelMedium,
                            color = CalorieOrange
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text("Meal Type:", style = MaterialTheme.typography.labelLarge, color = TextSecondary, modifier = Modifier.padding(bottom = 6.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MealType.values().forEach { type ->
                FilterChip(
                    selected = type == selectedMealType,
                    onClick = { onMealTypeChange(type) },
                    label = { Text("${type.emoji} ${type.label}", style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GojoViolet.copy(0.3f),
                        selectedLabelColor = GojoVioletLight,
                        containerColor = DarkCard,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onRescan,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                border = BorderStroke(1.dp, TextSecondary.copy(0.4f))
            ) {
                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Rescan")
            }
            Button(
                onClick = onSave,
                modifier = Modifier.weight(2f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GojoViolet)
            ) {
                Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Save to Diary", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit, onApiKey: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(100.dp)) {
            MegumiCharacter(modifier = Modifier.fillMaxSize())
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Analysis Failed",
            style = MaterialTheme.typography.headlineSmall,
            color = CursedRed
        )
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CursedRed.copy(0.1f)),
            border = BorderStroke(1.dp, CursedRed.copy(0.3f))
        ) {
            Text(
                message,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Megumi says: \"Even I have limits.\nCheck your API key or try again.\"",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onApiKey,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DomainBlue),
                border = BorderStroke(1.dp, DomainBlue.copy(0.5f))
            ) {
                Icon(Icons.Default.Key, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("API Key")
            }
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GojoViolet)
            ) {
                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Try Again")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApiKeyDialog(currentKey: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var key by remember { mutableStateOf(currentKey) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("Claude API Key", color = TextPrimary) },
        text = {
            Column {
                Text(
                    "Enter your Anthropic API key to enable AI food analysis.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text("sk-ant-...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GojoViolet,
                        focusedLabelColor = GojoViolet,
                        cursorColor = GojoViolet,
                        unfocusedTextColor = TextPrimary,
                        focusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(key) },
                colors = ButtonDefaults.buttonColors(containerColor = GojoViolet),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}
