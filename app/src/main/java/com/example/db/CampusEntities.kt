package com.example.db

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
    val isVerified: Boolean = false,
    val filieres: List<String> = emptyList(),
    val studentCount: Int = 0,
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
    val coursesCount: Int = 0,
    val activeGradings: Int = 0
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
    val membersCount: Int = 1,
    val nextEvent: String = ""
)
