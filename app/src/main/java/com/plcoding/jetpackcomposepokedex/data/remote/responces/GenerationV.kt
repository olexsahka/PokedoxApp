package com.plcoding.jetpackcomposepokedex.data.remote.responces

import com.google.gson.annotations.SerializedName

data class GenerationV(
    @SerializedName("black-white")
    val black_white: BlackWhite
)