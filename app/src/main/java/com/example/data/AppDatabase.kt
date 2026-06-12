package com.example.data

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
    suspend fun insertPost(post: PostEntity): Long

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
    suspend fun insertComment(comment: CommentEntity): Long

    // --- Articles / Blog ---
    @Query("SELECT * FROM articles ORDER BY timestamp DESC")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE category = :category ORDER BY timestamp DESC")
    fun getArticlesByCategory(category: String): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity): Long

    @Query("UPDATE articles SET reactionsCount = :reactionsCount WHERE id = :articleId")
    suspend fun updateArticleReactions(articleId: Int, reactionsCount: Int)

    @Query("UPDATE articles SET viewsCount = viewsCount + 1 WHERE id = :articleId")
    suspend fun incrementArticleViews(articleId: Int)

    // --- Groups / Communities ---
    @Query("SELECT * FROM groups ORDER BY name ASC")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity): Long

    @Query("UPDATE groups SET isJoined = :isJoined, membersCount = membersCount + :memberChange WHERE id = :groupId")
    suspend fun updateGroupJoinState(groupId: Int, isJoined: Boolean, memberChange: Int)

    // --- Jobs ---
    @Query("SELECT * FROM jobs ORDER BY postedTimestamp DESC")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity): Long

    @Query("UPDATE jobs SET isApplied = :isApplied WHERE id = :jobId")
    suspend fun updateJobAppliedState(jobId: Int, isApplied: Boolean)

    // --- Collaborative Projects ---
    @Query("SELECT * FROM collaborative_projects ORDER BY id DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Query("UPDATE collaborative_projects SET isJoined = :isJoined, membersCount = membersCount + :memberChange WHERE id = :projectId")
    suspend fun updateProjectJoinState(projectId: Int, isJoined: Boolean, memberChange: Int)

    // --- Tech Events ---
    @Query("SELECT * FROM events ORDER BY id ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity): Long

    @Query("UPDATE events SET isRegistered = :isRegistered, attendeesCount = attendeesCount + :attendeeChange WHERE id = :eventId")
    suspend fun updateEventRegisterState(eventId: Int, isRegistered: Boolean, attendeeChange: Int)

    // --- Messaging ---
    @Query("SELECT * FROM messages WHERE (senderPseudo = :p1 AND receiverPseudo = :p2) OR (senderPseudo = :p2 AND receiverPseudo = :p1) ORDER BY timestamp ASC")
    fun getChatMessages(p1: String, p2: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markNotificationAsRead(notificationId: Int)

    // --- Follow System ---
    @Query("SELECT * FROM follows WHERE followerEmail = :email")
    fun getFollowsByFollower(email: String): Flow<List<FollowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollow(follow: FollowEntity)

    @Query("DELETE FROM follows WHERE followerEmail = :followerEmail AND followedEmail = :followedEmail")
    suspend fun deleteFollow(followerEmail: String, followedEmail: String)

    @Query("SELECT EXISTS(SELECT 1 FROM follows WHERE followerEmail = :followerEmail AND followedEmail = :followedEmail LIMIT 1)")
    fun isFollowing(followerEmail: String, followedEmail: String): Flow<Boolean>

    @Query("DELETE FROM follows")
    suspend fun clearFollows()
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
        NotificationEntity::class,
        FollowEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): DevGabonDao
}
