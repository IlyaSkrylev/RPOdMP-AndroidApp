package com.example.androidapp

data class Favorite(
    var userId: String = "",
    var userEmail: String = "",

    var smartphonesId: List<String> = emptyList()
)

fun Favorite.toMap(): Map<String, Any> {
    return mapOf(
        "userId" to userId,
        "userEmail" to userEmail,
        "smartphonesId" to smartphonesId
    )
}
