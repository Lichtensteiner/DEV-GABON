package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val fullName: String,
    val pseudo: String,
    val bio: String,
    val city: String,
    val country: String,
    val skills: String, // Comma-separated: "Kotlin, Compose, Swift, NestJS"
    val experienceLevel: String, // "Junior", "Intermédiaire", "Senior", "Expert"
    val githubUrl: String,
    val linkedinUrl: String,
    val portfolioUrl: String,
    val profilePicture: String, // String ID for drawable or emoji indicator
    val isPro: Boolean = false,
    val isRecruiter: Boolean = false,
    val postCount: Int = 0,
    val articleCount: Int = 0,
    val subscriberCount: Int = 0,
    val isVerified: Boolean = false
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorName: String,
    val authorPseudo: String,
    val authorProfilePic: String,
    val authorIsVerified: Boolean = false,
    val authorIsPro: Boolean = false,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val userLiked: Boolean = false,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val category: String = "Général", // e.g. "Dev Web", "Dev Mobile", "DevOps"
    val isBookmarked: Boolean = false,
    val groupName: String? = null // Nullable if posted in public feed
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postId: Int,
    val authorName: String,
    val authorPseudo: String,
    val authorProfilePic: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val authorName: String,
    val authorPseudo: String,
    val authorProfilePic: String,
    val authorIsVerified: Boolean = false,
    val content: String, // Markdown-ish rich content
    val category: String, // "Développement Web", "Développement Mobile", "Intelligence Artificielle", "Cybersécurité", etc.
    val timestamp: Long = System.currentTimeMillis(),
    val viewsCount: Int = 0,
    val reactionsCount: Int = 0, // Likes/claps
    val commentsCount: Int = 0,
    val isBookmarked: Boolean = false
)

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val membersCount: Int = 1,
    val isJoined: Boolean = false,
    val category: String, // e.g. "Mobile", "Web", "Cloud"
    val adminPseudo: String
)

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val companyName: String,
    val companyLogo: String, // Emoji or short asset name
    val city: String = "Libreville",
    val country: String = "Gabon",
    val contractType: String, // "CDI", "CDD", "Stage", "Freelance", "Alternance"
    val techStack: String, // e.g. "Kotlin, Compose"
    val experienceLevel: String, // "Junior", "Intermédiare", "Senior"
    val description: String,
    val postedTimestamp: Long = System.currentTimeMillis(),
    val salaryRange: String? = null, // e.g. "600 000 - 900 000 FCFA"
    val isApplied: Boolean = false
)

@Entity(tableName = "collaborative_projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val creatorName: String,
    val creatorPseudo: String,
    val description: String,
    val techStack: String, // "Kotlin, PostgreSql"
    val profilesSearched: String, // "Front-end Mobile, Designer UI"
    val duration: String, // "3 mois"
    val status: String = "Recrutement", // "Recrutement", "En cours", "Terminé"
    val membersCount: Int = 1,
    val isJoined: Boolean = false
)

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val organizer: String,
    val description: String,
    val date: String, // e.g. "15 Juin 2026 à 18:30"
    val venue: String, // "La Maison de la Tech, Libreville"
    val eventType: String, // "Meetup", "Conférence", "Hackathon", "Atelier", "Formation"
    val attendeesCount: Int = 0,
    val isRegistered: Boolean = false
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderPseudo: String,
    val receiverPseudo: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val fileName: String? = null,
    val fileSize: String? = null
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "LIKE", "COMMENT", "MESSAGE", "JOB", "PROJECT", "SYSTEM"
    val senderName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
