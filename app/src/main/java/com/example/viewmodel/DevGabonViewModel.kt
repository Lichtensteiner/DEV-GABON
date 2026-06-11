package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.db.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

sealed interface Screen {
    object Feed : Screen
    object Blog : Screen
    object Campus : Screen
    object Careers : Screen
    object Communities : Screen
    object Messages : Screen
    object Profile : Screen
    object AdminDashboard : Screen
    object Landing : Screen
}

class DevGabonViewModel(private val repository: DevGabonRepository) : ViewModel() {

    // --- Application Startup Loading Context ---
    private val _isStartupLoading = MutableStateFlow(true)
    val isStartupLoading: StateFlow<Boolean> = _isStartupLoading.asStateFlow()

    private val _startupProgress = MutableStateFlow(0f)
    val startupProgress: StateFlow<Float> = _startupProgress.asStateFlow()

    private val _startupStatusText = MutableStateFlow("Initialisation...")
    val startupStatusText: StateFlow<String> = _startupStatusText.asStateFlow()

    init {
        // Run a simulated loading of database entities and profiles, incrementing progress beautifully
        viewModelScope.launch {
            val statusTexts = listOf(
                "Démarrage de DEV GABON...",
                "Connexion à la base de données locale...",
                "Chargement des profils de la communauté...",
                "Synchronisation des publications et actualités...",
                "Préparation de votre espace professionnel...",
                "Prêt à coder ! 🚀"
            )
            val steps = 25
            for (i in 1..steps) {
                kotlinx.coroutines.delay(80) // 25 * 80ms = 2.0s total load
                _startupProgress.value = i.toFloat() / steps
                
                val textIdx = ((i - 1) * statusTexts.size) / steps
                if (textIdx in statusTexts.indices) {
                    _startupStatusText.value = statusTexts[textIdx]
                }
            }
            _isStartupLoading.value = false
        }
    }

    // --- Theme & Language Context ---
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    private val _languageCode = MutableStateFlow("FR")
    val languageCode: StateFlow<String> = _languageCode.asStateFlow()

    fun toggleLanguage() {
        _languageCode.value = if (_languageCode.value == "FR") "EN" else "FR"
    }

    // --- Authentication Context ---
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun loginWithGoogle(email: String, fullName: String, pseudo: String, profilePic: String, role: String = "Développeur") {
        viewModelScope.launch {
            _activeUserEmail.value = email
            val existing = repository.getProfile(email).first()
            if (existing == null) {
                val newProfile = UserProfileEntity(
                    email = email,
                    fullName = fullName,
                    pseudo = pseudo,
                    profilePicture = profilePic,
                    bio = "Nouveau talent gabonais sur DevGabon !",
                    city = "Libreville",
                    country = "Gabon",
                    skills = "Kotlin, Java, Jetpack Compose",
                    experienceLevel = "Senior",
                    githubUrl = "",
                    linkedinUrl = "",
                    portfolioUrl = "",
                    isVerified = (email == "ludo.consulting3@gmail.com"),
                    role = role
                )
                repository.createProfile(newProfile)
            } else {
                // Instantly update the existing local database entry with real-time Google account credentials
                repository.createProfile(existing.copy(
                    fullName = fullName,
                    profilePicture = profilePic,
                    pseudo = if (existing.pseudo.isBlank() || existing.pseudo == "MartiDev" || existing.pseudo == "ludodev") pseudo else existing.pseudo
                ))
            }
            _isLoggedIn.value = true
            _currentScreen.value = Screen.Profile
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _activeUserEmail.value = ""
        _currentScreen.value = Screen.Feed
    }

    fun updateActiveUserRole(role: String) {
        viewModelScope.launch {
            val email = _activeUserEmail.value
            val profile = repository.getProfile(email).first()
            if (profile != null) {
                repository.createProfile(profile.copy(role = role))
            }
        }
    }

    // --- Admin Operations ---
    fun deleteUserByAdmin(profileId: Int, email: String) {
        viewModelScope.launch {
            repository.deleteProfile(profileId, email)
        }
    }

    fun resetAllDatabase() {
        viewModelScope.launch {
            val currentAdmin = activeUserProfile.value
            repository.clearAllDatabase(currentAdmin)
        }
    }

    fun createOrUpdateProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.createProfile(profile)
        }
    }

    // --- Navigation Context ---
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Feed)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenHistory = java.util.Stack<Screen>()

    fun navigateTo(screen: Screen) {
        val current = _currentScreen.value
        if (current != screen) {
            screenHistory.push(current)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenHistory.isNotEmpty()) {
            _currentScreen.value = screenHistory.pop()
            return true
        }
        return false
    }

    fun clearNavigationHistory() {
        screenHistory.clear()
    }

    // --- Active User Context ---
    private val _activeUserEmail = MutableStateFlow("")
    val activeUserEmail: StateFlow<String> = _activeUserEmail.asStateFlow()

    val activeUserProfile: StateFlow<UserProfileEntity?> = _activeUserEmail
        .flatMapLatest { email -> repository.getProfile(email) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- Reactive Data Streams ---
    val allProfiles: StateFlow<List<UserProfileEntity>> = repository.profiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPosts: StateFlow<List<PostEntity>> = repository.posts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedPosts: StateFlow<List<PostEntity>> = repository.bookmarkedPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allArticles: StateFlow<List<ArticleEntity>> = repository.articles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGroups: StateFlow<List<GroupEntity>> = repository.groups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allJobs: StateFlow<List<JobEntity>> = repository.jobs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProjects: StateFlow<List<ProjectEntity>> = repository.projects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEvents: StateFlow<List<EventEntity>> = repository.events
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Active Chat Details ---
    private val _activeChatRecipient = MutableStateFlow<UserProfileEntity?>(null)
    val activeChatRecipient: StateFlow<UserProfileEntity?> = _activeChatRecipient.asStateFlow()

    val activeChatMessages: StateFlow<List<MessageEntity>> = combine(
        _activeUserEmail,
        _activeChatRecipient
    ) { email, recipient ->
        if (recipient == null) {
            emptyList()
        } else {
            // Find current user's profile to get their pseudo
            val activeProfile = activeUserProfile.value
            val userPseudo = activeProfile?.pseudo ?: "Me"
            repository.getMessagesForChat(userPseudo, recipient.pseudo).first()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectChatRecipient(profile: UserProfileEntity?) {
        _activeChatRecipient.value = profile
        if (profile != null) {
            navigateTo(Screen.Messages)
        }
    }

    // --- Gemini Interactive Space ---
    private val _aiMessages = MutableStateFlow<List<MessageEntity>>(
        listOf(
            MessageEntity(
                senderPseudo = "Gemini",
                receiverPseudo = "User",
                messageText = "Salut ! Je suis l'assistant IA de DEV GABON. Comment puis-je t'aider aujourd'hui dans ta carrière ou ton code ? 🇬🇦💻",
                timestamp = System.currentTimeMillis()
            )
        )
    )
    val aiMessages: StateFlow<List<MessageEntity>> = _aiMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // --- Filters & UI States ---
    val articleFilter = MutableStateFlow("Tous")
    val jobContractFilter = MutableStateFlow("Tous")
    val jobExperienceFilter = MutableStateFlow("Tous")
    val searchPostQuery = MutableStateFlow("")
    val searchArticleQuery = MutableStateFlow("")
    val searchJobQuery = MutableStateFlow("")

    val filteredArticles: StateFlow<List<ArticleEntity>> = combine(
        allArticles,
        articleFilter,
        searchArticleQuery
    ) { list, filter, query ->
        list.filter {
            (filter == "Tous" || it.category == filter) &&
            (query.isEmpty() || it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredJobs: StateFlow<List<JobEntity>> = combine(
        allJobs,
        jobContractFilter,
        jobExperienceFilter,
        searchJobQuery
    ) { list, contract, experience, query ->
        list.filter {
            (contract == "Tous" || it.contractType == contract) &&
            (experience == "Tous" || it.experienceLevel == experience) &&
            (query.isEmpty() || it.title.contains(query, ignoreCase = true) || it.companyName.contains(query, ignoreCase = true) || it.techStack.contains(query, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Seed and prepare database in IO
        viewModelScope.launch {
            try {
                repository.checkAndSeedDatabase()
            } catch (e: Exception) {
                Log.e("DevGabonViewModel", "Seeding failed", e)
            }
        }
    }

    // --- Business Actions ---

    // 1. Social Interactions
    fun toggleLike(postId: Int, currentLiked: Boolean, currentLikes: Int) {
        viewModelScope.launch {
            repository.toggleLike(postId, currentLiked, currentLikes)
            
            // Add notification if liked by someone else
            if (!currentLiked) {
                val profile = activeUserProfile.value
                val userPseudo = profile?.fullName ?: "Un membre"
                repository.createNotification(
                    NotificationEntity(
                        type = "LIKE",
                        senderName = userPseudo,
                        message = "$userPseudo a aimé votre publication."
                    )
                )
            }
        }
    }

    fun toggleBookmark(postId: Int, currentBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleBookmark(postId, currentBookmarked)
        }
    }

    fun addPost(content: String, category: String = "Général", groupName: String? = null) {
        if (content.isBlank()) return
        val profile = activeUserProfile.value ?: return
        viewModelScope.launch {
            repository.createPost(
                PostEntity(
                    authorName = profile.fullName,
                    authorPseudo = profile.pseudo,
                    authorProfilePic = profile.profilePicture,
                    authorIsVerified = profile.isVerified,
                    authorIsPro = profile.isPro,
                    content = content,
                    category = category,
                    groupName = groupName
                )
            )
            // Increment local stats count
            repository.createProfile(profile.copy(postCount = profile.postCount + 1))
        }
    }

    // 2. Comments
    fun addComment(postId: Int, content: String) {
        if (content.isBlank()) return
        val profile = activeUserProfile.value ?: return
        viewModelScope.launch {
            repository.createComment(
                CommentEntity(
                    postId = postId,
                    authorName = profile.fullName,
                    authorPseudo = profile.pseudo,
                    authorProfilePic = profile.profilePicture,
                    content = content
                )
            )
            
            // Update comments count on the post
            val postsList = allPosts.value
            val targetedPost = postsList.find { it.id == postId }
            if (targetedPost != null) {
                repository.createPost(targetedPost.copy(commentsCount = targetedPost.commentsCount + 1))
                
                // Add alert notification
                repository.createNotification(
                    NotificationEntity(
                        type = "COMMENT",
                        senderName = profile.fullName,
                        message = "${profile.fullName} a commenté votre publication : '${content.take(15)}...'"
                    )
                )
            }
        }
    }

    fun getComments(postId: Int): kotlinx.coroutines.flow.Flow<List<CommentEntity>> {
        return repository.getComments(postId)
    }

    // 3. Articles
    fun publishArticle(title: String, content: String, category: String) {
        if (title.isBlank() || content.isBlank() || category.isBlank()) return
        val profile = activeUserProfile.value ?: return
        viewModelScope.launch {
            repository.createArticle(
                ArticleEntity(
                    title = title,
                    content = content,
                    category = category,
                    authorName = profile.fullName,
                    authorPseudo = profile.pseudo,
                    authorProfilePic = profile.profilePicture,
                    authorIsVerified = profile.isVerified
                )
            )
            repository.createProfile(profile.copy(articleCount = profile.articleCount + 1))
        }
    }

    fun reactToArticle(articleId: Int, currentReactions: Int) {
        viewModelScope.launch {
            repository.reactToArticle(articleId, currentReactions)
        }
    }

    fun viewArticle(articleId: Int) {
        viewModelScope.launch {
            repository.viewArticle(articleId)
        }
    }

    // 4. Communities / Groups
    fun createGroup(name: String, description: String, category: String) {
        if (name.isBlank() || description.isBlank() || category.isBlank()) return
        val profile = activeUserProfile.value ?: return
        viewModelScope.launch {
            repository.createGroup(
                GroupEntity(
                    name = name,
                    description = description,
                    category = category,
                    isJoined = true,
                    adminPseudo = profile.pseudo
                )
            )
        }
    }

    fun toggleJoinGroup(groupId: Int, currentJoined: Boolean) {
        viewModelScope.launch {
            repository.toggleGroupJoin(groupId, currentJoined)
        }
    }

    // 5. Careers & Jobs
    fun toggleApplyJob(jobId: Int, currentApplied: Boolean) {
        viewModelScope.launch {
            repository.toggleJobApplied(jobId, currentApplied)
            if (!currentApplied) {
                val profile = activeUserProfile.value
                val pName = profile?.fullName ?: "Un candidat"
                repository.createNotification(
                    NotificationEntity(
                        type = "JOB",
                        senderName = "Section Carrières",
                        message = "Votre candidature pour le poste a été envoyée aux recruteurs. Statut : En cours d'examen."
                    )
                )
            }
        }
    }

    fun publishJobOffer(title: String, company: String, logo: String, city: String, contract: String, tech: String, experience: String, description: String, salary: String?) {
        if (title.isBlank() || company.isBlank() || description.isBlank()) return
        viewModelScope.launch {
            repository.createJob(
                JobEntity(
                    title = title,
                    companyName = company,
                    companyLogo = logo.ifBlank { "🏢" },
                    city = city.ifBlank { "Libreville" },
                    contractType = contract,
                    techStack = tech,
                    experienceLevel = experience,
                    description = description,
                    salaryRange = salary
                )
            )
        }
    }

    // 6. Collaborative Projects
    fun publishProjectPitch(title: String, techStack: String, profilesSearched: String, duration: String, description: String) {
        if (title.isBlank() || description.isBlank() || techStack.isBlank()) return
        val profile = activeUserProfile.value ?: return
        viewModelScope.launch {
            repository.createProject(
                ProjectEntity(
                    title = title,
                    creatorName = profile.fullName,
                    creatorPseudo = profile.pseudo,
                    description = description,
                    techStack = techStack,
                    profilesSearched = profilesSearched,
                    duration = duration
                )
            )
        }
    }

    fun toggleJoinProject(projectId: Int, currentJoined: Boolean) {
        viewModelScope.launch {
            repository.toggleProjectJoin(projectId, currentJoined)
        }
    }

    // 7. Events
    fun toggleRegisterEvent(eventId: Int, currentRegistered: Boolean) {
        viewModelScope.launch {
            repository.toggleEventRegistration(eventId, currentRegistered)
        }
    }

    fun publishEvent(title: String, organizer: String, description: String, date: String, venue: String, type: String) {
        if (title.isBlank() || description.isBlank() || date.isBlank()) return
        viewModelScope.launch {
            repository.createEvent(
                EventEntity(
                    title = title,
                    organizer = organizer,
                    description = description,
                    date = date,
                    venue = venue,
                    eventType = type
                )
            )
        }
    }

    // 10. Notifications
    fun markNotificationAsRead(notifId: Int) {
        viewModelScope.launch {
            repository.markNotificationAsRead(notifId)
        }
    }

    // 8. Messages
    fun sendDirectMessage(text: String, fileUri: String? = null, fileSize: String? = null) {
        if (text.isBlank() && fileUri == null) return
        val activeProfile = activeUserProfile.value ?: return
        val recipient = _activeChatRecipient.value ?: return
        viewModelScope.launch {
            val userPseudo = activeProfile.pseudo
            repository.sendMessage(
                MessageEntity(
                    senderPseudo = userPseudo,
                    receiverPseudo = recipient.pseudo,
                    messageText = text,
                    fileName = fileUri,
                    fileSize = fileSize
                )
            )

            // Trigger simulated immediate reply for highly interactive feeling
            simulateReply(recipient)
        }
    }

    private suspend fun simulateReply(recipient: UserProfileEntity) {
        withContext(Dispatchers.IO) {
            kotlinx.coroutines.delay(1200) // Realistic delay
            val profile = activeUserProfile.value ?: return@withContext
            val responseTexts = listOf(
                "Super, merci pour ta réponse ! On en discute au prochain meetup Flutter à Libreville ?",
                "C'est noté. J'ai de l'expérience sur ce sujet avec mes anciens projets Ludo_Consulting.",
                "Excellent ! Envoie-moi tes disponibilités pour un call Google Meet cette semaine.",
                "Parfait, je regarde ça ce soir. Let's code ! Dev Gabon en avant ! 🇬🇦🚀"
            )
            repository.sendMessage(
                MessageEntity(
                    senderPseudo = recipient.pseudo,
                    receiverPseudo = profile.pseudo,
                    messageText = responseTexts.random()
                )
            )
            
            // Add notification alert
            repository.createNotification(
                NotificationEntity(
                    type = "MESSAGE",
                    senderName = recipient.fullName,
                    message = "${recipient.fullName} vous a envoyé un message : '${responseTexts.random().take(20)}...'"
                )
            )
        }
    }

    // 9. Profile Editor / Auth Session switcher
    fun updateProfile(
        fullName: String,
        pseudo: String,
        bio: String,
        city: String,
        skills: String,
        experienceLevel: String,
        github: String,
        linkedin: String,
        portfolio: String,
        isPro: Boolean,
        isRecruiter: Boolean
    ) {
        val current = activeUserProfile.value ?: return
        viewModelScope.launch {
            repository.createProfile(
                current.copy(
                    fullName = fullName.ifBlank { current.fullName },
                    pseudo = pseudo.ifBlank { current.pseudo },
                    bio = bio,
                    city = city.ifBlank { current.city },
                    skills = skills,
                    experienceLevel = experienceLevel,
                    githubUrl = github,
                    linkedinUrl = linkedin,
                    portfolioUrl = portfolio,
                    isPro = isPro,
                    isRecruiter = isRecruiter
                )
            )
        }
    }

    fun switchActiveProfile(email: String) {
        _activeUserEmail.value = email
    }

    // --- Gemini API Call ---
    fun sendAiMessage(promptText: String) {
        if (promptText.isBlank()) return
        
        val userItem = MessageEntity(
            senderPseudo = "Me",
            receiverPseudo = "Gemini",
            messageText = promptText,
            timestamp = System.currentTimeMillis()
        )
        
        _aiMessages.value = _aiMessages.value + userItem
        _isAiLoading.value = true
        
        viewModelScope.launch {
            val responseText = queryGemini(promptText)
            
            _aiMessages.value = _aiMessages.value + MessageEntity(
                senderPseudo = "Gemini",
                receiverPseudo = "User",
                messageText = responseText,
                timestamp = System.currentTimeMillis()
            )
            _isAiLoading.value = false
        }
    }

    private suspend fun queryGemini(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "⚠️ Clé API non configurée. Pour interagir avec l'intelligence artificielle de DEV GABON, veuillez saisir votre clé GEMINI_API_KEY dans le panneau des Secrets de Google AI Studio. En attendant, restez au top ! 🇬🇦🔥"
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "L'utilisateur demande : $prompt. Réponds-lui directement comme un architecte logiciel gabonais d'élite.")
                        })
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", "Tu es un guide technique suprême et consultant en Intelligence Artificielle et DevOps au Gabon. Encourage les jeunes gabonais, intègre des touches d'humour locales chaleureuses de Libreville et Akanda, et réponds en français de façon structurée et très claire.")
                    })
                })
            })
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext "Oups, une erreur réseau est survenue (HTTP ${response.code}). Veuillez réessayer !"
                }
                
                val bodyStr = response.body?.string() ?: ""
                val responseJson = JSONObject(bodyStr)
                val text = responseJson.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
                    
                text
            }
        } catch (e: Exception) {
            "Hmm, impossible de joindre l'IA de DEV GABON (${e.message}). Vérifie ta connexion internet ou réessaie dans un instant."
        }
    }

    class Factory(private val repository: DevGabonRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DevGabonViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DevGabonViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
