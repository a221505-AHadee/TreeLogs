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

//ROOT — NavHost
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

//REUSE — FEATURE BUTTON
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

//S1 - HOME SCREEN
@Composable
fun HomeScreen(navController: NavController, viewModel: TreeLogsViewModel) {

    var userName    by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("Yahoo") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        //Greeting header
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
            //Name input card
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

            //Stats row — from ViewModel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                //Stat card 1 — reads plantList.size
                Card(
                    modifier  = Modifier.weight(1f),
                    shape     = MaterialTheme.shapes.medium,
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "${viewModel.plantList.size}",
                            fontSize = 32.sp, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary)
                        Text(text = "Plants Logged",
                            style = MaterialTheme.typography.bodySmall, color = PLAccent)
                    }
                }
                //Stat card 2 — reads healthList.size
                Card(
                    modifier  = Modifier.weight(1f),
                    shape     = MaterialTheme.shapes.medium,
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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

            //Feature buttons to screen
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

        //Bottom nav bar
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
            //FAB — Add plant
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

//REUSE — BOTTOM NAV ITEM
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

//S2 - ADD PLANT — in progress
@Composable
fun AddPlantScreen(navController: NavController, viewModel: TreeLogsViewModel) {
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar(title = "Log New Plant", navController = navController)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Screen 2 — Add Plant (in progress)",
                style = MaterialTheme.typography.bodyLarge, color = PLTextMid)
        }
    }
}

//S3 - PLANT LIST — later
@Composable
fun PlantListScreen(navController: NavController, viewModel: TreeLogsViewModel) {
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar(title = "Plant List", navController = navController)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Screen 3 — Plant List (coming next)",
                style = MaterialTheme.typography.bodyLarge, color = PLTextMid)
        }
    }
}

//S4 - PLANT DETAIL — later
@Composable
fun PlantDetailScreen(navController: NavController, viewModel: TreeLogsViewModel) {
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar(title = "Plant Detail", navController = navController)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Screen 4 — Plant Detail (coming next)",
                style = MaterialTheme.typography.bodyLarge, color = PLTextMid)
        }
    }
}

//S5 - HEALTH CHECK — later
@Composable
fun HealthCheckScreen(navController: NavController, viewModel: TreeLogsViewModel) {
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar(title = "Health Check", navController = navController)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Screen 5 — Health Check (coming next)",
                style = MaterialTheme.typography.bodyLarge, color = PLTextMid)
        }
    }
}

//S6 - TIPS — later
@Composable
fun TipsScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
        .background(MaterialTheme.colorScheme.background)) {
        TopBar(title = "Guide & Tips", navController = navController)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Screen 6 — Guide and Tips (coming next)",
                style = MaterialTheme.typography.bodyLarge, color = PLTextMid)
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