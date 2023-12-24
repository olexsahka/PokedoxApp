package com.plcoding.jetpackcomposepokedex.data.remote.repository

import android.util.Log
import com.plcoding.jetpackcomposepokedex.data.remote.PokemonApi
import com.plcoding.jetpackcomposepokedex.data.remote.responces.Pokemon
import com.plcoding.jetpackcomposepokedex.data.remote.responces.PokemonList
import com.plcoding.jetpackcomposepokedex.utils.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor(
    private val api: PokemonApi
) : PokemonRepositoryInterface {
    override suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonLit(limit, offset)
        } catch (e: Exception) {
            Log.d("responce", e.toString())

            return Resource.Error("something error")
        }
        Log.d("responce", response.toString())

        return Resource.Success(response)
    }

    override suspend fun getPokemonDetail(name: String): Resource<Pokemon> {
        val response = try {
            Log.d("errrrrror", name)

            api.getPokemonDetail(name)
        } catch (e: Exception) {
            Log.d("errrrrror", e.toString())
            return Resource.Error("something error")
        }
        Log.d("errrrrror", response.toString())
        return Resource.Success(response)
    }
}

interface PokemonRepositoryInterface {
    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList>
    suspend fun getPokemonDetail(name: String): Resource<Pokemon>

}