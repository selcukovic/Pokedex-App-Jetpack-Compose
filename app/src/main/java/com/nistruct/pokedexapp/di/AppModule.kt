package com.nistruct.pokedexapp.di

import com.nistruct.pokedexapp.BuildConfig
import com.nistruct.pokedexapp.data.remote.PokeApi
import com.nistruct.pokedexapp.repository.PokemonRepository
import com.nistruct.pokedexapp.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG){
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else{
        OkHttpClient
            .Builder()
            .build()
    }
    @Singleton
    @Provides
    @Named("retrofit")
    fun provideRetrofit(okHttpClient: OkHttpClient) : Retrofit{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
    }
    @Provides
    @Singleton
    fun providePokeApi(@Named("retrofit") retrofit : Retrofit) : PokeApi{
        return retrofit.create(PokeApi::class.java)
    }

    @Singleton
    @Provides
    fun providePokemonRepository(api : PokeApi) = PokemonRepository(api)



}