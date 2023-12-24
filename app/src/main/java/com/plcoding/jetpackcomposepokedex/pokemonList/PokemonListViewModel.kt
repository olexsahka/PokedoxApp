package com.plcoding.jetpackcomposepokedex.pokemonList

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.plcoding.jetpackcomposepokedex.data.remote.models.PokeDexListEntry
import com.plcoding.jetpackcomposepokedex.data.remote.repository.PokemonRepositoryInterface
import com.plcoding.jetpackcomposepokedex.utils.Constants
import com.plcoding.jetpackcomposepokedex.utils.Constants.POKEMON_LIST_SIZE
import com.plcoding.jetpackcomposepokedex.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val pokemonRepositoryInterface: PokemonRepositoryInterface
) : ViewModel() {

    private var currPage = 0

    var pokemonList = mutableStateOf<List<PokeDexListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)


    private var cachedPokemonList = listOf<PokeDexListEntry>()
    private var isSearchStarting = true
    var isSearching = mutableStateOf(false)

    init {
        loadingPaginated()
    }

    fun searchPokemonList(query:String){
        val listToSearch = if (isSearchStarting){
            pokemonList.value
        }
        else{
            cachedPokemonList
        }
        viewModelScope.launch(Dispatchers.Default) {
            if(query.isEmpty()){
                pokemonList.value = cachedPokemonList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter {
                it.name.contains(query.trim(), ignoreCase = true)||it.num.toString() == query.trim()
            }
            if(isSearchStarting){
                cachedPokemonList = pokemonList.value
                isSearchStarting = false
            }
            pokemonList.value = results
            isSearching.value = true
        }

    }


    fun loadingPaginated() {
        viewModelScope.launch {
            isLoading.value = true
            val result = pokemonRepositoryInterface.getPokemonList(
                POKEMON_LIST_SIZE,
                currPage + POKEMON_LIST_SIZE
            )
            when (result) {
                is Resource.Success -> {
                    endReached.value = currPage + POKEMON_LIST_SIZE >= result.data!!.count
                    val pokeDexListEntry = result.data.results.mapIndexed{index, entry ->
                        val num  = if(entry.url.endsWith("/"))
                            entry.url.dropLast(1).takeLastWhile { it.isDigit()}
                        else
                            entry.url.takeLastWhile { it.isDigit()}
                        val url = "${Constants.IMG_BASE_URL}$num.png"
                        PokeDexListEntry(entry.name.capitalize(Locale.ROOT),url,num)
                    }
                    currPage++
                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokeDexListEntry
                }
                is Resource.Error -> {
                    loadError.value = result.message.toString()
                    isLoading.value = false
                }
            }
        }
    }

    fun calcDominantColor(drawable: Drawable, onFinished: (Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bmp).generate { palete ->
            palete?.dominantSwatch?.rgb?.let {
                onFinished(Color(it))
            }
        }
    }
}