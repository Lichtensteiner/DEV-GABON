package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.db.*
import com.example.viewmodel.DevGabonViewModel
import com.example.viewmodel.Screen

@Composable
fun ProfileImage(
    picture: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    if (picture.startsWith("http://") || picture.startsWith("https://")) {
        AsyncImage(
            model = picture,
            contentDescription = "Profile Picture",
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = modifier.size(size)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = if (picture.isNotBlank()) picture else "👤",
                    fontSize = (size.value * 0.45).sp
                )
            }
        }
    }
}

// --- Artisanal Custom Curve Chart (No External Dependencies) ---
@Composable
fun MetricGrowthCurve(
    points: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF10B981), // Gabon Green
    accentColor: Color = Color(0xFFFBBF24)  // Gabon Yellow
) {
    val colors = MaterialTheme.colorScheme
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        // Horizontal gridline strokes
        val gridLines = 3
        for (i in 0..gridLines) {
            val y = height * i / gridLines
            drawLine(
                color = colors.outline.copy(alpha = 0.08f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )
        }
        
        if (points.isNotEmpty()) {
            val maxX = (points.size - 1).coerceAtLeast(1)
            val maxY = points.maxOrNull()?.takeIf { it > 0f } ?: 10f
            
            val path = Path()
            val fillPath = Path()
            
            points.forEachIndexed { idx, value ->
                val x = width * idx / maxX
                val y = height - (height * value / maxY) * 0.75f - 10.dp.toPx()
                
                if (idx == 0) {
                    path.moveTo(x, y)
                    fillPath.moveTo(x, height)
                    fillPath.lineTo(x, y)
                } else {
                    val prevX = width * (idx - 1) / maxX
                    val prevY = height - (height * points[idx - 1] / maxY) * 0.75f - 10.dp.toPx()
                    
                    // Bezier anchor control coordinates
                    val cp1X = prevX + (x - prevX) / 2
                    val cp1Y = prevY
                    val cp2X = prevX + (x - prevX) / 2
                    val cp2Y = y
                    
                    path.cubicTo(cp1X, cp1Y, cp2X, cp2Y, x, y)
                    fillPath.cubicTo(cp1X, cp1Y, cp2X, cp2Y, x, y)
                }
                
                if (idx == points.size - 1) {
                    fillPath.lineTo(x, height)
                    fillPath.close()
                }
            }
            
            // Draw Area Gradient Filling
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent)
                )
            )
            
            // Draw main Stroke Curve
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx())
            )
            
            // Draw Interactive glowing Nodes
            points.forEachIndexed { idx, value ->
                val x = width * idx / maxX
                val y = height - (height * value / maxY) * 0.75f - 10.dp.toPx()
                
                drawCircle(
                    color = colors.background,
                    radius = 5.dp.toPx(),
                    center = Offset(x, y)
                )
                drawCircle(
                    color = accentColor,
                    radius = 3.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
    }
}

// --- Dynamic Role Distribution Donut Chart ---
@Composable
fun RoleDistributionDonut(
    developerCount: Int,
    designerCount: Int,
    recruiterCount: Int,
    studentCount: Int,
    modifier: Modifier = Modifier
) {
    val total = (developerCount + designerCount + recruiterCount + studentCount).toFloat().coerceAtLeast(1f)
    
    val pDev = developerCount / total
    val pDes = designerCount / total
    val pRec = recruiterCount / total
    val pStu = studentCount / total
    
    Canvas(modifier = modifier) {
        var startAngle = -90f
        val strokeWidth = 14.dp.toPx()
        val radiusSize = size.minDimension - strokeWidth
        val xOffset = (size.width - radiusSize) / 2
        val yOffset = (size.height - radiusSize) / 2
        
        val segments = listOf(
            Pair(pDev * 360f, Color(0xFF10B981)), // Green (Dev)
            Pair(pDes * 360f, Color(0xFFFBBF24)), // Yellow (Designer)
            Pair(pRec * 360f, Color(0xFF3B82F6)), // Blue (Recruiter)
            Pair(pStu * 360f, Color(0xFF8B5CF6))  // Purple (Student)
        )
        
        segments.forEach { (sweep, color) ->
            if (sweep > 0f) {
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(xOffset, yOffset),
                    size = androidx.compose.ui.geometry.Size(radiusSize, radiusSize),
                    style = Stroke(width = strokeWidth)
                )
                startAngle += sweep
            }
        }
    }
}

// --- Artisanal Bar Chart for publications ---
@Composable
fun ActivityHistogram(
    postsCount: Int,
    tutorialsCount: Int,
    jobsCount: Int,
    campusCount: Int,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val maxVal = maxOf(postsCount, tutorialsCount, jobsCount, campusCount).toFloat().coerceAtLeast(1f)
        
        val values = listOf(postsCount.toFloat(), tutorialsCount.toFloat(), jobsCount.toFloat(), campusCount.toFloat())
        val barColors = listOf(Color(0xFF10B981), Color(0xFFFBBF24), Color(0xFF3B82F6), Color(0xFF8B5CF6))
        
        val barWidth = (width / 4) * 0.45f
        val step = width / 4
        
        values.forEachIndexed { index, value ->
            val barHeight = (value / maxVal) * height * 0.82f
            val x = index * step + (step - barWidth) / 2
            val y = height - barHeight
            
            drawRoundRect(
                color = barColors[index],
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: DevGabonViewModel,
    onBack: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val languageCode by viewModel.languageCode.collectAsState()
    
    // Core data streams
    val profiles by viewModel.allProfiles.collectAsState()
    val posts by viewModel.allPosts.collectAsState()
    val articles by viewModel.allArticles.collectAsState()
    val jobs by viewModel.allJobs.collectAsState()
    val activeProfile by viewModel.activeUserProfile.collectAsState()
    
    // Campus data streams
    val schools by viewModel.allSchools.collectAsState()
    val teachers by viewModel.allTeachers.collectAsState()
    val libraryItems by viewModel.allLibraryItems.collectAsState()
    val internships by viewModel.allAcademicInternships.collectAsState()
    val clubs by viewModel.allAcademicClubs.collectAsState()
    val cohortPosts by viewModel.allCohortPosts.collectAsState()

    var activeTabState by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingProfile by remember { mutableStateOf<UserProfileEntity?>(null) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    // Dropdowns and dialog launchers for dynamic campus creations
    var campusTypeToCreate by remember { mutableStateOf<String?>(null) } // "school", "teacher", "library", "internship", "club", "cohort"

    val filteredProfiles = profiles.filter {
        it.fullName.contains(searchQuery, ignoreCase = true) ||
                it.pseudo.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true) ||
                it.role.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (languageCode == "FR") "Console d'Administration" else "Admin Console", 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 18.sp, 
                            color = colors.onBackground
                        )
                        Text(
                            text = if (languageCode == "FR") "PRO GABON - Surveillance & Pilotage Général" else "PRO GABON - Central Control Dashboard", 
                            fontSize = 11.sp, 
                            color = colors.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.background)
        ) {
            // Fine Gabon Flag Ribbon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
            ) {
                Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFF10B981)))
                Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFFBBF24)))
                Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFF3B82F6)))
            }

            // Modern Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = activeTabState,
                edgePadding = 12.dp,
                containerColor = colors.surface,
                contentColor = colors.primary
            ) {
                Tab(
                    selected = activeTabState == 0,
                    onClick = { activeTabState = 0 },
                    text = { Text(if (languageCode == "FR") "Analyses & Graphiques" else "Analytics & Charts", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = activeTabState == 1,
                    onClick = { activeTabState = 1 },
                    text = { Text(if (languageCode == "FR") "Gestion des Rôles" else "Roles & Accounts", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = activeTabState == 2,
                    onClick = { activeTabState = 2 },
                    text = { Text(if (languageCode == "FR") "Éléments Campus" else "Campus Control", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = activeTabState == 3,
                    onClick = { activeTabState = 3 },
                    text = { Text(if (languageCode == "FR") "Danger & Reset" else "Danger Zone", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            AnimatedContent(
                targetState = activeTabState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier.weight(1f),
                label = ""
            ) { tab ->
                when (tab) {
                    0 -> {
                        // --- STATS & CHARTS TAB ---
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    text = if (languageCode == "FR") "Indicateurs d'Activité Provinciale" else "Regional Activity Matrix",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = colors.primary
                                )
                                Spacer(Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    StatCard(
                                        title = "Membres",
                                        value = profiles.size.toString(),
                                        icon = Icons.Default.People,
                                        color = Color(0xFF10B981),
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatCard(
                                        title = "Posts",
                                        value = posts.size.toString(),
                                        icon = Icons.Default.ChatBubble,
                                        color = Color(0xFFFBBF24),
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatCard(
                                        title = "Tutos",
                                        value = articles.size.toString(),
                                        icon = Icons.Default.Book,
                                        color = Color(0xFF3B82F6),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Growth Curve Chart
                            item {
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                                    border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.15f))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = if (languageCode == "FR") "Courbe de Croissance Temporelle" else "Instantaneous Velocity Curve",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    text = if (languageCode == "FR") "Nouveaux inscrits et engagements récents" else "Recent user sign-ups trends",
                                                    fontSize = 10.sp,
                                                    color = colors.onSurfaceVariant
                                                )
                                            }
                                            Surface(
                                                color = Color(0xFF10B981).copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "+24% ce mois",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF10B981),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        
                                        Spacer(Modifier.height(16.dp))
                                        
                                        // Area Chart Component
                                        val testData = listOf(2f, 4f, 5f, 8f, profiles.size.toFloat().coerceAtLeast(10f))
                                        MetricGrowthCurve(
                                            points = testData,
                                            labels = listOf("Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5"),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(140.dp)
                                        )
                                        
                                        Spacer(Modifier.height(12.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            listOf("Sem 1", "Sem 2", "Sem 3", "Sem 4", "Membres Actuels").forEach { label ->
                                                Text(label, fontSize = 10.sp, color = colors.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }

                            // Donut and Bar Distribution split
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // 1. Donut Role Card
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                                        modifier = Modifier.weight(1.1f),
                                        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.15f))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = if (languageCode == "FR") "Répartition des Métiers" else "Professional Roles",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                            Spacer(Modifier.height(12.dp))
                                            
                                            // Compute counts
                                            val devs = profiles.count { it.role.contains("dev", ignoreCase = true) || it.role.contains("code", ignoreCase = true) }.coerceAtLeast(1)
                                            val des = profiles.count { it.role.contains("design", ignoreCase = true) || it.role.contains("ux", ignoreCase = true) }.coerceAtLeast(1)
                                            val rec = profiles.count { it.role.contains("recru", ignoreCase = true) || it.role.contains("rh", ignoreCase = true) }.coerceAtLeast(1)
                                            val students = profiles.count { it.role.contains("étud", ignoreCase = true) || it.role.contains("eleve", ignoreCase = true) }.coerceAtLeast(1)
                                            
                                            Box(
                                                modifier = Modifier
                                                    .size(70.dp)
                                                    .align(Alignment.CenterHorizontally),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                RoleDistributionDonut(
                                                    developerCount = devs,
                                                    designerCount = des,
                                                    recruiterCount = rec,
                                                    studentCount = students,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                                Text("${profiles.size} P", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                            
                                            Spacer(Modifier.height(10.dp))
                                            // Legend
                                            LegendRow(Color(0xFF10B981), "Dev: $devs")
                                            LegendRow(Color(0xFFFBBF24), "Designer: $des")
                                            LegendRow(Color(0xFF3B82F6), "Recruteur: $rec")
                                            LegendRow(Color(0xFF8B5CF6), "Étudiant: $students")
                                        }
                                    }

                                    // 2. Bar Chart content statistics
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                                        modifier = Modifier.weight(1f),
                                        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.15f))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = if (languageCode == "FR") "Actifs du Réseau" else "Network Contents",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                            Spacer(Modifier.height(16.dp))
                                            
                                            ActivityHistogram(
                                                postsCount = posts.size,
                                                tutorialsCount = articles.size,
                                                jobsCount = jobs.size,
                                                campusCount = schools.size,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(70.dp)
                                            )
                                            
                                            Spacer(Modifier.height(12.dp))
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                HistLegend(Color(0xFF10B981), "Feed: ${posts.size}")
                                                HistLegend(Color(0xFFFBBF24), "Tutos: ${articles.size}")
                                                HistLegend(Color(0xFF3B82F6), "Stages: ${internships.size}")
                                                HistLegend(Color(0xFF8B5CF6), "Clubs: ${clubs.size}")
                                            }
                                        }
                                    }
                                }
                            }
                            
                            item {
                                Spacer(Modifier.height(40.dp))
                            }
                        }
                    }
                    1 -> {
                        // --- USERS & STRUCTURED ROLES TAB ---
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (languageCode == "FR") "Gestion des Rôles des Comptes (${filteredProfiles.size})" else "Structured Roles Allocations",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = colors.primary
                                    )
                                    Text(
                                        text = if (languageCode == "FR") "Recherchez et configurez les badges et titres professionnels" else "Search and configure tech badges & titles",
                                        fontSize = 11.sp,
                                        color = colors.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { showAddDialog = true }) {
                                    Icon(Icons.Default.PersonAdd, contentDescription = "Add User", tint = colors.primary)
                                }
                            }
                            
                            Spacer(Modifier.height(10.dp))

                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text(if (languageCode == "FR") "Rechercher par nom, pseudo ou email..." else "Search email, pseudo, role...", fontSize = 12.sp) },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "", modifier = Modifier.size(20.dp)) },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colors.primary,
                                    unfocusedBorderColor = colors.outline.copy(alpha = 0.4f)
                                )
                            )
                            
                            Spacer(Modifier.height(12.dp))

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredProfiles, key = { it.id }) { user ->
                                    UserManagementRow(
                                        user = user,
                                        isActiveUser = user.email == activeProfile?.email,
                                        onToggleVerify = {
                                            viewModel.createOrUpdateProfile(user.copy(isVerified = !user.isVerified))
                                        },
                                        onEditClick = { editingProfile = user },
                                        onDeleteClick = {
                                            viewModel.deleteUserByAdmin(user.id, user.email)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        // --- CAMPUS GESTION TAB ---
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Column {
                                    Text(
                                        text = if (languageCode == "FR") "Administration du Campus Universitaire" else "Campus Operations Dashboard",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = colors.primary
                                    )
                                    Text(
                                        text = if (languageCode == "FR") "Ajoutez de nouveaux contenus en direct dans les onglets académiques" else "Contribute fresh courses or internships directly",
                                        fontSize = 11.sp,
                                        color = colors.onSurfaceVariant
                                    )
                                }
                            }

                            // 1. Campus Card
                            item {
                                CampusAdminWidget(
                                    title = if (languageCode == "FR") "🏫 Écoles & Campuses (${schools.size})" else "Campuses & Institutions",
                                    desc = if (languageCode == "FR") "Université de Libreville, ENI, IST..." else "Add, update or verify colleges",
                                    icon = Icons.Default.Home,
                                    onAddClick = { campusTypeToCreate = "school" },
                                    colors = colors
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        schools.forEach { sc ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(sc.logoEmoji, fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                                                    Column {
                                                        Text(sc.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                        Text(sc.type, fontSize = 10.sp, color = colors.onSurfaceVariant)
                                                    }
                                                }
                                                if (sc.isVerified) {
                                                    Icon(Icons.Default.Verified, "Certifié", tint = colors.secondary, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // 2. Bibliothèque
                            item {
                                CampusAdminWidget(
                                    title = if (languageCode == "FR") "📚 Bibliothèque Numérique (${libraryItems.size})" else "Digital Library Catalog",
                                    desc = if (languageCode == "FR") "Cours Kotlin, Mémoires ingénieurs, Durcissement de serveurs..." else "Publish educational reference docs",
                                    icon = Icons.Default.Book,
                                    onAddClick = { campusTypeToCreate = "library" },
                                    colors = colors
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        libraryItems.forEach { doc ->
                                            Column(modifier = Modifier.padding(6.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("📖", fontSize = 16.sp, modifier = Modifier.padding(end = 6.dp))
                                                    Text(doc.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                }
                                                Text("Auteur: ${doc.author} (${doc.category}) • ${doc.school}", fontSize = 10.sp, color = colors.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }

                            // 3. Enseignants
                            item {
                                CampusAdminWidget(
                                    title = if (languageCode == "FR") "👴 Enseignants & Professeurs (${teachers.size})" else "Academic Professors Faculty",
                                    desc = if (languageCode == "FR") "Faculté et professeurs certifiés des écoles gabonaises" else "Profile verified academic staff",
                                    icon = Icons.Default.School,
                                    onAddClick = { campusTypeToCreate = "teacher" },
                                    colors = colors
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        teachers.forEach { teach ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(teach.emoji, fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                                                    Column {
                                                        Text(teach.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                        Text(teach.specialties, fontSize = 10.sp, color = colors.onSurfaceVariant)
                                                    }
                                                }
                                                Text("${teach.coursesCount} cours", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.primary)
                                            }
                                        }
                                    }
                                }
                            }

                            // 4. Stages & Offres d'alternance
                            item {
                                CampusAdminWidget(
                                    title = if (languageCode == "FR") "💼 Stages & Offres Campus (${internships.size})" else "Colleges Internships Offers",
                                    desc = if (languageCode == "FR") "Gabon Telecom, ANINF, CDC, Moov..." else "Add internship opportunities",
                                    icon = Icons.Default.Work,
                                    onAddClick = { campusTypeToCreate = "internship" },
                                    colors = colors
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        internships.forEach { inter ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(inter.logoEmoji, fontSize = 16.sp, modifier = Modifier.padding(end = 6.dp))
                                                        Text(inter.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    }
                                                    Text("${inter.company} • Posté par ${inter.postedBySchool}", fontSize = 10.sp, color = colors.onSurfaceVariant)
                                                }
                                                Surface(
                                                    color = if (inter.status == "Disponible") Color(0xFF10B981).copy(alpha = 0.12f) else colors.outline.copy(alpha = 0.2f),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = inter.status,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (inter.status == "Disponible") Color(0xFF10B981) else colors.onSurfaceVariant,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // 5. Clubs techniques
                            item {
                                CampusAdminWidget(
                                    title = if (languageCode == "FR") "🤖 Clubs & Communautés d'étudiants (${clubs.size})" else "Student Development Clubs",
                                    desc = if (languageCode == "FR") "Club Robotique Oloumi, Club IA, Cybersécurité..." else "Register custom student coding associations",
                                    icon = Icons.Default.Groups,
                                    onAddClick = { campusTypeToCreate = "club" },
                                    colors = colors
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        clubs.forEach { cl ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(cl.emoji, fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                                                    Column {
                                                        Text(cl.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                        Text("Leader: ${cl.leaderName} • ${cl.category}", fontSize = 10.sp, color = colors.onSurfaceVariant)
                                                    }
                                                }
                                                Text("${cl.membersCount} membres", fontSize = 11.sp, color = colors.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }

                            // 6. Cohort Announcements
                            item {
                                CampusAdminWidget(
                                    title = if (languageCode == "FR") "📢 Mur de Promotion & Tribunes" else "Inter-cohort Promo Stream",
                                    desc = if (languageCode == "FR") "Messages et annonces académiques générales" else "Issue system official cohort notes",
                                    icon = Icons.Default.Campaign,
                                    onAddClick = { campusTypeToCreate = "cohort" },
                                    colors = colors
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        cohortPosts.forEach { cp ->
                                            Column(modifier = Modifier.padding(6.dp)) {
                                                Text(cp.first, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = colors.primary)
                                                Text(cp.second, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Spacer(Modifier.height(50.dp))
                            }
                        }
                    }
                    3 -> {
                        // --- DANGER & FLUSH MAINTENANCE TAB ---
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning, 
                                contentDescription = "Attention", 
                                tint = colors.error, 
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = if (languageCode == "FR") "Section Critique & Nettoyage" else "Database Core Sanitation",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = colors.onErrorContainer
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = if (languageCode == "FR") 
                                    "La réinitialisation générale vide instantanément les salons de discussions, les publications du flux, les offres de stages et les supports du campus. Votre profil d'admin restera préservé pour ne pas couper votre connexion."
                                    else "Clears database tables including messages, posts, and campus courses, while safeguarding your specific administrator session.",
                                fontSize = 12.sp,
                                color = colors.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(Modifier.height(24.dp))
                            Button(
                                onClick = { showResetConfirmDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.error),
                                modifier = Modifier.fillMaxWidth(0.9f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = "")
                                Spacer(Modifier.width(8.dp))
                                Text(if (languageCode == "FR") "Réinitialiser les Bases Gabonnaises" else "Wipe Datastore", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add User Dialog
    if (showAddDialog) {
        UserEditDialog(
            profile = null,
            languageCode = languageCode,
            onDismiss = { showAddDialog = false },
            onSave = { newProfile ->
                viewModel.createOrUpdateProfile(newProfile)
                showAddDialog = false
            }
        )
    }

    // Edit User Dialog
    if (editingProfile != null) {
        UserEditDialog(
            profile = editingProfile,
            languageCode = languageCode,
            onDismiss = { editingProfile = null },
            onSave = { updatedProfile ->
                viewModel.createOrUpdateProfile(updatedProfile)
                editingProfile = null
            }
        )
    }

    // Reset Database Confirmation Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text(if (languageCode == "FR") "Réinitialisation complète ?" else "Confirm wipe operation?") },
            text = { Text(if (languageCode == "FR") "Voulez-vous vider toutes les tables (locale SQLite & Firebase) d'un seul coup ? Cette opération n'est pas révocable." else "Are you sure you want to completely flush room and cloud collections?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllDatabase()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.error)
                ) {
                    Text(if (languageCode == "FR") "Oui, Purger" else "Yes, flush")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text(if (languageCode == "FR") "Annuler" else "Cancel")
                }
            }
        )
    }

    // --- Dynamic Campus Entity Creator Dialogs ---
    campusTypeToCreate?.let { kind ->
        CampusItemCreationDialog(
            type = kind,
            languageCode = languageCode,
            existingSchools = schools,
            onDismiss = { campusTypeToCreate = null },
            onSave = { choice ->
                when (choice) {
                    is CampusChoice.School -> viewModel.writeSchool(choice.data)
                    is CampusChoice.Teacher -> viewModel.writeTeacher(choice.data)
                    is CampusChoice.LibraryItem -> viewModel.writeLibraryItem(choice.data)
                    is CampusChoice.Internship -> viewModel.writeAcademicInternship(choice.data)
                    is CampusChoice.Club -> viewModel.writeAcademicClub(choice.data)
                    is CampusChoice.Cohort -> viewModel.writeCohortPost(sender = choice.sender, content = choice.content)
                }
                campusTypeToCreate = null
            }
        )
    }
}

// Sealed Choice classes to represent newly created models easily
sealed class CampusChoice {
    data class School(val data: AcademicSchool) : CampusChoice()
    data class Teacher(val data: AcademicTeacher) : CampusChoice()
    data class LibraryItem(val data: AcademicLibraryItem) : CampusChoice()
    data class Internship(val data: AcademicInternship) : CampusChoice()
    data class Club(val data: AcademicClub) : CampusChoice()
    data class Cohort(val sender: String, val content: String) : CampusChoice()
}

@Composable
fun HistLegend(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(text, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
fun LegendRow(color: Color, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, CircleShape)
        )
        Spacer(Modifier.width(6.dp))
        Text(text, fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.15f)),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(title, fontSize = 11.sp, color = colors.onSurfaceVariant)
                Icon(icon, contentDescription = "", tint = color.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
        }
    }
}

@Composable
fun CampusAdminWidget(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onAddClick: () -> Unit,
    colors: ColorScheme,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(icon, contentDescription = "", tint = colors.primary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(desc, fontSize = 10.sp, color = colors.onSurfaceVariant)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onAddClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = colors.secondary, modifier = Modifier.size(20.dp))
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
            if (isExpanded) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = colors.outline.copy(alpha = 0.1f))
                Spacer(Modifier.height(8.dp))
                content()
            }
        }
    }
}

@Composable
fun UserManagementRow(
    user: UserProfileEntity,
    isActiveUser: Boolean,
    onToggleVerify: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                ProfileImage(picture = user.profilePicture, size = 42.dp)
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .background(colors.surface, CircleShape)
                        .padding(1.5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = if (user.isOnline) Color(0xFF10B981) else Color.Gray,
                                shape = CircleShape
                            )
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = colors.onSurface)
                    Spacer(Modifier.width(4.dp))
                    if (user.isVerified) {
                        IconButton(onClick = onToggleVerify, modifier = Modifier.size(16.dp)) {
                            Icon(Icons.Default.Verified, "Certifié", tint = colors.secondary, modifier = Modifier.size(14.dp))
                        }
                    } else {
                        IconButton(onClick = onToggleVerify, modifier = Modifier.size(16.dp)) {
                            Icon(Icons.Outlined.Verified, "Non certifié", tint = colors.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                        }
                    }
                    if (user.isPro) {
                        Spacer(Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .background(colors.primaryContainer, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("PRO", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = colors.onPrimaryContainer)
                        }
                    }
                }
                Text("@${user.pseudo} • ${user.role}", fontSize = 11.sp, color = colors.onSurfaceVariant)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (user.isOnline) "🟢 En ligne (Temps réel)" else "🔘 Hors ligne",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (user.isOnline) Color(0xFF10B981) else colors.onSurfaceVariant
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("• ${user.email}", fontSize = 9.sp, color = colors.primary)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Role", tint = colors.primary, modifier = Modifier.size(16.dp))
                }
                if (!isActiveUser) {
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = colors.error, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditDialog(
    profile: UserProfileEntity?,
    languageCode: String,
    onDismiss: () -> Unit,
    onSave: (UserProfileEntity) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val isEditMode = profile != null

    var fullName by remember { mutableStateOf(profile?.fullName ?: "") }
    var email by remember { mutableStateOf(profile?.email ?: "") }
    var pseudo by remember { mutableStateOf(profile?.pseudo ?: "") }
    var bio by remember { mutableStateOf(profile?.bio ?: "") }
    
    // Structured Roles - preset list for robust role attribution
    val availableRoles = listOf("Développeur", "Designer UX/UI", "Recruteur RH", "Étudiant Campus", "Professeur Dr", "Administrateur")
    var selectedRole by remember { mutableStateOf(profile?.role ?: "Développeur") }
    
    var city by remember { mutableStateOf(profile?.city ?: "Libreville") }
    var skills by remember { mutableStateOf(profile?.skills ?: "") }
    var experienceLevel by remember { mutableStateOf(profile?.experienceLevel ?: "Senior") }
    var profilePicture by remember { mutableStateOf(profile?.profilePicture ?: "👩‍💻") }
    var isVerified by remember { mutableStateOf(profile?.isVerified ?: false) }
    var isPro by remember { mutableStateOf(profile?.isPro ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (isEditMode) 
                        (if (languageCode == "FR") "Attribuer Rôles & Modifier" else "Assign Roles & Edit User")
                        else (if (languageCode == "FR") "Ajouter un Utilisateur" else "Create account"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text(if (languageCode == "FR") "Nom Complet" else "Full Name", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Adresse Email (Google ID)", fontSize = 12.sp) },
                        singleLine = true,
                        enabled = !isEditMode,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pseudo,
                        onValueChange = { pseudo = it },
                        label = { Text("Pseudo", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // --- STRUCTURED ROLE SECTOR (Dropdown/Chips Selector) ---
                    Text(
                        text = if (languageCode == "FR") "Rôle & Mission de l'utilisateur (Structuré)" else "Select Structured User Role",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        availableRoles.take(3).forEach { r ->
                            FilterChip(
                                selected = selectedRole == r,
                                onClick = { selectedRole = r },
                                label = { Text(r, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        availableRoles.drop(3).forEach { r ->
                            FilterChip(
                                selected = selectedRole == r,
                                onClick = { selectedRole = r },
                                label = { Text(r, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = profilePicture,
                        onValueChange = { profilePicture = it },
                        label = { Text("Lien/Emoji Photo de profil", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = skills,
                        onValueChange = { skills = it },
                        label = { Text("Compétences (ex: Kotlin, Design, DevOps)", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Biographie / Description", fontSize = 12.sp) },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Ville (Gabon)", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = isVerified, onCheckedChange = { isVerified = it })
                        Text(if (languageCode == "FR") "Badge de Vérification Officielle" else "Official Verificaton badge", fontSize = 12.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = isPro, onCheckedChange = { isPro = it })
                        Text("Badge de contribution PRO", fontSize = 12.sp)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text(if (languageCode == "FR") "Annuler" else "Cancel") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (fullName.isNotBlank() && email.isNotBlank()) {
                                onSave(
                                    UserProfileEntity(
                                        id = profile?.id ?: 0,
                                        email = email.trim(),
                                        fullName = fullName.trim(),
                                        pseudo = if (pseudo.isBlank()) "User" else pseudo.trim(),
                                        bio = bio.ifBlank { "Membre DEV GABON" },
                                        city = city.ifBlank { "Libreville" },
                                        country = "Gabon",
                                        skills = skills,
                                        experienceLevel = experienceLevel,
                                        githubUrl = profile?.githubUrl ?: "",
                                        linkedinUrl = profile?.linkedinUrl ?: "",
                                        portfolioUrl = profile?.portfolioUrl ?: "",
                                        profilePicture = profilePicture.ifBlank { "👨‍💻" },
                                        isPro = isPro,
                                        isRecruiter = (selectedRole.contains("recru", ignoreCase = true) || selectedRole.contains("rh", ignoreCase = true)),
                                        postCount = profile?.postCount ?: 0,
                                        articleCount = profile?.articleCount ?: 0,
                                        subscriberCount = profile?.subscriberCount ?: 0,
                                        isVerified = isVerified,
                                        role = selectedRole,
                                        isOnline = profile?.isOnline ?: false
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                    ) {
                        Text(if (languageCode == "FR") "Enregistrer" else "Commit")
                    }
                }
            }
        }
    }
}

// --- DYNAMIC SUBSYSTEMS ITEM CREATOR DIALOG ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusItemCreationDialog(
    type: String, // "school", "teacher", "library", "internship", "club", "cohort"
    languageCode: String,
    existingSchools: List<AcademicSchool>,
    onDismiss: () -> Unit,
    onSave: (CampusChoice) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val title = when (type) {
                    "school" -> if (languageCode == "FR") "Créer un Établissement/Campus" else "Add School"
                    "teacher" -> if (languageCode == "FR") "Enregistrer Enseignant" else "Add Teacher"
                    "library" -> if (languageCode == "FR") "Uploader Document Bibliothèque" else "Add Lib Doc"
                    "internship" -> if (languageCode == "FR") "Publier Offre de Stage" else "Add Internship"
                    "club" -> if (languageCode == "FR") "Enregistrer Club d'étudiants" else "Add Club"
                    else -> if (languageCode == "FR") "Mur de Promotion - Annonce Officielle" else "New Cohort post"
                }

                Text(title, fontWeight = FontWeight.Black, fontSize = 16.sp, color = colors.primary, modifier = Modifier.padding(bottom = 12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (type) {
                        "school" -> {
                            var scName by remember { mutableStateOf("") }
                            var scDesc by remember { mutableStateOf("") }
                            var scAddr by remember { mutableStateOf("") }
                            var scWeb by remember { mutableStateOf("") }
                            var scType by remember { mutableStateOf("Université") }
                            var scEmail by remember { mutableStateOf("") }
                            var scVerified by remember { mutableStateOf(true) }

                            OutlinedTextField(value = scName, onValueChange = { scName = it }, label = { Text("Nom de l'Université / École") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = scDesc, onValueChange = { scDesc = it }, label = { Text("Description des parcours") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = scAddr, onValueChange = { scAddr = it }, label = { Text("Adresse Physique (Gabon)") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = scWeb, onValueChange = { scWeb = it }, label = { Text("Site Web") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = scEmail, onValueChange = { scEmail = it }, label = { Text("Mail de contact") }, modifier = Modifier.fillMaxWidth())

                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = scVerified, onCheckedChange = { scVerified = it })
                                Text("Établissement certifié par l'ANINF / État", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    if (scName.isNotBlank()) {
                                        val generatedId = (existingSchools.size + 10)
                                        val newSchool = AcademicSchool(
                                            id = generatedId,
                                            name = scName,
                                            logoEmoji = "🏫",
                                            type = scType,
                                            description = scDesc,
                                            address = scAddr,
                                            website = scWeb,
                                            tel = "+241 077-000-000",
                                            email = scEmail,
                                            isVerified = scVerified,
                                            filieres = listOf("Génie Logiciel", "Cybersécurité"),
                                            studentCount = 200,
                                            announcements = emptyList()
                                        )
                                        onSave(CampusChoice.School(newSchool))
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Sauvegarder Campus") }
                        }
                        "teacher" -> {
                            var teachName by remember { mutableStateOf("") }
                            var teachSpecs by remember { mutableStateOf("") }
                            var teachEmail by remember { mutableStateOf("") }
                            var teachCourses by remember { mutableStateOf("4") }

                            OutlinedTextField(value = teachName, onValueChange = { teachName = it }, label = { Text("Nom complet Enseignant") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = teachSpecs, onValueChange = { teachSpecs = it }, label = { Text("Spécialités professionnelles") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = teachEmail, onValueChange = { teachEmail = it }, label = { Text("E-mail institutionnel") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = teachCourses, onValueChange = { teachCourses = it }, label = { Text("Nombre de cours dispensés") }, modifier = Modifier.fillMaxWidth())

                            Button(
                                onClick = {
                                    if (teachName.isNotBlank()) {
                                        onSave(
                                            CampusChoice.Teacher(
                                                AcademicTeacher(
                                                    id = (System.currentTimeMillis() % 1000).toInt(),
                                                    name = teachName,
                                                    emoji = "👨‍🏫",
                                                    email = teachEmail.ifBlank { "prof@devgabon.ga" },
                                                    specialties = teachSpecs,
                                                    coursesCount = teachCourses.toIntOrNull() ?: 3,
                                                    activeGradings = 1
                                                )
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Assigner Enseignant") }
                        }
                        "library" -> {
                            var libTitle by remember { mutableStateOf("") }
                            var libDesc by remember { mutableStateOf("") }
                            var libCategory by remember { mutableStateOf("Cours") }
                            var libAuthor by remember { mutableStateOf("") }
                            var libSchool by remember { mutableStateOf("") }

                            OutlinedTextField(value = libTitle, onValueChange = { libTitle = it }, label = { Text("Titre du support") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = libDesc, onValueChange = { libDesc = it }, label = { Text("Résumé rapide du doc") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = libAuthor, onValueChange = { libAuthor = it }, label = { Text("Professeur / Auteur") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = libSchool, onValueChange = { libSchool = it }, label = { Text("École émettrice (ex: ENI)") }, modifier = Modifier.fillMaxWidth())

                            Button(
                                onClick = {
                                    if (libTitle.isNotBlank()) {
                                        onSave(
                                            CampusChoice.LibraryItem(
                                                AcademicLibraryItem(
                                                    id = (System.currentTimeMillis() % 1000).toInt(),
                                                    title = libTitle,
                                                    category = libCategory,
                                                    description = libDesc,
                                                    author = libAuthor,
                                                    school = libSchool
                                                )
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Uploader Support") }
                        }
                        "internship" -> {
                            var intTitle by remember { mutableStateOf("") }
                            var intComp by remember { mutableStateOf("") }
                            var intDesc by remember { mutableStateOf("") }
                            var intSchool by remember { mutableStateOf("") }

                            OutlinedTextField(value = intTitle, onValueChange = { intTitle = it }, label = { Text("Titre du Stage (ex: Analyste Cloud)") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = intComp, onValueChange = { intComp = it }, label = { Text("Société Gabonaise (ex: ANINF)") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = intDesc, onValueChange = { intDesc = it }, label = { Text("Description des exigences") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = intSchool, onValueChange = { intSchool = it }, label = { Text("École associée pour présélection") }, modifier = Modifier.fillMaxWidth())

                            Button(
                                onClick = {
                                    if (intTitle.isNotBlank()) {
                                        onSave(
                                            CampusChoice.Internship(
                                                AcademicInternship(
                                                    id = (System.currentTimeMillis() % 1000).toInt(),
                                                    title = intTitle,
                                                    company = intComp,
                                                    logoEmoji = "🏢",
                                                    description = intDesc,
                                                    postedBySchool = intSchool,
                                                    status = "Disponible"
                                                )
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Publier Offre de Stage") }
                        }
                        "club" -> {
                            var clName by remember { mutableStateOf("") }
                            var clSpecs by remember { mutableStateOf("") }
                            var clLead by remember { mutableStateOf("") }
                            var clDesc by remember { mutableStateOf("") }

                            OutlinedTextField(value = clName, onValueChange = { clName = it }, label = { Text("Nom du Club d'étudiants") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = clSpecs, onValueChange = { clSpecs = it }, label = { Text("Spécialités (ex: Robotique, IA)") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = clLead, onValueChange = { clLead = it }, label = { Text("Président / Leader") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = clDesc, onValueChange = { clDesc = it }, label = { Text("Objectifs & Projets") }, modifier = Modifier.fillMaxWidth())

                            Button(
                                onClick = {
                                    if (clName.isNotBlank()) {
                                        onSave(
                                            CampusChoice.Club(
                                                AcademicClub(
                                                    id = (System.currentTimeMillis() % 1000).toInt(),
                                                    name = clName,
                                                    emoji = "🤖",
                                                    category = clSpecs,
                                                    description = clDesc,
                                                    leaderName = clLead,
                                                    membersCount = 12,
                                                    nextEvent = "Sprint ce weekend"
                                                )
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Ajouter Club") }
                        }
                        else -> {
                            // "cohort"
                            var coSender by remember { mutableStateOf("Direction Académique") }
                            var coText by remember { mutableStateOf("") }

                            OutlinedTextField(value = coSender, onValueChange = { coSender = it }, label = { Text("Auteur de l'Annonce") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = coText, onValueChange = { coText = it }, label = { Text("Texte de l'annonce officielle") }, modifier = Modifier.fillMaxWidth())

                            Button(
                                onClick = {
                                    if (coText.isNotBlank()) {
                                        onSave(CampusChoice.Cohort(sender = coSender, content = coText))
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Relayer Annonce Officielle") }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) { Text("Sauter / Annuler") }
            }
        }
    }
}
