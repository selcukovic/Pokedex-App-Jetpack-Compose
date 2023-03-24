package com.nistruct.pokedexapp.pokemonlist

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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nistruct.pokedexapp.data.models.PokedexListEntry
import com.nistruct.pokedexapp.ui.theme.LightBlue
import com.nistruct.pokedexapp.R
import com.nistruct.pokedexapp.util.calcDominantColor
import com.nistruct.pokedexapp.util.loadImage


@Composable
fun PokemonListScreen(navController: NavController,
                      viewModel: PokemonListViewModel = hiltViewModel()) {
    Surface(
        color = LightBlue,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally))

            SearchBar(
                hint = "Search Pokemon",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
viewModel.searchPokemonList(it)
            }

            Spacer(modifier = Modifier.height(20.dp))
            PokemonList(navController =  navController)

        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {}
) {

    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }


    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = it.isFocused != true
                }
        )
        if (isHintDisplayed) {
            Text(
                text = hint, color = Color.LightGray,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun PokedexEntry(
    entry: PokedexListEntry,
    navController: NavController,
    modifier: Modifier,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val defaultDominanColor = MaterialTheme.colors.surface
    var dominantColor by remember { mutableStateOf(defaultDominanColor) }

    val image = loadImage(url = entry.imageUrl).value
    if (image != null) {
        calcDominantColor(image){
            dominantColor = it
        }
    }
    Box(contentAlignment = Alignment.Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominanColor
                    )
                )
            )
            .clickable {
                navController.navigate("pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName}")
            }
    ) {
        Column {
        /*
                    CoilImage(request = ImageRequest.Builder(LocalContext.current)
                        .data(entry.imageUrl)
                        .target {

                            viewModel.calcDominantColor(it) {
                                dominantColor = it
                            }
                        }
                        .build(),
                        contentDescription = null,
                        fadeIn = true,
                        modifier = Modifier
                            .size(120.dp)
                            .align(CenterHorizontally)
                    ) {

                        CircularProgressIndicator(
                            color = MaterialTheme.colors.onSecondary,
                            modifier = Modifier.scale(0.5f)
                        )

                    }
         */

            image?.asImageBitmap()?.let {
                Image(
                    bitmap = it,
                    contentDescription = entry.pokemonName,
                    modifier = Modifier
                        .size(120.dp)
                        .align(CenterHorizontally),
                )
            }

            Text(
                text = entry.pokemonName,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PokemonList(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {

    val pokemonList by remember { viewModel.pokemonList }
    val endReached by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }
    val isSearching by remember {viewModel.isSearching}


    LazyColumn(contentPadding = PaddingValues(16.dp)) {

        val itemCount = if (pokemonList.size % 2 == 0) {
            pokemonList.size / 2
        } else {
            pokemonList.size / 2 + 1
        }

        items(itemCount) {
            if ( it >= itemCount - 1 && !endReached && !isSearching  ){

                LaunchedEffect(key1 = true){
                    viewModel.loadPokemonPaginated()
                }
            }
            PokedexRow(rowIndex = it, entries = pokemonList, navController = navController)
        }
    }
    Box( modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center ){
        if (loadError.isNotEmpty()){
            RetrySection(error = loadError) {
                viewModel.loadPokemonPaginated()
            }
        }
    }
}

@Composable
fun PokedexRow(
    rowIndex: Int,
    entries: List<PokedexListEntry>,
    navController: NavController
) {
    Column() {
        Row {
            PokedexEntry(
                entry = entries[rowIndex * 2],
                navController = navController,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))

            if (entries.size >= rowIndex * 2 + 2) {
                PokedexEntry(
                    entry = entries[rowIndex * 2 + 1],
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))

            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun RetrySection(error: String, onRetry : () -> Unit) {

    Column {
        Text(text = error, color = Color.Red, fontSize = 18.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { onRetry()},

        modifier = Modifier.align(CenterHorizontally)) {
            Text(text = "Retry")
        }
    }
}