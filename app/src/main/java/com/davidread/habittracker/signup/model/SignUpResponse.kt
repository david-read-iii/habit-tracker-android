package com.davidread.habittracker.signup.model

import com.google.gson.annotations.SerializedName

data class SignUpResponse(@SerializedName("token") val token: String?)
