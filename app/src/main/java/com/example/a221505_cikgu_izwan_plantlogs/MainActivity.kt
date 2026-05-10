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
import androidx.compose.ui.graphics.Color
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
import com.example.a221505_cikgu_izwan_plantlogs.ui.theme.*

//ROUTES
object Routes {
    const val HOME         = "home"
    const val ADD_PLANT    = "add_plant"
    const val PLANT_LIST   = "plant_list"
    const val PLANT_DETAIL = "plant_detail"
    const val HEALTH_CHECK = "health_check"
    const val TIPS         = "tips"
}

//MAIN ACTIVITY
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantLogTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = MaterialTheme.colorScheme.background
                ) {
                    TreeLogsApp()
                }
            }
        }
    }
}

@Composable
fun TreeLogsApp() {
    val navController = rememberNavController()
    val viewModel     : TreeLogsViewModel = viewModel()

    NavHost(
        navController    = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME)         { HomeScreen(navController, viewModel) }
        composable(Routes.ADD_PLANT)    { AddPlantScreen(navController, viewModel) }
        composable(Routes.PLANT_LIST)   { PlantListScreen(navController, viewModel) }
        composable(Routes.PLANT_DETAIL) { PlantDetailScreen(navController, viewModel) }
        composable(Routes.HEALTH_CHECK) { HealthCheckScreen(navController, viewModel) }
        composable(Routes.TIPS)         { TipsScreen(navController) }
    }
}

//REUSE — TOP BAR
@Composable
fun TopBar(title: String, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PLHeroGreen)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back", tint = PLLight)
        }
        Text(text = title,
            style = MaterialTheme.typography.titleMedium, color = PLLight)
    }
}

@Composable
fun FeatureButton(
    label    : String,
    sublabel : String,
    icon     : ImageVector,
    isFilled : Boolean,
    onClick  : () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = MaterialTheme.shapes.medium,
        colors    = CardDefaults.cardColors(
            containerColor = if (isFilled) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        onClick   = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isFilled) PLWhiteOverlay else PLLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null,
                    tint = if (isFilled) PLWhite else PLMidGreen,
                    modifier = Modifier.size(26.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isFilled) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium)
                Text(text = sublabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isFilled) PLAccent else PLTextMid)
            }
            Icon(imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = if (isFilled) PLLight else PLGrayText,
                modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(text = label,
            style = MaterialTheme.typography.bodySmall,
            color = PLAccent)
        Text(text = value.ifBlank { "—" },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Medium)
    }
}

@Composable
fun BottomNavItem(
    label    : String,
    icon     : ImageVector,
    isActive : Boolean,
    onClick  : () -> Unit = {}
) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label,
            tint = if (isActive) MaterialTheme.colorScheme.primary else PLGrayText,
            modifier = Modifier.size(22.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall,
            color = if (isActive) MaterialTheme.colorScheme.primary else PLGrayText,
            fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
            textAlign = TextAlign.Center)
    }
}

@Composable
fun HomeScreen(navController: NavController, viewModel: TreeLogsViewModel) {


    var userName    by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("Ahmad") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PLHeroGreen)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PLLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Filled.Star, contentDescription = null,
                    tint = PLMidGreen, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {

                Text(text = "TreeLogs — Hello, $displayName!",
                    style = MaterialTheme.typography.titleMedium, color = PLLight)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "A221505 — SDG 15 Life on Land",
                    style = MaterialTheme.typography.bodySmall, color = PLAccent)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = MaterialTheme.shapes.medium,
                colors    = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Update your name",
                        style = MaterialTheme.typography.titleSmall, color = PLTextDark)

                    OutlinedTextField(
                        value = userName, onValueChange = { userName = it },
                        label = { Text("Your name",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor)
                    )

                    Button(
                        onClick = {
                            if (userName.isNotBlank()) { displayName = userName; userName = "" }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Update Greeting",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Card(
                    modifier  = Modifier.weight(1f),
                    shape     = MaterialTheme.shapes.medium,
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${viewModel.plantList.size}",
                            fontSize = 32.sp, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary)
                        Text(text = "Plants Logged",
                            style = MaterialTheme.typography.bodySmall, color = PLAccent)
                    }
                }

                Card(
                    modifier  = Modifier.weight(1f),
                    shape     = MaterialTheme.shapes.medium,
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${viewModel.healthList.size}",
                            fontSize = 32.sp, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "Health Checks",
                            style = MaterialTheme.typography.bodySmall, color = PLTextMid)
                    }
                }
            }

            Text(text = "Main Features",
                style = MaterialTheme.typography.titleSmall, color = PLTextDark)

            FeatureButton(
                label    = "Log New Plant",
                sublabel = "Add a new plant record",
                icon     = Icons.Filled.Add,
                isFilled = true,
                onClick  = { navController.navigate(Routes.ADD_PLANT) }
            )
            FeatureButton(
                label    = "Plant List",
                sublabel = "View all logged plants",
                icon     = Icons.Filled.List,
                isFilled = false,
                onClick  = { navController.navigate(Routes.PLANT_LIST) }
            )
            FeatureButton(
                label    = "Health Check",
                sublabel = "Check your plant's health",
                icon     = Icons.Filled.Favorite,
                isFilled = false,
                onClick  = { navController.navigate(Routes.HEALTH_CHECK) }
            )
            FeatureButton(
                label    = "Guide & Tips",
                sublabel = "SDG 15 info and plant care tips",
                icon     = Icons.Filled.Info,
                isFilled = false,
                onClick  = { navController.navigate(Routes.TIPS) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, PLBorderColor)
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            BottomNavItem("Home",   Icons.Filled.Home,     true)
            BottomNavItem("Plants", Icons.Filled.List,     false,
                onClick = { navController.navigate(Routes.PLANT_LIST) })
            //FAB — centre button
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { navController.navigate(Routes.ADD_PLANT) },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add",
                    tint = PLWhite, modifier = Modifier.size(26.dp))
            }
            BottomNavItem("Health", Icons.Filled.Favorite, false,
                onClick = { navController.navigate(Routes.HEALTH_CHECK) })
            BottomNavItem("Tips",   Icons.Filled.Info,     false,
                onClick = { navController.navigate(Routes.TIPS) })
        }
    }
}

@Composable
fun AddPlantScreen(navController: NavController, viewModel: TreeLogsViewModel) {


    var name     by remember { mutableStateOf("") }
    var species  by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var notes    by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBar(title = "Log New Plant", navController = navController)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Add a New Plant",
                style = MaterialTheme.typography.titleMedium, color = PLTextDark)
            Text(text = "A221505 — Fill in the plant details below",
                style = MaterialTheme.typography.bodySmall, color = PLTextMid)


            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = MaterialTheme.shapes.medium,
                colors    = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("Plant Name",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor)
                    )

                    OutlinedTextField(
                        value = species, onValueChange = { species = it },
                        label = { Text("Species",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor)
                    )

                    OutlinedTextField(
                        value = location, onValueChange = { location = it },
                        label = { Text("Location",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor)
                    )

                    OutlinedTextField(
                        value = notes, onValueChange = { notes = it },
                        label = { Text("Notes",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor)
                    )

                    Button(
                        onClick = {
                            if (name.isNotBlank() && species.isNotBlank()) {

                                viewModel.addPlant(
                                    name     = name,
                                    species  = species,
                                    location = location,
                                    notes    = notes
                                )

                                navController.navigate(Routes.PLANT_LIST)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),

                        enabled = name.isNotBlank() && species.isNotBlank(),
                        colors  = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Save Plant",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}


@Composable
fun PlantListScreen(navController: NavController, viewModel: TreeLogsViewModel) {

    //read list from ViewModel
    val plants = viewModel.plantList

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBar(title = "Plant List (${plants.size})", navController = navController)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "All Logged Plants",
                style = MaterialTheme.typography.titleMedium, color = PLTextDark)
            Text(text = "A221505 — Tap a plant to view detail",
                style = MaterialTheme.typography.bodySmall, color = PLTextMid)

            //Empty state
            if (plants.isEmpty()) {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = MaterialTheme.shapes.medium,
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Filled.List, contentDescription = null,
                            tint = PLGrayText, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "No plants logged yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = PLGrayText, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Tap Log New Plant to add your first plant.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PLGrayText, textAlign = TextAlign.Center)
                    }
                }
            } else {
                //each plant as a Card
                plants.forEachIndexed { index, plant ->
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = MaterialTheme.shapes.medium,
                        colors    = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        //Lab 4 — tap → selectPlant → navigate to detail
                        onClick = {
                            viewModel.selectPlant(plant)
                            navController.navigate(Routes.PLANT_DETAIL)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            //Plant number badge
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "#${index + 1}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = plant.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium)
                                Text(text = plant.species,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PLTextMid)
                                if (plant.location.isNotBlank()) {
                                    Text(text = "📍 ${plant.location}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PLGrayText)
                                }
                            }
                            //Delete button
                            TextButton(onClick = { viewModel.removePlant(index) }) {
                                Text("Delete",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            //Add another plant button
            Button(
                onClick  = { navController.navigate(Routes.ADD_PLANT) },
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.small
            ) {
                Text("+ Log New Plant",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}


@Composable
fun PlantDetailScreen(navController: NavController, viewModel: TreeLogsViewModel) {

    val plant = viewModel.selectedPlant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBar(title = "Plant Detail", navController = navController)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(text = "Plant Information",
                style = MaterialTheme.typography.titleMedium, color = PLTextDark)
            Text(text = "A221505 — Full plant record",
                style = MaterialTheme.typography.bodySmall, color = PLTextMid)

            if (plant == null) {
                //No plant state
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = MaterialTheme.shapes.medium,
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "No plant selected.",
                            style     = MaterialTheme.typography.bodyLarge,
                            color     = PLGrayText, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Go back and tap a plant from the list.",
                            style     = MaterialTheme.typography.bodySmall,
                            color     = PLGrayText, textAlign = TextAlign.Center)
                    }
                }
            } else {
                //all plant info
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = MaterialTheme.shapes.medium,
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        //Header row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(PLWhiteOverlay),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Filled.Star,
                                    contentDescription = null, tint = PLWhite,
                                    modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(text = "Plant Record",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimary)
                        }

                        HorizontalDivider(color = PLAccent.copy(alpha = 0.4f), thickness = 0.5.dp)

                        //ViewModel
                        DetailRow(label = "Plant Name", value = plant.name)
                        DetailRow(label = "Species",    value = plant.species)
                        DetailRow(label = "Location",   value = plant.location)
                        DetailRow(label = "Notes",      value = plant.notes)
                    }
                }

                //Back to list button
                Button(
                    onClick  = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Back to Plant List",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
fun HealthCheckScreen(navController: NavController, viewModel: TreeLogsViewModel) {

    //local state for form
    var plantName by remember { mutableStateOf("") }
    var status    by remember { mutableStateOf("") }
    var symptom   by remember { mutableStateOf("") }

    //expand card state
    var expandedIndex by remember { mutableStateOf(-1) }

    val healthChecks = viewModel.healthList

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBar(title = "Health Check", navController = navController)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(text = "Plant Health Check",
                style = MaterialTheme.typography.titleMedium, color = PLTextDark)
            Text(text = "A221505 — Log plant health status",
                style = MaterialTheme.typography.bodySmall, color = PLTextMid)

            //Card wrap health form
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = MaterialTheme.shapes.medium,
                colors    = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    //TextField - plant name
                    OutlinedTextField(
                        value = plantName, onValueChange = { plantName = it },
                        label = { Text("Plant Name",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor)
                    )
                    //TextField - status
                    OutlinedTextField(
                        value = status, onValueChange = { status = it },
                        label = { Text("Status (Healthy / Unhealthy)",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor)
                    )
                    //TextField - symptom
                    OutlinedTextField(
                        value = symptom, onValueChange = { symptom = it },
                        label = { Text("Observed Symptoms",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor)
                    )
                    //save to ViewModel
                    Button(
                        onClick = {
                            if (plantName.isNotBlank() && status.isNotBlank()) {
                                viewModel.addHealthCheck(
                                    plantName = plantName,
                                    status    = status,
                                    symptom   = symptom
                                )
                                plantName = ""; status = ""; symptom = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled  = plantName.isNotBlank() && status.isNotBlank(),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Save Health Check",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }

            if (healthChecks.isNotEmpty()) {
                Text(text = "Health Check Records (${healthChecks.size})",
                    style = MaterialTheme.typography.titleSmall, color = PLTextDark)


                healthChecks.forEachIndexed { index, check ->
                    Card(
                        modifier  = Modifier
                            .fillMaxWidth()
                            //Lab 3 — animation from Lab 3
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness    = Spring.StiffnessLow
                                )
                            )
                            .clickable {
                                expandedIndex = if (expandedIndex == index) -1 else index
                            },
                        shape     = MaterialTheme.shapes.medium,
                        colors    = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = check.plantName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium)
                                    Text(
                                        text  = check.status,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (check.status.contains("Healthy", true))
                                            PLMidGreen else MaterialTheme.colorScheme.error
                                    )
                                }
                                Row {
                                    TextButton(onClick = { viewModel.removeHealthCheck(index) }) {
                                        Text("Delete",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error)
                                    }
                                    Icon(
                                        imageVector = if (expandedIndex == index)
                                            Icons.Filled.KeyboardArrowUp
                                        else Icons.Filled.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = PLGrayText,
                                        modifier = Modifier.size(22.dp).align(Alignment.CenterVertically)
                                    )
                                }
                            }
                            //Expanded detail
                            if (expandedIndex == index && check.symptom.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = PLBorderColor, thickness = 0.5.dp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Symptoms: ${check.symptom}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PLTextMid)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TipsScreen(navController: NavController) {

    //Expand card state
    var expandedIndex by remember { mutableStateOf(-1) }

    //Static tips data
    val tips = listOf(
        Pair("SDG 15 — Life on Land",
            "SDG 15 aims to protect, restore and promote sustainable use of " +
                    "terrestrial ecosystems, halt deforestation, restore degraded land, " +
                    "and halt biodiversity loss. Malaysia is home to one of the world's " +
                    "oldest rainforests, over 130 million years old."),
        Pair("Watering Tips",
            "Coming Soon..." +
                    "If soil dry, water the soil... "),
        Pair("Sunlight Requirements",
            "Coming Soon..." +
                    "For Newbie, put it under sun..."),
        Pair("Soil and Fertilizer",
            "Coming Soon..." +
                    "Fertilize lightly from time to time...  Twice a week "),
        Pair("Common Plant Diseases",
            "Coming Soon..." +
                    "A lot of yellow leaves... and Holes and leaves... "),
        Pair("How TreeLogs Supports SDG 15",
            "By logging plants and tracking their health, TreeLogs encourages " +
                    "awareness of local plant biodiversity. Users can monitor plant " +
                    "conditions and take early action to protect plant life — " +
                    "directly contributing to SDG 15 Life on Land goals.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBar(title = "Guide & Tips", navController = navController)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Plant Care Guide",
                style = MaterialTheme.typography.titleMedium, color = PLTextDark)
            Text(text = "A221505 — SDG 15 info and plant tips",
                style = MaterialTheme.typography.bodySmall, color = PLTextMid)

            //SDG 15 header card
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = MaterialTheme.shapes.medium,
                colors    = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(PLWhiteOverlay),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "15", fontSize = 20.sp,
                            fontWeight = FontWeight.Bold, color = PLWhite)
                    }
                    Column {
                        Text(text = "SDG 15 — Life on Land",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimary)
                        Text(text = "Protect, restore and promote sustainable use of terrestrial ecosystems",
                            style = MaterialTheme.typography.bodySmall, color = PLAccent)
                    }
                }
            }

            //Lab 3 — expandable tip cards with animateContentSize
            tips.forEachIndexed { index, tip ->
                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        //Lab 3 — animation
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness    = Spring.StiffnessLow
                            )
                        )
                        .clickable {
                            expandedIndex = if (expandedIndex == index) -1 else index
                        },
                    shape     = MaterialTheme.shapes.medium,
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = tip.first,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = if (expandedIndex == index)
                                    Icons.Filled.KeyboardArrowUp
                                else Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = PLGrayText,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        //Expanded content
                        if (expandedIndex == index) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = PLBorderColor, thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = tip.second,
                                style = MaterialTheme.typography.bodyMedium,
                                color = PLTextMid)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

//PREVIEW
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTreeLogs() {
    PlantLogTheme {
        Surface(modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background) {
            TreeLogsApp()
        }
    }
}