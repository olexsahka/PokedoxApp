package com.plcoding.jetpackcomposepokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.plcoding.jetpackcomposepokedex.pokemonDetail.PokemonDetailScreen
import com.plcoding.jetpackcomposepokedex.pokemonList.PokemonListScreen
import com.plcoding.jetpackcomposepokedex.ui.theme.JetpackComposePokedexTheme
import com.plcoding.jetpackcomposepokedex.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposePokedexTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController, startDestination = Constants.pokemonListScreen
                ) {
                    composable(Constants.pokemonListScreen) {
                        PokemonListScreen(navController = navController)
                    }
                    composable("${Constants.pokemonDetailScreen}/{${Constants.dominantColor}}/{${Constants.pokemonName}}",
                        arguments =
                        listOf(
                            navArgument(Constants.dominantColor) {
                                type = NavType.IntType
                            },
                            navArgument(Constants.pokemonName) {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        val dominantColor = remember {
                            val color = it.arguments?.getInt(Constants.dominantColor)
                            color?.let { Color(it) } ?: Color.White
                        }
                        val pokemonType = remember {
                            it.arguments?.getString(Constants.pokemonName)
                        }
                        PokemonDetailScreen(
                            dominantColor = dominantColor,
                            name = pokemonType?.toLowerCase(Locale.ROOT) ?: "",
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
