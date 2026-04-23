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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
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

//Navigation Routes
object PlantRoutes {
    const val HOME         = "home"
    const val ADD_PLANT    = "add_plant"
    const val PLANT_DETAIL = "plant_detail"
}

//Main Activity
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
                    PlantLogApp()
                }
            }
        }
    }
}

//Root — NavHost
@Composable
fun PlantLogApp() {
    val navController  = rememberNavController()
    val plantViewModel : PlantViewModel = viewModel()

    NavHost(
        navController    = navController,
        startDestination = PlantRoutes.HOME
    ) {
        composable(route = PlantRoutes.HOME) {
            HomeScreen(navController = navController, plantViewModel = plantViewModel)
        }
        composable(route = PlantRoutes.ADD_PLANT) {
            AddPlantScreen(navController = navController, plantViewModel = plantViewModel)
        }
        composable(route = PlantRoutes.PLANT_DETAIL) {
            PlantDetailScreen(navController = navController, plantViewModel = plantViewModel)
        }
    }
}

@Composable
fun HomeScreen(navController: NavController, plantViewModel: PlantViewModel) {

    var userName          by remember { mutableStateOf("") }
    var displayName       by remember { mutableStateOf("Sara") }
    var searchText        by remember { mutableStateOf("") }
    var searchResult      by remember { mutableStateOf("") }
    var expandedToolIndex by remember { mutableStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            GreetingSection(displayName = displayName)
            Spacer(modifier = Modifier.height(8.dp))
            NameInputSection(
                userName     = userName,
                onNameChange = { userName = it },
                onEnterClick = {
                    if (userName.isNotBlank()) {
                        displayName = userName
                        userName    = ""
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            SearchBar(
                searchText     = searchText,
                onSearchChange = { searchText = it },
                onSearchClick  = {
                    if (searchText.isNotBlank()) {
                        searchResult = searchText
                        searchText   = ""
                    }
                }
            )
            if (searchResult.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                SearchResultText(searchResult = searchResult)
            }
            Spacer(modifier = Modifier.height(20.dp))
            NavigationButtons(navController = navController)
            Spacer(modifier = Modifier.height(20.dp))
            ActionCards(navController = navController)
            Spacer(modifier = Modifier.height(20.dp))
            ToolsSection(
                expandedToolIndex = expandedToolIndex,
                onToolClick       = { index ->
                    expandedToolIndex = if (expandedToolIndex == index) -1 else index
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        BottomNavBar(navController = navController)
    }
}

// Navigation Buttons
@Composable
fun NavigationButtons(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick  = { navController.navigate(PlantRoutes.ADD_PLANT) },
            modifier = Modifier.weight(1f),
            colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape    = MaterialTheme.shapes.small
        ) {
            Text(text = "+ Add Plant", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary)
        }
        Button(
            onClick  = { navController.navigate(PlantRoutes.PLANT_DETAIL) },
            modifier = Modifier.weight(1f),
            colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape    = MaterialTheme.shapes.small
        ) {
            Text(text = "View Detail", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary)
        }
    }
}


//  ADD PLANT SCREEN

@Composable
fun AddPlantScreen(navController: NavController, plantViewModel: PlantViewModel) {

    var plantName    by remember { mutableStateOf("") }
    var plantSpecies by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // back arrow
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
            Text(text = "Add Plant", style = MaterialTheme.typography.titleMedium, color = PLLight)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Add a New Plant",
                style = MaterialTheme.typography.titleMedium, color = PLTextDark)
            Text(text = "A221505 — Fill in the plant details",
                style = MaterialTheme.typography.bodySmall, color = PLTextMid)

            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = MaterialTheme.shapes.medium,
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Plant Name
                    OutlinedTextField(
                        value         = plantName,
                        onValueChange = { plantName = it },
                        label         = { Text(text = "Plant Name",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(),
                        shape    = MaterialTheme.shapes.small,
                        colors   = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = PLMidGreen,
                            unfocusedBorderColor = PLBorderColor
                        )
                    )

                    // Species
                    OutlinedTextField(
                        value         = plantSpecies,
                        onValueChange = { plantSpecies = it },
                        label         = { Text(text = "Species",
                            style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                        modifier = Modifier.fillMaxWidth(),
                        shape    = MaterialTheme.shapes.small,
                        colors   = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = PLMidGreen,
                            unfocusedBorderColor = PLBorderColor
                        )
                    )

                    // Save
                    Button(
                        onClick = {
                            if (plantName.isNotBlank() && plantSpecies.isNotBlank()) {
                                plantViewModel.updatePlant(
                                    name    = plantName,
                                    species = plantSpecies
                                )
                                navController.navigate(PlantRoutes.PLANT_DETAIL)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape    = MaterialTheme.shapes.small
                    ) {
                        Text(text = "Save Plant",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}


//  PLANT DETAIL SCREEN

@Composable
fun PlantDetailScreen(navController: NavController, plantViewModel: PlantViewModel) {

    val plant = plantViewModel.plantData

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // back arrow
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
            Text(text = "Plant Detail", style = MaterialTheme.typography.titleMedium, color = PLLight)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Plant Detail",
                style = MaterialTheme.typography.titleMedium, color = PLTextDark)
            Text(text = "A221505 — Saved plant information",
                style = MaterialTheme.typography.bodySmall, color = PLTextMid)

            if (plant.name.isEmpty()) {
                // No plant
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = MaterialTheme.shapes.medium,
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "No plant added yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = PLGrayText, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Go back and add a plant first.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PLGrayText, textAlign = TextAlign.Center)
                    }
                }
            } else {
                // Show saved plant
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = MaterialTheme.shapes.medium,
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
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
                            Text(text = "Plant Logged",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimary)
                        }

                        HorizontalDivider(color = PLAccent.copy(alpha = 0.4f), thickness = 0.5.dp)

                        // Plant Name ViewModel
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Plant Name",
                                style = MaterialTheme.typography.bodySmall, color = PLAccent)
                            Text(text = plant.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Medium)
                        }

                        // Species ViewModel
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Species",
                                style = MaterialTheme.typography.bodySmall, color = PLAccent)
                            Text(text = plant.species,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Button(
                    onClick  = {
                        navController.navigate(PlantRoutes.HOME) {
                            popUpTo(PlantRoutes.HOME) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape    = MaterialTheme.shapes.small
                ) {
                    Text(text = "Back to Home",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary)
                }

                OutlinedButton(
                    onClick  = { navController.navigate(PlantRoutes.ADD_PLANT) },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = MaterialTheme.shapes.small
                ) {
                    Text(text = "Add Another Plant",
                        style = MaterialTheme.typography.bodyMedium, color = PLMidGreen)
                }
            }
        }
    }
}


@Composable
fun GreetingSection(displayName: String) {
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
            Text(text = "Good day, $displayName!",
                style = MaterialTheme.typography.titleMedium, color = PLLight)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "A221505 — Start logging your green journey!",
                style = MaterialTheme.typography.bodySmall, color = PLAccent)
        }
    }
}

@Composable
fun NameInputSection(userName: String, onNameChange: (String) -> Unit, onEnterClick: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape     = MaterialTheme.shapes.medium,
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Update your name",
                style = MaterialTheme.typography.titleSmall, color = PLTextDark)
            OutlinedTextField(
                value = userName, onValueChange = onNameChange,
                label = { Text(text = "Enter your name",
                    style = MaterialTheme.typography.bodyMedium, color = PLTextMid) },
                modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor)
            )
            Button(
                onClick = onEnterClick, modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.small
            ) {
                Text(text = "Update Greeting",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun SearchBar(searchText: String, onSearchChange: (String) -> Unit, onSearchClick: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape     = MaterialTheme.shapes.medium,
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = searchText, onValueChange = onSearchChange,
                label = { Text(text = "Search for a plant",
                    style = MaterialTheme.typography.bodyMedium, color = PLGrayText) },
                modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.small,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PLMidGreen, unfocusedBorderColor = PLBorderColor),
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = null,
                        tint = PLMidGreen, modifier = Modifier.size(20.dp))
                }
            )
            Button(
                onClick = onSearchClick,
                colors  = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape   = MaterialTheme.shapes.small, modifier = Modifier.height(56.dp)
            ) {
                Text(text = "Search",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun SearchResultText(searchResult: String) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape     = MaterialTheme.shapes.medium,
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Searching for: $searchResult",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ActionCards(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier  = Modifier.weight(1f).height(180.dp),
            shape     = MaterialTheme.shapes.large,
            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            onClick   = { navController.navigate(PlantRoutes.ADD_PLANT) }
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
                    .background(PLWhiteOverlay), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Filled.Star, contentDescription = null,
                        tint = PLWhite, modifier = Modifier.size(26.dp))
                }
                Column {
                    Text(text = "LOG PLANT",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Record a new plant",
                        style = MaterialTheme.typography.bodySmall, color = PLAccent)
                }
            }
        }
        Card(
            modifier  = Modifier.weight(1f).height(180.dp),
            shape     = MaterialTheme.shapes.large,
            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
                    .background(PLLight), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Filled.Warning, contentDescription = null,
                        tint = PLMidGreen, modifier = Modifier.size(26.dp))
                }
                Column {
                    Text(text = "HEALTH CHECK",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Check your plant's status",
                        style = MaterialTheme.typography.bodySmall, color = PLTextMid)
                }
            }
        }
    }
}

@Composable
fun ToolsSection(expandedToolIndex: Int, onToolClick: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "Plant Tools", style = MaterialTheme.typography.titleSmall,
            color = PLTextDark, modifier = Modifier.padding(bottom = 4.dp))
        ToolItem("Ask Expert",  "Instant help for your plants",
            "Get personalised advice from our plant experts. Available 24/7.",
            Icons.Filled.Info,    true,  expandedToolIndex == 0) { onToolClick(0) }
        ToolItem("Sun Tracker", "Measure sunlight for your plants",
            "Track daily sunlight in your garden. Get recommendations.",
            Icons.Filled.Star,    false, expandedToolIndex == 1) { onToolClick(1) }
        ToolItem("Care Guides", "Learn how to care for your plants",
            "Browse step-by-step guides for 500+ plant species.",
            Icons.Filled.Warning, false, expandedToolIndex == 2) { onToolClick(2) }
    }
}

@Composable
fun ToolItem(
    label: String, sublabel: String, expandedDetail: String,
    icon: ImageVector, isFilled: Boolean, isExpanded: Boolean, onClick: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth()
            .animateContentSize(animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness    = Spring.StiffnessLow))
            .clickable { onClick() },
        shape     = MaterialTheme.shapes.medium,
        colors    = CardDefaults.cardColors(
            containerColor = if (isFilled) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                    .background(if (isFilled) PLWhiteOverlay else PLLight),
                    contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null,
                        tint = if (isFilled) PLWhite else PLMidGreen,
                        modifier = Modifier.size(26.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = label, style = MaterialTheme.typography.bodyLarge,
                        color = if (isFilled) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(text = sublabel, style = MaterialTheme.typography.bodySmall,
                        color = if (isFilled) PLAccent else PLTextMid)
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp
                    else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isFilled) PLWhite else PLGrayText,
                    modifier = Modifier.size(24.dp)
                )
            }
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    color = if (isFilled) PLAccent.copy(alpha = 0.4f) else PLBorderColor,
                    thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = expandedDetail, style = MaterialTheme.typography.bodyMedium,
                    color = if (isFilled) PLLight else PLTextMid)
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, PLBorderColor)
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        NavItem(label = "Home",      icon = Icons.Filled.Home,    isActive = true)
        NavItem(label = "Explore",   icon = Icons.Filled.Search,  isActive = false)
        Box(
            modifier = Modifier.size(56.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { navController.navigate(PlantRoutes.ADD_PLANT) },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "+", fontSize = 28.sp, color = PLWhite, fontWeight = FontWeight.Light)
        }
        NavItem(label = "My Plants", icon = Icons.Filled.Star,    isActive = false)
        NavItem(label = "More",      icon = Icons.Filled.MoreVert, isActive = false)
    }
}

@Composable
fun NavItem(label: String, icon: ImageVector, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(imageVector = icon, contentDescription = label,
            tint = if (isActive) MaterialTheme.colorScheme.primary else PLGrayText,
            modifier = Modifier.size(24.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall,
            color = if (isActive) MaterialTheme.colorScheme.primary else PLGrayText,
            fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
            textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "PlantLog Lab4")
@Composable
fun PlantLogPreview() {
    PlantLogTheme {
        Surface(modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background) {
            PlantLogApp()
        }
    }
}