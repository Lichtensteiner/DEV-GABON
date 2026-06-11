package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.viewmodel.DevGabonViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import android.util.Log

object Trans {
    fun get(key: String, code: String): String {
        return (if (code == "FR") fr[key] else en[key]) ?: fr[key] ?: key
    }

    private val fr = mapOf(
        "nav_mission" to "Mission",
        "nav_func" to "Fonctionnalités",
        "nav_annuaire" to "Annuaire",
        "nav_recrutement" to "Recrutement",
        "nav_contexte" to "Contexte",
        "nav_auteur" to "Auteur",
        "nav_join" to "Rejoindre",
        "nav_connect" to "Connexion",
        "nav_logout" to "Se déconnecter",
        "hero_title" to "Votre Carrière Tech & Littéraire au Gabon",
        "hero_sub" to "Une plateforme unique conçue pour connecter les développeurs, les écrivains et les recruteurs du Gabon. Partagez votre code, publiez vos articles et faites rayonner le talent gabonais.",
        "mission_title" to "Notre Mission",
        "mission_desc" to "DevGabon n'est pas qu'un simple réseau social. C'est un catalyseur d'innovation et de créativité pour la jeunesse gabonaise.",
        "mission_goal" to "Bâtir l'écosystème numérique de demain au Gabon",
        "obj_title" to "Objectif",
        "obj_desc" to "Fédérer les talents technologiques et littéraires du pays sur une plateforme unique et sécurisée.",
        "vision_title" to "Vision",
        "vision_desc" to "Devenir la référence incontournable pour le recrutement et le partage de connaissances au Gabon.",
        "val_title" to "Valeurs",
        "val_desc" to "Collaboration, excellence technique et promotion de la culture gabonaise à travers le numérique.",
        "func_title" to "Une plateforme complète pour tous les profils",
        "func_dev_title" to "Pour les Développeurs",
        "func_dev_desc" to "Exposez vos projets GitHub, partagez vos snippets de code et trouvez des opportunités de carrière.",
        "func_wr_title" to "Pour les Écrivains",
        "func_wr_desc" to "Publiez vos articles, recevez des retours de la communauté et faites-vous un nom.",
        "func_lib_title" to "Bibliothèque Numérique",
        "func_lib_desc" to "Une vitrine pour les ouvrages locaux, permettant aux auteurs de promouvoir leurs livres.",
        "func_rec_title" to "Espace Recrutement",
        "func_rec_desc" to "Les entreprises peuvent poster des offres et trouver les meilleurs profils IT du Gabon.",
        "ann_title" to "Annuaire & Réseau",
        "ann_subtitle" to "Connectez-vous avec les experts IT du Gabon",
        "ann_desc" to "Accédez à un annuaire complet des développeurs, ingénieurs et créateurs. Échangez, collaborez et bâtissez votre réseau professionnel local.",
        "ann_badge_dev" to "Développeurs",
        "ann_badge_wr" to "Écrivains",
        "ann_badge_rec" to "Recruteurs",
        "ann_badge_adm" to "Admins",
        "rec_title" to "Recrutement IT",
        "rec_subtitle" to "Trouvez les meilleurs talents IT du Gabon",
        "rec_desc" to "Les entreprises peuvent poster des offres et trouver les meilleurs profils IT du Gabon. Un espace dédié pour propulser l'emploi local.",
        "rec_btn" to "Accéder au réseau",
        "rec_mgr_title" to "Responsable Recrutement",
        "rec_mgr_sub" to "IT Recruitment • Gabon Tech",
        "idea_title" to "L'idée derrière le projet",
        "idea_author" to "Vision d'un Ingénieur Passionné",
        "idea_quote" to "\"En tant qu'ingénieur en informatique spécialisé en programmation de logiciels, j'ai vu le potentiel immense de la jeunesse gabonaise souvent freiné par le manque d'outils adaptés. DevGabon est ma réponse : un espace où la rigueur du code rencontre la beauté de la plume.\"",
        "idea_exp_title" to "Expertise Logicielle",
        "idea_exp_desc" to "Conception robuste et scalable.",
        "idea_soc_title" to "Engagement Social",
        "idea_soc_desc" to "Promouvoir l'excellence locale.",
        "footer_sub" to "La plateforme sociale qui connecte l'intelligence technologique et la créativité littéraire du Gabon.",
        "footer_quick" to "Liens Rapides",
        "footer_contact" to "Contact & Bureaux",
        "login_dialog_title" to "Authentification Google",
        "login_dialog_sub" to "Connexion directe et synchronisation en temps réel via l'infrastructure sécurisée de DevGabon.",
        "login_dialog_or" to "Ou créer un nouveau profil Google",
        "login_btn_sign" to "Se connecter à ce compte",
        "login_name_hint" to "Nom complet",
        "login_email_hint" to "Adresse Email",
        "login_pseudo_hint" to "Pseudo (ex: GabDev)",
        "login_create_btn" to "Créer mon profil Google Net"
    )

    private val en = mapOf(
        "nav_mission" to "Mission",
        "nav_func" to "Features",
        "nav_annuaire" to "Directory",
        "nav_recrutement" to "Recruitment",
        "nav_contexte" to "Context",
        "nav_auteur" to "Author",
        "nav_join" to "Join",
        "nav_connect" to "Sign In",
        "nav_logout" to "Sign Out",
        "hero_title" to "Your Tech & Literary Career in Gabon",
        "hero_sub" to "A unique platform designed to connect developers, writers, and recruiters in Gabon. Share your code, publish your articles, and showcase Gabonese talent.",
        "mission_title" to "Our Mission",
        "mission_desc" to "DevGabon is not just a social network. It is a catalyst for innovation and creativity for the Gabonese youth.",
        "mission_goal" to "Building tomorrow's digital ecosystem in Gabon",
        "obj_title" to "Objective",
        "obj_desc" to "Unite the country's technological and literary talents on a unique and secure platform.",
        "vision_title" to "Vision",
        "vision_desc" to "Become the ultimate reference for recruitment and knowledge sharing in Gabon.",
        "val_title" to "Values",
        "val_desc" to "Collaboration, technical excellence, and promotion of Gabonese culture through technology.",
        "func_title" to "A complete platform for all profiles",
        "func_dev_title" to "For Developers",
        "func_dev_desc" to "Showcase your GitHub projects, share code snippets, and find career opportunities.",
        "func_wr_title" to "For Writers",
        "func_wr_desc" to "Publish your articles, get community feedback, and establish your reputation.",
        "func_lib_title" to "Digital Library",
        "func_lib_desc" to "A showcase for local literary works, letting authors promote their books.",
        "func_rec_title" to "Recruitment Space",
        "func_rec_desc" to "Companies can post job offers and find the best IT profiles in Gabon.",
        "ann_title" to "Directory & Network",
        "ann_subtitle" to "Connect with Gabon's IT Experts",
        "ann_desc" to "Access a comprehensive directory of developers, engineers, and creators. Exchange, collaborate, and build your local professional network.",
        "ann_badge_dev" to "Developers",
        "ann_badge_wr" to "Writers",
        "ann_badge_rec" to "Recruiters",
        "ann_badge_adm" to "Admins",
        "rec_title" to "IT Recruitment",
        "rec_subtitle" to "Find the Best IT Talents in Gabon",
        "rec_desc" to "Companies can post offers and find local IT profiles. A dedicated space to power local employment.",
        "rec_btn" to "Access network",
        "rec_mgr_title" to "Recruitment Lead",
        "rec_mgr_sub" to "IT Recruitment • Gabon Tech",
        "idea_title" to "The idea behind the project",
        "idea_author" to "A Passionate Engineer's Vision",
        "idea_quote" to "\"As a software engineer specializing in software programming, I saw the immense potential of Gabon's youth often limited by the lack of custom tools. DevGabon is my answer: a space where programmatic rigor meets literary beauty.\"",
        "idea_exp_title" to "Software Expertise",
        "idea_exp_desc" to "Robust and scalable software design.",
        "idea_soc_title" to "Social Commitment",
        "idea_soc_desc" to "Promoting local excellence.",
        "footer_sub" to "The social platform connecting technological intelligence and literary creativity in Gabon.",
        "footer_quick" to "Quick Links",
        "footer_contact" to "Contact & Offices",
        "login_dialog_title" to "Google Authentication",
        "login_dialog_sub" to "Direct connection and real-time synchronization through the secure DevGabon infrastructure.",
        "login_dialog_or" to "Or create a new Google profile",
        "login_btn_sign" to "Sign in to this account",
        "login_name_hint" to "Full Name",
        "login_email_hint" to "Email Address",
        "login_pseudo_hint" to "Username (e.g. GabDev)",
        "login_create_btn" to "Create Google Net Profile"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingPageScreen(viewModel: DevGabonViewModel) {
    val languageCode by viewModel.languageCode.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val activeProfile by viewModel.activeUserProfile.collectAsState()
    val colors = MaterialTheme.colorScheme
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var showGoogleLoginDialog by remember { mutableStateOf(false) }

    // Navigation sections index map (matching indices in LazyColumn)
    // 0: Header/Hero, 1: Mission, 2: Fonctionnalites, 3: Annuaire, 4: Recrutement, 5: Contexte, 6: Auteur/Contact
    val navItems = listOf(
        Pair("nav_mission", 1),
        Pair("nav_func", 2),
        Pair("nav_annuaire", 3),
        Pair("nav_recrutement", 4),
        Pair("nav_contexte", 5),
        Pair("nav_auteur", 6)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            coroutineScope.launch { listState.animateScrollToItem(0) }
                        }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "DEV GABON Logo",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                    }
                },
                actions = {
                    // Dark theme toggle
                    IconButton(onClick = { viewModel.toggleDarkTheme() }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Theme Toggle"
                        )
                    }

                    // Translation flag toggle
                    IconButton(onClick = { viewModel.toggleLanguage() }) {
                        Text(
                            text = if (languageCode == "FR") "🇫🇷" else "🇬🇧",
                            fontSize = 20.sp
                        )
                    }

                    Spacer(Modifier.width(4.dp))

                    if (isLoggedIn) {
                        if (activeProfile != null) {
                            ProfileImage(
                                picture = activeProfile!!.profilePicture,
                                size = 32.dp,
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clickable {
                                        viewModel.navigateTo(Screen.Profile)
                                    }
                            )
                        }

                        // Rejoindre Button (Direct main navigation click)
                        Button(
                            onClick = { viewModel.navigateTo(Screen.Feed) },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(32.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = Trans.get("nav_join", languageCode),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.onPrimary
                            )
                        }

                        // Déconnexion Button
                        TextButton(
                            onClick = { viewModel.logout() },
                            colors = ButtonDefaults.textButtonColors(contentColor = colors.error)
                        ) {
                            Text(
                                text = Trans.get("nav_logout", languageCode),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    } else {
                        // Connexion (Google Single Sign-On Button)
                        TextButton(
                            onClick = { showGoogleLoginDialog = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = colors.primary)
                        ) {
                            Text(
                                text = Trans.get("nav_connect", languageCode),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface,
                    titleContentColor = colors.onSurface
                ),
                modifier = Modifier.shadow(2.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Horizontal Navigation Ribbon - Sticky, customizable, scrollable
            ScrollableTabRow(
                selectedTabIndex = 0,
                edgePadding = 12.dp,
                containerColor = colors.surfaceVariant.copy(alpha = 0.4f),
                indicator = {},
                divider = {
                    // Gabon flag ribbon divider
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                    ) {
                        Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFF10B981)))
                        Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFF59E0B)))
                        Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFF0EA5E9)))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                navItems.forEach { (key, index) ->
                    Tab(
                        selected = false,
                        onClick = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        },
                        text = {
                            Text(
                                text = Trans.get(key, languageCode),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            // Page Contents
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                // Item 0: Hero Section
                item {
                    val onGetClick = if (isLoggedIn) { { viewModel.navigateTo(Screen.Feed) } } else { { showGoogleLoginDialog = true } }
                    HeroSection(colors = colors, languageCode = languageCode, onGetStarted = onGetClick, isLoggedIn = isLoggedIn)
                }

                // Item 1: Mission Section
                item {
                    MissionSection(colors = colors, languageCode = languageCode)
                }

                // Item 2: Fonctionnalités Section
                item {
                    FeaturesSection(colors = colors, languageCode = languageCode)
                }

                // Item 3: Annuaire Section
                item {
                    val onExploreClick = if (isLoggedIn) { { viewModel.navigateTo(Screen.Feed) } } else { { showGoogleLoginDialog = true } }
                    DirectorySection(colors = colors, languageCode = languageCode, onExplore = onExploreClick)
                }

                // Item 4: Recrutement Section
                item {
                    val onAccessClick = if (isLoggedIn) { { viewModel.navigateTo(Screen.Careers) } } else { { showGoogleLoginDialog = true } }
                    RecruitmentSection(colors = colors, languageCode = languageCode, onAccess = onAccessClick)
                }

                // Item 5: Contexte / L'idée Section
                item {
                    ContextSection(colors = colors, languageCode = languageCode)
                }

                // Item 6: Auteur & Team Section
                item {
                    AuthorSection(viewModel = viewModel, colors = colors, languageCode = languageCode)
                }

                // Item 7: Footer
                item {
                    FooterSection(colors = colors, languageCode = languageCode, onNavigate = { idx ->
                        coroutineScope.launch { listState.animateScrollToItem(idx) }
                    })
                }
            }
        }
    }

    // Interactive Google Accounts Selector Sheet
    if (showGoogleLoginDialog) {
        GoogleLoginDialog(
            viewModel = viewModel,
            languageCode = languageCode,
            onDismiss = { showGoogleLoginDialog = false }
        )
    }
}

@Composable
fun HeroSection(colors: ColorScheme, languageCode: String, onGetStarted: () -> Unit, isLoggedIn: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colors.surfaceVariant.copy(alpha = 0.5f), colors.background)
                )
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.ludodev),
            contentDescription = "Background",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.22f
        )
        // Tint gradient layer to blend background beautifully
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            colors.background.copy(alpha = 0.3f),
                            colors.background
                        )
                    )
                )
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colors.primary.copy(alpha = 0.1f),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "🇬🇦 DEV GABON NETWORK",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }

            Text(
                text = Trans.get("hero_title", languageCode),
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                textAlign = TextAlign.Center,
                color = colors.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = Trans.get("hero_sub", languageCode),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = colors.onSurfaceVariant,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .widthIn(max = 600.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onGetStarted,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Google Sign In", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isLoggedIn) {
                            if (languageCode == "FR") "Rejoindre le Réseau" else "Rejoin Network"
                        } else {
                            if (languageCode == "FR") "Se connecter via Google" else "Connect with Google"
                        },
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Elegant Mock App Screens Floating Badges
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf("💻 CODE", "✍️ PLUME", "🏢 EMPLOI", "🇬🇦 GABON").forEach { label ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colors.surface,
                        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.2f)),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MissionSection(colors: ColorScheme, languageCode: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        BadgeHeader(title = Trans.get("mission_title", languageCode), colors = colors)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Mission Star",
                        tint = colors.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = Trans.get("mission_goal", languageCode),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colors.primary
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = Trans.get("mission_desc", languageCode),
                    fontSize = 14.sp,
                    color = colors.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Three Pillars representing Vision, Goal, Values
            listOf(
                Triple("obj_title", "obj_desc", Icons.Default.CheckCircle),
                Triple("vision_title", "vision_desc", Icons.Default.Info),
                Triple("val_title", "val_desc", Icons.Default.ThumbUp)
            ).forEach { (titleKey, descKey, icon) ->
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(icon, contentDescription = "", tint = colors.secondary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.height(4.dp))
                        Text(Trans.get(titleKey, languageCode), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.onSurface)
                        Spacer(Modifier.height(4.dp))
                        Text(Trans.get(descKey, languageCode), fontSize = 10.sp, color = colors.onSurfaceVariant, lineHeight = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturesSection(colors: ColorScheme, languageCode: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        BadgeHeader(title = Trans.get("func_title", languageCode), colors = colors)
        Spacer(Modifier.height(16.dp))

        val cards = listOf(
            Triple("func_dev_title", "func_dev_desc", "💻"),
            Triple("func_wr_title", "func_wr_desc", "✍️"),
            Triple("func_lib_title", "func_lib_desc", "📚"),
            Triple("func_rec_title", "func_rec_desc", "🏢")
        )

        cards.forEach { (titleKey, descKey, emoji) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(emoji, fontSize = 28.sp)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = Trans.get(titleKey, languageCode),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = colors.onSurface
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = Trans.get(descKey, languageCode),
                            fontSize = 12.sp,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DirectorySection(colors: ColorScheme, languageCode: String, onExplore: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceVariant.copy(alpha = 0.3f))
            .padding(24.dp)
    ) {
        BadgeHeader(title = Trans.get("ann_title", languageCode), colors = colors)
        Spacer(Modifier.height(8.dp))

        Text(
            text = Trans.get("ann_subtitle", languageCode),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = colors.primary
        )

        Text(
            text = Trans.get("ann_desc", languageCode),
            fontSize = 14.sp,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val roles = listOf("ann_badge_dev", "ann_badge_wr", "ann_badge_rec", "ann_badge_adm")
            roles.forEach { rKey ->
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = Trans.get(rKey, languageCode),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = colors.primary
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onExplore,
            colors = ButtonDefaults.buttonColors(containerColor = colors.secondary),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = if (languageCode == "FR") "Rechercher des Experts" else "Search Experts", fontSize = 12.sp)
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = "", modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun RecruitmentSection(colors: ColorScheme, languageCode: String, onAccess: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.2f))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.dev),
                contentDescription = "Recruitment Background",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.16f
            )
            // Color overlay to tie the card beautifully to the theme colors
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                colors.surface.copy(alpha = 0.65f),
                                colors.surface
                            )
                        )
                    )
            )
            Column(modifier = Modifier.padding(20.dp)) {
                BadgeHeader(title = Trans.get("rec_title", languageCode), colors = colors)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = Trans.get("rec_subtitle", languageCode),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colors.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = Trans.get("rec_desc", languageCode),
                    fontSize = 13.sp,
                    color = colors.onSurfaceVariant
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = colors.outline.copy(alpha = 0.2f))

                // Recruitment Lead Card
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = CircleShape,
                        color = colors.primary,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("👨‍💼", fontSize = 24.sp)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "M. Mve Zogo Ludovic Martinien",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = colors.onSurface
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, "Verified Recruiter", tint = colors.secondary, modifier = Modifier.size(14.dp))
                        }
                        Text(
                            Trans.get("rec_mgr_sub", languageCode),
                            fontSize = 11.sp,
                            color = colors.onSurfaceVariant
                        )
                        Text(
                            "ludo.consulting3@gmail.com",
                            fontSize = 10.sp,
                            color = colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onAccess,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) {
                    Text(text = Trans.get("rec_btn", languageCode), fontSize = 13.sp)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.LockOpen, contentDescription = "", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun ContextSection(colors: ColorScheme, languageCode: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        BadgeHeader(title = Trans.get("idea_title", languageCode), colors = colors)
        Spacer(Modifier.height(12.dp))

        Text(
            text = Trans.get("idea_author", languageCode),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = colors.secondary
        )

        Spacer(Modifier.height(8.dp))

        // Author Quote
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.surfaceVariant.copy(alpha = 0.2f),
            border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.1f))
        ) {
            Text(
                text = Trans.get("idea_quote", languageCode),
                fontStyle = FontStyle.Italic,
                fontSize = 13.sp,
                color = colors.onSurface,
                lineHeight = 18.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Software expertise box
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("🛡️", fontSize = 24.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(Trans.get("idea_exp_title", languageCode), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(Trans.get("idea_exp_desc", languageCode), fontSize = 11.sp, color = colors.onSurfaceVariant)
                }
            }

            // Social engagement box
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("🤝", fontSize = 24.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(Trans.get("idea_soc_title", languageCode), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(Trans.get("idea_soc_desc", languageCode), fontSize = 11.sp, color = colors.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun AuthorSection(viewModel: DevGabonViewModel, colors: ColorScheme, languageCode: String) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val activeProfile by viewModel.activeUserProfile.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLoggedIn && activeProfile != null) 
                colors.primaryContainer.copy(alpha = 0.2f) 
            else colors.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (isLoggedIn && activeProfile != null) {
                val profile = activeProfile!!
                // REAL-TIME CONNECTED USER PROFILE CARD ACCORDING TO USER STATE
                Text(
                    text = if (languageCode == "FR") "VOTRE PROFIL CONNECTE (TEMPS RÉEL)" else "YOUR CONNECTED PROFILE (REAL-TIME)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(3.dp, colors.primary, CircleShape)
                        .background(colors.surface),
                    contentAlignment = Alignment.Center
                ) {
                    ProfileImage(
                        picture = profile.profilePicture,
                        size = 84.dp
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = profile.fullName,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = colors.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "@${profile.pseudo}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.secondary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = profile.bio.ifBlank { if (languageCode == "FR") "Membre actif de la communauté DevGabon." else "Active member of the DevGabon community." },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = colors.outline.copy(alpha = 0.15f))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    AssistChip(
                        onClick = {},
                        label = { Text(profile.email, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        leadingIcon = { Icon(Icons.Default.Email, "", modifier = Modifier.size(14.dp)) },
                        shape = RoundedCornerShape(10.dp)
                    )
                    
                    TextButton(onClick = { viewModel.navigateTo(Screen.Profile) }) {
                        Text(
                            text = if (languageCode == "FR") "Gérer mon profil" else "Manage Profile",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.primary
                        )
                    }
                }
            } else {
                // Elegant framed profile photo using the real resident resource: ludodev
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(3.dp, colors.primary, CircleShape)
                        .background(colors.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.ludodev),
                        contentDescription = "M. Mve Zogo Ludovic Martinien",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "M. Mve Zogo Ludovic Martinien",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = colors.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Ingénieur Logiciel & Dev lichtensteiner",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Fondateur & Créateur de DEV GABON",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = colors.outline.copy(alpha = 0.15f))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = {},
                        label = { Text("ludo.consulting3@gmail.com", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Email, "", modifier = Modifier.size(14.dp)) },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FooterSection(colors: ColorScheme, languageCode: String, onNavigate: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(24.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "DEV GABON Logo",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "DevGabon",
                    fontWeight = FontWeight.Black,
                    color = colors.primary,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = Trans.get("footer_sub", languageCode),
                fontSize = 12.sp,
                color = colors.onSurfaceVariant,
                modifier = Modifier.widthIn(max = 400.dp)
            )

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = Trans.get("footer_quick", languageCode),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = colors.primary
                    )
                    Spacer(Modifier.height(6.dp))
                    listOf(
                        Pair("nav_mission", 1),
                        Pair("nav_func", 2),
                        Pair("nav_contexte", 5)
                    ).forEach { (rKey, idx) ->
                        Text(
                            text = Trans.get(rKey, languageCode),
                            fontSize = 11.sp,
                            color = colors.onSurfaceVariant,
                            modifier = Modifier
                                .padding(vertical = 3.dp)
                                .clickable { onNavigate(idx) }
                        )
                    }
                }

                Column(modifier = Modifier.weight(1.2f)) {
                    Text(
                        text = Trans.get("footer_contact", languageCode),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = colors.primary
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("🏢 Libreville, Gabon", fontSize = 11.sp, color = colors.onSurfaceVariant)
                    Text("✉️ ludo.consulting3@gmail.com", fontSize = 10.sp, color = colors.onSurfaceVariant)
                    Text("📞 +241 062-641-120", fontSize = 10.sp, color = colors.onSurfaceVariant)
                    Text("📞 +241 077-022-306", fontSize = 10.sp, color = colors.onSurfaceVariant)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp), color = colors.outline.copy(alpha = 0.2f))

            // Fine national flag ribbon (Green, Yellow, Blue stripes)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
            ) {
                Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFF10B981)))
                Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFF59E0B)))
                Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFF0EA5E9)))
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "© 2026 DevGabon. Fait avec ❤️ pour le Gabon.",
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BadgeHeader(title: String, colors: ColorScheme) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(18.dp)
                .background(colors.primary, RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primary,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun GoogleLoginDialog(
    viewModel: DevGabonViewModel,
    languageCode: String,
    onDismiss: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isVerifying by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var errorCode by remember { mutableStateOf<Int?>(null) }

    // Simulation state for headless test bypass inside the error screen
    var isSimulating by remember { mutableStateOf(false) }
    var simEmail by remember { mutableStateOf("") }
    var simName by remember { mutableStateOf("") }
    var simPseudo by remember { mutableStateOf("") }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        if (data == null) {
            errorMessage = if (languageCode == "FR") 
                "Aucun retour d'autorisation reçu des services Google Play (Action possiblement annulée)." 
                else "No authorization response from Google Play Services."
            isVerifying = false
            return@rememberLauncherForActivityResult
        }

        val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
            if (account != null) {
                val email = account.email ?: ""
                val name = account.displayName ?: ""
                val customPhoto = account.photoUrl?.toString() ?: ""
                var pseudo = if (email.isNotEmpty()) email.substringBefore("@") else "user"

                if (email.isNotBlank() && name.isNotBlank()) {
                    val finalPic = if (customPhoto.isNotEmpty()) customPhoto else "https://api.dicebear.com/7.x/pixel-art/png?seed=${pseudo.trim()}"
                    coroutineScope.launch {
                        viewModel.loginWithGoogle(
                            email = email.trim(),
                            fullName = name.trim(),
                            pseudo = pseudo.trim(),
                            profilePic = finalPic
                        )
                        isVerifying = false
                        onDismiss()
                    }
                } else {
                    errorMessage = if (languageCode == "FR") 
                        "Données de compte incomplètes (email ou nom manquant)." 
                        else "Incomplete account details (email or display name missing)."
                    isVerifying = false
                }
            } else {
                errorMessage = if (languageCode == "FR") 
                    "Échec de l'authentification Google (Compte introuvable)." 
                    else "Google sign-in returned no active account."
                isVerifying = false
            }
        } catch (e: com.google.android.gms.common.api.ApiException) {
            val status = e.statusCode
            errorCode = status
            android.util.Log.e("DevGabonGoogleAuth", "ApiException code: $status", e)
            errorMessage = when (status) {
                10 -> if (languageCode == "FR") {
                    "Erreur de configuration Firebase (DEVELOPER_ERROR - Code 10).\n\nL'empreinte SHA-1 de votre clé de signature d'application n'est pas encore enregistrée dans les paramètres de votre console Firebase (https://console.firebase.google.com/)."
                } else {
                    "Firebase Config Error (DEVELOPER_ERROR - Code 10).\n\nYour application's SHA-1 signing fingerprint is not yet registered in your Firebase console project settings (https://console.firebase.google.com/)."
                }
                7 -> if (languageCode == "FR") "Erreur de réseau (NETWORK_ERROR). Vérifiez votre connexion Internet." else "Network error. Please check your connection."
                12501 -> if (languageCode == "FR") "La connexion Google a été annulée par l'utilisateur." else "Google Sign-In was canceled by the user."
                else -> if (languageCode == "FR") "Erreur API Google Sign-In (Code $status) : ${e.localizedMessage ?: "Inconnu"}" else "Google API Error (Code $status): ${e.localizedMessage ?: "Unknown"}"
            }
            isVerifying = false
        } catch (e: Exception) {
            android.util.Log.e("DevGabonGoogleAuth", "Generic Sign-In Exception", e)
            errorMessage = e.localizedMessage ?: "Unknown connection error"
            isVerifying = false
        }
    }

    fun startGoogleFlow() {
        isVerifying = true
        errorMessage = null
        errorCode = null
        isSimulating = false
        try {
            val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build()
            val googleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso)
            googleSignInClient.signOut().addOnCompleteListener {
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            }
        } catch (e: Exception) {
            android.util.Log.e("DevGabonGoogleAuth", "Failed to construct Google Sign In client", e)
            errorMessage = if (languageCode == "FR") {
                "Impossible d'exécuter Google Sign-In : ${e.localizedMessage}"
            } else {
                "Could not initialize Google Sign-In client: ${e.localizedMessage}"
            }
            isVerifying = false
        }
    }

    LaunchedEffect(Unit) {
        startGoogleFlow()
    }

    Dialog(onDismissRequest = { if (!isVerifying) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isVerifying) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(colors.primaryContainer.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = colors.primary,
                            strokeWidth = 3.dp
                        )
                    }
                    Spacer(Modifier.height(18.dp))
                    Text(
                        text = if (languageCode == "FR") "Connexion via Google..." else "Connecting via Google...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = colors.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = if (languageCode == "FR") "Veuillez choisir un compte dans le sélecteur officiel" else "Please select an account in the official pop-up",
                        fontSize = 11.sp,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                } else if (isSimulating) {
                    // Simulating mode inside the login dialog (Bypass for headless environments)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color.White, CircleShape)
                                .border(1.dp, Color(0xFFE0E0E0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("G", fontWeight = FontWeight.Black, color = Color(0xFF4285F4), fontSize = 14.sp)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (languageCode == "FR") "Simulation de Compte Réel" else "Real Account Simulation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colors.onSurface
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (languageCode == "FR") {
                            "Indiquez vos identifiants réels pour créer votre propre profil avec 0 abonné de départ."
                        } else {
                            "Specify your real details to create your custom profile starting with 0 followers."
                        },
                        fontSize = 11.sp,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = simName,
                        onValueChange = { simName = it },
                        label = { Text(if (languageCode == "FR") "Votre nom et prénom réels" else "Your Real Full Name") },
                        placeholder = { Text("Ex: Martinien Ludovic") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, null, modifier = Modifier.size(18.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = simEmail,
                        onValueChange = { simEmail = it },
                        label = { Text(if (languageCode == "FR") "Votre Adresse E-mail Google" else "Your Google Email") },
                        placeholder = { Text("Ex: ludo.consulting3@gmail.com") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Email, null, modifier = Modifier.size(18.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (simEmail.isNotBlank() && simName.isNotBlank() && simEmail.contains("@")) {
                                val userPseudo = if (simPseudo.isNotBlank()) simPseudo.trim() else simEmail.substringBefore("@")
                                val customPic = "https://api.dicebear.com/7.x/pixel-art/png?seed=${userPseudo}"
                                coroutineScope.launch {
                                    viewModel.loginWithGoogle(
                                        email = simEmail.trim(),
                                        fullName = simName.trim(),
                                        pseudo = userPseudo,
                                        profilePic = customPic
                                    )
                                    onDismiss()
                                }
                            }
                        },
                        enabled = simEmail.contains("@") && simName.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (languageCode == "FR") "Valider et créer mon profil" else "Submit and Create Profile",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    TextButton(onClick = { isSimulating = false }) {
                        Text(
                            text = if (languageCode == "FR") "Retour aux diagnostics" else "Back to errors",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.primary
                        )
                    }
                } else {
                    // Google Auth Error diagnostics display
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = colors.error,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = if (languageCode == "FR") "Authentification Non Terminée" else "Authentication Unfinished",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = colors.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.errorContainer.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "Erreur de connexion Google",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = colors.onErrorContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (languageCode == "FR") "Fermer" else "Close", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { startGoogleFlow() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (languageCode == "FR") "Réessayer" else "Retry", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Elegant, clean, professional fallback bypass (simulation bypass) for testing
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = colors.outline.copy(alpha = 0.1f))
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = if (languageCode == "FR") {
                            "Vous testez sur un simulateur ou sans services Google configurés ?"
                        } else {
                            "Are you testing on an emulator or without Google services ?"
                        },
                        fontSize = 10.sp,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    TextButton(
                        onClick = { isSimulating = true }
                    ) {
                        Text(
                            text = if (languageCode == "FR") {
                                "⚙️ Simuler avec mes identifiants de test"
                            } else {
                                "⚙️ Simulate with my test credentials"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = colors.primary
                        )
                    }
                }
            }
        }
    }
}
