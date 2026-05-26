package com.kipucode.data.remote.firebase.dto

data class UserDto(
    val id : String,
    val name: String,
    val email: String,
    val isVerify: Boolean
)