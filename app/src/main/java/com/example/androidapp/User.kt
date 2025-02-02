package com.example.androidapp

data class User(val email: String= "",
                val uid: String= "",
                val firstName: String= "",
                val lastName: String= "",
                val patronymic: String= "",
                val birthDate: String= "",
                val sex: String= "",
                val telephoneNumber: String= "",
                val country: String= "",
                val city: String= "",
                val description: String= "",
                val avatar: String = ""
                )

fun User.toMap(): Map<String, Any> {
    return mapOf(
        "email" to email,
        "uid" to uid,
        "firstName" to firstName,
        "lastName" to lastName,
        "patronymic" to patronymic,
        "birthDate" to birthDate,
        "sex" to sex,
        "telNumber" to telephoneNumber,
        "country" to country,
        "city" to city,
        "description" to description,
        "avatar" to avatar
    )
}