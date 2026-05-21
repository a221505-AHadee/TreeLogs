package com.example.a221505_cikgu_izwan_plantlogs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.a221505_cikgu_izwan_plantlogs.data.HealthEntity
import com.example.a221505_cikgu_izwan_plantlogs.data.PlantEntity
import com.example.a221505_cikgu_izwan_plantlogs.ui.theme.*

// ── ROUTES ───────────────────────────────────────────────
object Routes {
    const val HOME         = "home"
    const val ADD_PLANT    = "add_plant"
    const val PLANT_LIST   = "plant_list"
    const val PLANT_DETAIL = "plant_detail"
    const val HEALTH_CHECK = "health_check"
    const val TIPS         = "tips"
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

// ── ROOT — NavHost + ViewModel with Room ──────────────────
@Composable
fun PlantLogsApp() {
    val navController = rememberNavController()

    // Lab 5 — ViewModel now uses Factory to inject Repository
    // Application class provides the repository
    val viewModel: PlantLogsViewModel = viewModel(
        factory = PlantLogsViewModelFactory(
            (androidx.compose.ui.platform.LocalContext.current
                .applicationContext as PlantLogsApplication).repository
        )
    )

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME)         { HomeScreen(navController, viewModel) }
        composable(Routes.ADD_PLANT)    { AddPlantScreen(navController, viewModel) }
        composable(Routes.PLANT_LIST)   { PlantListScreen(navController, viewModel) }
        composable(Routes.PLANT_DETAIL) { PlantDetailScreen(navController, viewModel) }
        composable(Routes.HEALTH_CHECK) { HealthCheckScreen(navController, viewModel) }
        composable(Routes.TIPS)         { TipsScreen(navController) }
    }
}

// ── REUSABLE — TOP BAR ────────────────────────────────────
@Composable
fun TopBar(title: String, navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth().background(PLHeroGreen)
        .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PLLight)
        }
        Text(title, style = MaterialTheme.typography.titleMedium, color = PLLight)
    }
}

// ── REUSABLE — FEATURE BUTTON ─────────────────────────────
@Composable
fun FeatureBtn(label: String, sublabel: String, icon: ImageVector, isFilled: Boolean, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isFilled) MaterialTheme.colorScheme.primary
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
                    color = if (isFilled) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium)
                Text(sublabel, style = MaterialTheme.typography.bodySmall,
                    color = if (isFilled) PLAccent else PLTextMid)
            }
            Icon(Icons.Filled.KeyboardArrowRight, null,
                tint = if (isFilled) PLLight else PLGrayText, modifier = Modifier.size(20.dp))
        }
    }
}

// ── REUSABLE — DETAIL ROW ─────────────────────────────────
@Composable
fun DetailRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = PLAccent)
        Text(value.ifBlank { "—" }, style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Medium)
    }
}

// ── REUSABLE — BOTTOM NAV ITEM ────────────────────────────
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

// ════════════════════════════════════════════════════════
//  S1 — HOME SCREEN
// ════════════════════════════════════════════════════════
@Composable
fun HomeScreen(navController: NavController, viewModel: PlantLogsViewModel) {

    var userName    by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("ANON") }

    // Lab 5 — collectAsState() converts StateFlow to Compose State
    // UI automatically redraws when Room database changes
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
                Spacer(modifier = Modifier.height(4.dp))
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
                    Text("Update your name",
                        style = MaterialTheme.typography.titleSmall, color = PLTextDark)
                    OutlinedTextField(value = userName, onValueChange = { userName = it },
                        label = { Text("Your name",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))
                    Button(onClick = { if (userName.isNotBlank()) { displayName = userName; userName = "" } },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.small) {
                        Text("Update Greeting",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }

            // Lab 5 — stats now read from Room via StateFlow
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary),
                    elevation = CardDefaults.cardElevation(3.dp)) {
                    Column(modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${plants.size}", fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary)
                        Text("Plants Logged",
                            style = MaterialTheme.typography.bodySmall, color = PLAccent)
                    }
                }
                Card(modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(3.dp)) {
                    Column(modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${healthChecks.size}", fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface)
                        Text("Health Checks",
                            style = MaterialTheme.typography.bodySmall, color = PLTextMid)
                    }
                }
            }

            Text("Main Features",
                style = MaterialTheme.typography.titleSmall, color = PLTextDark)

            FeatureBtn("Log New Plant",  "Add a new plant record",          Icons.Filled.Add,      true)  { navController.navigate(Routes.ADD_PLANT) }
            FeatureBtn("Plant List",     "View and edit logged plants",      Icons.Filled.List,     false) { navController.navigate(Routes.PLANT_LIST) }
            FeatureBtn("Health Check",   "Check and edit plant health",      Icons.Filled.Favorite, false) { navController.navigate(Routes.HEALTH_CHECK) }
            FeatureBtn("Guide & Tips",   "SDG 15 info and plant care tips",  Icons.Filled.Info,     false) { navController.navigate(Routes.TIPS) }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(modifier = Modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, PLBorderColor).navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically) {
            BotNavItem("Home",   Icons.Filled.Home,     true)
            BotNavItem("Plants", Icons.Filled.List,     false) { navController.navigate(Routes.PLANT_LIST) }
            Box(modifier = Modifier.size(52.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { navController.navigate(Routes.ADD_PLANT) },
                contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Add, "Add", tint = PLWhite, modifier = Modifier.size(26.dp))
            }
            BotNavItem("Health", Icons.Filled.Favorite, false) { navController.navigate(Routes.HEALTH_CHECK) }
            BotNavItem("Tips",   Icons.Filled.Info,     false) { navController.navigate(Routes.TIPS) }
        }
    }
}

// ════════════════════════════════════════════════════════
//  S2 — ADD PLANT SCREEN
//  Lab 5 — calls viewModel.addPlant() which saves to Room
//  Edit mode pre-fills from PlantEntity
// ════════════════════════════════════════════════════════
@Composable
fun AddPlantScreen(navController: NavController, viewModel: PlantLogsViewModel) {

    val isEditMode = viewModel.editingPlant != null
    val existing   = viewModel.editingPlant

    var name     by remember { mutableStateOf(existing?.name     ?: "") }
    var species  by remember { mutableStateOf(existing?.species  ?: "") }
    var location by remember { mutableStateOf(existing?.location ?: "") }
    var notes    by remember { mutableStateOf(existing?.notes    ?: "") }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar(if (isEditMode) "Edit Plant" else "Log New Plant", navController)

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Text(if (isEditMode) "Edit Plant Record" else "Add a New Plant",
                style = MaterialTheme.typography.titleMedium, color = PLTextDark)
            Text("A221505 — Fill in the details below",
                style = MaterialTheme.typography.bodySmall, color = PLTextMid)

            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    OutlinedTextField(value = name, onValueChange = { name = it },
                        label = { Text("Plant Name",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    OutlinedTextField(value = species, onValueChange = { species = it },
                        label = { Text("Species",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    OutlinedTextField(value = location, onValueChange = { location = it },
                        label = { Text("Location",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    OutlinedTextField(value = notes, onValueChange = { notes = it },
                        label = { Text("Notes",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(), minLines = 3,
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    Button(
                        onClick = {
                            if (name.isNotBlank() && species.isNotBlank()) {
                                if (isEditMode && existing != null) {
                                    // Lab 5 — update in Room database
                                    viewModel.updatePlant(existing, name, species, location, notes)
                                    viewModel.clearEditPlant()
                                } else {
                                    // Lab 5 — insert into Room database
                                    viewModel.addPlant(name, species, location, notes)
                                }
                                navController.navigate(Routes.PLANT_LIST)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled  = name.isNotBlank() && species.isNotBlank(),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.small) {
                        Text(if (isEditMode) "Update Plant" else "Save Plant",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary)
                    }

                    if (isEditMode) {
                        OutlinedButton(
                            onClick = { viewModel.clearEditPlant(); navController.popBackStack() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.small) {
                            Text("Cancel Edit",
                                style = MaterialTheme.typography.bodyMedium, color = PLTextDark)
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  S3 — PLANT LIST SCREEN
//  Lab 5 — reads from Room StateFlow
//  Delete removes from Room database permanently
// ════════════════════════════════════════════════════════
@Composable
fun PlantListScreen(navController: NavController, viewModel: PlantLogsViewModel) {

    // collectAsState() — reads StateFlow from Room
    val plants by viewModel.plantList.collectAsState()

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar("Plant List (${plants.size})", navController)

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

            Text("All Logged Plants",
                style = MaterialTheme.typography.titleMedium, color = PLTextDark)
            Text("A221505 — Tap to view | Edit or Delete",
                style = MaterialTheme.typography.bodySmall, color = PLTextMid)

            if (plants.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.List, null, tint = PLGrayText,
                            modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No plants logged yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = PLGrayText, textAlign = TextAlign.Center)
                        Text("Tap Log New Plant to get started.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PLGrayText, textAlign = TextAlign.Center)
                    }
                }
            } else {
                plants.forEachIndexed { index, plant ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {

                            Row(modifier = Modifier.clickable {
                                    viewModel.selectPlant(plant)
                                    navController.navigate(Routes.PLANT_DETAIL)
                                },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center) {
                                    Text("#${index + 1}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(plant.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium)
                                    Text(plant.species,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PLTextMid)
                                    if (plant.location.isNotBlank())
                                        Text("📍 ${plant.location}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = PLGrayText)
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = {
                                    viewModel.startEditPlant(plant)
                                    navController.navigate(Routes.ADD_PLANT)
                                }) {
                                    Icon(Icons.Filled.Edit, null,
                                        modifier = Modifier.size(14.dp), tint = PLMidGreen)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PLMidGreen)
                                }
                                // Lab 5 — delete from Room database permanently
                                TextButton(onClick = { viewModel.deletePlant(plant) }) {
                                    Icon(Icons.Filled.Delete, null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Delete",
                                        style = MaterialTheme.typography.bodySmall,
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.small) {
                Text("+ Log New Plant",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  S4 — PLANT DETAIL SCREEN
//  Reads selectedPlant (PlantEntity) from ViewModel
// ════════════════════════════════════════════════════════
@Composable
fun PlantDetailScreen(navController: NavController, viewModel: PlantLogsViewModel) {

    val plant = viewModel.selectedPlant

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar("Plant Detail", navController)

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

            Text("Plant Information",
                style = MaterialTheme.typography.titleMedium, color = PLTextDark)
            Text("A221505 — Full plant record from Room DB",
                style = MaterialTheme.typography.bodySmall, color = PLTextMid)

            if (plant == null) {
                Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No plant selected.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = PLGrayText, textAlign = TextAlign.Center)
                        Text("Go back and tap a plant from the list.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PLGrayText, textAlign = TextAlign.Center)
                    }
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary),
                    elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp))
                                .background(PLWhiteOverlay),
                                contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Star, null, tint = PLWhite,
                                    modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Text("Plant Record",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimary)
                        }
                        HorizontalDivider(color = PLAccent.copy(alpha = 0.4f), thickness = 0.5.dp)
                        DetailRow("Plant Name", plant.name)
                        DetailRow("Species",    plant.species)
                        DetailRow("Location",   plant.location)
                        DetailRow("Notes",      plant.notes)
                    }
                }
                Button(onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary),
                    shape = MaterialTheme.shapes.small) {
                    Text("Back to Plant List",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  S5 — HEALTH CHECK SCREEN
//  Lab 5 — saves to Room, reads from StateFlow
// ════════════════════════════════════════════════════════
@Composable
fun HealthCheckScreen(navController: NavController, viewModel: PlantLogsViewModel) {

    val isEditMode = viewModel.editingHealth != null
    val existing   = viewModel.editingHealth

    var plantName     by remember { mutableStateOf(existing?.plantName ?: "") }
    var status        by remember { mutableStateOf(existing?.status    ?: "") }
    var symptom       by remember { mutableStateOf(existing?.symptom   ?: "") }
    var expandedIndex by remember { mutableStateOf(-1) }

    // Lab 5 — reads from Room via StateFlow
    val healthChecks by viewModel.healthList.collectAsState()

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar("Health Check", navController)

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

            Text("Plant Health Check",
                style = MaterialTheme.typography.titleMedium, color = PLTextDark)
            Text("A221505 — Log and edit plant health status",
                style = MaterialTheme.typography.bodySmall, color = PLTextMid)

            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    Text(if (isEditMode) "Edit Health Record" else "New Health Check",
                        style = MaterialTheme.typography.titleSmall, color = PLTextDark)

                    OutlinedTextField(value = plantName, onValueChange = { plantName = it },
                        label = { Text("Plant Name",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    OutlinedTextField(value = status, onValueChange = { status = it },
                        label = { Text("Status (Healthy / Unhealthy)",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    OutlinedTextField(value = symptom, onValueChange = { symptom = it },
                        label = { Text("Observed Symptoms",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(), minLines = 2,
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor))

                    Button(
                        onClick = {
                            if (plantName.isNotBlank() && status.isNotBlank()) {
                                if (isEditMode && existing != null) {
                                    viewModel.updateHealthCheck(existing, plantName, status, symptom)
                                    viewModel.clearEditHealth()
                                } else {
                                    viewModel.addHealthCheck(plantName, status, symptom)
                                }
                                plantName = ""; status = ""; symptom = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled  = plantName.isNotBlank() && status.isNotBlank(),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.small) {
                        Text(if (isEditMode) "Update Record" else "Save Health Check",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary)
                    }

                    if (isEditMode) {
                        OutlinedButton(
                            onClick = {
                                viewModel.clearEditHealth()
                                plantName = ""; status = ""; symptom = ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.small) {
                            Text("Cancel Edit",
                                style = MaterialTheme.typography.bodyMedium, color = PLTextDark)
                        }
                    }
                }
            }

            if (healthChecks.isNotEmpty()) {
                Text("Health Records (${healthChecks.size})",
                    style = MaterialTheme.typography.titleSmall, color = PLTextDark)

                healthChecks.forEachIndexed { index, check ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                            .animateContentSize(animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness    = Spring.StiffnessLow))
                            .clickable { expandedIndex = if (expandedIndex == index) -1 else index },
                        shape     = MaterialTheme.shapes.medium,
                        colors    = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(check.plantName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium)
                                    Text(check.status,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (check.status.contains("Healthy", true))
                                            PLMidGreen else MaterialTheme.colorScheme.error)
                                }
                                TextButton(onClick = {
                                    viewModel.startEditHealth(check)
                                    expandedIndex = -1
                                }) {
                                    Icon(Icons.Filled.Edit, null,
                                        modifier = Modifier.size(14.dp), tint = PLMidGreen)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PLMidGreen)
                                }
                                TextButton(onClick = { viewModel.deleteHealthCheck(check) }) {
                                    Icon(Icons.Filled.Delete, null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Delete",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error)
                                }
                                Icon(
                                    if (expandedIndex == index) Icons.Filled.KeyboardArrowUp
                                    else Icons.Filled.KeyboardArrowDown,
                                    null, tint = PLGrayText, modifier = Modifier.size(22.dp))
                            }
                            if (expandedIndex == index && check.symptom.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = PLBorderColor, thickness = 0.5.dp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Symptoms: ${check.symptom}",
                                    style = MaterialTheme.typography.bodyMedium, color = PLTextMid)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════
//  S6 — TIPS SCREEN (unchanged from Project 1)
// ════════════════════════════════════════════════════════
@Composable
fun TipsScreen(navController: NavController) {

    var expandedIndex by remember { mutableStateOf(-1) }

    val tips = listOf(
        Pair("SDG 15 — Life on Land",
            "SDG 15 aims to protect, restore and promote sustainable use of " +
            "terrestrial ecosystems, halt deforestation, restore degraded land " +
            "and halt biodiversity loss. Malaysia is home to one of the world's " +
            "oldest rainforests, over 130 million years old."),
        Pair("Watering Tips",
            "Most plants need watering when the top inch of soil is dry. " +
            "Water deeply but infrequently to encourage deep root growth. " +
            "Overwatering is the most common cause of plant death."),
        Pair("Sunlight Requirements",
            "Most plants need 6 to 8 hours of sunlight per day. " +
            "Place sun-loving plants near south-facing windows. " +
            "Low light plants like ferns thrive in indirect light."),
        Pair("Soil and Fertilizer",
            "Use well-draining soil to prevent root rot. " +
            "Fertilize during the growing season — spring and summer. " +
            "Organic fertilizers release nutrients slowly and improve soil health."),
        Pair("Common Plant Diseases",
            "Yellow leaves often indicate overwatering or nutrient deficiency. " +
            "Brown leaf tips may mean low humidity or too much fertilizer. " +
            "White powder on leaves is powdery mildew — improve air circulation."),
        Pair("How PlantLogs Supports SDG 15",
            "By logging plants and tracking their health, PlantLogs encourages " +
            "awareness of local plant biodiversity. Users can monitor plant " +
            "conditions and take early action to protect plant life — " +
            "directly supporting SDG 15 Life on Land goals.")
    )

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar("Guide & Tips", navController)

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

            Text("Plant Care Guide",
                style = MaterialTheme.typography.titleMedium, color = PLTextDark)
            Text("A221505 — SDG 15 info and plant tips",
                style = MaterialTheme.typography.bodySmall, color = PLTextMid)

            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(4.dp)) {
                Row(modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(PLWhiteOverlay),
                        contentAlignment = Alignment.Center) {
                        Text("15", fontSize = 20.sp,
                            fontWeight = FontWeight.Bold, color = PLWhite)
                    }
                    Column {
                        Text("SDG 15 — Life on Land",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimary)
                        Text("Protect, restore and promote sustainable use of ecosystems",
                            style = MaterialTheme.typography.bodySmall, color = PLAccent)
                    }
                }
            }

            tips.forEachIndexed { index, tip ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .animateContentSize(animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness    = Spring.StiffnessLow))
                        .clickable { expandedIndex = if (expandedIndex == index) -1 else index },
                    shape     = MaterialTheme.shapes.medium,
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(tip.first, style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                            Icon(
                                if (expandedIndex == index) Icons.Filled.KeyboardArrowUp
                                else Icons.Filled.KeyboardArrowDown,
                                null, tint = PLGrayText, modifier = Modifier.size(22.dp))
                        }
                        if (expandedIndex == index) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = PLBorderColor, thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(tip.second, style = MaterialTheme.typography.bodyMedium,
                                color = PLTextMid)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ── PREVIEW ───────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPlantLogs() {
    PlantLogTheme {
        Surface(modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background) {
            // Preview shows blank — needs Application context for Room
        }
    }
}
