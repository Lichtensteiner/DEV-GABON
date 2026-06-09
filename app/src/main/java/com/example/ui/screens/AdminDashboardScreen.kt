package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.db.UserProfileEntity
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: DevGabonViewModel,
    onBack: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val profiles by viewModel.allProfiles.collectAsState()
    val posts by viewModel.allPosts.collectAsState()
    val articles by viewModel.allArticles.collectAsState()
    val jobs by viewModel.allJobs.collectAsState()
    val activeProfile by viewModel.activeUserProfile.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingProfile by remember { mutableStateOf<UserProfileEntity?>(null) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

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
                        Text("Console d'Administration", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.onBackground)
                        Text("DEV GABON - Tableau de bord de contrôle", fontSize = 11.sp, color = colors.secondary)
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Ajouter utilisateur")
            }
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text("Statistiques Générales", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colors.onBackground)
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Membres",
                            value = profiles.size.toString(),
                            icon = Icons.Default.People,
                            color = colors.primary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Billets Feed",
                            value = posts.size.toString(),
                            icon = Icons.Default.ChatBubble,
                            color = colors.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Articles",
                            value = articles.size.toString(),
                            icon = Icons.Default.Book,
                            color = colors.tertiary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // System Actions (Clear Database / Put DB to zero)
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.errorContainer.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, colors.error.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = "Attention", tint = colors.error, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Zone de Danger & Maintenance", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.onErrorContainer)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Réinitialisez ou mettez la base de données de l'application à zéro. Toutes les publications, offres de carrières, tchats et profils existants seront vidés en temps réel de SQLite et de Firebase Cloud, excepté le vôtre pour conserver votre accès.",
                                fontSize = 11.sp,
                                color = colors.onErrorContainer.copy(alpha = 0.8f),
                                lineHeight = 15.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { showResetConfirmDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.error),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = "")
                                Spacer(Modifier.width(8.dp))
                                Text("Mettre la base de données à zéro", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // User Management Section
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Gestion des Comptes (${filteredProfiles.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = colors.onBackground
                            )
                            Text(
                                "Modifiez, supprimez et certifiez",
                                fontSize = 10.sp,
                                color = colors.secondary
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        // Search users input field
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Rechercher nom, pseudo, email ou rôle...", fontSize = 12.sp) },
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
                    }
                }

                // User profile list items
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

                item {
                    Spacer(Modifier.height(80.dp)) // Padding for FAB
                }
            }
        }
    }

    // Add User Dialog
    if (showAddDialog) {
        UserEditDialog(
            profile = null,
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
            title = { Text("Mise à zéro complète ?") },
            text = { Text("Êtes-vous sûr de vouloir vider toutes les tables de la base de données DEV GABON (local et Firebase) d'un coup ? Cette opération est irréversible.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllDatabase()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.error)
                ) {
                    Text("Oui, Réinitialiser")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Annuler")
                }
            }
        )
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
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
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
            ProfileImage(picture = user.profilePicture, size = 42.dp)
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
                Text(user.email, fontSize = 10.sp, color = colors.primary)
            }

            // Commands Row
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = colors.primary, modifier = Modifier.size(16.dp))
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
    onDismiss: () -> Unit,
    onSave: (UserProfileEntity) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val isEditMode = profile != null

    var fullName by remember { mutableStateOf(profile?.fullName ?: "") }
    var email by remember { mutableStateOf(profile?.email ?: "") }
    var pseudo by remember { mutableStateOf(profile?.pseudo ?: "") }
    var bio by remember { mutableStateOf(profile?.bio ?: "") }
    var role by remember { mutableStateOf(profile?.role ?: "Développeur") }
    var city by remember { mutableStateOf(profile?.city ?: "Libreville") }
    var country by remember { mutableStateOf(profile?.country ?: "Gabon") }
    var skills by remember { mutableStateOf(profile?.skills ?: "") }
    var experienceLevel by remember { mutableStateOf(profile?.experienceLevel ?: "Junior") }
    var githubUrl by remember { mutableStateOf(profile?.githubUrl ?: "") }
    var profilePicture by remember { mutableStateOf(profile?.profilePicture ?: "👩‍💻") }
    var isVerified by remember { mutableStateOf(profile?.isVerified ?: false) }
    var isPro by remember { mutableStateOf(profile?.isPro ?: false) }

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
                Text(
                    text = if (isEditMode) "Modifier l'utilisateur" else "Ajouter un utilisateur",
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
                        label = { Text("Nom Complet", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Adresse Email (Google)", fontSize = 12.sp) },
                        singleLine = true,
                        enabled = !isEditMode, // Don't change email primary key on edit
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pseudo,
                        onValueChange = { pseudo = it },
                        label = { Text("Pseudo unique", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Rôle (ex: Développeur, Recruteur, Écrivain)", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = profilePicture,
                        onValueChange = { profilePicture = it },
                        label = { Text("Lien/Emoji de la photo de profil (Google)", fontSize = 12.sp) },
                        placeholder = { Text("ex: 😎 ou lien https://lh3.googleusercontent.com/...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = skills,
                        onValueChange = { skills = it },
                        label = { Text("Compétences (séparer par virgules)", fontSize = 12.sp) },
                        placeholder = { Text("ex: Kotlin, Compose, Figma") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Biographie", fontSize = 12.sp) },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Ville", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Verification toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = isVerified, onCheckedChange = { isVerified = it })
                        Text("Utilisateur Certifié (Badge vert)", fontSize = 12.sp)
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
                    TextButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (fullName.isNotBlank() && email.isNotBlank()) {
                                val user = UserProfileEntity(
                                    id = profile?.id ?: 0,
                                    email = email.trim(),
                                    fullName = fullName.trim(),
                                    pseudo = if (pseudo.isBlank()) "User" else pseudo.trim(),
                                    bio = if (bio.isBlank()) "Membre DEV GABON" else bio.trim(),
                                    city = if (city.isBlank()) "Libreville" else city.trim(),
                                    country = country,
                                    skills = skills,
                                    experienceLevel = experienceLevel,
                                    githubUrl = githubUrl,
                                    linkedinUrl = "",
                                    portfolioUrl = "",
                                    profilePicture = if (profilePicture.isBlank()) "👨‍💻" else profilePicture,
                                    isPro = isPro,
                                    isRecruiter = (role.contains("recru", ignoreCase = true) || role.contains("rh", ignoreCase = true)),
                                    postCount = profile?.postCount ?: 0,
                                    articleCount = profile?.articleCount ?: 0,
                                    subscriberCount = profile?.subscriberCount ?: 0,
                                    isVerified = isVerified,
                                    role = role
                                )
                                onSave(user)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}
