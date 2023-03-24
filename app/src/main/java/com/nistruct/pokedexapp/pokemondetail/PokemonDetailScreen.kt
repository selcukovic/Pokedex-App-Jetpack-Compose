package com.nistruct.pokedexapp.pokemondetail


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.nistruct.pokedexapp.R
import com.nistruct.pokedexapp.data.remote.responses.Pokemon
import com.nistruct.pokedexapp.data.remote.responses.Type
import com.nistruct.pokedexapp.util.Resource
import com.nistruct.pokedexapp.util.parseStatToAbbr
import com.nistruct.pokedexapp.util.parseStatToColor
import com.nistruct.pokedexapp.util.parseTypeToColor


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PokemonDetailScreen(
    dominantColor: Color,
    pokemonName: String,
    navController: NavController,
    topPadding: Dp = 20.dp,
    pokemonImageSize: Dp = 200.dp,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {

    val pokemonInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading()) {
        value = viewModel.getPokemonInfo(pokemonName)
    }.value



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dominantColor)
            .padding(bottom = 16.dp)
    ) {
        PokemonDetailTopSection(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .align(Alignment.TopCenter)
        )
        if (pokemonInfo != null) {
            PokemonDetailStateWrapper(
                pokemonInfo = pokemonInfo,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 120.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(color = MaterialTheme.colors.surface)
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                loadingModifier = Modifier
                    .size(100.dp)
                    .align(Alignment.Center)
                    .padding(
                        top = 120.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )

            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize(), contentAlignment = Alignment.TopCenter
        ) {

            if (pokemonInfo is Resource.Success) {
                pokemonInfo.data?.sprites.let {

                    GlideImage(
                        model = it?.front_default, contentDescription = "",

                        modifier = Modifier
                            .size(pokemonImageSize)
                            .offset(y = topPadding)
                    )

                }
            }
        }
    }
}

@Composable
fun PokemonDetailTopSection(
    navController: NavController,
    modifier: Modifier
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Black,
                        Color.Transparent
                    )
                )
            )
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(x = 16.dp, y = 16.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
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
                modifier
                    .offset((-20).dp)
                    .fillMaxWidth()
                    .padding(start = 40.dp)
            )

        }
        is Resource.Error -> {
            Text(text = pokemonInfo.message!!, color = Color.Red, modifier = modifier)
        }
        is Resource.Loading -> {
            CircularProgressIndicator(
                color = MaterialTheme.colors.onSecondary,
                modifier = loadingModifier
            )
        }
    }
}

@Composable
fun PokemonDetailSection(
    pokemonInfo: Pokemon,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .offset(y = 80.dp)
            .verticalScroll(scrollState)
    ) {

        Text(
            text = "#${pokemonInfo.id} ${pokemonInfo.name.capitalize(java.util.Locale.ROOT)}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface,
            fontSize = 30.sp
        )
        PokemonTypeSection(types = pokemonInfo.types)
        PokemonDetailDataSection(
            pokemonWeightInKg = pokemonInfo.weight,
            pokemonHeight = pokemonInfo.height
        )
        PokemonBaseStats(pokemonInfo = pokemonInfo)
    }
}

@Composable
fun PokemonTypeSection(types: List<Type>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
    ) {
        for (type in types) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .shadow(5.dp, CircleShape)
                    .clip(CircleShape)
                    .background(parseTypeToColor(type))
                    .shadow(10.dp, CircleShape)
                    .height(35.dp)
            ) {
                Text(
                    text = type.type.name.replaceFirstChar { it.uppercaseChar() },
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun PokemonDetailDataSection(
    pokemonWeightInKg: Int,
    pokemonHeight: Int
) {
    val pokemonWeightInKg = remember {
        pokemonWeightInKg / 10f
    }
    val pokemonHeightInMeters = remember {
        pokemonHeight / 10f
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        PokemonDetailDataItem(
            dataValue = pokemonWeightInKg,
            dataUnit = "kg",
            dataIcon = painterResource(id = R.drawable.ic_weight),
            modifier = Modifier.weight(1f)
        )
        Spacer(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color.LightGray)
        )
        PokemonDetailDataItem(
            dataValue = pokemonHeightInMeters,
            dataUnit = "m",
            dataIcon = painterResource(id = R.drawable.ic_height),
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
        verticalArrangement = Arrangement.Center, modifier = modifier
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
        } else {
            0f
        },
        animationSpec = tween(
            animDuration,
            animDelay
        )
    )
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(
                if (isSystemInDarkTheme()) {
                    Color(0xFF505050)
                } else {
                    Color.LightGray
                }
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(curPercent.value)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(statColor)
                .padding(horizontal = 8.dp)
        ) {

            Text(text = statName, fontWeight = FontWeight.Bold)
            Text(
                text = (curPercent.value * statValue).toInt().toString(),
                fontWeight = FontWeight.Bold,
            )

        }
    }

}

@Composable
fun PokemonBaseStats(
    pokemonInfo: Pokemon,
    animDelayPerItem: Int = 100,
) {
    val maxBaseStat = remember {
        pokemonInfo.stats.maxOf { it.base_stat }
    }
    Column(modifier = Modifier.fillMaxWidth()) {

        Text(
            text = "Base Stats:",
            color = MaterialTheme.colors.onSurface,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.height(4.dp))

        for (i in pokemonInfo.stats.indices) {

            val stat = pokemonInfo.stats[i]

            PokemonStat(
                statName = parseStatToAbbr(stat),
                statValue = stat.base_stat,
                statMaxValue = maxBaseStat,
                statColor = parseStatToColor(stat),
                animDelay = i * animDelayPerItem
            )
            Spacer(modifier = Modifier.height(4.dp))

        }


    }


}