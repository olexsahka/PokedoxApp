package com.plcoding.jetpackcomposepokedex.pokemonDetail

import android.view.View
import androidx.lifecycle.ViewModel
import com.plcoding.jetpackcomposepokedex.data.remote.repository.PokemonRepository
import com.plcoding.jetpackcomposepokedex.data.remote.repository.PokemonRepositoryInterface
import com.plcoding.jetpackcomposepokedex.data.remote.responces.Pokemon
import com.plcoding.jetpackcomposepokedex.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    val repository: PokemonRepositoryInterface
): ViewModel() {

    suspend fun getPOkemonInfo(name: String): Resource<Pokemon>{
        return repository.getPokemonDetail(name)
    }
}