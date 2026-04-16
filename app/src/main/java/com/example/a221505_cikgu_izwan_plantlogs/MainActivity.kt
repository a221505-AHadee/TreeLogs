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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a221505_cikgu_izwan_plantlogs.ui.theme.PlantLogTheme
import com.example.a221505_cikgu_izwan_plantlogs.ui.theme.PLAccent
import com.example.a221505_cikgu_izwan_plantlogs.ui.theme.PLBorderColor
import com.example.a221505_cikgu_izwan_plantlogs.ui.theme.PLGrayText
import com.example.a221505_cikgu_izwan_plantlogs.ui.theme.PLHeroGreen
import com.example.a221505_cikgu_izwan_plantlogs.ui.theme.PLLight
import com.example.a221505_cikgu_izwan_plantlogs.ui.theme.PLMidGreen
import com.example.a221505_cikgu_izwan_plantlogs.ui.theme.PLTextDark
import com.example.a221505_cikgu_izwan_plantlogs.ui.theme.PLTextMid
import com.example.a221505_cikgu_izwan_plantlogs.ui.theme.PLWhite
import com.example.a221505_cikgu_izwan_plantlogs.ui.theme.PLWhiteOverlay

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
                    PlantLogScreen()
                }
            }
        }
    }
}

@Composable
fun PlantLogScreen() {

    var userName          by remember { mutableStateOf("") }
    var displayName       by remember { mutableStateOf("Yahoo") }
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
            ActionCards()
            Spacer(modifier = Modifier.height(20.dp))
            ToolsSection(
                expandedToolIndex = expandedToolIndex,
                onToolClick       = { index ->
                    expandedToolIndex = if (expandedToolIndex == index) -1 else index
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        BottomNavBar()
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
            Icon(
                imageVector        = Icons.Filled.Star,
                contentDescription = null,
                tint               = PLMidGreen,
                modifier           = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text  = "Good day, $displayName!",
                style = MaterialTheme.typography.titleMedium,
                color = PLLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text  = "A221505 — Start logging your green journey!",
                style = MaterialTheme.typography.bodySmall,
                color = PLAccent
            )
        }
    }
}
@Composable
fun NameInputSection(
    userName     : String,
    onNameChange : (String) -> Unit,
    onEnterClick : () -> Unit
) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape     = MaterialTheme.shapes.medium,
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text  = "Update your name",
                style = MaterialTheme.typography.titleSmall,
                color = PLTextDark
            )
            OutlinedTextField(
                value         = userName,
                onValueChange = onNameChange,
                label         = {
                    Text(
                        text  = "Enter your name",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PLTextMid
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape    = MaterialTheme.shapes.small,
                colors   = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = PLMidGreen,
                    unfocusedBorderColor = PLBorderColor
                )
            )
            Button(
                onClick  = onEnterClick,
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text  = "Update Greeting",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    searchText     : String,
    onSearchChange : (String) -> Unit,
    onSearchClick  : () -> Unit
) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape     = MaterialTheme.shapes.medium,
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value         = searchText,
                onValueChange = onSearchChange,
                label         = {
                    Text(
                        text  = "Search for a plant",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PLGrayText
                    )
                },
                modifier = Modifier.weight(1f),
                shape    = MaterialTheme.shapes.small,
                colors   = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = PLMidGreen,
                    unfocusedBorderColor = PLBorderColor
                ),
                leadingIcon = {
                    Icon(
                        imageVector        = Icons.Filled.Search,
                        contentDescription = null,
                        tint               = PLMidGreen,
                        modifier           = Modifier.size(20.dp)
                    )
                }
            )
            Button(
                onClick  = onSearchClick,
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape    = MaterialTheme.shapes.small,
                modifier = Modifier.height(56.dp)
            ) {
                Text(
                    text  = "Search",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun SearchResultText(searchResult: String) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape     = MaterialTheme.shapes.medium,
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector        = Icons.Filled.Search,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text       = "Searching for: $searchResult",
                style      = MaterialTheme.typography.bodyMedium,
                color      = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ActionCards() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier  = Modifier.weight(1f).height(180.dp),
            shape     = MaterialTheme.shapes.large,
            colors    = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PLWhiteOverlay),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.Star, contentDescription = null,
                        tint = PLWhite, modifier = Modifier.size(26.dp))
                }
                Column {
                    Text(text = "LOG PLANT",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Record a new plant",
                        style = MaterialTheme.typography.bodySmall,
                        color = PLAccent)
                }
            }
        }

        Card(
            modifier  = Modifier.weight(1f).height(180.dp),
            shape     = MaterialTheme.shapes.large,
            colors    = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PLLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.Warning, contentDescription = null,
                        tint = PLMidGreen, modifier = Modifier.size(26.dp))
                }
                Column {
                    Text(text = "HEALTH CHECK",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Check your plant's status",
                        style = MaterialTheme.typography.bodySmall,
                        color = PLTextMid)
                }
            }
        }
    }
}

@Composable
fun ToolsSection(
    expandedToolIndex : Int,
    onToolClick       : (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text     = "Plant Tools",
            style    = MaterialTheme.typography.titleSmall,
            color    = PLTextDark,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        ToolItem(
            label          = "Ask Expert",
            sublabel       = "Instant help for your plants",
            expandedDetail = "Get personalised advice from our plant experts. Available 24/7 for all your plant questions.",
            icon           = Icons.Filled.Info,
            isFilled       = true,
            isExpanded     = expandedToolIndex == 0,
            onClick        = { onToolClick(0) }
        )
        ToolItem(
            label          = "Sun Tracker",
            sublabel       = "Measure sunlight for your plants",
            expandedDetail = "Track daily sunlight levels in your garden. Get recommendations based on your plant species.",
            icon           = Icons.Filled.Star,
            isFilled       = false,
            isExpanded     = expandedToolIndex == 1,
            onClick        = { onToolClick(1) }
        )
        ToolItem(
            label          = "Care Guides",
            sublabel       = "Learn how to care for your plants",
            expandedDetail = "Browse step-by-step care guides for over 500 plant species. Written by botanists.",
            icon           = Icons.Filled.Warning,
            isFilled       = false,
            isExpanded     = expandedToolIndex == 2,
            onClick        = { onToolClick(2) }
        )
    }
}


@Composable
fun ToolItem(
    label         : String,
    sublabel      : String,
    expandedDetail: String,
    icon          : ImageVector,
    isFilled      : Boolean,
    isExpanded    : Boolean,
    onClick       : () -> Unit
) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness    = Spring.StiffnessLow
                )
            )
            .clickable { onClick() },
        shape     = MaterialTheme.shapes.medium,
        colors    = CardDefaults.cardColors(
            containerColor = if (isFilled) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isFilled) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text  = sublabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isFilled) PLAccent else PLTextMid
                    )
                }
                Icon(
                    imageVector        = if (isExpanded) Icons.Filled.KeyboardArrowUp
                    else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint               = if (isFilled) PLWhite else PLGrayText,
                    modifier           = Modifier.size(24.dp)
                )
            }
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    color     = if (isFilled) PLAccent.copy(alpha = 0.4f) else PLBorderColor,
                    thickness = 0.5.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text  = expandedDetail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isFilled) PLLight else PLTextMid
                )
            }
        }
    }
}

@Composable
fun BottomNavBar() {
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
        NavItem(label = "Home",      icon = Icons.Filled.Home,     isActive = true)
        NavItem(label = "Explore",   icon = Icons.Filled.Search,   isActive = false)
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "+", fontSize = 28.sp, color = PLWhite,
                fontWeight = FontWeight.Light)
        }
        NavItem(label = "My Plants", icon = Icons.Filled.Star,     isActive = false)
        NavItem(label = "More",      icon = Icons.Filled.MoreVert,  isActive = false)
    }
}

@Composable
fun NavItem(label: String, icon: ImageVector, isActive: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = label,
            tint               = if (isActive) MaterialTheme.colorScheme.primary else PLGrayText,
            modifier           = Modifier.size(24.dp)
        )
        Text(
            text       = label,
            style      = MaterialTheme.typography.labelSmall,
            color      = if (isActive) MaterialTheme.colorScheme.primary else PLGrayText,
            fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
            textAlign  = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "PlantLog Lab3")
@Composable
fun PlantLogPreview() {
    PlantLogTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color    = MaterialTheme.colorScheme.background
        ) {
            PlantLogScreen()
        }
    }
}