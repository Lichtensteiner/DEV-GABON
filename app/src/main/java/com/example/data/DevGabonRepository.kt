package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log

class DevGabonRepository(
    private val context: android.content.Context? = null,
    private val dao: DevGabonDao
) {

    init {
        try {
            startRealtimeListeners()
        } catch (e: Throwable) {
            Log.e("FirebaseSync", "Failed to start real-time listener sync loop", e)
        }
    }

    private fun writeToFirestore(collection: String, documentId: String, data: Any) {
        try {
            FirebaseFirestore.getInstance().collection(collection).document(documentId).set(data)
                .addOnFailureListener {
                    Log.w("FirebaseSync", "Failed to write to firestore: collection=$collection documentId=$documentId", it)
                }
        } catch (e: Throwable) {
            Log.w("FirebaseSync", "Firestore not available for write", e)
        }
    }

    private fun startRealtimeListeners() {
        val firestore = try {
            val ctx = context
            if (ctx != null) {
                if (com.google.firebase.FirebaseApp.getApps(ctx).isEmpty()) {
                    com.google.firebase.FirebaseApp.initializeApp(ctx)
                }
            }
            FirebaseFirestore.getInstance()
        } catch (e: Throwable) {
            Log.w("FirebaseSync", "Firebase Firestore is not initialized or configured", e)
            return
        }

        val scope = CoroutineScope(Dispatchers.IO)

        // 1. Sync Posts in Realtime
        firestore.collection("posts").addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("FirebaseSync", "Posts listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshots != null) {
                for (doc in snapshots.documentChanges) {
                    try {
                        val d = doc.document.data
                        val id = (d["id"] as? Long)?.toInt() ?: 0
                        if (id == 0) continue
                        val post = PostEntity(
                            id = id,
                            authorName = d["authorName"] as? String ?: "",
                            authorPseudo = d["authorPseudo"] as? String ?: "",
                            authorProfilePic = d["authorProfilePic"] as? String ?: "😎",
                            authorIsVerified = d["authorIsVerified"] as? Boolean ?: false,
                            authorIsPro = d["authorIsPro"] as? Boolean ?: false,
                            content = d["content"] as? String ?: "",
                            timestamp = d["timestamp"] as? Long ?: System.currentTimeMillis(),
                            likesCount = (d["likesCount"] as? Long)?.toInt() ?: 0,
                            userLiked = d["userLiked"] as? Boolean ?: false,
                            commentsCount = (d["commentsCount"] as? Long)?.toInt() ?: 0,
                            sharesCount = (d["sharesCount"] as? Long)?.toInt() ?: 0,
                            category = d["category"] as? String ?: "Général",
                            isBookmarked = d["isBookmarked"] as? Boolean ?: false,
                            groupName = d["groupName"] as? String
                        )
                        scope.launch { dao.insertPost(post) }
                    } catch (ex: Throwable) {
                        Log.e("FirebaseSync", "Error parsing post document", ex)
                    }
                }
            }
        }

        // 2. Sync Profiles in Realtime
        firestore.collection("profiles").addSnapshotListener { snapshots, e ->
            if (e != null) return@addSnapshotListener
            if (snapshots != null) {
                for (doc in snapshots.documentChanges) {
                    try {
                        val d = doc.document.data
                        val email = d["email"] as? String ?: continue
                        val profile = UserProfileEntity(
                            id = (d["id"] as? Long)?.toInt() ?: 0,
                            email = email,
                            fullName = d["fullName"] as? String ?: "",
                            pseudo = d["pseudo"] as? String ?: "",
                            bio = d["bio"] as? String ?: "",
                            city = d["city"] as? String ?: "",
                            country = d["country"] as? String ?: "",
                            skills = d["skills"] as? String ?: "",
                            experienceLevel = d["experienceLevel"] as? String ?: "Junior",
                            githubUrl = d["githubUrl"] as? String ?: "",
                            linkedinUrl = d["linkedinUrl"] as? String ?: "",
                            portfolioUrl = d["portfolioUrl"] as? String ?: "",
                            profilePicture = d["profilePicture"] as? String ?: "😎",
                            isPro = d["isPro"] as? Boolean ?: false,
                            isRecruiter = d["isRecruiter"] as? Boolean ?: false,
                            postCount = (d["postCount"] as? Long)?.toInt() ?: 0,
                            articleCount = (d["articleCount"] as? Long)?.toInt() ?: 0,
                            subscriberCount = (d["subscriberCount"] as? Long)?.toInt() ?: 0,
                            isVerified = d["isVerified"] as? Boolean ?: false
                        )
                        scope.launch { dao.insertProfile(profile) }
                    } catch (ex: Throwable) {
                        Log.e("FirebaseSync", "Error parsing profile document", ex)
                    }
                }
            }
        }

        // 3. Sync Comments in Realtime
        firestore.collection("comments").addSnapshotListener { snapshots, e ->
            if (e != null) return@addSnapshotListener
            if (snapshots != null) {
                for (doc in snapshots.documentChanges) {
                    try {
                        val d = doc.document.data
                        val id = (d["id"] as? Long)?.toInt() ?: 0
                        if (id == 0) continue
                        val comment = CommentEntity(
                            id = id,
                            postId = (d["postId"] as? Long)?.toInt() ?: 0,
                            authorName = d["authorName"] as? String ?: "",
                            authorPseudo = d["authorPseudo"] as? String ?: "",
                            authorProfilePic = d["authorProfilePic"] as? String ?: "😎",
                            content = d["content"] as? String ?: "",
                            timestamp = d["timestamp"] as? Long ?: System.currentTimeMillis()
                        )
                        scope.launch { dao.insertComment(comment) }
                    } catch (ex: Throwable) {
                        Log.e("FirebaseSync", "Error parsing comment document", ex)
                    }
                }
            }
        }

        // 4. Sync Articles in Realtime
        firestore.collection("articles").addSnapshotListener { snapshots, e ->
            if (e != null) return@addSnapshotListener
            if (snapshots != null) {
                for (doc in snapshots.documentChanges) {
                    try {
                        val d = doc.document.data
                        val id = (d["id"] as? Long)?.toInt() ?: 0
                        if (id == 0) continue
                        val article = ArticleEntity(
                            id = id,
                            title = d["title"] as? String ?: "",
                            authorName = d["authorName"] as? String ?: "",
                            authorPseudo = d["authorPseudo"] as? String ?: "",
                            authorProfilePic = d["authorProfilePic"] as? String ?: "😎",
                            authorIsVerified = d["authorIsVerified"] as? Boolean ?: false,
                            content = d["content"] as? String ?: "",
                            category = d["category"] as? String ?: "Général",
                            timestamp = d["timestamp"] as? Long ?: System.currentTimeMillis(),
                            viewsCount = (d["viewsCount"] as? Long)?.toInt() ?: 0,
                            reactionsCount = (d["reactionsCount"] as? Long)?.toInt() ?: 0,
                            commentsCount = (d["commentsCount"] as? Long)?.toInt() ?: 0,
                            isBookmarked = d["isBookmarked"] as? Boolean ?: false
                        )
                        scope.launch { dao.insertArticle(article) }
                    } catch (ex: Throwable) {
                        Log.e("FirebaseSync", "Error parsing article document", ex)
                    }
                }
            }
        }

        // 5. Sync Jobs in Realtime
        firestore.collection("jobs").addSnapshotListener { snapshots, e ->
            if (e != null) return@addSnapshotListener
            if (snapshots != null) {
                for (doc in snapshots.documentChanges) {
                    try {
                        val d = doc.document.data
                        val id = (d["id"] as? Long)?.toInt() ?: 0
                        if (id == 0) continue
                        val job = JobEntity(
                            id = id,
                            title = d["title"] as? String ?: "",
                            companyName = d["companyName"] as? String ?: "",
                            companyLogo = d["companyLogo"] as? String ?: "🏢",
                            city = d["city"] as? String ?: "Libreville",
                            country = d["country"] as? String ?: "Gabon",
                            contractType = d["contractType"] as? String ?: "CDI",
                            techStack = d["techStack"] as? String ?: "",
                            experienceLevel = d["experienceLevel"] as? String ?: "Junior",
                            description = d["description"] as? String ?: "",
                            postedTimestamp = d["postedTimestamp"] as? Long ?: System.currentTimeMillis(),
                            salaryRange = d["salaryRange"] as? String,
                            isApplied = d["isApplied"] as? Boolean ?: false
                        )
                        scope.launch { dao.insertJob(job) }
                    } catch (ex: Throwable) {
                        Log.e("FirebaseSync", "Error parsing job document", ex)
                    }
                }
            }
        }

        // 6. Sync Messages in Realtime
        firestore.collection("messages").addSnapshotListener { snapshots, e ->
            if (e != null) return@addSnapshotListener
            if (snapshots != null) {
                for (doc in snapshots.documentChanges) {
                    try {
                        val d = doc.document.data
                        val id = (d["id"] as? Long)?.toInt() ?: 0
                        if (id == 0) continue
                        val message = MessageEntity(
                            id = id,
                            senderPseudo = d["senderPseudo"] as? String ?: "",
                            receiverPseudo = d["receiverPseudo"] as? String ?: "",
                            messageText = d["messageText"] as? String ?: "",
                            timestamp = d["timestamp"] as? Long ?: System.currentTimeMillis(),
                            fileName = d["fileName"] as? String,
                            fileSize = d["fileSize"] as? String
                        )
                        scope.launch { dao.insertMessage(message) }
                    } catch (ex: Throwable) {
                        Log.e("FirebaseSync", "Error parsing message document", ex)
                    }
                }
            }
        }
    }

    // Streams
    val profiles: Flow<List<UserProfileEntity>> = dao.getAllProfiles()
    val posts: Flow<List<PostEntity>> = dao.getAllPosts()
    val bookmarkedPosts: Flow<List<PostEntity>> = dao.getBookmarkedPosts()
    val articles: Flow<List<ArticleEntity>> = dao.getAllArticles()
    val groups: Flow<List<GroupEntity>> = dao.getAllGroups()
    val jobs: Flow<List<JobEntity>> = dao.getAllJobs()
    val projects: Flow<List<ProjectEntity>> = dao.getAllProjects()
    val events: Flow<List<EventEntity>> = dao.getAllEvents()
    val messages: Flow<List<MessageEntity>> = dao.getAllMessages()
    val notifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()

    fun getProfile(email: String): Flow<UserProfileEntity?> = dao.getProfileByEmail(email)
    fun getComments(postId: Int): Flow<List<CommentEntity>> = dao.getCommentsForPost(postId)
    fun getMessagesForChat(p1: String, p2: String): Flow<List<MessageEntity>> = dao.getChatMessages(p1, p2)
    fun getPostsForGroup(groupName: String): Flow<List<PostEntity>> = dao.getPostsByGroup(groupName)
    fun getArticlesByCategory(category: String): Flow<List<ArticleEntity>> = dao.getArticlesByCategory(category)

    // Modification methods
    suspend fun createProfile(profile: UserProfileEntity) {
        dao.insertProfile(profile)
        writeToFirestore("profiles", profile.email, profile)
    }

    suspend fun createPost(post: PostEntity) {
        dao.insertPost(post)
        writeToFirestore("posts", post.id.toString(), post)
    }

    suspend fun createComment(comment: CommentEntity) {
        dao.insertComment(comment)
        writeToFirestore("comments", comment.id.toString(), comment)
    }

    suspend fun createArticle(article: ArticleEntity) {
        dao.insertArticle(article)
        writeToFirestore("articles", article.id.toString(), article)
    }

    suspend fun createGroup(group: GroupEntity) {
        dao.insertGroup(group)
        writeToFirestore("groups", group.id.toString(), group)
    }

    suspend fun createJob(job: JobEntity) {
        dao.insertJob(job)
        writeToFirestore("jobs", job.id.toString(), job)
    }

    suspend fun createProject(project: ProjectEntity) {
        dao.insertProject(project)
        writeToFirestore("projects", project.id.toString(), project)
    }

    suspend fun createEvent(event: EventEntity) {
        dao.insertEvent(event)
        writeToFirestore("events", event.id.toString(), event)
    }

    suspend fun sendMessage(msg: MessageEntity) {
        dao.insertMessage(msg)
        writeToFirestore("messages", msg.id.toString(), msg)
    }

    suspend fun createNotification(notif: NotificationEntity) {
        dao.insertNotification(notif)
        writeToFirestore("notifications", notif.id.toString(), notif)
    }

    suspend fun toggleLike(postId: Int, currentLiked: Boolean, currentLikes: Int) {
        val newLiked = !currentLiked
        val newLikes = if (newLiked) currentLikes + 1 else currentLikes - 1
        dao.updatePostLikeState(postId, newLikes, newLiked)
        try {
            FirebaseFirestore.getInstance().collection("posts").document(postId.toString())
                .update("likesCount", newLikes, "userLiked", newLiked)
        } catch (e: Throwable) {}
    }

    suspend fun toggleBookmark(postId: Int, currentBookmarked: Boolean) {
        dao.updatePostBookmarkState(postId, !currentBookmarked)
        try {
            FirebaseFirestore.getInstance().collection("posts").document(postId.toString())
                .update("isBookmarked", !currentBookmarked)
        } catch (e: Throwable) {}
    }

    suspend fun toggleGroupJoin(groupId: Int, currentJoined: Boolean) {
        val newJoined = !currentJoined
        val change = if (newJoined) 1 else -1
        dao.updateGroupJoinState(groupId, newJoined, change)
        try {
            FirebaseFirestore.getInstance().collection("groups").document(groupId.toString())
                .update("isJoined", newJoined)
        } catch (e: Throwable) {}
    }

    suspend fun toggleProjectJoin(projectId: Int, currentJoined: Boolean) {
        val newJoined = !currentJoined
        val change = if (newJoined) 1 else -1
        dao.updateProjectJoinState(projectId, newJoined, change)
        try {
            FirebaseFirestore.getInstance().collection("projects").document(projectId.toString())
                .update("isJoined", newJoined)
        } catch (e: Throwable) {}
    }

    suspend fun toggleEventRegistration(eventId: Int, currentRegistered: Boolean) {
        val newRegistered = !currentRegistered
        val change = if (newRegistered) 1 else -1
        dao.updateEventRegisterState(eventId, newRegistered, change)
        try {
            FirebaseFirestore.getInstance().collection("events").document(eventId.toString())
                .update("isRegistered", newRegistered)
        } catch (e: Throwable) {}
    }

    suspend fun toggleJobApplied(jobId: Int, currentApplied: Boolean) {
        dao.updateJobAppliedState(jobId, !currentApplied)
        try {
            FirebaseFirestore.getInstance().collection("jobs").document(jobId.toString())
                .update("isApplied", !currentApplied)
        } catch (e: Throwable) {}
    }

    suspend fun reactToArticle(articleId: Int, currentReactions: Int) {
        dao.updateArticleReactions(articleId, currentReactions + 1)
        try {
            FirebaseFirestore.getInstance().collection("articles").document(articleId.toString())
                .update("reactionsCount", currentReactions + 1)
        } catch (e: Throwable) {}
    }

    suspend fun viewArticle(articleId: Int) {
        dao.incrementArticleViews(articleId)
        try {
            FirebaseFirestore.getInstance().collection("articles").document(articleId.toString())
                .update("viewsCount", com.google.firebase.firestore.FieldValue.increment(1))
        } catch (e: Throwable) {}
    }

    suspend fun markNotificationAsRead(notifId: Int) {
        dao.markNotificationAsRead(notifId)
        try {
            FirebaseFirestore.getInstance().collection("notifications").document(notifId.toString())
                .update("isRead", true)
        } catch (e: Throwable) {}
    }

    // Checking and seeding initial data if DB is empty
    suspend fun checkAndSeedDatabase() {
        val allProfiles = profiles.first()
        if (allProfiles.isEmpty()) {
            seedDatabase()
        }
    }

    private suspend fun seedDatabase() {
        // 1. Seed profiles representing tech leaders in Gabon
        val seedProfiles = listOf(
            UserProfileEntity(
                email = "martinien.mve@devgabon.ga",
                fullName = "Martinien Ludovic Mve Zogo",
                pseudo = "MartiDev",
                bio = "Consultant IT, Architecte Logiciel Senior et promoteur de Ludo_Consulting. Passionné par les technologies mobiles, Cloud et l'enseignement du code au Gabon.",
                city = "Libreville",
                country = "Gabon",
                skills = "Kotlin, Jetpack Compose, NestJS, Flutter, PostgreSQL, Docker",
                experienceLevel = "Expert",
                githubUrl = "https://github.com/MartiDev241",
                linkedinUrl = "https://linkedin.com/in/martinien-mve-zogo",
                portfolioUrl = "https://ludoconsulting.tech",
                profilePicture = "😎",
                isPro = true,
                isRecruiter = true,
                postCount = 4,
                articleCount = 2,
                subscriberCount = 280,
                isVerified = true
            ),
            UserProfileEntity(
                email = "audrey.gabon@gmail.com",
                fullName = "Audrey Beka",
                pseudo = "AudreyUX",
                bio = "Designer UI/UX passionnée par la création d'interfaces fluides et engageantes pour applications web et mobiles au Gabon. Cofondatrice de Gabon Tech Women.",
                city = "Libreville",
                country = "Gabon",
                skills = "Figma, Adobe XD, Material 3, User Research, Design Systems",
                experienceLevel = "Senior",
                githubUrl = "https://github.com/AudreyUX",
                linkedinUrl = "https://linkedin.com/in/audrey-beka",
                portfolioUrl = "https://audreydesign.ga",
                profilePicture = "👩‍💻",
                isPro = true,
                isRecruiter = false,
                postCount = 2,
                articleCount = 1,
                subscriberCount = 145,
                isVerified = true
            ),
            UserProfileEntity(
                email = "franck.akanda@gmail.com",
                fullName = "Franck Nzengue",
                pseudo = "FranckSys",
                bio = "Administrateur Systèmes, Spécialiste DevOps et SecOps. J'aide les banques locales à sécuriser leurs infrastructures numériques.",
                city = "Akanda",
                country = "Gabon",
                skills = "Linux, AWS, Docker, Kubernetes, Ansible, Cybersecurity",
                experienceLevel = "Intermédiaire",
                githubUrl = "https://github.com/FranckDevOps",
                linkedinUrl = "https://linkedin.com/in/franck-nzengue",
                portfolioUrl = "https://francksys.net",
                profilePicture = "👨‍💻",
                isPro = false,
                isRecruiter = false,
                postCount = 1,
                articleCount = 1,
                subscriberCount = 75,
                isVerified = false
            ),
            UserProfileEntity(
                email = "recrutement@gabontelecom.ga",
                fullName = "Moov Africa Gabon HR",
                pseudo = "MoovRecrut",
                bio = "Compte officiel du département Recrutement et Capital Humain de Moov Africa Gabon Telecom.",
                city = "Libreville",
                country = "Gabon",
                skills = "Recrutement IT, Sourcing, Talent Management",
                experienceLevel = "Expert",
                githubUrl = "",
                linkedinUrl = "https://linkedin.com/company/moovgo",
                portfolioUrl = "https://moovafricagabontelecom.ga",
                profilePicture = "🏢",
                isPro = true,
                isRecruiter = true,
                postCount = 0,
                articleCount = 0,
                subscriberCount = 1920,
                isVerified = true
            )
        )

        for (profile in seedProfiles) {
            dao.insertProfile(profile)
        }

        // 2. Seed Groups
        val seedGroups = listOf(
            GroupEntity(
                name = "Développeurs Web Gabon",
                description = "Le plus grand groupe de partage sur le dev web (React, NextJS, Angular, Node, Django) au Gabon !",
                category = "Développement Web",
                membersCount = 142,
                isJoined = true,
                adminPseudo = "MartiDev"
            ),
            GroupEntity(
                name = "Flutter Gabon",
                description = "La communauté nationale des passionnés de Flutter, Dart et du développement cross-platform mobile.",
                category = "Développement Mobile",
                membersCount = 89,
                isJoined = false,
                adminPseudo = "AudreyUX"
            ),
            GroupEntity(
                name = "Cybersécurité Gabon",
                description = "Groupe d'échange sur les thématiques de sécurité, CTF, audits de code et résilience numérique au Gabon.",
                category = "Cybersécurité",
                membersCount = 57,
                isJoined = false,
                adminPseudo = "FranckSys"
            ),
            GroupEntity(
                name = "Intelligence Artificielle & Data 241",
                description = "Intelligence artificielle, Machine Learning, Data Science et Big Data appliqués au développement africain.",
                category = "Intelligence Artificielle",
                membersCount = 43,
                isJoined = false,
                adminPseudo = "MartiDev"
            )
        )

        for (group in seedGroups) {
            dao.insertGroup(group)
        }

        // 3. Seed Posts
        val seedPosts = listOf(
            PostEntity(
                authorName = "Martinien Ludovic Mve Zogo",
                authorPseudo = "MartiDev",
                authorProfilePic = "😎",
                authorIsVerified = true,
                authorIsPro = true,
                content = "Bonjour la commu ! Je suis très heureux de lancer la version Beta de DEV GABON sur Android aujourd'hui ! 🇬🇦🚀 Notre objectif est de regrouper tous nos talents ici. N'hésitez pas à partager vos projets scolaires, de startups ou professionnels.",
                likesCount = 54,
                userLiked = true,
                commentsCount = 2,
                sharesCount = 12,
                category = "Général"
            ),
            PostEntity(
                authorName = "Audrey Beka",
                authorPseudo = "AudreyUX",
                authorProfilePic = "👩‍💻",
                authorIsVerified = true,
                authorIsPro = true,
                content = "Est-ce qu'il y a des développeurs qui s'intéressent aux guides d'accessibilité de Material Design 3 ? J'ai préparé une maquette Figma de référence pour les contrastes des thèmes foncés en zone tropicale (avec forte luminosité ambiante). Dites-moi si vous voulez le lien ! 🎨👇",
                likesCount = 28,
                userLiked = false,
                commentsCount = 1,
                sharesCount = 3,
                category = "Développement Web"
            ),
            PostEntity(
                authorName = "Franck Nzengue",
                authorPseudo = "FranckSys",
                authorProfilePic = "👨‍💻",
                authorIsVerified = false,
                authorIsPro = false,
                content = "Alerte de sécurité : Une vulnérabilité critique affecte les versions de bibliothèque de cryptographie SSH courantes. Pensez à mettre à jour vos distributions Debian et Ubuntu sur vos VPS hébergés dans les datacenters de Libreville ce week-end !",
                likesCount = 19,
                userLiked = false,
                commentsCount = 0,
                sharesCount = 5,
                category = "Cybersécurité"
            )
        )

        for (post in seedPosts) {
            dao.insertPost(post)
        }

        // 4. Seed Comments
        val seedComments = listOf(
            CommentEntity(
                postId = 1,
                authorName = "Audrey Beka",
                authorPseudo = "AudreyUX",
                authorProfilePic = "👩‍💻",
                content = "Excellent travail Martinien ! L'interface mobile est super fluide, j'adore le thème aux couleurs de notre nation forestière !"
            ),
            CommentEntity(
                postId = 1,
                authorName = "Franck Nzengue",
                authorPseudo = "FranckSys",
                authorProfilePic = "👨‍💻",
                content = "Enfin une plateforme dédiée ! Je vais pouvoir y publier mes astuces DevOps. Merci pour cette belle initiative."
            ),
            CommentEntity(
                postId = 2,
                authorName = "Martinien Ludovic Mve Zogo",
                authorPseudo = "MartiDev",
                authorProfilePic = "😎",
                content = "Oui je suis super intéressé Audrey ! Surtout pour adapter les écrans des smartphones des agents terrain en extérieur sous le soleil de Libreville ☀️."
            )
        )

        for (comment in seedComments) {
            dao.insertComment(comment)
        }

        // 5. Seed Articles
        val seedArticles = listOf(
            ArticleEntity(
                title = "Sécuriser les APIs REST avec NestJS et PostgreSQL au Gabon",
                authorName = "Martinien Ludovic Mve Zogo",
                authorPseudo = "MartiDev",
                authorProfilePic = "😎",
                authorIsVerified = true,
                content = """Dans ce tutoriel complet, nous allons explorer l'implémentation de mécanismes robustes de sécurisation sur un backend NestJS. 
                    
### Ce que nous allons couvrir :
1. **Authentification JWT avec rotation des Refresh Tokens**
2. **Protection contre les attaques par force brute (Rate Limiting)**
3. **Sécurisation des headers HTTP avec Helmet**
4. **Validation stricte des payloads SQL pour éviter les injections**

### 1. Installation des packages clés
Commençons par installer les modules essentiels de sécurité dans notre projet NestJS :
```bash
npm install --save @nestjs/throttler helmet bcrypt @types/bcrypt
```

### 2. Configuration du Throttler Guard (Rate Limit)
Dans votre fichier `app.module.ts`, importez et configurez le ThrottlerModule pour limiter le nombre de requêtes par adresse IP :
```typescript
import { ThrottlerModule } from '@nestjs/throttler';

@Module({
  imports: [
    ThrottlerModule.forRoot([{
      ttl: 60000, // 1 minute
      limit: 10,  // Max 10 requêtes de reconnexion
    }]),
  ],
})
export class AppModule {}
```

### Conclusion
La mise en place de ces gardias simples permet de réduire de 95% les tentatives d'attaques automatisées sur vos backends hébergés sur vos serveurs gabonais.""",
                category = "Développement Web",
                viewsCount = 142,
                reactionsCount = 45,
                commentsCount = 3
            ),
            ArticleEntity(
                title = "Introduction au design adaptatif pour applications Android en Jetpack Compose",
                authorName = "Audrey Beka",
                authorPseudo = "AudreyUX",
                authorProfilePic = "👩‍💻",
                authorIsVerified = true,
                content = """L'avènement des téléphones pliables et des tablettes impose d'adapter nos architectures graphiques sur Android. Pour satisfaire l'accessibilité locale, Jetpack Compose intègre un système robuste de classes de fenêtres (Window Size Classes).

### Les points cardinaux d'un bon design :
1. **Toujours utiliser le pixel dynamique dps** pour s'ajuster à toutes les tailles.
2. **Garantir des cibles tactiles de 48dp au minimum** (particulièrement précieux pour une utilisation lors des trajets en taxi urbain).
3. **S'adapter aux modes portrait et paysage** sans perdre les états actuels de recherche.

Chaque application moderne devrait être pensée de manière fluide dès le départ !""",
                category = "Développement Mobile",
                viewsCount = 98,
                reactionsCount = 31,
                commentsCount = 1
            )
        )

        for (article in seedArticles) {
            dao.insertArticle(article)
        }

        // 6. Seed Jobs
        val seedJobs = listOf(
            JobEntity(
                title = "Développeur Full-Stack NestJS / NextJS",
                companyName = "Ludo_Consulting",
                companyLogo = "💻",
                city = "Libreville",
                contractType = "CDI",
                techStack = "TypeScript, NestJS, Next.js, PostgreSQL",
                experienceLevel = "Senior",
                description = "Nous recherchons un Développeur Senior pour piloter les chantiers de transformation digitale de nos clients à Libreville. Vous dirigerez une équipe de 3 juniors et serez le garant de l'architecture backend.\nMinimum 5 ans d'expérience.",
                salaryRange = "800 000 - 1 200 000 CFA",
                isApplied = false
            ),
            JobEntity(
                title = "Junior Android Developer (Jetpack Compose)",
                companyName = "Gabon Tech Academy",
                companyLogo = "📱",
                city = "Libreville",
                contractType = "Stage",
                techStack = "Kotlin, Jetpack Compose, Git",
                experienceLevel = "Junior",
                description = "En collaboration étroite avec nos formateurs, vous participerez au développement de notre application interne de suivi scolaire. Une parfaite occasion de monter en compétences sur Compose !",
                salaryRange = "150 000 - 250 000 CFA",
                isApplied = false
            ),
            JobEntity(
                title = "Consultant DevOps & Cloud",
                companyName = "Moov Africa Gabon Telecom",
                companyLogo = "🏢",
                city = "Libreville",
                contractType = "Freelance",
                techStack = "Docker, Kubernetes, AWS, GitLab CI",
                experienceLevel = "Senior",
                description = "Mission de 6 mois renouvelable pour restructurer le pipeline de déploiement continu d'un grand projet de facturation en ligne. Idéal pour des ingénieurs habitués à travailler sous contraintes de bande passante.",
                salaryRange = "2 500 000 CFA / mois",
                isApplied = false
            )
        )

        for (job in seedJobs) {
            dao.insertJob(job)
        }

        // 7. Seed Projects
        val seedProjects = listOf(
            ProjectEntity(
                title = "OpenStreetMap Gabon Map",
                creatorName = "Martinien Ludovic Mve Zogo",
                creatorPseudo = "MartiDev",
                description = "Projet open-source visant à cartographier avec précision de nouveaux quartiers de Libreville (Akanda, Owendo) et l'intérieur du pays pour améliorer les livraisons locales de e-commerce.",
                techStack = "Leaflet, GeoJSON, Kotlin, OpenStreetMap API",
                profilesSearched = "Développeurs Frontend, Cartographes amateurs",
                duration = "6 mois",
                status = "Recrutement",
                membersCount = 5,
                isJoined = true
            ),
            ProjectEntity(
                title = "SOS Gab - Plateforme de Don de Sang",
                creatorName = "Audrey Beka",
                creatorPseudo = "AudreyUX",
                description = "Une application communautaire pour faciliter le don d'urgence de sang à Libreville. Les hôpitaux émettent des alertes et les donneurs volontaires reçoivent des notifications ciblées par quartier.",
                techStack = "Flutter, Firebase Cloud Messaging, Node.js",
                profilesSearched = "2 Développeurs Flutter, Consultant RGPD / Médical",
                duration = "3 mois",
                status = "Recrutement",
                membersCount = 2,
                isJoined = false
            )
        )

        for (project in seedProjects) {
            dao.insertProject(project)
        }

        // 8. Seed Events
        val seedEvents = listOf(
            EventEntity(
                title = "Meetup des Développeurs Gabon 2026",
                organizer = "Ludo_Consulting & Partners",
                description = "La plus grande rencontre physique des pros de l'informatique gabonais. Tables rondes sur l'entrepreneuriat numérique, démos de projets locaux et séance de networking géant.",
                date = "Samedi 20 Juin 2026 à 14h00",
                venue = "Hôtel Nomad, Sablière, Libreville",
                eventType = "Meetup",
                attendeesCount = 120,
                isRegistered = true
            ),
            EventEntity(
                title = "Hackathon Gabon Tech Women 2026",
                organizer = "Gabon Tech Women",
                description = "48 heures pour concevoir un prototype d'application favorisant l'éducation des jeunes filles dans l'Ogooué-Maritime et l'Estuaire. Nombreux prix et mentorat de 6 mois à la clé.",
                date = "Du 10 au 12 Juillet 2026",
                venue = "Sing (Société d'Incubation Numérique), Centre-ville",
                eventType = "Hackathon",
                attendeesCount = 64,
                isRegistered = false
            )
        )

        for (event in seedEvents) {
            dao.insertEvent(event)
        }

        // 9. Seed Messaging
        val seedMessages = listOf(
            MessageEntity(
                senderPseudo = "AudreyUX",
                receiverPseudo = "MartiDev",
                messageText = "Salut Martinien ! Est-ce que tu as jeté un œil à ma proposition d'interface pour le module Articles ?",
                timestamp = System.currentTimeMillis() - 86400000 // 1 day ago
            ),
            MessageEntity(
                senderPseudo = "MartiDev",
                receiverPseudo = "AudreyUX",
                messageText = "Hello Audrey ! Oui, c'est vraiment propre. J'adore les cartes de contribution et les badges d'auteur. On va intégrer ça direct sur la base de données Room locale !",
                timestamp = System.currentTimeMillis() - 86000000
            ),
            MessageEntity(
                senderPseudo = "AudreyUX",
                receiverPseudo = "MartiDev",
                messageText = "Super ! Je t'envoie aussi un petit mockup au format PDF pour l'espace Recrutement d'ici demain.",
                timestamp = System.currentTimeMillis() - 72000000,
                fileName = "recrutement_spec_gabon.pdf",
                fileSize = "2.4 MB"
            )
        )

        for (msg in seedMessages) {
            dao.insertMessage(msg)
        }

        // 10. Seed Notifications
        val seedNotifs = listOf(
            NotificationEntity(
                type = "SYSTEM",
                senderName = "Ludo_Consulting",
                message = "Bienvenue sur DEV GABON ! Votre compte a été configuré avec succès et vous bénéficiez du badge de Contributeur.",
                isRead = false
            ),
            NotificationEntity(
                type = "LIKE",
                senderName = "Audrey Beka",
                message = "Audrey Beka a aimé votre publication : 'Lancement de DEV GABON Beta !'",
                isRead = false
            ),
            NotificationEntity(
                type = "MESSAGE",
                senderName = "Audrey Beka",
                message = "Nouveau message privé de AudreyUX : 'Salut Martinien ! Est-ce que tu as jeté...'",
                isRead = true
            )
        )

        for (notif in seedNotifs) {
            dao.insertNotification(notif)
        }
    }
}
