package com.plcoding.jetpackcomposepokedex.data.remote.responces

import com.google.gson.annotations.SerializedName

data class GenerationIii(
    val emerald: Emerald,
    @SerializedName("firered-leafgreen")
    val firered_leafgreen: FireredLeafgreen,
    @SerializedName("ruby-sapphire")
    val ruby_sapphire: RubySapphire
)