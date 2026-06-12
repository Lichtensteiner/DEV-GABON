package com.example.ui.screens

import android.text.format.DateUtils
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.db.*
import com.example.viewmodel.DevGabonViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: DevGabonViewModel) {
    val isStartupLoading by viewModel.isStartupLoading.collectAsState()

    if (isStartupLoading) {
        StartupSplashScreen(viewModel = viewModel)
        return
    }

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    if (!isLoggedIn) {
        LandingPageScreen(viewModel = viewModel)
        return
    }

    val currentScreen by viewModel.currentScreen.collectAsState()
    
    // Intercept hardware/system back button to go back to recently visited options
    androidx.activity.compose.BackHandler(enabled = currentScreen != Screen.Feed) {
        viewModel.navigateBack()
    }
    val activeProfile by viewModel.activeUserProfile.collectAsState()
    val profiles by viewModel.allProfiles.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    // Screen Dimensions for responsiveness
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    var showNotifsDialog by remember { mutableStateOf(false) }
    var showAddPostDialog by remember { mutableStateOf(false) }
    var showAddArticleDialog by remember { mutableStateOf(false) }
    var showAddJobDialog by remember { mutableStateOf(false) }
    var showAddGroupDialog by remember { mutableStateOf(false) }
    
    // Theme references
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.navigateTo(Screen.Landing) }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "DEV GABON Logo",
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Column {
                            Text(
                                "DEV GABON", 
                                fontWeight = FontWeight.Black, 
                                fontSize = 18.sp,
                                letterSpacing = 1.sp,
                                color = colors.onBackground
                            )
                            Text(
                                "COMMUNAUTÉ TECH", 
                                fontSize = 9.sp, 
                                fontWeight = FontWeight.Bold,
                                color = colors.onSurfaceVariant,
                                letterSpacing = 1.6.sp,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                actions = {
                    // Quick stats/info badge
                    if (activeProfile != null) {
                        AssistChip(
                            onClick = { viewModel.navigateTo(Screen.Profile) },
                            label = { Text(activeProfile!!.pseudo, fontWeight = FontWeight.Bold) },
                            leadingIcon = { ProfileImage(picture = activeProfile!!.profilePicture, size = 18.dp) },
                            colors = AssistChipDefaults.assistChipColors(
                                labelColor = colors.onSurface
                            ),
                            border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.5f))
                        )
                    }

                    // Notification Button with Alert Badge
                    Box {
                        IconButton(onClick = { showNotifsDialog = true }) {
                            val unreadCount = notifications.count { !it.isRead }
                            if (unreadCount > 0) {
                                Badge(
                                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
                                    containerColor = colors.secondary
                                ) {
                                    Text(unreadCount.toString(), color = colors.onSecondary, fontSize = 9.sp)
                                }
                            }
                            Icon(Icons.Default.Notifications, contentDescription = "Alertes")
                        }
                    }

                    // Admin Console button (visible for test account)
                    if (activeProfile?.email == "ludo.consulting3@gmail.com") {
                        IconButton(onClick = { viewModel.navigateTo(Screen.AdminDashboard) }) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Console Admin", tint = colors.secondary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background,
                    titleContentColor = colors.onBackground
                )
            )
        },
        bottomBar = {
            if (!isTablet) {
                Column {
                    // Fine national flag ribbon (Green, Yellow, Blue stripes) as a separator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                    ) {
                        Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFF34D399)))
                        Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFFBBF24)))
                        Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFF60A5FA)))
                    }
                    NavigationBar(
                        containerColor = colors.surface,
                        tonalElevation = 0.dp,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        NavigationBarItem(
                            selected = currentScreen is Screen.Feed,
                            onClick = { viewModel.navigateTo(Screen.Feed) },
                            icon = { Icon(if (currentScreen is Screen.Feed) Icons.Default.People else Icons.Outlined.People, "Social") },
                            label = { Text("Fil Feed", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colors.onPrimary,
                                selectedTextColor = colors.primary,
                                indicatorColor = colors.primary
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen is Screen.Blog,
                            onClick = { viewModel.navigateTo(Screen.Blog) },
                            icon = { Icon(if (currentScreen is Screen.Blog) Icons.Default.List else Icons.Outlined.List, "Blog") },
                            label = { Text("Blog Tech", maxLines = 1) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colors.onPrimary,
                                selectedTextColor = colors.primary,
                                indicatorColor = colors.primary
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen is Screen.Campus,
                            onClick = { viewModel.navigateTo(Screen.Campus) },
                            icon = { Icon(if (currentScreen is Screen.Campus) Icons.Default.School else Icons.Outlined.School, "Campus") },
                            label = { Text("Campus", maxLines = 1) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colors.onPrimary,
                                selectedTextColor = colors.primary,
                                indicatorColor = colors.primary
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen is Screen.Careers,
                            onClick = { viewModel.navigateTo(Screen.Careers) },
                            icon = { Icon(if (currentScreen is Screen.Careers) Icons.Default.Work else Icons.Outlined.Work, "Emplois") },
                            label = { Text("Emplois", maxLines = 1) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colors.onPrimary,
                                selectedTextColor = colors.primary,
                                indicatorColor = colors.primary
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen is Screen.Communities,
                            onClick = { viewModel.navigateTo(Screen.Communities) },
                            icon = { Icon(if (currentScreen is Screen.Communities) Icons.Default.People else Icons.Outlined.People, "Groupes") },
                            label = { Text("Groupes", maxLines = 1) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colors.onPrimary,
                                selectedTextColor = colors.primary,
                                indicatorColor = colors.primary
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen is Screen.Messages,
                            onClick = { viewModel.navigateTo(Screen.Messages) },
                            icon = { Icon(if (currentScreen is Screen.Messages) Icons.Default.Send else Icons.Outlined.Send, "Messages") },
                            label = { Text("Chats", maxLines = 1) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colors.onPrimary,
                                selectedTextColor = colors.primary,
                                indicatorColor = colors.primary
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen is Screen.Profile,
                            onClick = { viewModel.navigateTo(Screen.Profile) },
                            icon = { Icon(if (currentScreen is Screen.Profile) Icons.Default.Person else Icons.Outlined.Person, "Profil") },
                            label = { Text("CV", maxLines = 1) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colors.onPrimary,
                                selectedTextColor = colors.primary,
                                indicatorColor = colors.primary
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Adaptive Navigation Rail on Expand screens (Tablets/Landscape)
            if (isTablet) {
                NavigationRail(
                    containerColor = colors.surfaceVariant,
                    header = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Text("DEV", fontWeight = FontWeight.Bold, color = colors.primary)
                            Text("GA", fontWeight = FontWeight.Light, color = colors.onSurfaceVariant)
                        }
                    },
                    modifier = Modifier.fillMaxHeight(),
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavigationRailItem(
                        selected = currentScreen is Screen.Feed,
                        onClick = { viewModel.navigateTo(Screen.Feed) },
                        icon = { Icon(Icons.Default.People, "Social - Réseau") },
                        label = { Text("Réseau") }
                    )
                    NavigationRailItem(
                        selected = currentScreen is Screen.Blog,
                        onClick = { viewModel.navigateTo(Screen.Blog) },
                        icon = { Icon(Icons.Default.List, "Blog Tech") },
                        label = { Text("Tutos") }
                    )
                    NavigationRailItem(
                        selected = currentScreen is Screen.Campus,
                        onClick = { viewModel.navigateTo(Screen.Campus) },
                        icon = { Icon(Icons.Default.School, "Campus Universités") },
                        label = { Text("Campus") }
                    )
                    NavigationRailItem(
                        selected = currentScreen is Screen.Careers,
                        onClick = { viewModel.navigateTo(Screen.Careers) },
                        icon = { Icon(Icons.Default.Work, "Emplois / Projets") },
                        label = { Text("Carrières") }
                    )
                    NavigationRailItem(
                        selected = currentScreen is Screen.Communities,
                        onClick = { viewModel.navigateTo(Screen.Communities) },
                        icon = { Icon(Icons.Default.People, "Groupes & Events") },
                        label = { Text("Clubs") }
                    )
                    NavigationRailItem(
                        selected = currentScreen is Screen.Messages,
                        onClick = { viewModel.navigateTo(Screen.Messages) },
                        icon = { Icon(Icons.Default.Send, "Messagerie") },
                        label = { Text("Directs") }
                    )
                    NavigationRailItem(
                        selected = currentScreen is Screen.Profile,
                        onClick = { viewModel.navigateTo(Screen.Profile) },
                        icon = { Icon(Icons.Default.Person, "Mon Profil") },
                        label = { Text("Mon CV") }
                    )
                }
                
                // Vertical divider with Gabon yellow highlight
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(colors.primary.copy(alpha = 0.3f))
                )
            }

            // Screen Contents
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .animateContentSize()
            ) {
                when (currentScreen) {
                    is Screen.Feed -> FeedScreen(
                        viewModel = viewModel,
                        onAddClick = { showAddPostDialog = true }
                    )
                    is Screen.Blog -> BlogScreen(
                        viewModel = viewModel,
                        onAddClick = { showAddArticleDialog = true }
                    )
                    is Screen.Campus -> CampusScreen(
                        viewModel = viewModel
                    )
                    is Screen.Careers -> CareersScreen(
                        viewModel = viewModel,
                        isTablet = isTablet,
                        onAddJobClick = { showAddJobDialog = true }
                    )
                    is Screen.Communities -> CommunitiesScreen(
                        viewModel = viewModel,
                        onAddGroupClick = { showAddGroupDialog = true }
                    )
                    is Screen.Messages -> MessagesScreen(
                        viewModel = viewModel,
                        isTablet = isTablet
                    )
                    is Screen.Profile -> ProfileScreen(
                        viewModel = viewModel,
                        isTablet = isTablet
                    )
                    is Screen.AdminDashboard -> AdminDashboardScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(Screen.Profile) }
                    )
                    is Screen.Landing -> LandingPageScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    // --- DIALOGS (Highly Interactive modal UI components) ---

    // 1. Notifications Dialog
    if (showNotifsDialog) {
        Dialog(onDismissRequest = { showNotifsDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Activités & Alertes 🔔", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { showNotifsDialog = false }) {
                            Icon(Icons.Default.Close, "Dismiss")
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    if (notifications.isEmpty()) {
                        Text(
                            "Aucune notification récente.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            color = colors.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                            items(notifications) { notif ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.markNotificationAsRead(notif.id) }
                                        .background(if (notif.isRead) Color.Transparent else colors.primary.copy(alpha = 0.08f))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = when (notif.type) {
                                            "LIKE" -> colors.secondary.copy(alpha = 0.2f)
                                            "COMMENT" -> colors.primary.copy(alpha = 0.2f)
                                            "MESSAGE" -> colors.tertiary.copy(alpha = 0.2f)
                                            else -> colors.outline.copy(alpha = 0.2f)
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = when (notif.type) {
                                                    "LIKE" -> "❤️"
                                                    "COMMENT" -> "💬"
                                                    "MESSAGE" -> "✉️"
                                                    "JOB" -> "💼"
                                                    else -> "📢"
                                                },
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(notif.message, fontSize = 13.sp, fontWeight = if (notif.isRead) FontWeight.Normal else FontWeight.Bold)
                                        Text(
                                            text = DateUtils.getRelativeTimeSpanString(notif.timestamp).toString(),
                                            fontSize = 9.sp,
                                            color = colors.onSurfaceVariant
                                        )
                                    }
                                }
                                HorizontalDivider(color = colors.outline.copy(alpha = 0.2f))
                            }
                        }
                    }
                }
            }
        }
    }

    // 2. Add Social Post Dialog
    if (showAddPostDialog) {
        var postText by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf("Général") }
        Dialog(onDismissRequest = { showAddPostDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nouvelle Publication 🚀", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = postText,
                        onValueChange = { postText = it },
                        placeholder = { Text("Partagez un code, une idée technique ou posez une question à la commu...") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(Modifier.height(12.dp))
                    Text("Catégorie :", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    LazyRow(modifier = Modifier.padding(vertical = 4.dp)) {
                        val categories = listOf("Général", "Développement Web", "Développement Mobile", "Cloud", "Cybersécurité")
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) },
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddPostDialog = false }) { Text("Annuler") }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.addPost(postText, selectedCategory)
                                showAddPostDialog = false
                            },
                            enabled = postText.isNotBlank()
                        ) {
                            Text("Publier")
                        }
                    }
                }
            }
        }
    }

    // 3. Add Article Dialog
    if (showAddArticleDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Développement Web") }
        Dialog(onDismissRequest = { showAddArticleDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(androidx.compose.foundation.rememberScrollState())
                ) {
                    Text("Rédiger un Article / Tutoriel 📝", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titre de l'article") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    
                    val categories = listOf(
                        "Développement Web", "Développement Mobile", 
                        "Intelligence Artificielle", "Cybersécurité", 
                        "Cloud Computing", "DevOps", "Bases de données"
                    )
                    
                    Text("Thématique :", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    LazyRow(modifier = Modifier.padding(vertical = 4.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat) },
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Contenu (Markdown supporté)") },
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddArticleDialog = false }) { Text("Annuler") }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.publishArticle(title, content, category)
                                showAddArticleDialog = false
                            },
                            enabled = title.isNotBlank() && content.isNotBlank()
                        ) {
                            Text("Publier l'Article")
                        }
                    }
                }
            }
        }
    }

    // 4. Add Job Dialog
    if (showAddJobDialog) {
        var title by remember { mutableStateOf("") }
        var company by remember { mutableStateOf("") }
        var contract by remember { mutableStateOf("CDI") }
        var tech by remember { mutableStateOf("") }
        var exp by remember { mutableStateOf("Intermédiaire") }
        var salary by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        
        Dialog(onDismissRequest = { showAddJobDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(androidx.compose.foundation.rememberScrollState())
                ) {
                    Text("Nouvelle Offre d'Emploi 💼", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = title, onValueChange = { title = it },
                        label = { Text("Intitulé du poste") }, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = company, onValueChange = { company = it },
                        label = { Text("Nom de l'entreprise") }, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                            OutlinedTextField(
                                value = salary, onValueChange = { salary = it },
                                label = { Text("Salaire (ex: 500k CFA)") }, modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Box(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                            OutlinedTextField(
                                value = tech, onValueChange = { tech = it },
                                label = { Text("Techs (ex: Flutter, Git)") }, modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    
                    Text("Niveau d'expérience requis :")
                    Row {
                        listOf("Junior", "Intermédiaire", "Senior").forEach { level ->
                            Row(
                                modifier = Modifier.padding(end = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = exp == level, onClick = { exp = level })
                                Text(level, fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    
                    Text("Type de contrat :")
                    Row {
                        listOf("CDI", "CDD", "Stage", "Freelance").forEach { type ->
                            Row(
                                modifier = Modifier.padding(end = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = contract == type, onClick = { contract = type })
                                Text(type, fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = desc, onValueChange = { desc = it },
                        label = { Text("Description complète du rôle") },
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddJobDialog = false }) { Text("Annuler") }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.publishJobOffer(title, company, "🏢", "Libreville", contract, tech, exp, desc, salary)
                                showAddJobDialog = false
                            },
                            enabled = title.isNotBlank() && company.isNotBlank() && desc.isNotBlank()
                        ) {
                            Text("Publier l'Offre")
                        }
                    }
                }
            }
        }
    }

    // 5. Add Community Group Dialog
    if (showAddGroupDialog) {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Développement Web") }
        Dialog(onDismissRequest = { showAddGroupDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Créer un Groupe / Club Tech 👥", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("Nom du groupe (ex: Dev Python Libreville)") }, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = description, onValueChange = { description = it },
                        label = { Text("Description de la communauté") }, modifier = Modifier.fillMaxWidth().height(80.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    
                    Text("Domaine :", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    val domains = listOf("Développement Web", "Développement Mobile", "Cybersécurité", "Intelligence Artificielle")
                    LazyRow(modifier = Modifier.padding(vertical = 4.dp)) {
                        items(domains) { dom ->
                            FilterChip(
                                selected = category == dom,
                                onClick = { category = dom },
                                label = { Text(dom) },
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddGroupDialog = false }) { Text("Annuler") }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.createGroup(name, description, category)
                                showAddGroupDialog = false
                            },
                            enabled = name.isNotBlank() && description.isNotBlank()
                        ) {
                            Text("Créer le Groupe")
                        }
                    }
                }
            }
        }
    }
}

// ============================================
// FIL D'ACTUALITÉ / FEED SCREEN (Réseau Social)
// ============================================

@Composable
fun FeedScreen(viewModel: DevGabonViewModel, onAddClick: () -> Unit) {
    val posts by viewModel.allPosts.collectAsState()
    val searchPostQuery by viewModel.searchPostQuery.collectAsState()
    val colors = MaterialTheme.colorScheme

    val filteredPosts = posts.filter {
        searchPostQuery.isEmpty() || it.content.contains(searchPostQuery, ignoreCase = true) || it.authorName.contains(searchPostQuery, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            // Header stats or banner info
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = searchPostQuery,
                onValueChange = { viewModel.searchPostQuery.value = it },
                placeholder = { Text("🔍 Filtrer les publications...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(12.dp))

            if (filteredPosts.isEmpty()) {
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✏️ Oups, aucun post ne correspond.", style = MaterialTheme.typography.titleMedium, color = colors.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text("Soyez le premier à ajouter un message !", fontSize = 12.sp, color = colors.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 80.dp)) {
                    items(filteredPosts) { post ->
                        PostCard(post = post, viewModel = viewModel)
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }

        // FAB to publish updates
        FloatingActionButton(
            onClick = onAddClick,
            containerColor = colors.primary,
            contentColor = colors.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, "Publier")
        }
    }
}

@Composable
fun PostCard(post: PostEntity, viewModel: DevGabonViewModel) {
    val colors = MaterialTheme.colorScheme
    var expandedComments by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    val comments by viewModel.getComments(post.id).collectAsState(initial = emptyList())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Profile Avatar simulation with a beautiful emoji box
                Surface(
                    shape = CircleShape,
                    color = colors.primary.copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(post.authorProfilePic, fontSize = 20.sp)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(post.authorName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        if (post.authorIsVerified) {
                            Icon(
                                Icons.Default.CheckCircle, 
                                contentDescription = "Vérifié",
                                tint = colors.tertiary, 
                                modifier = Modifier.size(16.dp).padding(start = 4.dp)
                            )
                        }
                    }
                    Text("@${post.authorPseudo} • ${DateUtils.getRelativeTimeSpanString(post.timestamp)}", fontSize = 11.sp, color = colors.onSurfaceVariant)
                }
                
                // Show Category chip
                Surface(
                    shape = CircleShape,
                    color = colors.primary.copy(alpha = 0.12f),
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        post.category,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        color = colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(post.content, fontSize = 14.sp, lineHeight = 20.sp)
            Spacer(Modifier.height(16.dp))

            // Interaction buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like Button
                Row(
                    modifier = Modifier.clickable { viewModel.toggleLike(post.id, post.userLiked, post.likesCount) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (post.userLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "J'aime",
                        tint = if (post.userLiked) colors.primary else colors.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(post.likesCount.toString(), fontSize = 12.sp, color = colors.onSurfaceVariant)
                }

                // Comment Trigger Button
                Row(
                    modifier = Modifier.clickable { expandedComments = !expandedComments },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Send,
                        contentDescription = "Commenter",
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(post.commentsCount.toString(), fontSize = 12.sp, color = colors.onSurfaceVariant)
                }

                // Bookmark / Favoris
                Row(
                    modifier = Modifier.clickable { viewModel.toggleBookmark(post.id, post.isBookmarked) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (post.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Favori",
                        tint = if (post.isBookmarked) colors.secondary else colors.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expanding Comments Block
            if (expandedComments) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colors.outline.copy(alpha = 0.2f))
                
                // Comments List representation
                if (comments.isEmpty()) {
                    Text("Aucun commentaire, écrivez le premier !", fontSize = 11.sp, color = colors.onSurfaceVariant)
                } else {
                    Column {
                        comments.take(5).forEach { comment ->
                            Row(modifier = Modifier.padding(bottom = 10.dp)) {
                                Text("${comment.authorProfilePic} ", fontSize = 14.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "${comment.authorName} (@${comment.authorPseudo})", 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 11.sp
                                    )
                                    Text(comment.content, fontSize = 12.sp, color = colors.onSurface)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                // Quick comment add text field
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Écrire une réponse...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            viewModel.addComment(post.id, commentText)
                            commentText = ""
                        },
                        enabled = commentText.isNotBlank()
                    ) {
                        Icon(Icons.Default.Send, "Envoyer", tint = colors.primary)
                    }
                }
            }
        }
    }
}

// ============================================
// ESPACE ARTICLES & BLOG TECH SCREEN (Bibliothèque numérique)
// ============================================

@Composable
fun BlogScreen(viewModel: DevGabonViewModel, onAddClick: () -> Unit) {
    val searchArticleQuery by viewModel.searchArticleQuery.collectAsState()
    val filteredArticles by viewModel.filteredArticles.collectAsState()
    val activeFilter by viewModel.articleFilter.collectAsState()
    val colors = MaterialTheme.colorScheme

    var selectedArticleForDetail by remember { mutableStateOf<ArticleEntity?>(null) }

    if (selectedArticleForDetail != null) {
        // Deep Article Reader Panel
        ArticleDetailView(
            article = selectedArticleForDetail!!,
            onBackClick = { selectedArticleForDetail = null },
            viewModel = viewModel
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                Spacer(Modifier.height(12.dp))
                // Search field
                OutlinedTextField(
                    value = searchArticleQuery,
                    onValueChange = { viewModel.searchArticleQuery.value = it },
                    placeholder = { Text("🔎 Filtrer les tutoriels ou actualités...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(12.dp))
                
                // Categories horiz carousel
                val categories = listOf(
                    "Tous", "Développement Web", "Développement Mobile", 
                    "Intelligence Artificielle", "Cybersécurité", 
                    "Cloud Computing", "DevOps", "Bases de données"
                )
                LazyRow {
                    items(categories) { cat ->
                        FilterChip(
                            selected = activeFilter == cat,
                            onClick = { viewModel.articleFilter.value = cat },
                            label = { Text(cat) },
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (filteredArticles.isEmpty()) {
                    Column(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📚 Aucun article technique rédigé dans cette section.", style = MaterialTheme.typography.titleMedium, color = colors.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Text("Lancez-vous et partagez votre savoir !", fontSize = 11.sp, color = colors.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 80.dp)) {
                        items(filteredArticles) { article ->
                            ArticleCard(article = article, onClick = {
                                selectedArticleForDetail = article
                                viewModel.viewArticle(article.id)
                            })
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = onAddClick,
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                Icon(Icons.Default.Edit, "Rédiger")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleCard(article: ArticleEntity, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = colors.tertiary.copy(alpha = 0.12f)
                ) {
                    Text(
                        article.category,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        color = colors.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = DateUtils.getRelativeTimeSpanString(article.timestamp).toString(),
                    fontSize = 10.sp,
                    color = colors.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(10.dp))
            Text(
                article.title, 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold,
                color = colors.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(article.authorProfilePic, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(article.authorName, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("@${article.authorPseudo}", fontSize = 10.sp, color = colors.onSurfaceVariant)
                }
                Spacer(Modifier.weight(1f))
                
                // Views and Reactions counters
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, "Vues", tint = colors.onSurfaceVariant, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(article.viewsCount.toString(), fontSize = 11.sp, color = colors.onSurfaceVariant)
                    Spacer(Modifier.width(10.dp))
                    Icon(Icons.Default.ThumbUp, "Applaudir", tint = colors.onSurfaceVariant, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(article.reactionsCount.toString(), fontSize = 11.sp, color = colors.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun ArticleDetailView(article: ArticleEntity, onBackClick: () -> Unit, viewModel: DevGabonViewModel) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
            .padding(16.dp)
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, "Retour")
        }
        
        Spacer(Modifier.height(8.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = colors.tertiary.copy(alpha = 0.12f)
        ) {
            Text(
                article.category,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                fontSize = 12.sp,
                color = colors.tertiary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))
        Text(article.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(12.dp))

        // Author section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surfaceVariant.copy(alpha = 0.5f))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(article.authorProfilePic, fontSize = 28.sp)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(article.authorName, fontWeight = FontWeight.Bold)
                Text("@${article.authorPseudo} • ${DateUtils.formatDateTime(null, article.timestamp, DateUtils.FORMAT_SHOW_DATE)}", fontSize = 11.sp, color = colors.onSurfaceVariant)
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { viewModel.reactToArticle(article.id, article.reactionsCount) },
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Text("👏 Applaudir", fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(20.dp))
        
        // Article rich content
        Text(
            text = article.content,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 15.sp,
            lineHeight = 24.sp,
            color = colors.onBackground
        )

        Spacer(Modifier.height(40.dp))
    }
}

// ============================================
// CARRIÈRES, EMPLOIS ET PROJETS COLLABORATIFS
// ============================================

@Composable
fun CareersScreen(viewModel: DevGabonViewModel, isTablet: Boolean, onAddJobClick: () -> Unit) {
    val jobs by viewModel.filteredJobs.collectAsState()
    val projects by viewModel.allProjects.collectAsState()
    val contractFilter by viewModel.jobContractFilter.collectAsState()
    val experienceFilter by viewModel.jobExperienceFilter.collectAsState()
    val searchQuery by viewModel.searchJobQuery.collectAsState()
    
    val colors = MaterialTheme.colorScheme
    
    // Toggle between Careers (Jobs) Board and Collaborative Projects (Pitch your idea)
    var currentSubTab by remember { mutableStateOf("Jobs") } // "Jobs" or "Projects"
    var showPitchProjectDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(12.dp))
            
            // Sub-tabs
            TabRow(
                selectedTabIndex = if (currentSubTab == "Jobs") 0 else 1,
                containerColor = colors.background,
                contentColor = colors.primary
            ) {
                Tab(
                    selected = currentSubTab == "Jobs",
                    onClick = { currentSubTab = "Jobs" },
                    text = { Text("Offres d'Emploi 💼", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = currentSubTab == "Projects",
                    onClick = { currentSubTab = "Projects" },
                    text = { Text("Projets Collaboratifs 🤝", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(Modifier.height(12.dp))

            if (currentSubTab == "Jobs") {
                // Featured Opportunity Card from Geometric Balance design
                FeaturedOpportunityCard(viewModel = viewModel)
                
                // Search & Filter
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchJobQuery.value = it },
                    placeholder = { Text("🔎 Titre, techno (ex: NestJS)...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(8.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                        Text("Contrat :", fontSize = 10.sp, color = colors.onSurfaceVariant)
                        Spacer(Modifier.height(2.dp))
                        val contracts = listOf("Tous", "CDI", "Stage", "Freelance")
                        LazyRow {
                            items(contracts) { item ->
                                FilterChip(
                                    selected = contractFilter == item,
                                    onClick = { viewModel.jobContractFilter.value = item },
                                    label = { Text(item, fontSize = 11.sp) },
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }
                    }
                    Box(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                        Text("Expérience :", fontSize = 10.sp, color = colors.onSurfaceVariant)
                        Spacer(Modifier.height(2.dp))
                        val levels = listOf("Tous", "Junior", "Senior")
                        LazyRow {
                            items(levels) { item ->
                                FilterChip(
                                    selected = experienceFilter == item,
                                    onClick = { viewModel.jobExperienceFilter.value = item },
                                    label = { Text(item, fontSize = 11.sp) },
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (jobs.isEmpty()) {
                    Column(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("💼 Aucune offre ne correspond aux filtres.", style = MaterialTheme.typography.titleMedium, color = colors.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 80.dp)) {
                        items(jobs) { job ->
                            JobCard(job = job, viewModel = viewModel)
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            } else {
                // Projects pitch listing
                if (projects.isEmpty()) {
                    Column(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🤝 Aucun projet collaboratif recherché pour le moment.", style = MaterialTheme.typography.titleMedium, color = colors.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Text("Créez votre propre équipe en proposant votre projet !", fontSize = 11.sp, color = colors.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 80.dp)) {
                        items(projects) { project ->
                            ProjectCard(project = project, viewModel = viewModel)
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        // Add Click Fab Adaptor
        FloatingActionButton(
            onClick = {
                if (currentSubTab == "Jobs") {
                    onAddJobClick()
                } else {
                    showPitchProjectDialog = true
                }
            },
            containerColor = colors.primary,
            contentColor = colors.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, "Ajouter")
        }
    }

    // Collaborative Project Pitch setup
    if (showPitchProjectDialog) {
        var pTitle by remember { mutableStateOf("") }
        var pDuration by remember { mutableStateOf("3 mois") }
        var pTechs by remember { mutableStateOf("") }
        var pProfiles by remember { mutableStateOf("") }
        var pDesc by remember { mutableStateOf("") }
        
        Dialog(onDismissRequest = { showPitchProjectDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(androidx.compose.foundation.rememberScrollState())
                ) {
                    Text("Proposer une idée de Projet 💡", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = pTitle, onValueChange = { pTitle = it },
                        label = { Text("Titre (ex: Marketplace Pêche Libreville)") }, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pTechs, onValueChange = { pTechs = it },
                        label = { Text("Technologies (ex: Flutter, Supabase)") }, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pProfiles, onValueChange = { pProfiles = it },
                        label = { Text("Profils recherchés (ex: Mobile Specialist, DevOps)") }, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pDuration, onValueChange = { pDuration = it },
                        label = { Text("Durée estimée (ex: 2 mois)") }, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = pDesc, onValueChange = { pDesc = it },
                        label = { Text("Présentation détaillée, buts et objectifs") },
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showPitchProjectDialog = false }) { Text("Annuler") }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.publishProjectPitch(pTitle, pTechs, pProfiles, pDuration, pDesc)
                                showPitchProjectDialog = false
                            },
                            enabled = pTitle.isNotBlank() && pDesc.isNotBlank()
                        ) {
                            Text("Publier le Pitch")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedOpportunityCard(viewModel: DevGabonViewModel) {
    val colors = MaterialTheme.colorScheme
    val jobsList by viewModel.allJobs.collectAsState()
    val moovJob = jobsList.find { it.companyName.contains("Moov Africa") }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.2f))
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(160.dp).padding(16.dp)) {
            // Geometric balanced circle ornament on right side of the card
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 24.dp, y = 24.dp)
                    .background(colors.primary.copy(alpha = 0.12f), CircleShape)
            )

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Surface(
                        shape = CircleShape,
                        color = colors.surface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = "OPPORTUNITÉ EN VEDETTE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = colors.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Recrutement React Native\nSenior @ Moov Africa",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "Libreville • Freelance • 800k+",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                val isApplied = moovJob?.isApplied == true
                Button(
                    onClick = { 
                        moovJob?.let {
                            viewModel.toggleApplyJob(it.id, it.isApplied)
                        }
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isApplied) colors.outline else colors.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = if (isApplied) "Déjà postulé ✓" else "Postuler en 1 clic",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isApplied) colors.onSurface else colors.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun JobCard(job: JobEntity, viewModel: DevGabonViewModel) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = colors.primary.copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(job.companyLogo, fontSize = 20.sp)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(job.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
                    Text("${job.companyName} • ${job.city}, ${job.country}", fontSize = 12.sp, color = colors.onSurfaceVariant)
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.primary
                ) {
                    Text(
                        job.contractType,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        color = colors.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(job.description, fontSize = 13.sp, maxLines = 3, overflow = TextOverflow.Ellipsis, lineHeight = 18.sp)
            
            Spacer(Modifier.height(12.dp))
            
            // Tech stack badges wrapped
            Text("Stack requise :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Row {
                job.techStack.split(",").take(4).forEach { tech ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = colors.surfaceVariant,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(tech.trim(), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = colors.outline.copy(alpha = 0.15f))
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (job.salaryRange != null) {
                    Text(job.salaryRange!!, fontWeight = FontWeight.Black, color = colors.primary, fontSize = 13.sp)
                } else {
                    Text("Salaire Confidentiel", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontSize = 12.sp)
                }
                
                Button(
                    onClick = { viewModel.toggleApplyJob(job.id, job.isApplied) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (job.isApplied) colors.outline else colors.primary
                    )
                ) {
                    Text(if (job.isApplied) "Déjà postulé ✓" else "Postuler en 1 clic")
                }
            }
        }
    }
}

@Composable
fun ProjectCard(project: ProjectEntity, viewModel: DevGabonViewModel) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.tertiary.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("💡", fontSize = 16.sp)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(project.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Pitché par @${project.creatorPseudo}", fontSize = 11.sp, color = colors.onSurfaceVariant)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.tertiary.copy(alpha = 0.12f)
                ) {
                    Text(
                        project.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        color = colors.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(project.description, fontSize = 13.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(10.dp))

            Column {
                Row {
                    Text("Technologies : ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = colors.onSurfaceVariant)
                    Text(project.techStack, fontSize = 11.sp)
                }
                Spacer(Modifier.height(4.dp))
                Row {
                    Text("Profils recherchés : ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = colors.onSurfaceVariant)
                    Text(project.profilesSearched, fontSize = 11.sp)
                }
                Spacer(Modifier.height(4.dp))
                Row {
                    Text("Durée estimée : ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = colors.onSurfaceVariant)
                    Text(project.duration, fontSize = 11.sp)
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${project.membersCount} collaborateurs", fontSize = 12.sp, color = colors.primary, fontWeight = FontWeight.SemiBold)
                
                Button(
                    onClick = { viewModel.toggleJoinProject(project.id, project.isJoined) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (project.isJoined) colors.outline else colors.tertiary
                    )
                ) {
                    Text(if (project.isJoined) "Déjà rejoint ✓" else "Postuler au Projet")
                }
            }
        }
    }
}

// ============================================
// COMMUNAUTÉS, GROUPES ET LES ÉVÉNEMENTS TECH
// ============================================

@Composable
fun CommunitiesScreen(viewModel: DevGabonViewModel, onAddGroupClick: () -> Unit) {
    val groups by viewModel.allGroups.collectAsState()
    val events by viewModel.allEvents.collectAsState()
    val colors = MaterialTheme.colorScheme

    var subTabSelection by remember { mutableStateOf("Groups") } // "Groups" or "Events"

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(12.dp))
        
        TabRow(
            selectedTabIndex = if (subTabSelection == "Groups") 0 else 1,
            containerColor = colors.background,
            contentColor = colors.primary
        ) {
            Tab(
                selected = subTabSelection == "Groups",
                onClick = { subTabSelection = "Groups" },
                text = { Text("Groupes & Clubs 👥", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = subTabSelection == "Events",
                onClick = { subTabSelection = "Events" },
                text = { Text("Événements & Meetups 📅", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(Modifier.height(12.dp))

        if (subTabSelection == "Groups") {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Rejoindre un Club local", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TextButton(onClick = onAddGroupClick) {
                    Text("+ Créer un groupe", color = colors.primary)
                }
            }

            if (groups.isEmpty()) {
                Text("Aucun groupe créé.", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(groups) { group ->
                        GroupCard(group = group, viewModel = viewModel)
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        } else {
            // Events list
            Text("Les rassemblements de la commu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
            
            if (events.isEmpty()) {
                Text("Aucun événement à l'affiche.", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(events) { event ->
                        EventCard(event = event, viewModel = viewModel)
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun GroupCard(group: GroupEntity, viewModel: DevGabonViewModel) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colors.primary.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = when (group.category) {
                                "Développement Web" -> "🌐"
                                "Développement Mobile" -> "📱"
                                "Cybersécurité" -> "🛡️"
                                else -> "🤖"
                            },
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(group.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("${group.membersCount} membres • Géré par @${group.adminPseudo}", fontSize = 11.sp, color = colors.onSurfaceVariant)
                }
                
                Button(
                    onClick = { viewModel.toggleJoinGroup(group.id, group.isJoined) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (group.isJoined) colors.outline else colors.primary
                    )
                ) {
                    Text(if (group.isJoined) "Quitter" else "Rejoindre")
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(group.description, fontSize = 12.sp, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
fun EventCard(event: EventEntity, viewModel: DevGabonViewModel) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.primary.copy(alpha = 0.15f))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (event.eventType) {
                            "Meetup" -> "🤝"
                            "Conférence" -> "🎤"
                            "Hackathon" -> "🏆"
                            else -> "🔨"
                        },
                        fontSize = 20.sp
                    )
                    Text(event.eventType, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.primary)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(event.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Organisé par ${event.organizer}", fontSize = 12.sp, color = colors.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(event.description, fontSize = 12.sp, color = colors.onSurfaceVariant, lineHeight = 16.sp)
            Spacer(Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surfaceVariant.copy(alpha = 0.5f))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, "Date", tint = colors.primary, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(event.date, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, "Lieu", tint = colors.secondary, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(event.venue, fontSize = 11.sp)
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${event.attendeesCount} participants inscrits", fontSize = 12.sp, color = colors.primary, fontWeight = FontWeight.SemiBold)
                
                Button(
                    onClick = { viewModel.toggleRegisterEvent(event.id, event.isRegistered) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (event.isRegistered) colors.outline else colors.primary
                    )
                ) {
                    Text(if (event.isRegistered) "Je participe ✓" else "S'inscrire")
                }
            }
        }
    }
}

// ============================================
// MESSAGERIE INSTANTANÉE & ASSISTANT GEMINI AI
// ============================================

@Composable
fun MessagesScreen(viewModel: DevGabonViewModel, isTablet: Boolean) {
    val profiles by viewModel.allProfiles.collectAsState()
    val activeChatRecipient by viewModel.activeChatRecipient.collectAsState()
    val chatMessages by viewModel.activeChatMessages.collectAsState()
    val aiMessages by viewModel.aiMessages.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    
    val colors = MaterialTheme.colorScheme
    
    // Switch between Private Chats with developers or the smart AI Assistant Counselor
    var chatTypeSelection by remember { mutableStateOf("Directs") } // "Directs" or "AI"

    if (isTablet) {
        // Dual Pane tablet layout (List on left, Chat window on right)
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .background(colors.surfaceVariant.copy(alpha = 0.2f))
                    .padding(12.dp)
            ) {
                Text("Messagerie", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                
                TabRow(
                    selectedTabIndex = if (chatTypeSelection == "Directs") 0 else 1,
                    containerColor = Color.Transparent,
                    contentColor = colors.primary
                ) {
                    Tab(
                        selected = chatTypeSelection == "Directs",
                        onClick = { chatTypeSelection = "Directs" },
                        text = { Text("Directs", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = chatTypeSelection == "AI",
                        onClick = { chatTypeSelection = "AI" },
                        text = { Text("IA Gabon Tech", fontSize = 12.sp) }
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (chatTypeSelection == "Directs") {
                    LazyColumn {
                        items(profiles.filter { it.email != viewModel.activeUserEmail.value }) { dev ->
                            val isSelected = activeChatRecipient?.id == dev.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) colors.primary.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable { viewModel.selectChatRecipient(dev) }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ProfileImage(picture = dev.profilePicture, size = 32.dp)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(dev.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        if (dev.isOnline) {
                                            Spacer(Modifier.width(4.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .background(Color(0xFF4CAF50), shape = CircleShape)
                                            )
                                        }
                                    }
                                    Text("@${dev.pseudo}", fontSize = 10.sp, color = colors.onSurfaceVariant)
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectChatRecipient(null) },
                        colors = CardDefaults.cardColors(containerColor = colors.primary.copy(alpha = 0.1f))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🤖", fontSize = 24.sp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Assistant Carrière AI", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Gemini Flash 3.5", fontSize = 10.sp, color = colors.primary)
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .border(width = 1.dp, color = colors.outline.copy(alpha = 0.2f))
            ) {
                if (chatTypeSelection == "Directs" && activeChatRecipient != null) {
                    ChatWindow(
                        recipientName = activeChatRecipient!!.fullName,
                        recipientPic = activeChatRecipient!!.profilePicture,
                        messagesList = chatMessages,
                        isAiLoading = false,
                        onSendMessage = { text -> viewModel.sendDirectMessage(text) }
                    )
                } else if (chatTypeSelection == "AI") {
                    ChatWindow(
                        recipientName = "Assistant IA DEV GABON",
                        recipientPic = "🤖",
                        messagesList = aiMessages,
                        isAiLoading = isAiLoading,
                        onSendMessage = { text -> viewModel.sendAiMessage(text) }
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("💬", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("Sélectionnez un contact pour commencer à discuter", color = colors.onSurfaceVariant, fontSize = 13.sp)
                    }
                }
            }
        }
    } else {
        // Standard phone layout (Switches between Chat Lists and Opened Windows)
        // Check if a specific recipient is clicked or if we clicked AI
        var activeChatByPhone by remember { mutableStateOf<String?> (null) } // null / "Direct" / "AI"

        if (activeChatByPhone == "Direct" && activeChatRecipient != null) {
            Box(Modifier.fillMaxSize()) {
                ChatWindow(
                    recipientName = activeChatRecipient!!.fullName,
                    recipientPic = activeChatRecipient!!.profilePicture,
                    messagesList = chatMessages,
                    isAiLoading = false,
                    onBackClick = { activeChatByPhone = null },
                    onSendMessage = { text -> viewModel.sendDirectMessage(text) }
                )
            }
        } else if (activeChatByPhone == "AI") {
            Box(Modifier.fillMaxSize()) {
                ChatWindow(
                    recipientName = "Assistant IA Carrière",
                    recipientPic = "🤖",
                    messagesList = aiMessages,
                    isAiLoading = isAiLoading,
                    onBackClick = { activeChatByPhone = null },
                    onSendMessage = { text -> viewModel.sendAiMessage(text) }
                )
            }
        } else {
            // Opened Messages list on Phone
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("Messagerie & IA 💬", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                
                // AI Button highlight
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { activeChatByPhone = "AI" },
                    colors = CardDefaults.cardColors(containerColor = colors.primary.copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.3f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = colors.primary, modifier = Modifier.size(40.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🤖", fontSize = 18.sp)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Consulter l'Assistant IA (Gemini)", fontWeight = FontWeight.Black, fontSize = 14.sp, color = colors.primary)
                            Text("Conseil en recrutement, relecture de CV, templates.", fontSize = 11.sp, color = colors.onSurfaceVariant)
                        }
                        Icon(Icons.Default.ArrowForward, "Continuer", tint = colors.primary)
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text("Discussions Directes", fontWeight = FontWeight.Bold, color = colors.onSurfaceVariant, style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))

                LazyColumn {
                    items(profiles.filter { it.email != viewModel.activeUserEmail.value }) { dev ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.selectChatRecipient(dev)
                                    activeChatByPhone = "Direct"
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProfileImage(picture = dev.profilePicture, size = 36.dp)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(dev.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    if (dev.isOnline) {
                                        Spacer(Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(Color(0xFF4CAF50), shape = CircleShape)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("En ligne", color = Color(0xFF4CAF50), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                                Text("@${dev.pseudo}", fontSize = 11.sp, color = colors.onSurfaceVariant)
                            }
                            Icon(Icons.Default.ArrowForward, "Chat", tint = colors.onSurfaceVariant.copy(alpha = 0.5f))
                        }
                        HorizontalDivider(color = colors.outline.copy(alpha = 0.1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ChatWindow(
    recipientName: String,
    recipientPic: String,
    messagesList: List<MessageEntity>,
    isAiLoading: Boolean,
    onBackClick: (() -> Unit)? = null,
    onSendMessage: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var inputText by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surfaceVariant.copy(alpha = 0.5f))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, "Retour")
                }
            }
            Surface(shape = CircleShape, color = colors.primary.copy(alpha = 0.15f), modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(recipientPic, fontSize = 18.sp)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(recipientName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(if (isAiLoading) "Gemini est en train de réfléchir..." else "En ligne", fontSize = 10.sp, color = if (isAiLoading) colors.primary else colors.onSurfaceVariant)
            }
        }

        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            reverseLayout = false,
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messagesList) { msg ->
                val isMe = msg.senderPseudo == "Me" || msg.senderPseudo == "User" || msg.senderPseudo == "MartiDev"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 2.dp,
                            bottomEnd = if (isMe) 2.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMe) colors.primary else colors.surfaceVariant
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                msg.messageText, 
                                fontSize = 13.sp, 
                                color = if (isMe) colors.onPrimary else colors.onBackground
                            )
                            
                            // Render simulated attached file if present
                            if (msg.fileName != null) {
                                Spacer(Modifier.height(6.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = colors.background.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth().clickable { }
                                ) {
                                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, "File", tint = if (isMe) colors.onPrimary else colors.primary, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Column {
                                            Text(msg.fileName!!, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text(msg.fileSize ?: "", fontSize = 8.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            if (isAiLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colors.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                "Analyse et rédaction en cours...",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(12.dp),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }

        // Send bar
        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth(),
            color = colors.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { 
                    // Simulate attaching a specs doc
                    onSendMessage("J'attache mon spécification de projet.")
                }) {
                    Icon(Icons.Default.Add, "Attacher", tint = colors.onSurfaceVariant)
                }
                
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Écrire un message...", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline.copy(alpha = 0.5f)
                    )
                )
                
                Spacer(Modifier.width(4.dp))
                
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    enabled = inputText.isNotBlank()
                ) {
                    Icon(
                        Icons.Default.Send, 
                        "Envoyer", 
                        tint = if (inputText.isNotBlank()) colors.primary else colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ============================================
// VALORISATION DES TALENTS / GESTION DU PROFIL
// ============================================

@Composable
fun ProfileScreen(viewModel: DevGabonViewModel, isTablet: Boolean) {
    val activeProfile by viewModel.activeUserProfile.collectAsState()
    val profiles by viewModel.allProfiles.collectAsState()
    val languageCode by viewModel.languageCode.collectAsState()
    val followingEmails by viewModel.followingEmails.collectAsState()
    val simulationEnabled by viewModel.isOnlineSimulationEnabled.collectAsState()
    val colors = MaterialTheme.colorScheme

    var isEditing by remember { mutableStateOf(false) }
    var currentProfileTab by remember { mutableStateOf(0) }
    
    // Detailed CV view dialog target for any clicked community user
    var selectedProfileForCv by remember { mutableStateOf<UserProfileEntity?>(null) }

    if (activeProfile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val currentProfile = activeProfile!!

        if (isEditing) {
            ProfileEditorScreen(
                profile = currentProfile,
                onDismiss = { isEditing = false },
                onSave = { name, pseudo, bio, city, skills, level, git, lin, port, isPro, isRecruiter ->
                    viewModel.updateProfile(name, pseudo, bio, city, skills, level, git, lin, port, isPro, isRecruiter)
                    isEditing = false
                }
            )
        } else {
            if (isTablet) {
                // Adaptive layout: Professional Tablet dual-pane view
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column (1.2f weight) - Profile static overview & actions
                    Card(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight()
                            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // National colours ribbon banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFF0EA5E9))
                                        )
                                    )
                            )
                            
                            Spacer(Modifier.height(12.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ProfileImage(
                                    picture = currentProfile.profilePicture,
                                    modifier = Modifier.border(3.dp, colors.background, CircleShape),
                                    size = 64.dp
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(currentProfile.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        if (currentProfile.isVerified) {
                                            Icon(Icons.Default.Verified, "Verify", tint = colors.tertiary, modifier = Modifier.size(15.dp).padding(start = 2.dp))
                                        }
                                    }
                                    Text("@${currentProfile.pseudo} • ${currentProfile.city}", fontSize = 11.sp, color = colors.onSurfaceVariant)
                                }
                            }
                            
                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = colors.outline.copy(alpha = 0.08f))
                            Spacer(Modifier.height(12.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                Icon(Icons.Default.Email, "Email", tint = colors.primary, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(currentProfile.email, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                Icon(Icons.Default.Handyman, "Level", tint = colors.primary, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(currentProfile.experienceLevel, fontSize = 11.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                Icon(Icons.Default.DeveloperMode, "Role", tint = colors.primary, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(currentProfile.role, fontSize = 11.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                val onlineColor = if (currentProfile.isOnline) Color(0xFF10B981) else Color.Gray
                                Box(modifier = Modifier.size(8.dp).background(onlineColor, CircleShape))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (currentProfile.isOnline) "En ligne (Temps réel)" else "Hors Ligne",
                                    fontSize = 11.sp,
                                    color = onlineColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(Modifier.height(16.dp))
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (currentProfile.isPro) {
                                    Surface(shape = RoundedCornerShape(6.dp), color = colors.primary.copy(alpha = 0.15f)) {
                                        Text("⭐ PRO DEV", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = colors.primary)
                                    }
                                }
                                if (currentProfile.isRecruiter) {
                                    Surface(shape = RoundedCornerShape(6.dp), color = colors.secondary.copy(alpha = 0.15f)) {
                                        Text("🏢 RECRUTEUR RH", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = colors.secondary)
                                    }
                                }
                            }
                            
                            Spacer(Modifier.height(24.dp))
                            
                            Button(
                                onClick = { isEditing = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Edit, "Modifier", modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Modifier mon CV", fontSize = 11.sp)
                            }
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { viewModel.logout() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.error),
                                border = BorderStroke(1.dp, colors.error.copy(alpha = 0.5f))
                            ) {
                                Icon(Icons.Default.ExitToApp, "Logout", modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(if (languageCode == "FR") "Quitter" else "Sign Out", fontSize = 11.sp)
                            }
                        }
                    }

                    // Right Column (2.0f weight) - Content workspace with sub-tabs
                    Column(
                        modifier = Modifier.weight(2.0f)
                    ) {
                        TabRow(
                            selectedTabIndex = currentProfileTab,
                            containerColor = colors.background,
                            contentColor = colors.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Tab(
                                selected = currentProfileTab == 0,
                                onClick = { currentProfileTab = 0 },
                                text = { Text(if (languageCode == "FR") "Mon CV Virtuel" else "My CV Resume", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                icon = { Icon(Icons.Default.ContactPage, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                            Tab(
                                selected = currentProfileTab == 1,
                                onClick = { currentProfileTab = 1 },
                                text = { Text(if (languageCode == "FR") "Membres en Direct (${profiles.count { it.isOnline }}/${profiles.size})" else "Live Members (${profiles.count { it.isOnline }}/${profiles.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                icon = { Icon(Icons.Default.LeakAdd, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            when (currentProfileTab) {
                                0 -> {
                                    Column(modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                                        CVContentLayout(profile = currentProfile, languageCode = languageCode, colors = colors)
                                    }
                                }
                                1 -> {
                                    Column(modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                                        LiveMembersLayout(
                                            viewModel = viewModel,
                                            profiles = profiles,
                                            currentProfile = currentProfile,
                                            followingEmails = followingEmails,
                                            simulationEnabled = simulationEnabled,
                                            languageCode = languageCode,
                                            colors = colors,
                                            onViewCv = { clickedProf -> selectedProfileForCv = clickedProf }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Mobile Layout: Fluid, highly optimized linear layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(androidx.compose.foundation.rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Header ribbon
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFF0EA5E9))
                                )
                            )
                    )

                    // Profile summary overlapping slightly
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.offset(y = (-24).dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            ProfileImage(
                                picture = currentProfile.profilePicture,
                                modifier = Modifier.border(3.dp, colors.background, CircleShape),
                                size = 68.dp
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(currentProfile.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    if (currentProfile.isVerified) {
                                        Icon(Icons.Default.Verified, "Verified Badge", tint = colors.tertiary, modifier = Modifier.size(16.dp).padding(start = 4.dp))
                                    }
                                }
                                Text("@${currentProfile.pseudo} • ${currentProfile.city}, Gabon", fontSize = 12.sp, color = colors.onSurfaceVariant)
                                Spacer(Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Email, "Email", tint = colors.primary, modifier = Modifier.size(12.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(currentProfile.email, fontSize = 11.sp, color = colors.primary, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    // Account settings actions
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).offset(y = (-12).dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            if (currentProfile.isPro) {
                                Surface(shape = RoundedCornerShape(6.dp), color = colors.primary.copy(alpha = 0.15f), modifier = Modifier.padding(end = 4.dp)) {
                                    Text("⭐ PRO DEV", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.primary)
                                }
                            }
                            if (currentProfile.isRecruiter) {
                                Surface(shape = RoundedCornerShape(6.dp), color = colors.secondary.copy(alpha = 0.15f)) {
                                    Text("🏢 RECRUTEUR RH", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.secondary)
                                }
                            }
                        }

                        Row {
                            Button(
                                onClick = { isEditing = true },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                                modifier = Modifier.padding(end = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Edit, "Modifier", modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Mon CV", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.logout() },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.error),
                                border = BorderStroke(1.dp, colors.error.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.ExitToApp, "Logout", modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(2.dp))
                                Text(if (languageCode == "FR") "Quitter" else "Sign Out", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // Tab layouts bar
                    TabRow(
                        selectedTabIndex = currentProfileTab,
                        containerColor = colors.background,
                        contentColor = colors.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Tab(
                            selected = currentProfileTab == 0,
                            onClick = { currentProfileTab = 0 },
                            text = { Text(if (languageCode == "FR") "Mon CV & Parcours" else "Developer CV", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.ContactPage, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        Tab(
                            selected = currentProfileTab == 1,
                            onClick = { currentProfileTab = 1 },
                            text = { Text(if (languageCode == "FR") "Direct (${profiles.count { it.isOnline }}/${profiles.size})" else "Live (${profiles.count { it.isOnline }}/${profiles.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.Default.LeakAdd, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }

                    when (currentProfileTab) {
                        0 -> {
                            CVContentLayout(profile = currentProfile, languageCode = languageCode, colors = colors)
                        }
                        1 -> {
                            LiveMembersLayout(
                                viewModel = viewModel,
                                profiles = profiles,
                                currentProfile = currentProfile,
                                followingEmails = followingEmails,
                                simulationEnabled = simulationEnabled,
                                languageCode = languageCode,
                                colors = colors,
                                onViewCv = { clickedProf -> selectedProfileForCv = clickedProf }
                            )
                        }
                    }
                    Spacer(Modifier.height(40.dp))
                }
            }
        }

        // Render detailed resume overlay card if a user is clicked
        selectedProfileForCv?.let { clickedProfile ->
            MemberCVDetailsDialog(
                profile = clickedProfile,
                languageCode = languageCode,
                onDismiss = { selectedProfileForCv = null }
            )
        }
    }
}

@Composable
fun ProfileStatItem(num: String, label: String) {
    val colors = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(num, fontWeight = FontWeight.Black, fontSize = 18.sp, color = colors.primary)
        Text(label, fontSize = 10.sp, color = colors.onSurfaceVariant)
    }
}

@Composable
fun ProfileLinkItem(icon: String, text: String, placeholder: String, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 16.sp)
            Spacer(Modifier.width(12.dp))
            Text(text.ifBlank { placeholder }, fontSize = 12.sp, color = if (text.isBlank()) colors.onSurfaceVariant else colors.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun CVContentLayout(profile: UserProfileEntity, languageCode: String, colors: ColorScheme) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Bio Header Card in resume theme style
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.primary.copy(alpha = 0.03f)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.08f))
        ) {
            Column(Modifier.padding(14.dp)) {
                Text(
                    text = "${profile.role} • ${profile.experienceLevel}",
                    fontWeight = FontWeight.Black,
                    color = colors.primary,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = profile.bio.ifBlank { if (languageCode == "FR") "Aucun paragraphe de présentation défini." else "No bio provided." },
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = colors.onSurface
                )
                
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ProfileStatItem(num = profile.postCount.toString(), label = "Posts")
                    ProfileStatItem(num = profile.articleCount.toString(), label = "Tutos")
                    ProfileStatItem(num = profile.subscriberCount.toString(), label = "Abonnés")
                }
            }
        }

        // Categorized Technical Core Skills
        Text(
            text = if (languageCode == "FR") "Compétences Techniques Clés" else "Core Engineering Skills",
            fontWeight = FontWeight.Bold,
            color = colors.primary,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        val skList = profile.skills.split(",").map { it.trim() }.filter { it.isNotBlank() }
        val mobileWeb = skList.filter { 
            val s = it.lowercase()
            s.contains("kotlin") || s.contains("compose") || s.contains("flutter") || s.contains("swift") || 
            s.contains("figma") || s.contains("adobe") || s.contains("ux") || s.contains("ui") || s.contains("design") ||
            s.contains("web") || s.contains("front") || s.contains("react") || s.contains("html") || s.contains("css") || s.contains("js")
        }
        val backendCloud = skList.filter { 
            val s = it.lowercase()
            s.contains("nest") || s.contains("node") || s.contains("spring") || s.contains("api") || s.contains("django") || 
            s.contains("aws") || s.contains("docker") || s.contains("kubernetes") || s.contains("cloud") || s.contains("cyber") || s.contains("sec") || s.contains("linux") || s.contains("ansible")
        }
        val restSkills = skList.filter { !mobileWeb.contains(it) && !backendCloud.contains(it) }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (skList.isEmpty()) {
                    Text(
                        text = if (languageCode == "FR") "Aucune compétence n'a été répertoriée." else "No technical skills listed.",
                        fontSize = 11.sp,
                        color = colors.onSurfaceVariant
                    )
                } else {
                    if (mobileWeb.isNotEmpty()) {
                        SkillGroupRow(title = "UIs, Mobile & Web Frontends", skills = mobileWeb, colors = colors, accentColor = Color(0xFF10B981))
                    }
                    if (backendCloud.isNotEmpty()) {
                        SkillGroupRow(title = "APIs, System Services & Cloud", skills = backendCloud, colors = colors, accentColor = Color(0xFF3B82F6))
                    }
                    if (restSkills.isNotEmpty()) {
                        SkillGroupRow(title = "Autres Stack DevOps & Outils", skills = restSkills, colors = colors, accentColor = Color(0xFFF59E0B))
                    }
                }
            }
        }

        // Timeline of customized Career Achievements
        Text(
            text = if (languageCode == "FR") "Expériences Professionnelles" else "Engineering Career Timeline",
            fontWeight = FontWeight.Bold,
            color = colors.primary,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        ProfessionalTimeline(
            level = profile.experienceLevel,
            skills = profile.skills,
            languageCode = languageCode,
            colors = colors
        )

        // Educations & Diplômes
        Text(
            text = if (languageCode == "FR") "Études & Diplômes Informatiques" else "Education history",
            fontWeight = FontWeight.Bold,
            color = colors.primary,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                EducationItem(
                    year = "2021 - 2024",
                    school = if (profile.city == "Libreville" || profile.city.isBlank()) "Institut National des Sciences de Gestion (INSG), Libreville" else "Campus Universitaire de ${profile.city}",
                    degree = if (languageCode == "FR") "Master de Spécialité en Conception Logicielle" else "Master in Software Engineering & Architecture",
                    colors = colors
                )
                HorizontalDivider(color = colors.outline.copy(alpha = 0.05f))
                EducationItem(
                    year = "2018 - 2021",
                    school = "École Nationale de l'Informatique (ENI) du Gabon, Libreville",
                    degree = if (languageCode == "FR") "Licence Académique en Administration Systèmes & Réseaux" else "B.S. in Computer Systems Administration",
                    colors = colors
                )
            }
        }

        // Portfolio & networks and social profiles
        Text(
            text = if (languageCode == "FR") "Présence Numérique & Portfolio" else "Social Connections",
            fontWeight = FontWeight.Bold,
            color = colors.primary,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ProfileLinkItem(icon = "🌐", text = profile.portfolioUrl, placeholder = "Portfolio personnel et sites web", onClick = {})
            ProfileLinkItem(icon = "🐙", text = profile.githubUrl, placeholder = "Dépôt de projets publics GitHub", onClick = {})
            ProfileLinkItem(icon = "💼", text = profile.linkedinUrl, placeholder = "Profil de recrutement LinkedIn", onClick = {})
        }
    }
}

@Composable
fun SkillGroupRow(title: String, skills: List<String>, colors: ColorScheme, accentColor: Color) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(6.dp).background(accentColor, CircleShape))
            Spacer(Modifier.width(6.dp))
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = colors.onSurfaceVariant)
        }
        Spacer(Modifier.height(4.dp))
        FlowRow {
            skills.forEach { sk ->
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = colors.primary.copy(alpha = 0.08f),
                    modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = sk,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
            }
        }
    }
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.horizontalScroll(androidx.compose.foundation.rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            content()
        }
    }
}

@Composable
fun ProfessionalTimeline(level: String, skills: String, languageCode: String, colors: ColorScheme) {
    val timelineData = remember(level, skills) {
        when (level) {
            "Junior" -> listOf(
                Triple("2023 - Présent", "Développeur Logiciel Mobile Jr", "Conception d'interfaces utilisateurs fluides avec Kotlin et Compose. Résolution d'anomalies de code et intégration progressive de services web REST."),
                Triple("2022 - 2023", "Développeur Web Stagiaire", "Prise en main des méthodologies agiles, découverte de l'univers de l'intégration continue et écriture de cas de tests.")
            )
            "Intermédiaire" -> listOf(
                Triple("2022 - Présent", "Ingénieur d'Application Full Stack", "Développement d'APIs transactionnelles sécurisées et implémentation d'UIs dynamiques de production. Refactorisation de bases de données relationnelles locales."),
                Triple("2020 - 2022", "Développeur Solutions IT polyvalent", "Responsable du déploiement opérationnel d'interfaces pour le secteur public et privé gabonais.")
            )
            "Senior" -> listOf(
                Triple("2021 - Présent", "Tech Lead & Manager Technique", "Supervision d'équipes agiles gabonnaises, audits réguliers de sécurité applicative et modélisation de plans de déploiement cloud résilients."),
                Triple("2018 - 2021", "Développeur Logiciel Principal Mobile", "Prise en charge complète de la conception technique à la mise en ligne d'applications pour divers secteurs locaux d'envergure.")
            )
            else -> listOf( // Expert
                Triple("2020 - Présent", "Architecte Solutions Principal & Consultant", "Définition de stratégies technologiques à haut impact. Accompagnement de grandes entités locales pour le passage à l'échelle d'infrastructures informatiques."),
                Triple("2015 - 2020", "Directeur Technique d'Engineering", "Encadrement, mentorat et recrutement d'ingénieurs au Gabon. Évangélisation de Kotlin et des architectures modernes.")
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            timelineData.forEachIndexed { index, (period, title, desc) ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 2.dp, end = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(colors.primary, CircleShape)
                        )
                        if (index < timelineData.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(55.dp)
                                    .background(colors.outline.copy(alpha = 0.15f))
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(period, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.onSurfaceVariant)
                        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.primary)
                        Spacer(Modifier.height(2.dp))
                        Text(desc, fontSize = 11.sp, lineHeight = 15.sp, color = colors.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun EducationItem(year: String, school: String, degree: String, colors: ColorScheme) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(degree, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = colors.primary)
            Text(year, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = colors.onSurfaceVariant)
        }
        Spacer(Modifier.height(2.dp))
        Text(school, fontSize = 10.sp, color = colors.onSurfaceVariant)
    }
}

@Composable
fun LiveMembersLayout(
    viewModel: DevGabonViewModel,
    profiles: List<UserProfileEntity>,
    currentProfile: UserProfileEntity,
    followingEmails: Set<String>,
    simulationEnabled: Boolean,
    languageCode: String,
    colors: ColorScheme,
    onViewCv: (UserProfileEntity) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Real-time Traffic Simulator controls card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = colors.tertiaryContainer.copy(alpha = 0.35f)),
            border = BorderStroke(1.dp, colors.tertiary.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (simulationEnabled) Color(0xFF10B981) else Color.Gray,
                                    shape = CircleShape
                                )
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (simulationEnabled) "Trafic en direct : ACTIF 🟢" else "Trafic en direct : PAUSE 🔘",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onTertiaryContainer
                        )
                    }
                    
                    Switch(
                        checked = simulationEnabled,
                        onCheckedChange = { viewModel.toggleOnlineSimulation() }
                    )
                }
                
                Text(
                    text = if (languageCode == "FR") 
                        "Le simulateur connecte et déconnecte des profils gabonais de façon aléatoire toutes les 6.5 secondes pour simuler le trafic de l'application." 
                        else "Simulates real-time application traffic by toggling user online status in database every 6.5s.",
                    fontSize = 10.sp,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.triggerRandomConnectionEvent() },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.tertiary),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Bolt, "Force", modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Déclencher une connexion", fontSize = 9.sp)
                    }
                    
                    Surface(
                        color = colors.onTertiaryContainer.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "En ligne: ${profiles.count { it.isOnline }} / ${profiles.size}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            color = colors.tertiary
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = if (languageCode == "FR") "Rechercher des membres (Temps réel)" else "Live Search Community Members",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall,
            color = colors.primary
        )
        
        var liveMemberQuery by remember { mutableStateOf("") }
        OutlinedTextField(
            value = liveMemberQuery,
            onValueChange = { liveMemberQuery = it },
            placeholder = { Text(if (languageCode == "FR") "Filtrer par nom, pseudo, rôle, ou techno..." else "Filter by name, skills...", fontSize = 11.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "", modifier = Modifier.size(16.dp)) }
        )

        val filteredLiveProfiles = profiles.filter {
            it.fullName.contains(liveMemberQuery, ignoreCase = true) ||
            it.pseudo.contains(liveMemberQuery, ignoreCase = true) ||
            it.role.contains(liveMemberQuery, ignoreCase = true) ||
            it.skills.contains(liveMemberQuery, ignoreCase = true)
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filteredLiveProfiles.forEach { prof ->
                val isSelf = prof.email == currentProfile.email
                val isFollowing = followingEmails.contains(prof.email)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelf) colors.primary.copy(alpha = 0.05f) else colors.surfaceVariant.copy(alpha = 0.15f)
                    ),
                    border = if (isSelf) BorderStroke(1.dp, colors.primary.copy(alpha = 0.25f)) else null,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                ProfileImage(picture = prof.profilePicture, size = 38.dp)
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(prof.fullName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        if (prof.isVerified) {
                                            Icon(Icons.Default.Verified, "Verified", tint = colors.tertiary, modifier = Modifier.size(13.dp).padding(start = 2.dp))
                                        }
                                    }
                                    Text("@${prof.pseudo} • ${prof.role}", fontSize = 10.sp, color = colors.onSurfaceVariant)
                                    
                                    Spacer(Modifier.height(4.dp))
                                    
                                    // Live connected indicator
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(
                                                    color = if (prof.isOnline) Color(0xFF10B981) else Color.Gray,
                                                    shape = CircleShape
                                                )
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = if (prof.isOnline) "En ligne (Temps réel)" else "Hors ligne",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (prof.isOnline) Color(0xFF10B981) else colors.onSurfaceVariant
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = "• ${prof.subscriberCount} abonnés",
                                            fontSize = 9.sp,
                                            color = colors.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!isSelf) {
                                    Button(
                                        onClick = { viewModel.toggleFollow(prof.email) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isFollowing) Color(0xFF10B981) else colors.primary
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text(
                                            text = if (isFollowing) "Abonné ✓" else "+ S'abonner",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Surface(
                                        color = colors.primary.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "Moi",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.primary
                                        )
                                    }
                                }

                                OutlinedButton(
                                    onClick = { viewModel.switchActiveProfile(prof.email) },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(26.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.secondary),
                                    border = BorderStroke(1.dp, colors.secondary.copy(alpha = 0.4f))
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Switch", modifier = Modifier.size(10.dp))
                                    Spacer(Modifier.width(2.dp))
                                    Text("Basculer", fontSize = 8.sp)
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = colors.outline.copy(alpha = 0.05f))
                        Spacer(Modifier.height(6.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tech: " + prof.skills.split(",").take(3).joinToString(", "),
                                fontSize = 9.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = colors.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            
                            Button(
                                onClick = { onViewCv(prof) },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primaryContainer, contentColor = colors.onPrimaryContainer),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text("Consulter CV 📄", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MemberCVDetailsDialog(
    profile: UserProfileEntity,
    languageCode: String,
    onDismiss: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ProfileImage(picture = profile.profilePicture, size = 42.dp)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(profile.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                if (profile.isVerified) {
                                    Icon(Icons.Default.Verified, "Verified", tint = colors.tertiary, modifier = Modifier.size(13.dp).padding(start = 2.dp))
                                }
                            }
                            Text("@${profile.pseudo} • ${profile.city}, Gabon", fontSize = 10.sp, color = colors.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, "Dismiss", modifier = Modifier.size(18.dp))
                    }
                }
                
                HorizontalDivider(color = colors.outline.copy(alpha = 0.08f))
                Spacer(Modifier.height(10.dp))
                
                Box(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                        CVContentLayout(profile = profile, languageCode = languageCode, colors = colors)
                    }
                }
                
                Spacer(Modifier.height(10.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (languageCode == "FR") "Fermer le profil CV" else "Close CV Profile", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileEditorScreen(
    profile: UserProfileEntity,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String, String, String, Boolean, Boolean) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    var name by remember { mutableStateOf(profile.fullName) }
    var pseudo by remember { mutableStateOf(profile.pseudo) }
    var bio by remember { mutableStateOf(profile.bio) }
    var city by remember { mutableStateOf(profile.city) }
    var skills by remember { mutableStateOf(profile.skills) }
    var level by remember { mutableStateOf(profile.experienceLevel) }
    var github by remember { mutableStateOf(profile.githubUrl) }
    var linkedin by remember { mutableStateOf(profile.linkedinUrl) }
    var portfolio by remember { mutableStateOf(profile.portfolioUrl) }
    var isPro by remember { mutableStateOf(profile.isPro) }
    var isRecruiter by remember { mutableStateOf(profile.isRecruiter) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Modifier mon CV Numérique 📝", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Fermer") }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text("Nom Complet") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = pseudo, onValueChange = { pseudo = it },
            label = { Text("Pseudo unique") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = city, onValueChange = { city = it },
            label = { Text("Ville (Ex: Libreville, Akanda, Port-Gentil)") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = bio, onValueChange = { bio = it },
            label = { Text("Biographie professionnelle") }, modifier = Modifier.fillMaxWidth().height(100.dp)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = skills, onValueChange = { skills = it },
            label = { Text("Compétences clé (séparées par une virgule)") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Text("Niveau d'expérience :", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
        val levels = listOf("Junior", "Intermédiaire", "Senior", "Expert")
        Row {
            levels.forEach { item ->
                Row(
                    modifier = Modifier.padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = level == item, onClick = { level = item })
                    Text(item, fontSize = 11.sp)
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = portfolio, onValueChange = { portfolio = it },
            label = { Text("Lien Portfolio") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = github, onValueChange = { github = it },
            label = { Text("Lien GitHub") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = linkedin, onValueChange = { linkedin = it },
            label = { Text("Lien LinkedIn") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // Role switches
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Activer Badge Développeur PRO", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Affiche une étoile et met votre profil en avant.", fontSize = 10.sp, color = colors.onSurfaceVariant)
            }
            Switch(checked = isPro, onCheckedChange = { isPro = it })
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Je suis Recruteur / Entreprise", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Permet de publier des offres d'emploi IT.", fontSize = 10.sp, color = colors.onSurfaceVariant)
            }
            Switch(checked = isRecruiter, onCheckedChange = { isRecruiter = it })
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                onSave(name, pseudo, bio, city, skills, level, github, linkedin, portfolio, isPro, isRecruiter)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer les modifications")
        }
    }
}
