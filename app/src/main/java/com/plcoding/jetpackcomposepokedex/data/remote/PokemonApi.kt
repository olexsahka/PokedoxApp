package com.plcoding.jetpackcomposepokedex.data.remote

import com.plcoding.jetpackcomposepokedex.data.remote.responces.Pokemon
import com.plcoding.jetpackcomposepokedex.data.remote.responces.PokemonList
import com.plcoding.jetpackcomposepokedex.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface PokemonApi {
    @GET("pokemon")
    suspend fun getPokemonLit(
        @Query("limit") limit : Int,
        @Query("offset") offset : Int
    ): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(
        @Path("name") name : String
    ):Pokemon
}