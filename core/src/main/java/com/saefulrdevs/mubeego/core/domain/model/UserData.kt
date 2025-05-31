package com.saefulrdevs.mubeego.core.domain.model

data class UserData(
    val uid: String,
    val fullname: String,
    val email: String,
    val isPremium: Boolean, // baru
    val createdAt: Long? = null // baru
)

fun UserData.toMap(): Map<String, Any?> = mapOf(
    "uid" to uid,
    "fullname" to fullname,
    "email" to email,
    "isPremium" to isPremium,
    "createdAt" to createdAt
)
