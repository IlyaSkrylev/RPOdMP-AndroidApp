package com.example.androidapp

data class Comment(
    val date: String = "",
    val time: String = "",
    val idSmartphone: String = "",
    val idUser: String = "",
    val comment: String = ""
)

fun Comment.toMap(): Map<String, Any> {
    return mapOf(
        "date" to date,
        "time" to time,
        "idSmartphone" to idSmartphone,
        "idUser" to idUser,
        "comment" to comment
    )
}