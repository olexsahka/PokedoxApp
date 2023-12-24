package com.plcoding.jetpackcomposepokedex.pokemonDetail

import android.widget.Space
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import com.plcoding.jetpackcomposepokedex.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import com.google.accompanist.coil.CoilImage
import com.plcoding.jetpackcomposepokedex.data.remote.responces.Pokemon
import com.plcoding.jetpackcomposepokedex.data.remote.responces.Type
import com.plcoding.jetpackcomposepokedex.ui.theme.lightGrey
import com.plcoding.jetpackcomposepokedex.utils.Resource
import com.plcoding.jetpackcomposepokedex.utils.parseStatToAbbr
import com.plcoding.jetpackcomposepokedex.utils.parseStatToColor
import com.plcoding.jetpackcomposepokedex.utils.parseTypeToColor
import java.util.*
import kotlin.math.round


@Composable
fun PokemonDetailScreen(
    dominantColor: Color,
    name: String,
    navController: NavController,
    topPadding: Dp = 20.dp,
    pokemonImageSize: Dp = 200.dp,
    viewModel: PokemonDetailViewModel = hiltNavGraphViewModel()
) {
    val pokemonInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading()) {
        value = viewModel.getPOkemonInfo(name)
    }.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dominantColor)
            .padding(bottom = 16.dp)
    ) {
        PokemonTopSection(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .align(TopStart)
        )
        PokemonDetailStateWrapper(
            pokemonInfo = pokemonInfo,
            Modifier
                .fillMaxSize()
                .padding(
                    start = 16.dp,
                    top = topPadding + pokemonImageSize / 2f,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colors.surface)
                .padding(16.dp)
                .align(BottomCenter),
            loadingModifier = Modifier
                .size(100.dp)
                .padding(
                    start = 16.dp,
                    top = topPadding + pokemonImageSize / 2f,
                    end = 16.dp,
                    bottom = 16.dp
                )
        )
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            if (pokemonInfo is Resource.Success) {
                pokemonInfo.data?.sprites?.let {
                    CoilImage(
                        data = it.front_default,
                        contentDescription = pokemonInfo.data.name,
                        fadeIn = true,
                        modifier = Modifier
                            .size(pokemonImageSize)
                            .offset(y = topPadding)
                    ) {

                    }
                }
            }
        }
    }
}


@Composable
fun PokemonTopSection(
    navController: NavController, modifier: Modifier
) {
    Box(
        contentAlignment = TopStart, modifier = modifier.background(
            Brush.verticalGradient(listOf(Color.Black, Color.Transparent))
        )
    ) {
        Icon(imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(16.dp, 16.dp)
                .clickable {
                    navController.popBackStack()
                })
    }
}

@Composable
fun PokemonDetailStateWrapper(
    pokemonInfo: Resource<Pokemon>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier
) {
    when (pokemonInfo) {
        is Resource.Success -> {
            PokemonDetailSection(
                pokemonInfo = pokemonInfo.data!!,
                modifier = modifier.offset(y = (-20).dp)
            )
        }
        is Resource.Error -> {
            Text(text = pokemonInfo.message!!, color = Color.Red, modifier = modifier)
        }
        is Resource.Loading -> {
            CircularProgressIndicator(
                color = MaterialTheme.colors.primary, modifier = loadingModifier
            )
        }
    }

}

@Composable
fun PokemonDetailSection(
    pokemonInfo: Pokemon, modifier: Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .offset(y = 100.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "#${pokemonInfo.id} ${pokemonInfo.name.capitalize(Locale.ROOT)}",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface
        )
        PokemonTypeSection(types = pokemonInfo.types)
        PokemonDetailDataSection(
            pokemonWeight = pokemonInfo.weight,
            pokemonHeight = pokemonInfo.height
        )
        PokemonBaseStats(pokemonInfo = pokemonInfo)
    }
}

@Composable
fun PokemonTypeSection(types: List<Type>) {
    Row(verticalAlignment = CenterVertically, modifier = Modifier.padding(16.dp)) {
        for (type in types) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .clip(CircleShape)
                    .background(
                        parseTypeToColor(type)
                    )
                    .height(35.dp)
            ) {
                Text(
                    text = type.type.name.capitalize(Locale.ROOT),
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun PokemonDetailDataSection(
    pokemonWeight: Int,
    pokemonHeight: Int,
    sectionHeight: Dp = 80.dp
) {
    val pokemonWeightKg = remember {
        round(pokemonWeight * 100f) / 1000f

    }
    val pokemonHeightMetres = remember {
        round(pokemonHeight * 100f) / 1000f
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        PokemonDetailDataItem(
            dataValue = pokemonWeightKg, dataUnit = "kg", dataIcon = painterResource(
                id = R.drawable.ic_weight
            ),
            modifier = Modifier.weight(1f)
        )
        Spacer(
            modifier = Modifier
                .size(1.dp, sectionHeight)
                .background(Color.LightGray)
        )
        PokemonDetailDataItem(
            dataValue = pokemonHeightMetres, dataUnit = "m", dataIcon = painterResource(
                id = R.drawable.ic_height
            ),
            modifier = Modifier.weight(1f)
        )
    }

}

@Composable
fun PokemonDetailDataItem(
    dataValue: Float,
    dataUnit: String,
    dataIcon: Painter,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Icon(painter = dataIcon, contentDescription = null, tint = MaterialTheme.colors.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "$dataValue$dataUnit", color = MaterialTheme.colors.onSurface)


    }
}

@Composable
fun PokemonStat(
    statName: String,
    statValue: Int,
    statMaxValue: Int,
    statColor: Color,
    height: Dp = 28.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val curPercent = animateFloatAsState(
        targetValue = if (animationPlayed) {
            statValue / statMaxValue.toFloat()
        } else 0f, animationSpec = tween(animDuration, animDelay)
    )
    LaunchedEffect(key1 = true, block = {
        animationPlayed = true
    })
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(
                if (isSystemInDarkTheme()) {
                    Color(0xff505050)
                } else
                    Color.LightGray
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(curPercent.value)
                .clip(CircleShape)
                .background(statColor)
                .padding(horizontal = 8.dp)
        ) {
            Text(text = statName, fontWeight = FontWeight.Bold)
            Text(
                text = (curPercent.value + statMaxValue).toInt().toString(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PokemonBaseStats(pokemonInfo: Pokemon, animateDelayPerItem: Int = 100) {
    val maxBaseStats = remember {
        pokemonInfo.stats.maxOf { it.base_stat }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Base Stats", fontSize = 20.sp,
            color = MaterialTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        for (i in pokemonInfo.stats.indices) {
            val stat = pokemonInfo.stats[i]
            PokemonStat(
                statName = parseStatToAbbr(stat),
                statValue = stat.base_stat,
                statMaxValue = maxBaseStats,
                statColor = parseStatToColor(stat),
                animDelay = i * animateDelayPerItem
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

}