package com.plcoding.jetpackcomposepokedex.di

import com.plcoding.jetpackcomposepokedex.data.remote.PokemonApi
import com.plcoding.jetpackcomposepokedex.data.remote.repository.PokemonRepository
import com.plcoding.jetpackcomposepokedex.data.remote.repository.PokemonRepositoryInterface
import com.plcoding.jetpackcomposepokedex.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun providePokemonRepositoryInterface(
        api: PokemonApi
    ): PokemonRepositoryInterface = PokemonRepository(api)

    @Singleton
    @Provides
    fun providePokemonAPi():PokemonApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.baseURL)
            .build().create(PokemonApi::class.java)
    }
}