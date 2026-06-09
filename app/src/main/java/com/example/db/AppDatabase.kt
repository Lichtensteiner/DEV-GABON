package com.example.db

// Force clean recompilation of database classes
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface DevGabonDao {

    // --- User Profile ---
    @Query("SELECT * FROM user_profiles WHERE email = :email LIMIT 1")
    fun getProfileByEmail(email: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles")
    fun getAllProfiles(): Flow<List<UserProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    // --- Social Posts ---
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE groupName = :groupName ORDER BY timestamp DESC")
    fun getPostsByGroup(groupName: String): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Query("UPDATE posts SET likesCount = :likesCount, userLiked = :userLiked WHERE id = :postId")
    suspend fun updatePostLikeState(postId: Int, likesCount: Int, userLiked: Boolean)

    @Query("UPDATE posts SET isBookmarked = :isBookmarked WHERE id = :postId")
    suspend fun updatePostBookmarkState(postId: Int, isBookmarked: Boolean)

    @Query("SELECT * FROM posts WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedPosts(): Flow<List<PostEntity>>

    // --- Comments ---
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Int): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    // --- Articles / Blog ---
    @Query("SELECT * FROM articles ORDER BY timestamp DESC")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE category = :category ORDER BY timestamp DESC")
    fun getArticlesByCategory(category: String): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    @Query("UPDATE articles SET reactionsCount = :reactionsCount WHERE id = :articleId")
    suspend fun updateArticleReactions(articleId: Int, reactionsCount: Int)

    @Query("UPDATE articles SET viewsCount = viewsCount + 1 WHERE id = :articleId")
    suspend fun incrementArticleViews(articleId: Int)

    // --- Groups / Communities ---
    @Query("SELECT * FROM groups ORDER BY name ASC")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity)

    @Query("UPDATE groups SET isJoined = :isJoined, membersCount = membersCount + :memberChange WHERE id = :groupId")
    suspend fun updateGroupJoinState(groupId: Int, isJoined: Boolean, memberChange: Int)

    // --- Jobs ---
    @Query("SELECT * FROM jobs ORDER BY postedTimestamp DESC")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity)

    @Query("UPDATE jobs SET isApplied = :isApplied WHERE id = :jobId")
    suspend fun updateJobAppliedState(jobId: Int, isApplied: Boolean)

    // --- Collaborative Projects ---
    @Query("SELECT * FROM collaborative_projects ORDER BY id DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Query("UPDATE collaborative_projects SET isJoined = :isJoined, membersCount = membersCount + :memberChange WHERE id = :projectId")
    suspend fun updateProjectJoinState(projectId: Int, isJoined: Boolean, memberChange: Int)

    // --- Tech Events ---
    @Query("SELECT * FROM events ORDER BY id ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Query("UPDATE events SET isRegistered = :isRegistered, attendeesCount = attendeesCount + :attendeeChange WHERE id = :eventId")
    suspend fun updateEventRegisterState(eventId: Int, isRegistered: Boolean, attendeeChange: Int)

    // --- Messaging ---
    @Query("SELECT * FROM messages WHERE (senderPseudo = :p1 AND receiverPseudo = :p2) OR (senderPseudo = :p2 AND receiverPseudo = :p1) ORDER BY timestamp ASC")
    fun getChatMessages(p1: String, p2: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markNotificationAsRead(notificationId: Int)

    // --- User Management & Reset DB ---
    @Query("DELETE FROM user_profiles WHERE id = :id")
    suspend fun deleteProfileById(id: Int)

    @Query("DELETE FROM user_profiles WHERE email = :email")
    suspend fun deleteProfileByEmail(email: String)

    @Query("DELETE FROM user_profiles")
    suspend fun clearProfiles()

    @Query("DELETE FROM posts")
    suspend fun clearPosts()

    @Query("DELETE FROM comments")
    suspend fun clearComments()

    @Query("DELETE FROM articles")
    suspend fun clearArticles()

    @Query("DELETE FROM groups")
    suspend fun clearGroups()

    @Query("DELETE FROM jobs")
    suspend fun clearJobs()

    @Query("DELETE FROM collaborative_projects")
    suspend fun clearProjects()

    @Query("DELETE FROM events")
    suspend fun clearEvents()

    @Query("DELETE FROM messages")
    suspend fun clearMessages()

    @Query("DELETE FROM notifications")
    suspend fun clearNotifications()
}

@Database(
    entities = [
        UserProfileEntity::class,
        PostEntity::class,
        CommentEntity::class,
        ArticleEntity::class,
        GroupEntity::class,
        JobEntity::class,
        ProjectEntity::class,
        EventEntity::class,
        MessageEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): DevGabonDao
}
