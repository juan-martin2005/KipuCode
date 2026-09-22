package com.kipucode.domain.model

import androidx.annotation.DrawableRes

data class Avatar(
    val id: String,
    @DrawableRes val resId: Int,
    val name: String = ""
)
