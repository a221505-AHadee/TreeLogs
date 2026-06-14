package com.example.a221505_cikgu_izwan_plantlogs

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import java.io.File
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.a221505_cikgu_izwan_plantlogs.data.HealthEntity
import com.example.a221505_cikgu_izwan_plantlogs.data.PlantEntity
import com.example.a221505_cikgu_izwan_plantlogs.ui.theme.*
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import java.util.concurrent.Executors

// ── ROUTES ───────────────────────────────────────────────
object Routes {
    const val HOME         = "home"
    const val ADD_PLANT    = "add_plant"
    const val PLANT_LIST   = "plant_list"
    const val PLANT_DETAIL = "plant_detail"
    const val HEALTH_CHECK = "health_check"
    const val TIPS         = "tips"
    const val SCAN         = "scan"          // NEW Screen 7
    const val COMMUNITY    = "community"     // NEW Screen 8
}

// ── MAIN ACTIVITY ─────────────────────────────────────────
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantLogTheme {
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                    PlantLogsApp()
                }
            }
        }
    }
}

// ── ROOT ─────────────────────────────────────────────────
@Composable
fun PlantLogsApp() {
    val navController = rememberNavController()
    val viewModel: PlantLogsViewModel = viewModel(
        factory = PlantLogsViewModelFactory(
            (LocalContext.current.applicationContext as PlantLogsApplication).repository
        )
    )

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME)         { HomeScreen(navController, viewModel) }
        composable(Routes.ADD_PLANT)    { AddPlantScreen(navController, viewModel) }
        composable(Routes.PLANT_LIST)   { PlantListScreen(navController, viewModel) }
        composable(Routes.PLANT_DETAIL) { PlantDetailScreen(navController, viewModel) }
        composable(Routes.HEALTH_CHECK) { HealthCheckScreen(navController, viewModel) }
        composable(Routes.TIPS)         { TipsScreen(navController) }
        composable(Routes.SCAN)         { ScanScreen(navController, viewModel) }
        composable(Routes.COMMUNITY)    { CommunityScreen(navController, viewModel) }
    }
}

// ── REUSABLE COMPOSABLES ──────────────────────────────────

@Composable
fun TopBar(title: String, navController: NavController, onBack: (() -> Unit)? = null) {
    Row(modifier = Modifier.fillMaxWidth().background(PLHeroGreen)
        .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            onBack?.invoke()
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.HOME) { inclusive = true }
            }
        }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PLLight)
        }
        Text(title, style = MaterialTheme.typography.titleMedium, color = PLLight)
    }
}

@Composable
fun FeatureBtn(label: String, sublabel: String, icon: ImageVector, isFilled: Boolean, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isFilled) PLHeroGreen
            else MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp), onClick = onClick) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                .background(if (isFilled) PLWhiteOverlay else PLLight),
                contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = if (isFilled) PLWhite else PLMidGreen,
                    modifier = Modifier.size(26.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.bodyLarge,
                    color = if (isFilled) PLWhite
                    else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium)
                Text(sublabel, style = MaterialTheme.typography.bodySmall,
                    color = if (isFilled) PLAccent else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Filled.KeyboardArrowRight, null,
                tint = if (isFilled) PLLight else PLGrayText, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = PLHeroGreen.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium)
        Text(value.ifBlank { "—" }, style = MaterialTheme.typography.bodyLarge,
            color = PLHeroGreen, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun BotNavItem(label: String, icon: ImageVector, isActive: Boolean, onClick: () -> Unit = {}) {
    Column(modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Icon(icon, label, tint = if (isActive) MaterialTheme.colorScheme.primary else PLGrayText,
            modifier = Modifier.size(22.dp))
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = if (isActive) MaterialTheme.colorScheme.primary else PLGrayText,
            fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
            textAlign = TextAlign.Center)
    }
}

// ── HELPER — Generate QR Bitmap ───────────────────────────
fun generateQrBitmap(text: String, size: Int = 512): Bitmap? {
    return try {
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            text, BarcodeFormat.QR_CODE, size, size
        )
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y,
                    if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) { null }
}

// ── HELPER — Full Screen Image Viewer Dialog ──────────────
@Composable
fun FullScreenImageViewer(imagePath: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.95f))
            .clickable { onDismiss() },
            contentAlignment = Alignment.Center) {
            AsyncImage(
                model = File(imagePath),
                contentDescription = "Full screen photo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
            IconButton(onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).statusBarsPadding()) {
                Icon(Icons.Filled.Close, "Close", tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }
    }
}

// ── HELPER — Multi Image Picker + Gallery Row ─────────────
@Composable
fun MultiImagePicker(
    imageUris: String,
    onUpdate: (String) -> Unit,
    label: String = "Photos"
) {
    val context = LocalContext.current
    val images = remember(imageUris) {
        if (imageUris.isBlank()) emptyList()
        else imageUris.split("|").filter { it.isNotBlank() }
    }
    var fullScreenImage by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val filename = "img_${System.currentTimeMillis()}.jpg"
            val savedPath = copyImageToInternalStorage(context, it, filename)
            if (savedPath != null) {
                val updated = (images + savedPath).joinToString("|")
                onUpdate(updated)
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("$label (${images.size})", style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface)

        if (images.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                images.forEach { path ->
                    Box(modifier = Modifier.size(90.dp)) {
                        AsyncImage(
                            model = File(path),
                            contentDescription = "Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.small)
                                .clickable { fullScreenImage = path }
                        )
                        IconButton(
                            onClick = { onUpdate(images.filter { it != path }.joinToString("|")) },
                            modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Filled.Close, "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }
                // Add more button
                Box(modifier = Modifier.size(90.dp).clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.AddAPhoto, "Add photo", tint = PLMidGreen, modifier = Modifier.size(28.dp))
                }
            }
        } else {
            OutlinedButton(onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small) {
                Icon(Icons.Filled.AddAPhoto, null, modifier = Modifier.size(18.dp), tint = PLMidGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Photos from Gallery", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }

    fullScreenImage?.let { path ->
        FullScreenImageViewer(imagePath = path, onDismiss = { fullScreenImage = null })
    }
}

// ── HELPER — Image Gallery Row (read-only, for detail views) ──
// Shows photos with tap-to-fullscreen, no remove button
@Composable
fun ImageGalleryRow(imageUris: String) {
    val images = remember(imageUris) {
        if (imageUris.isBlank()) emptyList()
        else imageUris.split("|").filter { it.isNotBlank() && File(it).exists() }
    }
    var fullScreenImage by remember { mutableStateOf<String?>(null) }

    if (images.isNotEmpty()) {
        if (images.size == 1) {
            AsyncImage(
                model = File(images[0]),
                contentDescription = "Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(200.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { fullScreenImage = images[0] }
            )
        } else {
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                images.forEach { path ->
                    AsyncImage(
                        model = File(path),
                        contentDescription = "Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(150.dp).clip(MaterialTheme.shapes.medium)
                            .clickable { fullScreenImage = path }
                    )
                }
            }
        }
    }

    fullScreenImage?.let { path ->
        FullScreenImageViewer(imagePath = path, onDismiss = { fullScreenImage = null })
    }
}

// ════════════════════════════════════════════════════════
//  S1 — HOME SCREEN
// ════════════════════════════════════════════════════════
@Composable
fun HomeScreen(navController: NavController, viewModel: PlantLogsViewModel) {
    var userName    by remember { mutableStateOf("") }
    val displayName = viewModel.displayName
    val plants       by viewModel.plantList.collectAsState()
    val healthChecks by viewModel.healthList.collectAsState()

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {

        Row(modifier = Modifier.fillMaxWidth().background(PLHeroGreen)
            .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp))
                .background(PLLight), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Star, null, tint = PLMidGreen, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("PlantLogs — Hello, $displayName!",
                    style = MaterialTheme.typography.titleMedium, color = PLLight)
                Text("A221505 — SDG 15 Life on Land",
                    style = MaterialTheme.typography.bodySmall, color = PLAccent)
            }
        }

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Update your name", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                    OutlinedTextField(value = userName, onValueChange = { userName = it },
                        label = { Text("Your name", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))
                    Button(onClick = { if (userName.isNotBlank()) { viewModel.updateDisplayName(userName); userName = "" } },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.small) {
                        Text("Update Greeting", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = PLLight),
                    elevation = CardDefaults.cardElevation(3.dp)) {
                    Column(modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${plants.size}", fontSize = 32.sp, fontWeight = FontWeight.Bold,
                            color = PLHeroGreen)
                        Text("Plants Logged", style = MaterialTheme.typography.bodySmall, color = PLHeroGreen.copy(alpha = 0.7f))
                    }
                }
                Card(modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(3.dp)) {
                    Column(modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${healthChecks.size}", fontSize = 32.sp, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface)
                        Text("Health Checks", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Text("Main Features", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)

            FeatureBtn("Log New Plant",    "Add and generate QR for plant",      Icons.Filled.Add,       true)  { navController.navigate(Routes.ADD_PLANT) }
            FeatureBtn("Plant List",       "View and manage all plants",          Icons.Filled.List,      false) { navController.navigate(Routes.PLANT_LIST) }
            FeatureBtn("Scan QR",          "Scan plant QR to view full info",     Icons.Filled.QrCodeScanner, false) { navController.navigate(Routes.SCAN) }
            FeatureBtn("Health Check",     "Log plant health status",             Icons.Filled.Favorite,  false) { navController.navigate(Routes.HEALTH_CHECK) }
            FeatureBtn("Community",        "Share and view community plants",     Icons.Filled.People,    false) { navController.navigate(Routes.COMMUNITY) }
            FeatureBtn("Guide & Tips",     "SDG 15 info and plant care tips",     Icons.Filled.Info,      false) { navController.navigate(Routes.TIPS) }

            // Clear all data — useful for testing/demo with a clean database
            var showClearConfirm by remember { mutableStateOf(false) }
            OutlinedButton(onClick = { showClearConfirm = true },
                modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Icon(Icons.Filled.DeleteForever, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear All Data (Reset)", style = MaterialTheme.typography.bodySmall)
            }
            if (showClearConfirm) {
                AlertDialog(
                    onDismissRequest = { showClearConfirm = false },
                    title = { Text("Clear all data?") },
                    text = { Text("This deletes all plants and health records from this device. This cannot be undone.") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.clearAllData()
                            showClearConfirm = false
                        }) { Text("Clear", color = MaterialTheme.colorScheme.error) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearConfirm = false }) { Text("Cancel") }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
            .border(1.dp, PLBorderColor).navigationBarsPadding()
            .padding(horizontal = 4.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                BotNavItem("Home",      Icons.Filled.Home,           true)
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                BotNavItem("Plants",    Icons.Filled.List,           false) { navController.navigate(Routes.PLANT_LIST) }
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(52.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { navController.navigate(Routes.SCAN) },
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.QrCodeScanner, "Scan", tint = PLWhite, modifier = Modifier.size(26.dp))
                }
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                BotNavItem("Community", Icons.Filled.People,         false) { navController.navigate(Routes.COMMUNITY) }
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                BotNavItem("Tips",      Icons.Filled.Info,           false) { navController.navigate(Routes.TIPS) }
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  S2 — ADD PLANT SCREEN (with GPS + QR generation)
// ════════════════════════════════════════════════════════
@Composable
fun AddPlantScreen(navController: NavController, viewModel: PlantLogsViewModel) {
    val context = LocalContext.current
    val isEditMode = viewModel.editingPlant != null
    val existing   = viewModel.editingPlant

    var name            by remember(existing?.id) { mutableStateOf(existing?.name            ?: "") }
    var species         by remember(existing?.id) { mutableStateOf(existing?.species         ?: "") }
    var location        by remember(existing?.id) { mutableStateOf(existing?.location        ?: "") }
    var latitude        by remember(existing?.id) { mutableStateOf(existing?.latitude?.toString()  ?: "") }
    var longitude       by remember(existing?.id) { mutableStateOf(existing?.longitude?.toString() ?: "") }
    var healthStatus    by remember(existing?.id) { mutableStateOf(existing?.healthStatus    ?: "") }
    var fertiliserType  by remember(existing?.id) { mutableStateOf(existing?.fertiliserType  ?: "") }
    var fertiliserRatio by remember(existing?.id) { mutableStateOf(existing?.fertiliserRatio ?: "") }
    var comment         by remember(existing?.id) { mutableStateOf(existing?.comment         ?: "") }
    var notes           by remember(existing?.id) { mutableStateOf(existing?.notes           ?: "") }
    var imageUris       by remember(existing?.id) { mutableStateOf(existing?.imageUris       ?: "") }

    // If user presses system back without saving, clear edit state
    // so next "Log New Plant" opens a blank form
    BackHandler {
        viewModel.clearEditPlant()
        navController.popBackStack()
    }

    // GPS permission launcher
    val locationPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // Get last known location
            val fusedClient = com.google.android.gms.location.LocationServices
                .getFusedLocationProviderClient(context)
            try {
                fusedClient.lastLocation.addOnSuccessListener { loc ->
                    loc?.let {
                        latitude  = it.latitude.toString()
                        longitude = it.longitude.toString()
                        location  = "${it.latitude}, ${it.longitude}"
                    }
                }
            } catch (e: SecurityException) { }
        }
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar(if (isEditMode) "Edit Plant" else "Log New Plant", navController, onBack = { viewModel.clearEditPlant() })

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            Text(if (isEditMode) "Edit Plant Record" else "Add a New Plant",
                style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text("A221505 — Fill in details. QR auto-generated on save.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    Text("Plant Details", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)

                    OutlinedTextField(value = name, onValueChange = { name = it },
                        label = { Text("Plant Name", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    OutlinedTextField(value = species, onValueChange = { species = it },
                        label = { Text("Species", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    // GPS Location row
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(value = location, onValueChange = { location = it },
                            label = { Text("Location", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.small,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))
                        // GPS auto-fill button
                        IconButton(onClick = {
                            if (ContextCompat.checkSelfPermission(context,
                                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                val fusedClient = com.google.android.gms.location.LocationServices
                                    .getFusedLocationProviderClient(context)
                                try {
                                    fusedClient.lastLocation.addOnSuccessListener { loc ->
                                        loc?.let {
                                            latitude  = it.latitude.toString()
                                            longitude = it.longitude.toString()
                                            location  = "Lat: ${String.format("%.4f", it.latitude)}, Lng: ${String.format("%.4f", it.longitude)}"
                                        }
                                    }
                                } catch (e: SecurityException) { }
                            } else {
                                locationPermLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        }) {
                            Icon(Icons.Filled.LocationOn, "Get GPS", tint = PLMidGreen)
                        }
                    }

                    if (latitude.isNotBlank()) {
                        Text("📍 GPS: $latitude, $longitude",
                            style = MaterialTheme.typography.bodySmall, color = PLMidGreen)
                    }

                    HorizontalDivider(color = PLBorderColor, thickness = 0.5.dp)

                    MultiImagePicker(
                        imageUris = imageUris,
                        onUpdate  = { imageUris = it },
                        label     = "Plant Photos"
                    )

                    HorizontalDivider(color = PLBorderColor, thickness = 0.5.dp)
                    Text("Health & Care", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)

                    OutlinedTextField(value = healthStatus, onValueChange = { healthStatus = it },
                        label = { Text("Health Status", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    OutlinedTextField(value = fertiliserType, onValueChange = { fertiliserType = it },
                        label = { Text("Fertiliser Type (e.g. NPK)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    OutlinedTextField(value = fertiliserRatio, onValueChange = { fertiliserRatio = it },
                        label = { Text("Fertiliser Ratio (e.g. 10:5:5)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    OutlinedTextField(value = comment, onValueChange = { comment = it },
                        label = { Text("Comment", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    OutlinedTextField(value = notes, onValueChange = { notes = it },
                        label = { Text("Notes", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(), minLines = 2, shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    Button(
                        onClick = {
                            if (name.isNotBlank() && species.isNotBlank()) {
                                val lat = latitude.toDoubleOrNull() ?: 0.0
                                val lng = longitude.toDoubleOrNull() ?: 0.0
                                if (isEditMode && existing != null) {
                                    viewModel.updatePlant(existing, name, species, location,
                                        lat, lng, healthStatus, fertiliserType,
                                        fertiliserRatio, comment, notes, imageUris)
                                    viewModel.clearEditPlant()
                                } else {
                                    viewModel.addPlant(name, species, location,
                                        lat, lng, healthStatus, fertiliserType,
                                        fertiliserRatio, comment, notes, imageUris)
                                }
                                navController.navigate(Routes.PLANT_LIST)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled  = name.isNotBlank() && species.isNotBlank(),
                        colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape    = MaterialTheme.shapes.small) {
                        Text(if (isEditMode) "Update Plant" else "Save Plant + Generate QR",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary)
                    }

                    // Cancel — always clears edit state so "Log New Plant"
                    // from Home opens a blank form next time
                    OutlinedButton(onClick = {
                        viewModel.clearEditPlant()
                        navController.popBackStack()
                    }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small) {
                        Text("Cancel", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  S3 — PLANT LIST SCREEN
// ════════════════════════════════════════════════════════
@Composable
fun PlantListScreen(navController: NavController, viewModel: PlantLogsViewModel) {
    val plants by viewModel.plantList.collectAsState()

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar("Plant List (${plants.size})", navController)

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

            Text("All Logged Plants", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text("A221505 — Tap to view detail | Long press QR to share",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (plants.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.List, null, tint = PLGrayText, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No plants logged yet.", style = MaterialTheme.typography.bodyLarge,
                            color = PLGrayText, textAlign = TextAlign.Center)
                    }
                }
            } else {
                plants.forEachIndexed { index, plant ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.clickable {
                                viewModel.selectPlant(plant)
                                navController.navigate(Routes.PLANT_DETAIL)
                            }, verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                val firstImage = plant.getImageList().firstOrNull()
                                if (firstImage != null && File(firstImage).exists()) {
                                    AsyncImage(
                                        model = File(firstImage),
                                        contentDescription = "Photo of ${plant.name}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                                    )
                                } else {
                                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center) {
                                        Text("#${index + 1}", style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold)
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(plant.name, style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                                    Text(plant.species, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("🔖 ${plant.plantCode}", style = MaterialTheme.typography.bodySmall, color = PLGrayText)
                                    if (plant.location.isNotBlank())
                                        Text("📍 ${plant.location}", style = MaterialTheme.typography.bodySmall, color = PLGrayText)
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = {
                                    viewModel.startEditPlant(plant)
                                    navController.navigate(Routes.ADD_PLANT)
                                }) {
                                    Icon(Icons.Filled.Edit, null, modifier = Modifier.size(14.dp), tint = PLMidGreen)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit", style = MaterialTheme.typography.bodySmall, color = PLMidGreen)
                                }
                                TextButton(onClick = { viewModel.deletePlant(plant) }) {
                                    Icon(Icons.Filled.Delete, null, modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Delete", style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = { navController.navigate(Routes.ADD_PLANT) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.small) {
                Text("+ Log New Plant", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  S4 — PLANT DETAIL SCREEN
//  Shows QR code + weather
// ════════════════════════════════════════════════════════
@Composable
fun PlantDetailScreen(navController: NavController, viewModel: PlantLogsViewModel) {
    val plant = viewModel.selectedPlant
    val context = LocalContext.current

    // Fetch weather when screen opens
    LaunchedEffect(plant) {
        plant?.let {
            viewModel.fetchWeather(it.latitude, it.longitude)
        }
    }

    val weather     = viewModel.weatherData
    val shareStatus = viewModel.shareStatus

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar("Plant Detail", navController)

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

            Text("Plant Information", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text("A221505 — Full plant record from Room DB",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (plant == null) {
                Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No plant selected.", style = MaterialTheme.typography.bodyLarge,
                            color = PLGrayText, textAlign = TextAlign.Center)
                    }
                }
            } else {
                // Plant photos — if available, with full-screen tap and multi-image support
                if (plant.imageUris.isNotBlank()) {
                    ImageGalleryRow(imageUris = plant.imageUris)
                }

                // Plant info card — fixed light green background (matches homepage style)
                // for consistent contrast in both light and dark mode
                Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = PLLight),
                    elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp))
                                .background(PLHeroGreen.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Star, null, tint = PLHeroGreen, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text("Plant Record", style = MaterialTheme.typography.titleSmall,
                                    color = PLHeroGreen)
                                Text("Code: ${plant.plantCode}", style = MaterialTheme.typography.bodySmall,
                                    color = PLHeroGreen.copy(alpha = 0.7f))
                            }
                        }
                        HorizontalDivider(color = PLHeroGreen.copy(alpha = 0.25f), thickness = 0.5.dp)
                        DetailRow("Plant Name",      plant.name)
                        DetailRow("Species",         plant.species)
                        DetailRow("Location",        plant.location)
                        DetailRow("Health Status",   plant.healthStatus)
                        DetailRow("Fertiliser Type", plant.fertiliserType)
                        DetailRow("Fertiliser Ratio",plant.fertiliserRatio)
                        DetailRow("Comment",         plant.comment)
                        DetailRow("Notes",           plant.notes)
                        if (plant.latitude != 0.0) {
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier.clickable {
                                    val uri = android.net.Uri.parse(
                                        "geo:${plant.latitude},${plant.longitude}?q=${plant.latitude},${plant.longitude}(${plant.name})")
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                                    intent.setPackage("com.google.android.apps.maps")
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // Fallback — open in browser if Maps app not installed
                                        val webUri = android.net.Uri.parse(
                                            "https://www.google.com/maps/search/?api=1&query=${plant.latitude},${plant.longitude}")
                                        context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, webUri))
                                    }
                                }) {
                                Text("GPS", style = MaterialTheme.typography.bodySmall, color = PLHeroGreen.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium)
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Lat: ${String.format("%.4f",plant.latitude)}, Lng: ${String.format("%.4f",plant.longitude)}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = PLHeroGreen, fontWeight = FontWeight.SemiBold)
                                    Icon(Icons.Filled.Map, "Open in Maps", tint = PLHeroGreen,
                                        modifier = Modifier.size(18.dp))
                                }
                                Text("Tap to view in Google Maps", style = MaterialTheme.typography.labelSmall,
                                    color = PLHeroGreen.copy(alpha = 0.7f))
                            }
                        }
                    }
                }

                // Edit button — quick access
                Button(onClick = {
                    viewModel.startEditPlant(plant)
                    navController.navigate(Routes.ADD_PLANT)
                }, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PLMidGreen),
                    shape = MaterialTheme.shapes.small) {
                    Icon(Icons.Filled.Edit, null, tint = PLWhite, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Plant Record", color = PLWhite, style = MaterialTheme.typography.bodyMedium)
                }

                // QR Code card — with download + share buttons
                val qrBitmap = remember(plant.plantCode) { generateQrBitmap(plant.plantCode) }
                if (qrBitmap != null) {
                    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("QR Code — ${plant.plantCode}",
                                style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                            Text("Print this and place it near your plant",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center)
                            androidx.compose.foundation.Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "QR Code for ${plant.plantCode}",
                                modifier = Modifier.size(200.dp)
                            )
                            Text(plant.plantCode, style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)

                            // Download + Share buttons
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { saveQrToGallery(context, qrBitmap, plant.plantCode) },
                                    modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.small) {
                                    Icon(Icons.Filled.Download, null, modifier = Modifier.size(16.dp), tint = PLMidGreen)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Download", style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface)
                                }
                                OutlinedButton(
                                    onClick = { shareQrBitmap(context, qrBitmap, plant.plantCode) },
                                    modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.small) {
                                    Icon(Icons.Filled.Share, null, modifier = Modifier.size(16.dp), tint = PLMidGreen)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Share", style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }

                // Weather card — live from OpenWeatherMap API
                Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.WbSunny, null, tint = PLMidGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Live Weather", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                        if (viewModel.weatherLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = PLMidGreen)
                        } else if (weather != null) {
                            Text("🌡️ ${weather.main.temp}°C — ${weather.weather.firstOrNull()?.description ?: ""}",
                                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text("💧 Humidity: ${weather.main.humidity}%",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("🌬️ Wind: ${weather.wind.speed} m/s",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            // Watering advice based on temp
                            val advice = when {
                                weather.main.temp > 35 -> "🔥 Very hot — water immediately!"
                                weather.main.temp > 28 -> "☀️ Hot — water in the morning or evening"
                                weather.main.humidity > 80 -> "💦 High humidity — reduce watering"
                                else -> "✅ Good conditions for watering"
                            }
                            Text(advice, style = MaterialTheme.typography.bodySmall,
                                color = PLMidGreen, fontWeight = FontWeight.Medium)
                        } else if (plant.latitude == 0.0) {
                            Text("No GPS coordinates — add location to see weather",
                                style = MaterialTheme.typography.bodySmall, color = PLGrayText)
                        } else {
                            Text(viewModel.weatherError.ifBlank { "Loading weather..." },
                                style = MaterialTheme.typography.bodySmall, color = PLGrayText)
                        }
                    }
                }

                // Plant Care Tips card — local reference data
                // (Trefle and OpenFarm APIs are both discontinued/broken as of 2025;
                // OpenWeatherMap remains the live REST API for this app)
                Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Spa, null, tint = PLMidGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Care Reminders",
                                style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                        if (plant.fertiliserType.isNotBlank() || plant.fertiliserRatio.isNotBlank()) {
                            Text("🌿 Fertiliser: ${plant.fertiliserType.ifBlank { "—" }} (${plant.fertiliserRatio.ifBlank { "ratio not set" }})",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                        val statusTip = when {
                            plant.healthStatus.contains("Healthy", true) ->
                                "✅ Plant is healthy — maintain current watering and fertiliser schedule."
                            plant.healthStatus.contains("Unhealthy", true) ->
                                "⚠️ Plant marked unhealthy — check soil moisture, sunlight exposure, and consider adjusting fertiliser ratio."
                            else -> "ℹ️ Log a health check to get personalised care reminders here."
                        }
                        Text(statusTip, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                        Text("See the Tips screen for general NPK fertiliser and watering guidance.",
                            style = MaterialTheme.typography.bodySmall, color = PLGrayText)
                    }
                }

                // Share to community button
                Button(onClick = {
                    val deviceId = android.provider.Settings.Secure.getString(
                        context.contentResolver, android.provider.Settings.Secure.ANDROID_ID
                    ) ?: "unknown_device"
                    viewModel.shareToCommunity(plant, deviceId)
                },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PLMidGreen),
                    shape = MaterialTheme.shapes.small) {
                    Icon(Icons.Filled.Share, null, tint = PLWhite, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share to Community (Firebase)",
                        style = MaterialTheme.typography.bodyMedium, color = PLWhite)
                }

                if (shareStatus.isNotBlank()) {
                    Text(shareStatus, style = MaterialTheme.typography.bodySmall,
                        color = PLMidGreen, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth())
                }

                Button(onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = MaterialTheme.shapes.small) {
                    Text("Back to Plant List", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  S5 — HEALTH CHECK SCREEN
// ════════════════════════════════════════════════════════
@Composable
fun HealthCheckScreen(navController: NavController, viewModel: PlantLogsViewModel) {
    val isEditMode = viewModel.editingHealth != null
    val existing   = viewModel.editingHealth

    var plantName     by remember(existing?.id) { mutableStateOf(existing?.plantName ?: "") }
    var status        by remember(existing?.id) { mutableStateOf(existing?.status    ?: "") }
    var symptom       by remember(existing?.id) { mutableStateOf(existing?.symptom   ?: "") }
    var imageUris     by remember(existing?.id) { mutableStateOf(existing?.imageUris ?: "") }
    var expandedIndex by remember { mutableStateOf(-1) }
    val healthChecks by viewModel.healthList.collectAsState()
    val context = LocalContext.current

    // If user presses system back without saving, clear edit state
    BackHandler {
        viewModel.clearEditHealth()
        navController.popBackStack()
    }


    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar("Health Check", navController, onBack = { viewModel.clearEditHealth() })

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

            Text("Plant Health Check", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text("A221505 — Log plant health status",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(if (isEditMode) "Edit Health Record" else "New Health Check",
                        style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)

                    OutlinedTextField(value = plantName, onValueChange = { plantName = it },
                        label = { Text("Plant Name", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    OutlinedTextField(value = status, onValueChange = { status = it },
                        label = { Text("Status (Healthy / Unhealthy)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    OutlinedTextField(value = symptom, onValueChange = { symptom = it },
                        label = { Text("Observed Symptoms", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(), minLines = 2, shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    MultiImagePicker(
                        imageUris = imageUris,
                        onUpdate  = { imageUris = it },
                        label     = "Health Photos"
                    )

                    Button(onClick = {
                        if (plantName.isNotBlank() && status.isNotBlank()) {
                            if (isEditMode && existing != null) {
                                viewModel.updateHealthCheck(existing, plantName, status, symptom, imageUris)
                                viewModel.clearEditHealth()
                            } else {
                                viewModel.addHealthCheck(plantName, status, symptom, imageUris)
                            }
                            plantName = ""; status = ""; symptom = ""; imageUris = ""
                        }
                    }, modifier = Modifier.fillMaxWidth(),
                        enabled = plantName.isNotBlank() && status.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.small) {
                        Text(if (isEditMode) "Update Record" else "Save Health Check",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary)
                    }

                    // Cancel — always clears edit state so "Health Check"
                    // from Home opens a blank form next time
                    OutlinedButton(onClick = {
                        viewModel.clearEditHealth()
                        plantName = ""; status = ""; symptom = ""; imageUris = ""
                        navController.popBackStack()
                    }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small) {
                        Text("Cancel", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            if (healthChecks.isNotEmpty()) {
                Text("Health Records (${healthChecks.size})",
                    style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)

                healthChecks.forEachIndexed { index, check ->
                    Card(modifier = Modifier.fillMaxWidth()
                        .animateContentSize(animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow))
                        .clickable { expandedIndex = if (expandedIndex == index) -1 else index },
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(check.plantName, style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                                    Text(check.status, style = MaterialTheme.typography.bodySmall,
                                        color = if (check.status.contains("Healthy", true))
                                            PLMidGreen else MaterialTheme.colorScheme.error)
                                }
                                TextButton(onClick = { viewModel.startEditHealth(check); expandedIndex = -1 }) {
                                    Text("Edit", style = MaterialTheme.typography.bodySmall, color = PLMidGreen)
                                }
                                TextButton(onClick = { viewModel.deleteHealthCheck(check) }) {
                                    Text("Delete", style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error)
                                }
                                Icon(if (expandedIndex == index) Icons.Filled.KeyboardArrowUp
                                else Icons.Filled.KeyboardArrowDown,
                                    null, tint = PLGrayText, modifier = Modifier.size(22.dp))
                            }
                            if (expandedIndex == index && (check.symptom.isNotBlank() || check.imageUris.isNotBlank())) {
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = PLBorderColor, thickness = 0.5.dp)
                                Spacer(modifier = Modifier.height(8.dp))
                                if (check.symptom.isNotBlank()) {
                                    Text("Symptoms: ${check.symptom}",
                                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (check.imageUris.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    ImageGalleryRow(imageUris = check.imageUris)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  S6 — TIPS SCREEN
// ════════════════════════════════════════════════════════
@Composable
fun TipsScreen(navController: NavController) {
    var expandedIndex by remember { mutableStateOf(-1) }
    val tips = listOf(
        Pair("SDG 15 — Life on Land", "SDG 15 aims to protect, restore and promote sustainable use of terrestrial ecosystems, halt deforestation and halt biodiversity loss. Malaysia is home to one of the world's oldest rainforests, over 130 million years old."),
        Pair("Watering Tips", "Most plants need watering when the top inch of soil is dry. Water deeply but infrequently to encourage deep root growth. Overwatering is the most common cause of plant death."),
        Pair("Sunlight Requirements", "Most plants need 6 to 8 hours of sunlight per day. Place sun-loving plants near south-facing windows. Low light plants like ferns thrive in indirect light."),
        Pair("Fertiliser Guide", "NPK ratio stands for Nitrogen:Phosphorus:Potassium. High Nitrogen promotes leaf growth. High Phosphorus promotes root and flower growth. High Potassium promotes overall plant health."),
        Pair("Common Plant Diseases", "Yellow leaves indicate overwatering or nutrient deficiency. Brown leaf tips mean low humidity or too much fertiliser. White powder on leaves is powdery mildew — improve air circulation."),
        Pair("How PlantLogs Supports SDG 15", "By scanning QR codes placed at plant locations, users can instantly view plant health, weather conditions and care recommendations — encouraging active plant monitoring and protection.")
    )

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar("Guide & Tips", navController)

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Plant Care Guide", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text("A221505 — SDG 15 info and plant tips",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = PLLight),
                elevation = CardDefaults.cardElevation(4.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(PLHeroGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center) {
                        Text("15", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PLHeroGreen)
                    }
                    Column {
                        Text("SDG 15 — Life on Land",
                            style = MaterialTheme.typography.titleSmall,
                            color = PLHeroGreen)
                        Text("Protect, restore and promote sustainable use of ecosystems",
                            style = MaterialTheme.typography.bodySmall, color = PLHeroGreen.copy(alpha = 0.7f))
                    }
                }
            }

            tips.forEachIndexed { index, tip ->
                Card(modifier = Modifier.fillMaxWidth()
                    .animateContentSize(animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow))
                    .clickable { expandedIndex = if (expandedIndex == index) -1 else index },
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(tip.first, style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                            Icon(if (expandedIndex == index) Icons.Filled.KeyboardArrowUp
                            else Icons.Filled.KeyboardArrowDown,
                                null, tint = PLGrayText, modifier = Modifier.size(22.dp))
                        }
                        if (expandedIndex == index) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = PLBorderColor, thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(tip.second, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ════════════════════════════════════════════════════════
//  S7 — SCAN SCREEN (ML Kit QR Scanner + CameraX)
// ════════════════════════════════════════════════════════
@Composable
fun ScanScreen(navController: NavController, viewModel: PlantLogsViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    var scanResult by remember { mutableStateOf("") }
    var scanning   by remember { mutableStateOf(true) }

    val cameraPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    val scannedPlant = viewModel.scannedPlant

    // When plant found after scan → navigate to detail
    LaunchedEffect(scannedPlant) {
        scannedPlant?.let {
            viewModel.selectPlant(it)
            viewModel.clearScannedPlant()
            navController.navigate(Routes.PLANT_DETAIL)
        }
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar("Scan Plant QR", navController)

        if (!hasCameraPermission) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Icon(Icons.Filled.CameraAlt, null, tint = PLMidGreen, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Camera permission needed to scan QR codes",
                    style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { cameraPermLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = MaterialTheme.shapes.small) {
                    Text("Grant Camera Permission", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                // CameraX preview
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also { analysis ->
                                    analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                                        if (!scanning) {
                                            imageProxy.close()
                                            return@setAnalyzer
                                        }
                                        val mediaImage = imageProxy.image
                                        if (mediaImage != null) {
                                            val image = InputImage.fromMediaImage(
                                                mediaImage, imageProxy.imageInfo.rotationDegrees)
                                            val scanner = BarcodeScanning.getClient()
                                            scanner.process(image)
                                                .addOnSuccessListener { barcodes ->
                                                    for (barcode in barcodes) {
                                                        barcode.rawValue?.let { value ->
                                                            if (value.startsWith("PLT-") && scanning) {
                                                                scanning    = false
                                                                scanResult  = value
                                                                // Look up plant in Room
                                                                viewModel.findPlantByCode(value)
                                                            }
                                                        }
                                                    }
                                                }
                                                .addOnCompleteListener { imageProxy.close() }
                                        } else imageProxy.close()
                                    }
                                }

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (e: Exception) { }
                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay UI
                Column(modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween) {

                    // Top instruction
                    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))) {
                        Text("Point camera at plant QR code",
                            style = MaterialTheme.typography.bodyMedium, color = Color.White,
                            modifier = Modifier.padding(12.dp), textAlign = TextAlign.Center)
                    }

                    // Bottom result
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (scanResult.isNotBlank()) {
                            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(containerColor = PLMidGreen)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("✅ QR Detected: $scanResult",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = PLWhite, fontWeight = FontWeight.Bold)
                                    Text("Looking up plant in database...",
                                        style = MaterialTheme.typography.bodySmall, color = PLLight)
                                }
                            }
                        }
                        Button(onClick = { scanning = true; scanResult = "" },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = MaterialTheme.shapes.small) {
                            Text("Scan Again", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  S8 — COMMUNITY SCREEN (Firebase Firestore)
// ════════════════════════════════════════════════════════
@Composable
fun CommunityScreen(navController: NavController, viewModel: PlantLogsViewModel) {

    // Load community plants when screen opens
    LaunchedEffect(Unit) { viewModel.loadCommunityPlants() }

    val communityPlants = viewModel.communityPlants
    val loading         = viewModel.communityLoading

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar("Community Plants", navController)

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            Text("Community Plant Board", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text("A221505 — Plants shared by the community via Firebase",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            // Refresh button
            Button(onClick = { viewModel.loadCommunityPlants() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PLMidGreen),
                shape = MaterialTheme.shapes.small) {
                Icon(Icons.Filled.Refresh, null, tint = PLWhite, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Refresh from Firebase", color = PLWhite,
                    style = MaterialTheme.typography.bodyMedium)
            }

            if (loading) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PLMidGreen)
                }
            } else if (communityPlants.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.People, null, tint = PLGrayText, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No community plants yet.",
                            style = MaterialTheme.typography.bodyLarge, color = PLGrayText,
                            textAlign = TextAlign.Center)
                        Text("Add a plant and tap 'Share to Community' in Plant Detail.",
                            style = MaterialTheme.typography.bodySmall, color = PLGrayText,
                            textAlign = TextAlign.Center)
                    }
                }
            } else {
                communityPlants.forEach { plant ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Plant photo — shows if image file exists on this device
                            val imageUris = plant["imageUris"]?.toString() ?: ""
                            if (imageUris.isNotBlank() && imageUris != "null") {
                                ImageGalleryRow(imageUris = imageUris)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.Spa, null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(plant["name"]?.toString() ?: "Unknown",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium)
                                    Text(plant["species"]?.toString() ?: "",
                                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                // Shared badge
                                Card(shape = RoundedCornerShape(6.dp),
                                    colors = CardDefaults.cardColors(containerColor = PLLight)) {
                                    Text("Community", style = MaterialTheme.typography.labelSmall,
                                        color = PLMidGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            if (!plant["location"].toString().isNullOrBlank() && plant["location"].toString() != "null")
                                Text("📍 ${plant["location"]}", style = MaterialTheme.typography.bodySmall, color = PLGrayText)
                            if (!plant["healthStatus"].toString().isNullOrBlank() && plant["healthStatus"].toString() != "null")
                                Text("❤️ Health: ${plant["healthStatus"]}", style = MaterialTheme.typography.bodySmall,
                                    color = if (plant["healthStatus"].toString().contains("Healthy", true))
                                        PLMidGreen else MaterialTheme.colorScheme.error)
                            if (!plant["fertiliserType"].toString().isNullOrBlank() && plant["fertiliserType"].toString() != "null")
                                Text("🌿 Fertiliser: ${plant["fertiliserType"]} ${plant["fertiliserRatio"]}",
                                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Shared by: ${plant["sharedBy"] ?: "Anonymous"}",
                                style = MaterialTheme.typography.labelSmall, color = PLGrayText)
                        }
                    }
                }
            }
        }
    }
}

// ── PREVIEW ───────────────────────────────────────────────
@ComposePreview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPlantLogs() {
    PlantLogTheme {
        Surface(modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background) {
            Text("PlantLogs A221505 — Preview Mode",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium)
        }
    }
}