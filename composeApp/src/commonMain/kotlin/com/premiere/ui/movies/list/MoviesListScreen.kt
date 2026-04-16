package com.premiere.ui.movies.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.premiere.model.MovieSummary
import com.premiere.repository.MovieSort
import com.premiere.ui.theme.BackgroundDark
import com.premiere.ui.theme.ChipGray
import com.premiere.ui.theme.NavBackground
import com.premiere.ui.theme.PremiereGold
import com.premiere.ui.theme.PremiereRed
import com.premiere.ui.theme.SurfaceCard
import com.premiere.ui.theme.SurfaceElevated
import com.premiere.ui.theme.TextCaption
import com.premiere.ui.theme.TextFaded
import com.premiere.ui.theme.TextLabel
import com.premiere.ui.theme.TextMeta
import com.premiere.ui.theme.TextSecondary
import com.premiere.util.formatToString

@Composable
fun MoviesListRoute(viewModel: MoviesListViewModel) {
    val state by viewModel.state.collectAsState()

    MoviesListScreen(
        state = state,
        onRetry = { viewModel.onIntent(MoviesListContract.Intent.Retry) },
        onSortChange = { viewModel.onIntent(MoviesListContract.Intent.ChangeSort(it)) },
        onMovieClick = { viewModel.onIntent(MoviesListContract.Intent.MovieClicked(it)) },
        onFilterClick = { viewModel.onIntent(MoviesListContract.Intent.FilterClicked) }
    )
}

@Composable
fun MoviesListScreen(
    state: MoviesListContract.State,
    onRetry: () -> Unit,
    onSortChange: (MovieSort) -> Unit,
    onMovieClick: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundDark,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            Column {
                HeaderBar(onFilterClick = onFilterClick)
                SortBar(
                    selectedSort = state.selectedSort,
                    totalCount = state.totalCount,
                    onSortChange = onSortChange
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(innerPadding)
                .navigationBarsPadding()
        ) {
            MoviesListContent(
                state = state,
                onRetry = onRetry,
                onMovieClick = onMovieClick
            )
        }
    }
}

@Composable
private fun HeaderBar(
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🎬 Premiere",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onFilterClick,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PremiereRed,
                contentColor = Color.White
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = "⚙ Filter",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SortBar(
    selectedSort: MovieSort,
    totalCount: Int,
    onSortChange: (MovieSort) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SortDropdown(
            selectedSort = selectedSort,
            onSortChange = onSortChange
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "$totalCount movies",
            color = TextFaded,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun SortDropdown(
    selectedSort: MovieSort,
    onSortChange: (MovieSort) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            modifier = Modifier.clickable { expanded = true },
            shape = RoundedCornerShape(18.dp),
            color = SurfaceElevated
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sort: ",
                    color = TextLabel,
                    fontSize = 11.sp
                )
                Text(
                    text = selectedSort.label(),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " ↓  ▼",
                    color = TextCaption,
                    fontSize = 11.sp
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = SurfaceCard
        ) {
            MovieSort.entries.forEach { sort ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = sort.label(),
                            color = Color.White
                        )
                    },
                    onClick = {
                        expanded = false
                        onSortChange(sort)
                    }
                )
            }
        }
    }
}

@Composable
private fun MoviesListContent(
    state: MoviesListContract.State,
    onRetry: () -> Unit,
    onMovieClick: (String) -> Unit
) {
    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PremiereRed)
            }
        }

        state.errorMessage != null -> {
            MessageState(
                title = "Failed to load movies",
                message = state.errorMessage,
                actionLabel = "Retry",
                onAction = onRetry
            )
        }

        state.isEmpty -> {
            MessageState(
                title = "No movies found",
                message = "Try adjusting your filters",
                actionLabel = "Retry",
                onAction = onRetry
            )
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                state.movies.forEach { movie ->
                    MovieCard(
                        movie = movie,
                        onClick = { onMovieClick(movie.id) }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun MessageState(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = message,
            color = TextFaded,
            style = MaterialTheme.typography.bodyLarge
        )
        Button(
            onClick = onAction,
            colors = ButtonDefaults.buttonColors(containerColor = PremiereRed)
        ) {
            Text(actionLabel)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MovieCard(
    movie: MovieSummary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceCard)
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        PosterImage(
            title = movie.title,
            posterPath = movie.posterPath
        )

        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = movie.title,
                color = Color.White,
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 14.sp
                ),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = movie.year?.toString() ?: "Unknown year",
                color = TextMeta,
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "★ ${movie.imdbRating?.let { it.formatToString() } ?: "-"}",
                    color = PremiereGold,
                    style = TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 13.sp
                    ),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${formatVotes(movie.imdbVotes)} votes",
                    color = TextFaded,
                    style = TextStyle(
                        fontSize = 11.sp,
                        lineHeight = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                movie.genres.forEach { genre ->
                    GenreChip(name = genre.name)
                }
            }
        }
    }
}

@Composable
private fun PosterImage(
    title: String,
    posterPath: String?
) {
    val imageUrl = posterPath?.let { "https://image.tmdb.org/t/p/w185$it" }

    if (imageUrl != null) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .width(55.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .width(55.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title.take(1),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GenreChip(
    name: String
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(ChipGray)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = name,
            color = TextSecondary,
            style = TextStyle(
                fontSize = 10.sp,
                lineHeight = 10.sp
            )
        )
    }
}

private fun MovieSort.label(): String = when (this) {
    MovieSort.RATING -> "Rating"
    MovieSort.YEAR -> "Year"
    MovieSort.TITLE -> "Title"
    MovieSort.POPULARITY -> "Popularity"
}

private fun formatVotes(votes: Int?): String {
    if (votes == null) return "-"
    if (votes >= 1_000_000) return "${(votes / 1_000_000f).formatToString(1)}M"
    if (votes >= 1_000) return "${(votes / 1_000f).formatToString(0)}K"
    return votes.toString()
}
