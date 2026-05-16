package com.davidread.habittracker.signup.model

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("timezone") val timezone: String
)
