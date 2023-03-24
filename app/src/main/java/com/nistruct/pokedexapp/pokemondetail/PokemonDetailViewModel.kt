package com.nistruct.pokedexapp.pokemondetail

import androidx.lifecycle.ViewModel
import com.nistruct.pokedexapp.data.remote.responses.Pokemon
import com.nistruct.pokedexapp.repository.PokemonRepository
import com.nistruct.pokedexapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel  @Inject constructor(private val pokemonRepository: PokemonRepository): ViewModel() {

    suspend fun getPokemonInfo(pokemonName : String) : Resource<Pokemon>{
        return pokemonRepository.getPokemonInfo(pokemonName)
    }

}