package com.example.androidapp

import android.media.Image

data class User(val firstName: String? = null, val surname: String? = null, val patronymic: String? = null,
                val birthDay: Date?, val gender: Gender, val telephonNumber: String?, val counrtry: String?,
                val city: String?, val destination: String?, val avatar: Image?, val email: String?
                )
