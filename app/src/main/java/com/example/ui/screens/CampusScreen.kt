package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.db.*
import com.example.viewmodel.DevGabonViewModel
import kotlinx.coroutines.launch

// Seed data structures for dynamic academic interaction in the Campus ecosystem
data class AcademicSchool(
    val id: Int,
    val name: String,
    val logoEmoji: String,
    val type: String, // "Université", "École", "Centre de Formation"
    val description: String,
    val address: String,
    val website: String,
    val tel: String,
    val email: String,
    val isVerified: Boolean,
    val filieres: List<String>,
    val studentCount: Int,
    val announcements: List<String> = emptyList()
)

data class AcademicLibraryItem(
    val id: Int,
    val title: String,
    val category: String, // "Cours", "Mémoire", "Projet de fin d'étude", "Livre numérique", "Tutoriel"
    val description: String,
    val author: String,
    val school: String,
    val url: String = "https://devgabon.net/library/",
    val timestamp: Long = System.currentTimeMillis()
)

data class AcademicTeacher(
    val id: Int,
    val name: String,
    val emoji: String,
    val email: String,
    val specialties: String,
    val coursesCount: Int,
    val activeGradings: Int
)

data class AcademicInternship(
    val id: Int,
    val title: String,
    val company: String,
    val logoEmoji: String,
    val description: String,
    val postedBySchool: String,
    val status: String = "Disponible", // "Disponible", "En cours", "Rapport validé"
    val studentAssigned: String? = null
)

data class AcademicClub(
    val id: Int,
    val name: String,
    val emoji: String,
    val category: String,
    val description: String,
    val leaderName: String,
    val membersCount: Int,
    val nextEvent: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusScreen(viewModel: DevGabonViewModel) {
    val languageCode by viewModel.languageCode.collectAsState()
    val activeProfile by viewModel.activeUserProfile.collectAsState()
    val colors = MaterialTheme.colorScheme
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    // Determine current user's role/account type
    val customRole = activeProfile?.role ?: "Développeur"

    // Seeding dynamic state lists for interactive use
    var schoolsList by remember {
        mutableStateOf(
            listOf(
                AcademicSchool(
                    id = 1,
                    name = "Université de Libreville (UL)",
                    logoEmoji = "🏫",
                    type = "Université",
                    description = "La plus grande institution académique publique du pays, formant l'élite de l'ingénierie centrale.",
                    address = "Boulevard Triompthal, Libreville, Gabon",
                    website = "www.univ-libreville.ga",
                    tel = "+241 077-445-123",
                    email = "contact@univ-libreville.ga",
                    isVerified = true,
                    filieres = listOf("Génie Logiciel", "Réseaux et Télécommunications", "Cybersécurité", "Intelligence Artificielle"),
                    studentCount = 2450,
                    announcements = listOf(
                        "Session de Hackathon Communautaire - Inscriptions Ouvertes !",
                        "Ouverture du nouveau pôle de Recherche en Intelligence Artificielle."
                    )
                ),
                AcademicSchool(
                    id = 2,
                    name = "École Nationale d'Informatique (ENI)",
                    logoEmoji = "💻",
                    type = "École",
                    description = "École d'excellence professionnelle reconnue pour la rigueur de ses filières de programmation.",
                    address = "Ancienne Sobraga, Libreville, Gabon",
                    website = "www.eni.ga",
                    tel = "+241 062-889-110",
                    email = "admin@eni.ga",
                    isVerified = true,
                    filieres = listOf("Génie Logiciel", "Systèmes & Réseaux IT", "Développement Mobile"),
                    studentCount = 550,
                    announcements = listOf("Soutenance de mémoires de la promotion 2026 fixée au 15 Septembre.")
                ),
                AcademicSchool(
                    id = 3,
                    name = "Institut Supérieur de Technologie (IST)",
                    logoEmoji = "🚀",
                    type = "Centre de Formation",
                    description = "Établissement supérieur axé sur l'innovation industrielle et l'alternance en entreprise.",
                    address = "Zone Industrielle d'Oloumi, Libreville",
                    website = "www.ist.ga",
                    tel = "+241 074-129-906",
                    email = "registrar@ist.ga",
                    isVerified = true,
                    filieres = listOf("Maintenance Réseaux", "IoT & Électronique", "Administration Cloud"),
                    studentCount = 820
                )
            )
        )
    }

    var libraryList by remember {
        mutableStateOf(
            listOf(
                AcademicLibraryItem(
                    id = 1,
                    title = "Support complet : Introduction au langage Kotlin",
                    category = "Cours",
                    description = "Syntaxe moderne, variables nullables, programmation fonctionnelle et coroutines appliquées.",
                    author = "M. Mve Zogo Ludovic Martinien",
                    school = "Université de Libreville"
                ),
                AcademicLibraryItem(
                    id = 2,
                    title = "Analyse comparative des protocoles de routage sans-fil dans les zones denses gabonaises",
                    category = "Mémoire",
                    description = "Étude de cas des performances réseaux lors des pics d'accès internet à Libreville.",
                    author = "Jean-Claude Biyogo",
                    school = "École Nationale d'Informatique"
                ),
                AcademicLibraryItem(
                    id = 3,
                    title = "Application Android de suivi nutritionnel pour l'Hôpital Militaire de Libreville",
                    category = "Projet de fin d'étude",
                    description = "Prototype fonctionnel développé en Jetpack Compose permettant le suivi en temps réel des patients.",
                    author = "Laurine Massala (Promotion GL 2026)",
                    school = "Université de Libreville"
                ),
                AcademicLibraryItem(
                    id = 4,
                    title = "Manuel de Cybersécurité : durcissement des serveurs locaux",
                    category = "Livre numérique",
                    description = "Pratiques de sécurisation pour les infrastructures et les datacenters d'Afrique centrale.",
                    author = "Didier Obiang",
                    school = "Institut Supérieur de Technologie"
                )
            )
        )
    }

    var teachersList by remember {
        mutableStateOf(
            listOf(
                AcademicTeacher(1, "Pr. Jean-Paul Mbenga", "👴", "jp.mbenga@univ-libreville.ga", "Algorithmique complexe, IA", 8, 3),
                AcademicTeacher(2, "Dr. Sandrine Bignoumba", "👩‍🏫", "s.bignoumba@eni.ga", "Bases de données SQL / NoSQL", 12, 1),
                AcademicTeacher(3, "M. Pierre-Alain Ondo", "👨‍🏫", "pa.ondo@ist.ga", "Systèmes distribués & Kubernetes", 5, 4)
            )
        )
    }

    var internshipsList by remember {
        mutableStateOf(
            listOf(
                AcademicInternship(1, "Assistant Développeur Mobile Android", "Gabon Telecom", "🏢", "Développement d'outils internes d'assistance client.", "Université de Libreville", "Disponible"),
                AcademicInternship(2, "Stagiaire Cloud & DevOps", "ANINF Gabon", "📡", "Intégration d'outils d'automatisation CI/CD sur Kubernetes.", "École Nationale d'Informatique", "En cours", "MartiDev"),
                AcademicInternship(3, "Développeur Full-Stack Junior / Stage", "Caisse de Dépôts et Consignations", "🏦", "Conception d'une application d'historisation bancaire.", "Institut Supérieur de Technologie", "Disponible")
            )
        )
    }

    var clubsList by remember {
        mutableStateOf(
            listOf(
                AcademicClub(1, "Club Robotique d'Oloumi", "🤖", "Robotique", "Conception matérielle d'automates basés sur Raspberry et Arduino.", "Arnaud Nguema", 28, "Atelier d'interfaçage IOT - Vendredi 16h"),
                AcademicClub(2, "Club IA Gabon AI", "🧠", "Intelligence Artificielle", "Exploration de modèles génératifs et de reconnaissance visuelle locale.", "Hassan Meye", 42, "Meetup d'introduction aux LLMs - Samedi 10h"),
                AcademicClub(3, "Club Cybersécurité GL 2026", "🛡️", "Cybersécurité", "Entraînement CTF national et analyse de menaces réseau au Gabon.", "Audrey Beka", 19, "Entraînement CTF inter-écoles - Dimanche 14h"),
                AcademicClub(4, "Club Dev Mobile Libreville", "📱", "Développement Mobile", "Prototypage rapide d'applications mobiles utiles aux citoyens gabonais.", "Rudy Bounga", 35, "Sprint SwiftUI & Compose - Mercredi 18h")
            )
        )
    }

    // Dynamic promotion/cohort messages (Promotion Génie Logiciel 2026)
    var cohortPosts by remember {
        mutableStateOf(
            listOf(
                Pair("Ludo Mve", "Salut l'équipe ! Le dépôt GitHub de notre projet de fin d'études est configuré. Ajoutez vos pseudos ! 💻🔥"),
                Pair("AudreyUX", "J'ai partagé les maquettes Figma dans notre espace partagé. Des avis sur la charte Gabonaise ? 🇬🇦"),
                Pair("MartiDev", "Excellent ! Je m'occupe de la squelette de l'application Jetpack Compose ce soir.")
            )
        )
    }

    // State managers
    var activeTab by remember { mutableStateOf(0) }
    val tabTitles = if (languageCode == "FR") {
        listOf("Campus", "Bibliothèque", "Enseignants", "Promos & Clubs", "Stages", "Rôles")
    } else {
        listOf("Campus", "Library", "Teachers", "Promos & Clubs", "Stages", "Roles")
    }

    // Modal Triggers
    var showAddSchoolDialog by remember { mutableStateOf(false) }
    var showAddFiliereDialog by remember { mutableStateOf(false) }
    var showAddDocDialog by remember { mutableStateOf(false) }
    var showAddCourseDialog by remember { mutableStateOf(false) }
    var showAddInternshipDialog by remember { mutableStateOf(false) }
    var showAddPromoPostDialog by remember { mutableStateOf(false) }
    var showVerificationDialog by remember { mutableStateOf(false) }

    // Forms States
    var selectedSchoolForFiliere by remember { mutableStateOf<AcademicSchool?>(null) }
    var selectedSchoolForPromoGroup by remember { mutableStateOf<AcademicSchool?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Gabon Ribbon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        ) {
            Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFF10B981)))
            Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFF59E0B)))
            Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFF0EA5E9)))
        }

        // Header section emphasizing the multi-role ecosystem
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surfaceVariant.copy(alpha = 0.4f))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colors.primary.copy(alpha = 0.1f),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🇬🇦", fontSize = 28.sp)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (languageCode == "FR") "CAMPUS NUMÉRIQUE GABON" else "GABON DIGITAL CAMPUS",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp,
                        color = colors.primary
                    )
                    Text(
                        text = if (languageCode == "FR") {
                            "Accès en temps réel • Rôle Actif : $customRole"
                        } else {
                            "Real-time Access • Active Role: $customRole"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurfaceVariant
                    )
                }

                // Interactive role switcher for live demo capability
                var showRoleMenu by remember { mutableStateOf(false) }
                Box {
                    AssistChip(
                        onClick = { showRoleMenu = true },
                        label = { Text(customRole, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        leadingIcon = { Icon(Icons.Default.Tune, "Switch role", modifier = Modifier.size(12.dp)) },
                        colors = AssistChipDefaults.assistChipColors(labelColor = colors.onSurfaceVariant)
                    )

                    DropdownMenu(
                        expanded = showRoleMenu,
                        onDismissRequest = { showRoleMenu = false }
                    ) {
                        listOf(
                            "Développeur",
                            "Étudiant",
                            "Enseignant",
                            "École / Université",
                            "Entreprise",
                            "Administrateur",
                            "Modérateur"
                        ).forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r, fontSize = 13.sp) },
                                onClick = {
                                    // Switch active credential with instantaneous reload
                                    viewModel.updateActiveUserRole(r)
                                    showRoleMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Horizontal Category Tab Panel
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            edgePadding = 8.dp,
            containerColor = colors.surface,
            contentColor = colors.primary
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = activeTab == index,
                    onClick = { activeTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        // Main Dynamic Scrollable Workspace
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "CampusNavigationAnim"
            ) { targetIndex ->
                when (targetIndex) {
                    0 -> SchoolsWorkspace(
                        schools = schoolsList,
                        userRole = customRole,
                        languageCode = languageCode,
                        colors = colors,
                        onAddSchool = { showAddSchoolDialog = true },
                        onAddFiliere = { school ->
                            selectedSchoolForFiliere = school
                            showAddFiliereDialog = true
                        },
                        onVerifySchool = { showVerificationDialog = true }
                    )
                    1 -> LibraryWorkspace(
                        libraryItems = libraryList,
                        userRole = customRole,
                        languageCode = languageCode,
                        colors = colors,
                        onAddDoc = { showAddDocDialog = true }
                    )
                    2 -> TeachersWorkspace(
                        teachers = teachersList,
                        userRole = customRole,
                        languageCode = languageCode,
                        colors = colors,
                        onAddCourse = { showAddCourseDialog = true }
                    )
                    3 -> PromosClubsWorkspace(
                        clubs = clubsList,
                        cohortPosts = cohortPosts,
                        userRole = customRole,
                        languageCode = languageCode,
                        colors = colors,
                        onAddPost = { showAddPromoPostDialog = true }
                    )
                    4 -> InternshipsWorkspace(
                        internships = internshipsList,
                        userRole = customRole,
                        languageCode = languageCode,
                        colors = colors,
                        onAddInternship = { showAddInternshipDialog = true },
                        onRegisterLocalReport = { id, student ->
                            internshipsList = internshipsList.map {
                                if (it.id == id) {
                                    it.copy(status = "Rapport validé", studentAssigned = student)
                                } else it
                            }
                        }
                    )
                    5 -> RolesMatrixWorkspace(
                        languageCode = languageCode,
                        colors = colors
                    )
                }
            }
        }
    }

    // --- POPUP DIALOGS FOR REAL-TIME SIMULATION & PERSISTED MUTATIONS ---

    // 1. Add School Dialog
    if (showAddSchoolDialog) {
        var schoolName by remember { mutableStateOf("") }
        var schoolDesc by remember { mutableStateOf("") }
        var schoolAddress by remember { mutableStateOf("") }
        var schoolType by remember { mutableStateOf("Université") }
        var schoolTel by remember { mutableStateOf("") }
        var schoolEmail by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddSchoolDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                    Text(
                        text = if (languageCode == "FR") "Ajouter un Établissement" else "Add Institution",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colors.primary
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = schoolName,
                        onValueChange = { schoolName = it },
                        label = { Text("Nom de l'établissement") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = schoolDesc,
                        onValueChange = { schoolDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = schoolAddress,
                        onValueChange = { schoolAddress = it },
                        label = { Text("Adresse") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Université", "École", "Centre de Formation").forEach { opt ->
                            FilterChip(
                                selected = schoolType == opt,
                                onClick = { schoolType = opt },
                                label = { Text(opt, fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = schoolTel,
                        onValueChange = { schoolTel = it },
                        label = { Text("Téléphone (+241)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = schoolEmail,
                        onValueChange = { schoolEmail = it },
                        label = { Text("Adresse E-mail") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddSchoolDialog = false }) {
                            Text("Annuler")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (schoolName.isNotBlank()) {
                                    val newSchool = AcademicSchool(
                                        id = schoolsList.size + 1,
                                        name = schoolName,
                                        logoEmoji = if (schoolType == "Université") "🏫" else "💻",
                                        type = schoolType,
                                        description = schoolDesc,
                                        address = schoolAddress,
                                        website = "www.${schoolName.lowercase().replace(" ", "")}.ga",
                                        tel = schoolTel,
                                        email = schoolEmail,
                                        isVerified = false,
                                        filieres = listOf("Tronc Commun Informatique"),
                                        studentCount = 10
                                    )
                                    schoolsList = schoolsList + newSchool
                                }
                                showAddSchoolDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                        ) {
                            Text("Créer")
                        }
                    }
                }
            }
        }
    }

    // 2. Add Filiere Dialog
    if (showAddFiliereDialog && selectedSchoolForFiliere != null) {
        var filiereName by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showAddFiliereDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ajouter une filière d'études - ${selectedSchoolForFiliere!!.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = colors.primary
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = filiereName,
                        onValueChange = { filiereName = it },
                        label = { Text("Nom de la Filière (ex: Génie Logiciel)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddFiliereDialog = false }) {
                            Text("Annuler")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (filiereName.isNotBlank()) {
                                    schoolsList = schoolsList.map { s ->
                                        if (s.id == selectedSchoolForFiliere!!.id) {
                                            s.copy(filieres = s.filieres + filiereName)
                                        } else s
                                    }
                                }
                                showAddFiliereDialog = false
                            }
                        ) {
                            Text("Ajouter")
                        }
                    }
                }
            }
        }
    }

    // 3. Add Doc in Digital Library
    if (showAddDocDialog) {
        var docTitle by remember { mutableStateOf("") }
        var docCategory by remember { mutableStateOf("Cours") }
        var docDesc by remember { mutableStateOf("") }
        var docAuthor by remember { mutableStateOf(activeProfile?.fullName ?: "Visiteur") }
        var docSchool by remember { mutableStateOf("Université de Libreville") }

        Dialog(onDismissRequest = { showAddDocDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Publier un Document Académique",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = colors.primary
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = docTitle,
                        onValueChange = { docTitle = it },
                        label = { Text("Titre du document") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = docDesc,
                        onValueChange = { docDesc = it },
                        label = { Text("Description succincte") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    Text("Catégorie :", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Cours", "Mémoire", "Projet de fin d'étude", "Livre numérique", "Tutoriel").forEach { opt ->
                            FilterChip(
                                selected = docCategory == opt,
                                onClick = { docCategory = opt },
                                label = { Text(opt, fontSize = 9.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = docAuthor,
                        onValueChange = { docAuthor = it },
                        label = { Text("Auteur") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = docSchool,
                        onValueChange = { docSchool = it },
                        label = { Text("Établissement") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddDocDialog = false }) {
                            Text("Annuler")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (docTitle.isNotBlank()) {
                                    val newItem = AcademicLibraryItem(
                                        id = libraryList.size + 1,
                                        title = docTitle,
                                        category = docCategory,
                                        description = docDesc,
                                        author = docAuthor,
                                        school = docSchool
                                    )
                                    libraryList = libraryList + newItem
                                }
                                showAddDocDialog = false
                            }
                        ) {
                            Text("Publier l'ouvrage")
                        }
                    }
                }
            }
        }
    }

    // 4. Add Course Dialog (Teacher workspace)
    if (showAddCourseDialog) {
        var teacherName by remember { mutableStateOf(activeProfile?.fullName ?: "Pr. Mbenga") }
        var fieldSub by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddCourseDialog = false }) {
            Card( shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = colors.surface), modifier = Modifier.padding(16.dp) ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Devenir Enseignant / Mettre à jour", fontWeight = FontWeight.Bold, color = colors.primary)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = teacherName,
                        onValueChange = { teacherName = it },
                        label = { Text("Votre Nom complet") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fieldSub,
                        onValueChange = { fieldSub = it },
                        label = { Text("Vos Spécialités d'enseignement") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddCourseDialog = false }) { Text("Annuler") }
                        Button(onClick = {
                            if (teacherName.isNotBlank() && fieldSub.isNotBlank()) {
                                teachersList = teachersList + AcademicTeacher(
                                    id = teachersList.size + 1,
                                    name = teacherName,
                                    emoji = "👨‍🏫",
                                    email = "${teacherName.lowercase().replace(" ", "")}@devgabon.ga",
                                    specialties = fieldSub,
                                    coursesCount = 1,
                                    activeGradings = 1
                                )
                            }
                            showAddCourseDialog = false
                        }) { Text("Enregistrer") }
                    }
                }
            }
        }
    }

    // 5. Add Internship Offer
    if (showAddInternshipDialog) {
        var intTitle by remember { mutableStateOf("") }
        var intCompany by remember { mutableStateOf("") }
        var intDesc by remember { mutableStateOf("") }
        var intSchool by remember { mutableStateOf("Université de Libreville") }

        Dialog(onDismissRequest = { showAddInternshipDialog = false }) {
            Card( shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = colors.surface), modifier = Modifier.padding(16.dp) ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Créer une Offre d'Alternance / Stage", fontWeight = FontWeight.Bold, color = colors.primary)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = intTitle, onValueChange = { intTitle = it }, label = { Text("Intitulé du poste") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(value = intCompany, onValueChange = { intCompany = it }, label = { Text("Entreprise / Organisme") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(value = intDesc, onValueChange = { intDesc = it }, label = { Text("Description des tâches") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(value = intSchool, onValueChange = { intSchool = it }, label = { Text("École partenaire") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddInternshipDialog = false }) { Text("Annuler") }
                        Button(onClick = {
                            if (intTitle.isNotBlank()) {
                                internshipsList = internshipsList + AcademicInternship(
                                    id = internshipsList.size + 1,
                                    title = intTitle,
                                    company = intCompany,
                                    logoEmoji = "💼",
                                    description = intDesc,
                                    postedBySchool = intSchool,
                                    status = "Disponible"
                                )
                            }
                            showAddInternshipDialog = false
                        }) { Text("Diffuser") }
                    }
                }
            }
        }
    }

    // 6. Cohort Private Chat
    if (showAddPromoPostDialog) {
        var userText by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showAddPromoPostDialog = false }) {
            Card( shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = colors.surface), modifier = Modifier.padding(16.dp) ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Discuter dans la Promotion GL 2026", fontWeight = FontWeight.Bold, color = colors.primary)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = userText, onValueChange = { userText = it }, label = { Text("Votre message") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddPromoPostDialog = false }) { Text("Fermer") }
                        Button(onClick = {
                            if (userText.isNotBlank()) {
                                cohortPosts = cohortPosts + Pair(activeProfile?.fullName ?: "MartiDev", userText)
                            }
                            showAddPromoPostDialog = false
                        }) { Text("Envoyer") }
                    }
                }
            }
        }
    }

    // 7. Verification Request (Schools)
    if (showVerificationDialog) {
        Dialog(onDismissRequest = { showVerificationDialog = false }) {
            Card( shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = colors.surface), modifier = Modifier.padding(16.dp) ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Demande de Badge Vérifié ✅", fontWeight = FontWeight.Bold, color = colors.primary, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Soumettez les registres officiels de votre établissement au comité de modération de DevGabon pour obtenir le sceau officiel d'authenticité.",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        // Immediately verifies the very first school to showcase success dynamically
                        schoolsList = schoolsList.map { s -> if (s.id == 1) s.copy(isVerified = true) else s }
                        showVerificationDialog = false
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))) {
                        Text("Envoyer les Certificats d'Agrément")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// WORKSPACES RENDER CODE
// -------------------------------------------------------------

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun SchoolsWorkspace(
    schools: List<AcademicSchool>,
    userRole: String,
    languageCode: String,
    colors: ColorScheme,
    onAddSchool: () -> Unit,
    onAddFiliere: (AcademicSchool) -> Unit,
    onVerifySchool: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (languageCode == "FR") "Établissements Agréés" else "Approved Institutions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colors.primary
                    )
                    Text("Filières technologiques au Gabon", fontSize = 11.sp, color = colors.onSurfaceVariant)
                }

                // If role matches School/University or Admin, they get creation actions
                if (userRole == "École / Université" || userRole == "Administrateur") {
                    Button(
                        onClick = onAddSchool,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.Add, "Créer", modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Établissement", fontSize = 11.sp)
                    }
                }
            }
        }

        items(schools) { school ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.12f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = colors.primary.copy(alpha = 0.1f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(school.logoEmoji, fontSize = 22.sp)
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(school.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                if (school.isVerified) {
                                    Spacer(Modifier.width(4.dp))
                                    Text("✅", fontSize = 12.sp) // Custom verified badge as requested
                                }
                            }
                            Text(school.type, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.secondary)
                        }
                        
                        // Verification action if not verified
                        if (!school.isVerified && (userRole == "École / Université" || userRole == "Administrateur")) {
                            IconButton(onClick = onVerifySchool, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.VerifiedUser, "Vérifier", tint = colors.error, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(school.description, fontSize = 12.sp, color = colors.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))

                    Text("📍 ${school.address}", fontSize = 10.sp, fontStyle = FontStyle.Italic)
                    Text("✉️ ${school.email} | 📞 ${school.tel}", fontSize = 10.sp, color = colors.onSurfaceVariant)

                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = colors.outline.copy(alpha = 0.1f))

                    Text(
                        text = if (languageCode == "FR") "Filières numériques :" else "Digital Sectors:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = colors.primary
                    )

                    // Filiere labels
                    FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        school.filieres.forEach { f ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = colors.surfaceVariant.copy(alpha = 0.6f),
                                border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.08f))
                            ) {
                                Text(
                                    text = f,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        
                        // Action to let school add modern fields
                        if (userRole == "École / Université" || userRole == "Administrateur") {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = colors.primary.copy(alpha = 0.1f),
                                modifier = Modifier.clickable { onAddFiliere(school) }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Add, "", modifier = Modifier.size(10.dp), tint = colors.primary)
                                    Spacer(Modifier.width(2.dp))
                                    Text("Filière", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.primary)
                                }
                            }
                        }
                    }

                    if (school.announcements.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            color = colors.primary.copy(alpha = 0.04f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("📢 Communiqué Officiel", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = colors.primary)
                                school.announcements.forEach { announce ->
                                    Text("• $announce", fontSize = 11.sp, fontStyle = FontStyle.Italic)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryWorkspace(
    libraryItems: List<AcademicLibraryItem>,
    userRole: String,
    languageCode: String,
    colors: ColorScheme,
    onAddDoc: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (languageCode == "FR") "Bibliothèque Numérique" else "Digital Library",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colors.primary
                    )
                    Text("Ouvrages, mémoires, supports de cours validés", fontSize = 11.sp, color = colors.onSurfaceVariant)
                }

                if (userRole == "Enseignant" || userRole == "École / Université" || userRole == "Administrateur") {
                    Button(
                        onClick = onAddDoc,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.UploadFile, "", modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Publier", fontSize = 10.sp)
                    }
                }
            }
        }

        items(libraryItems) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val iconCode = when (item.category) {
                            "Cours" -> "📚"
                            "Mémoire" -> "🎓"
                            "Projet de fin d'étude" -> "🛠️"
                            "Livre numérique" -> "📖"
                            else -> "📝"
                        }
                        Text(iconCode, fontSize = 24.sp)
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(item.category, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = colors.secondary)
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                    Text(item.description, fontSize = 11.sp, color = colors.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auteur : ${item.author}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Source : ${item.school}", fontSize = 9.sp, color = colors.onSurfaceVariant)
                        }

                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = colors.secondary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Icon(Icons.Default.Download, "", modifier = Modifier.size(10.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Télécharger (PDF)", fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeachersWorkspace(
    teachers: List<AcademicTeacher>,
    userRole: String,
    languageCode: String,
    colors: ColorScheme,
    onAddCourse: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (languageCode == "FR") "Suivi des Enseignants" else "Faculty & Mentors",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colors.primary
                    )
                    Text("Encadrement de mémoires, cours & TD", fontSize = 11.sp, color = colors.onSurfaceVariant)
                }

                Button(
                    onClick = onAddCourse,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(Icons.Default.School, "", modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("S'enregistrer", fontSize = 10.sp)
                }
            }
        }

        items(teachers) { teacher ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = colors.primary.copy(alpha = 0.1f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(teacher.emoji, fontSize = 24.sp)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(teacher.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.Default.Check, "Agréé", tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                            }
                            Text(teacher.email, fontSize = 10.sp, color = colors.onSurfaceVariant)
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                    Text("🔬 Spécialités d'encadrement :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.primary)
                    Text(teacher.specialties, fontSize = 11.sp, color = colors.onSurfaceVariant)

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = colors.outline.copy(alpha = 0.1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column {
                                Text("Cours rédigés", fontSize = 10.sp, color = colors.onSurfaceVariant)
                                Text("${teacher.coursesCount} supports", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Mémoires encadrés", fontSize = 10.sp, color = colors.onSurfaceVariant)
                                Text("${teacher.activeGradings} mémoires", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Call action / Mentoring triggers
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = colors.secondary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Icon(Icons.Default.MailOutline, "", modifier = Modifier.size(10.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Contacter", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PromosClubsWorkspace(
    clubs: List<AcademicClub>,
    cohortPosts: List<Pair<String, String>>,
    userRole: String,
    languageCode: String,
    colors: ColorScheme,
    onAddPost: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Promotion Cohorts Space (Génie Logiciel 2026)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("🎓 Promotion Génie Logiciel 2026", fontWeight = FontWeight.Black, fontSize = 14.sp, color = colors.primary)
                            Text("Forum de discussion privé de la cohorte", fontSize = 10.sp)
                        }
                        Button(
                            onClick = onAddPost,
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text("Chatter", fontSize = 10.sp)
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        cohortPosts.forEach { (sender, text) ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = colors.surface),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(modifier = Modifier.padding(8.dp)) {
                                    Surface(shape = CircleShape, color = colors.secondary, modifier = Modifier.size(24.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(sender.take(1), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.onSecondary)
                                        }
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(sender, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        Text(text, fontSize = 10.sp, color = colors.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                    Text("💡 Pièce jointe partagée : [Soutenance_Planning_2026.xlsx] - 540 Ko", fontSize = 9.sp, fontStyle = FontStyle.Italic, color = colors.primary)
                }
            }
        }

        // Clubs Space
        item {
            Text(
                text = "Clubs Informatiques & Communautés",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = colors.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(clubs) { club ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(club.emoji, fontSize = 28.sp)
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(club.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(club.description, fontSize = 11.sp, color = colors.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        Text("📅 Évènement : ${club.nextEvent}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.primary)
                        Text("Leader : ${club.leaderName} | ${club.membersCount} membres", fontSize = 9.sp, color = colors.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun InternshipsWorkspace(
    internships: List<AcademicInternship>,
    userRole: String,
    languageCode: String,
    colors: ColorScheme,
    onAddInternship: () -> Unit,
    onRegisterLocalReport: (Int, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Espace Stages / Alternance",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colors.primary
                    )
                    Text("Suivi des mémoires et stages par les universités", fontSize = 11.sp, color = colors.onSurfaceVariant)
                }

                if (userRole == "École / Université" || userRole == "Administrateur" || userRole == "Entreprise") {
                    Button(
                        onClick = onAddInternship,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.Add, "", modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Publier Stage", fontSize = 10.sp)
                    }
                }
            }
        }

        items(internships) { intern ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(intern.logoEmoji, fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(intern.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(intern.company, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.primary)
                        }
                        
                        // Status badge
                        val stateColor = when (intern.status) {
                            "Disponible" -> Color(0xFF10B981)
                            "En cours" -> Color(0xFFF59E0B)
                            else -> Color(0xFF0EA5E9)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = stateColor.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, stateColor)
                        ) {
                            Text(
                                text = intern.status,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = stateColor
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(intern.description, fontSize = 11.sp, color = colors.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("🎓 École affiliée : ${intern.postedBySchool}", fontSize = 10.sp, fontStyle = FontStyle.Italic)

                    if (intern.studentAssigned != null) {
                        Text("👤 Étudiant attitré : ${intern.studentAssigned}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.secondary)
                    }

                    // Interactive actions for school/teacher validation
                    if (intern.status != "Rapport validé" && (userRole == "École / Université" || userRole == "Enseignant")) {
                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = colors.outline.copy(alpha = 0.1f))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { onRegisterLocalReport(intern.id, "MartiDev") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, "", modifier = Modifier.size(10.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Valider le Rapport de Stage", fontSize = 10.sp)
                            }
                        }
                    } else if (intern.status == "Disponible" && userRole == "Étudiant") {
                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = colors.outline.copy(alpha = 0.1f))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { onRegisterLocalReport(intern.id, "Moi-même") },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text("Postuler", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RolesMatrixWorkspace(
    languageCode: String,
    colors: ColorScheme
) {
    val rolesDef = listOf(
        Pair("Visiteur", "Consultation de l'annuaire, de la bibliothèque publique et du fil d'actualités générale sans modification."),
        Pair("Étudiant", "Accès complet au réseau d'apprentissage, rejoignez des groupes de promotion privés, postulez à des offres de stage et d'alternance."),
        Pair("Développeur", "Partage de code source, soumission d'articles techniques, collaboration sur les projets collectifs innovants."),
        Pair("Enseignant", "Formation, publication de cours validés & TD, correction de projets étudiants et encadrement de mémoires."),
        Pair("École / Université", "Gestion des pages officielles agréées, validation des promotions, publication des annonces importantes, affectation et suivi de stages."),
        Pair("Entreprise", "Espace de recrutement privilégié, diffusion d'offres de stage/CDI, recherche d'experts IT qualifiés à Libreville."),
        Pair("Modérateur", "Surveillance générale, nettoyage et modération de contenus non conformes à l'éthique gabonaise."),
        Pair("Administrateur", "Gestion totale de la plateforme, déploiement des modules techniques, validation des établissements.")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = if (languageCode == "FR") "Matrice des Rôles & Accès" else "Multi-Role Access Architecture",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = colors.primary
            )
            Text(
                "Le premier réseau social académique et technologique du Gabon",
                fontSize = 12.sp,
                color = colors.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
        }

        items(rolesDef) { (roleName, roleDesc) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val roleEmoji = when (roleName) {
                        "Visiteur" -> "👀"
                        "Étudiant" -> "🎓"
                        "Développeur" -> "💻"
                        "Enseignant" -> "👨‍🏫"
                        "École / Université" -> "🏫"
                        "Entreprise" -> "🏢"
                        "Modérateur" -> "🛡️"
                        else -> "⚙️"
                    }
                    Text(roleEmoji, fontSize = 32.sp)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(roleName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.primary)
                        Spacer(Modifier.height(2.dp))
                        Text(roleDesc, fontSize = 11.sp, color = colors.onSurfaceVariant, lineHeight = 16.sp)
                    }
                }
            }
        }
    }
}
