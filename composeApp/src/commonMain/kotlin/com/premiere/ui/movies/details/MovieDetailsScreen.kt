package com.premiere.ui.movies.details
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.premiere.model.MovieDetail
import com.premiere.model.MovieImage
import com.premiere.model.Person
import com.premiere.util.formatToString

private val DetailBackground = Color(0xFF121212)
private val DetailSurface = Color(0xFF1E1E2E)
private val DetailHeader = Color(0xFF1A1A2E)
private val DetailMuted = Color(0xFF999999)
private val DetailBlue = Color(0xFF4DABF7)
private val DetailRed = Color(0xFFE50914)
private val DetailGold = Color(0xFFF5C518)

@Composable
fun MovieDetailsRoute(
    viewModel: MovieDetailsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MovieDetailsContract.Effect.NavigateBack -> onBack()
                is MovieDetailsContract.Effect.OpenTrailer -> {
                    uriHandler.openUri(effect.url)
                }
            }
        }
    }

    MovieDetailsScreen(
        state = state,
        onRetry = { viewModel.onIntent(MovieDetailsContract.Intent.Retry) },
        onBack = { viewModel.onIntent(MovieDetailsContract.Intent.BackClicked) },
        onPlayTrailer = { viewModel.onIntent(MovieDetailsContract.Intent.PlayTrailerClicked) }
    )
}

@Composable
fun MovieDetailsScreen(
    state: MovieDetailsContract.State,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onPlayTrailer: () -> Unit
) {
    when {
        state.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DetailBackground),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = DetailRed)
            }
        }

        state.errorMessage != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DetailBackground)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Failed to load movie details",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.errorMessage,
                    color = DetailMuted,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = DetailRed)
                ) {
                    Text("Retry")
                }
            }
        }

        state.movie != null -> {
            MovieDetailsContent(
                movie = state.movie,
                actors = state.actors,
                backdrops = state.backdrops,
                trailerUrl = state.trailerUrl,
                onBack = onBack,
                onPlayTrailer = onPlayTrailer
            )
        }
    }
}

@Composable
private fun MovieDetailsContent(
    movie: MovieDetail,
    actors: List<Person>,
    backdrops: List<MovieImage>,
    trailerUrl: String?,
    onBack: () -> Unit,
    onPlayTrailer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailBackground)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        DetailBackdrop(
            backdropPath = movie.backdropPath,
            hasTrailer = trailerUrl != null,
            onBack = onBack,
            onPlayTrailer = onPlayTrailer
        )

        DetailHeader(
            movie = movie
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 2.dp)
        ) {
            RatingsRow(movie = movie)

            DetailGenres(movie = movie)

            SectionTitle("Overview")
            Text(
                text = movie.overview ?: "No description available.",
                color = Color(0xFFBBBBBB),
                style = TextStyle(fontSize = 13.sp, lineHeight = 21.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SectionTitle("Info")
            InfoBadges(movie = movie)

            if (backdrops.isNotEmpty()) {
                SectionTitle("Images")
                ImagesRow(backdrops)
            }

            if (actors.isNotEmpty()) {
                SectionTitle("Actors")
                ActorsList(actors)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun DetailBackdrop(
    backdropPath: String?,
    hasTrailer: Boolean,
    onBack: () -> Unit,
    onPlayTrailer: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(DetailHeader)
    ) {
        val backdropUrl = backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" }

        if (backdropUrl != null) {
            AsyncImage(
                model = backdropUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.8f
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, DetailBackground),
                        startY = 120f
                    )
                )
        )

        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 12.dp, start = 12.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Text("←", color = Color.White, fontSize = 18.sp)
        }

        if (hasTrailer) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(DetailRed.copy(alpha = 0.9f))
                    .clickable(onClick = onPlayTrailer),
                contentAlignment = Alignment.Center
            ) {
                Text("▶", color = Color.White, fontSize = 26.sp)
            }
        }
    }
}

@Composable
private fun DetailHeader(
    movie: MovieDetail
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .lift(up = 55.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AsyncImage(
            model = movie.posterPath?.let { "https://image.tmdb.org/t/p/w342$it" },
            contentDescription = movie.title,
            modifier = Modifier
                .width(95.dp)
                .height(140.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, Color(0xFF222222), RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .padding(top = 46.dp)
                .weight(1f)
        ) {
            Text(
                text = movie.title,
                color = Color.White,
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 21.sp
                ),
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${movie.year ?: "—"} • ${movie.runtime?.let { "$it min" } ?: "—"}",
                color = DetailMuted,
                fontSize = 12.sp
            )
        }
    }
}

private fun Modifier.lift(up: androidx.compose.ui.unit.Dp): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val upPx = up.roundToPx()
        layout(placeable.width, (placeable.height - upPx).coerceAtLeast(0)) {
            placeable.placeRelative(0, -upPx)
        }
    }

@Composable
private fun RatingsRow(movie: MovieDetail) {
    Row(
        modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "⭐ ${movie.imdbRating?.let { it.formatToString() } ?: "—"}",
            color = DetailGold,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "/10",
            color = Color(0xFF777777),
            fontSize = 11.sp
        )
        Text(
            text = movie.imdbVotes?.let { "${formatVotes(it)} votes" } ?: "",
            color = DetailMuted,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        if (movie.tmdbRating != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("TMDB", color = DetailBlue, fontSize = 11.sp)
                Text(movie.tmdbRating.formatToString(), color = DetailBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DetailGenres(movie: MovieDetail) {
    Row(
        modifier = Modifier.padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        movie.genres.forEach { genre ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(DetailRed)
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Text(
                    text = genre.name,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFFDDDDDD),
        style = TextStyle(
            fontSize = 13.sp,
            lineHeight = 13.sp,
            letterSpacing = 0.5.sp
        ),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
    )
}

@Composable
private fun InfoBadges(movie: MovieDetail) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InfoBadge(
            label = "Budget",
            value = formatMoney(movie.budget),
            modifier = Modifier.weight(1f)
        )
        InfoBadge(
            label = "Revenue",
            value = formatMoney(movie.revenue),
            modifier = Modifier.weight(1f)
        )
        InfoBadge(
            label = "Language",
            value = movie.languageCode?.uppercase() ?: "—",
            modifier = Modifier.weight(1f)
        )
        InfoBadge(
            label = "Popularity",
            value = movie.popularity?.let { it.formatToString() } ?: "—",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun InfoBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(DetailSurface, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .height(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = DetailMuted,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun ImagesRow(backdrops: List<MovieImage>) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        backdrops.forEach { image ->
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w780${image.filePath}",
                contentDescription = null,
                modifier = Modifier
                    .width(160.dp)
                    .height(90.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun ActorsList(actors: List<Person>) {
    Column(
        modifier = Modifier.padding(bottom = 20.dp)
    ) {
        actors.forEachIndexed { index, actor ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = actor.profilePath?.let { "https://image.tmdb.org/t/p/w185$it" },
                    contentDescription = actor.name,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A2A3E)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = actor.name,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (index != actors.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DetailSurface)
                )
            }
        }
    }
}

private fun formatVotes(votes: Int): String {
    if (votes >= 1_000_000) return "${(votes / 1_000_000f).formatToString(1)}M"
    if (votes >= 1_000) return "${(votes / 1_000f).formatToString(0)}K"
    return votes.toString()
}

private fun formatMoney(amount: Long?): String {
    if (amount == null || amount == 0L) return "—"
    if (amount >= 1_000_000_000) return "$${(amount / 1_000_000_000f).formatToString(1)}B"
    if (amount >= 1_000_000) return "$${(amount / 1_000_000f).formatToString(0)}M"
    return "$" + amount.toString()
}
