package com.nistruct.pokedexapp.repository

import com.nistruct.pokedexapp.data.remote.PokeApi
import com.nistruct.pokedexapp.data.remote.responses.Pokemon
import com.nistruct.pokedexapp.data.remote.responses.PokemonList
import com.nistruct.pokedexapp.util.Resource
import javax.inject.Inject


class PokemonRepository @Inject constructor(private val api: PokeApi) {

    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit, offset)
        } catch (e: java.lang.Exception){
            return Resource.Error("An unknown error occured")
        }
        return  Resource.Success(response)
    }
    suspend fun getPokemonInfo(pokemonName : String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfo(pokemonName)
        } catch (e: java.lang.Exception){
            return Resource.Error("An unknown error occured")
        }
        return  Resource.Success(response)
    }

}