@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.plcoding.jetpackcomposepokedex.pokemonList

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import coil.request.ImageRequest
import com.google.accompanist.coil.CoilImage
import com.plcoding.jetpackcomposepokedex.R
import com.plcoding.jetpackcomposepokedex.data.remote.models.PokeDexListEntry
import com.plcoding.jetpackcomposepokedex.data.remote.responces.PokemonList
import com.plcoding.jetpackcomposepokedex.ui.theme.RobotoCondensed
import com.plcoding.jetpackcomposepokedex.utils.Constants


@Preview
@Composable()
fun PokemonListScreen(navController: NavController,viewModel: PokemonListViewModel = hiltNavGraphViewModel()) {
    Surface(
        color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                contentDescription = "pokemon",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally)
            )
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), hint = "Search ..."
            ) {
                viewModel.searchPokemonList(it)
            }
            Spacer(modifier = Modifier.height(16.dp))
            PokemonList(navController = navController)
        }

    }
}

@Composable
fun SearchBar(
    modifier: Modifier, hint: String = "", onSearch: (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }
    Box(modifier = modifier) {
        BasicTextField(value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = it != FocusState.Active && text.isNotEmpty()
                })
        if (isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}


@Composable
fun PokemonList(
    navController: NavController, viewModel: PokemonListViewModel = hiltNavGraphViewModel()
) {
    val pokemonList by remember {
        viewModel.pokemonList
    }
    val endReached by remember {
        viewModel.endReached
    }
    val loadError by remember {
        viewModel.loadError
    }
    val isLoading by remember {
        viewModel.isLoading
    }
    val isSearching by remember {
        viewModel.isSearching
    }
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        val itemCount = if (pokemonList.size % 2 == 0) pokemonList.size / 2
        else pokemonList.size / 2 + 1
        items(itemCount){
            if((it >= (itemCount - 1)) && !endReached && !isLoading && !isSearching){
                LaunchedEffect(key1 = true, block ={
                    viewModel.loadingPaginated()
                } )
            }
            PokedexRow(rowINdex = it, entries = pokemonList, navController = navController)
        }
    }
    Box(contentAlignment = Center, modifier = Modifier.fillMaxSize()) {
        if(isLoading)
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        if (loadError.isNotEmpty()){
            RetrySection(error = loadError) {
                viewModel.loadingPaginated()
            }
        }
    }
}

@Composable
fun PokedexEntry(
    entry: PokeDexListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltNavGraphViewModel()
) {
    val defaultDominantColor = MaterialTheme.colors.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }
    Box(contentAlignment = Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor, defaultDominantColor
                    )
                )
            )
            .clickable {
                navController.navigate(route = "${Constants.pokemonDetailScreen}/${dominantColor.toArgb()}/${entry.name}")
            }) {
        Column {
            Log.d("imageUTL",entry.imgUrl)
            CoilImage(
                request = ImageRequest.Builder(LocalContext.current).data(entry.imgUrl).target {
                    viewModel.calcDominantColor(it) {
                        dominantColor = it
                    }
                }.build(),
                contentDescription = entry.name,
                fadeIn = true,
                modifier = Modifier
                    .size(120.dp)
                    .align(CenterHorizontally)
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.primary, modifier = Modifier.scale(0.5f)
                )
            }
            Text(
                text = entry.name,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
fun PokedexRow(
    rowINdex: Int, entries: List<PokeDexListEntry>, navController: NavController
) {
    Column {
        Row {
            PokedexEntry(
                entry = entries[rowINdex * 2],
                navController = navController,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            if (entries.size >= rowINdex * 2 + 2) {
                PokedexEntry(
                    entry = entries[rowINdex * 2 + 1],
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            } else Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Composable
fun RetrySection(error: String, onRetry: () ->Unit){
    Column {
        Text(text = error, color = Color.Red, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onRetry() }, modifier = Modifier.align(CenterHorizontally)) { 
            Text(text = "Retry")
        }
    }
}



