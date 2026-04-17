package com.premiere.ui.movies.filter

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.premiere.repository.MovieFilters
import com.premiere.ui.theme.BackgroundDark
import com.premiere.ui.theme.NavBackground
import com.premiere.ui.theme.PremiereRed
import com.premiere.ui.theme.PremiereRedDark
import com.premiere.ui.theme.SliderInactive
import com.premiere.ui.theme.SurfaceChip
import com.premiere.ui.theme.SurfaceElevated
import com.premiere.ui.theme.TextBright
import com.premiere.ui.theme.TextMuted
import com.premiere.util.formatToString

@Composable
fun FilterMoviesRoute(
    viewModel: FilterMoviesViewModel,
    onApplyFilters: (MovieFilters) -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FilterMoviesContract.Effect.ApplyFilters -> onApplyFilters(effect.filters)
                FilterMoviesContract.Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    FilterMoviesScreen(
        state = state,
        onSearchChanged = { viewModel.onIntent(FilterMoviesContract.Intent.SearchChanged(it)) },
        onGenreSelected = { viewModel.onIntent(FilterMoviesContract.Intent.GenreSelected(it)) },
        onMinYearChanged = { viewModel.onIntent(FilterMoviesContract.Intent.MinYearChanged(it)) },
        onMaxYearChanged = { viewModel.onIntent(FilterMoviesContract.Intent.MaxYearChanged(it)) },
        onRatingChanged = { viewModel.onIntent(FilterMoviesContract.Intent.RatingChanged(it)) },
        onApplyFilters = { viewModel.onIntent(FilterMoviesContract.Intent.ApplyFilters) },
        onClearAll = { viewModel.onIntent(FilterMoviesContract.Intent.ClearAll) },
        onBack = { viewModel.onIntent(FilterMoviesContract.Intent.BackClicked) }
    )
}

@Composable
fun FilterMoviesScreen(
    state: FilterMoviesContract.State,
    onSearchChanged: (String) -> Unit,
    onGenreSelected: (Int?) -> Unit,
    onMinYearChanged: (String) -> Unit,
    onMaxYearChanged: (String) -> Unit,
    onRatingChanged: (Float) -> Unit,
    onApplyFilters: () -> Unit,
    onClearAll: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundDark,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavBackground)
                    .statusBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .clickable(onClick = onBack)
                )

                Text(
                    text = "Filter Movies",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                TextButton(onClick = onClearAll) {
                    Text(
                        text = "Clear All",
                        color = PremiereRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundDark)
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PremiereRed)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundDark)
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 28.dp)
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                FilterSection(title = "SEARCH") {
                    StyledTextField(
                        value = state.searchQuery,
                        onValueChange = onSearchChanged,
                        placeholder = "Search by movie title..."
                    )
                }

                FilterSection(title = "GENRE") {
                    GenreSection(
                        state = state,
                        onGenreSelected = onGenreSelected
                    )
                }

                FilterSection(title = "YEAR RANGE") {
                    YearRangeSection(
                        minYear = state.minYear,
                        maxYear = state.maxYear,
                        onMinYearChanged = onMinYearChanged,
                        onMaxYearChanged = onMaxYearChanged
                    )
                }

                FilterSection(title = "MINIMUM RATING") {
                    RatingSection(
                        minRating = state.minRating,
                        onRatingChanged = onRatingChanged
                    )
                }

                state.errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = PremiereRed,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = onApplyFilters,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(74.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PremiereRed)
                ) {
                    Text(
                        text = "Apply Filters",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = title,
            color = TextBright,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp
        )
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenreSection(
    state: FilterMoviesContract.State,
    onGenreSelected: (Int?) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        state.genres.forEach { genre ->
            GenreChip(
                name = genre.name,
                selected = state.selectedGenreId == genre.id,
                onClick = { onGenreSelected(genre.id) }
            )
        }
    }
}

@Composable
private fun YearRangeSection(
    minYear: String,
    maxYear: String,
    onMinYearChanged: (String) -> Unit,
    onMaxYearChanged: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "From",
                color = TextMuted,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            StyledNumberField(
                value = minYear,
                onValueChange = onMinYearChanged,
                placeholder = "1920"
            )
        }

        Text(
            text = "—",
            color = TextMuted,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 28.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "To",
                color = TextMuted,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            StyledNumberField(
                value = maxYear,
                onValueChange = onMaxYearChanged,
                placeholder = "2025"
            )
        }
    }
}

@Composable
private fun RatingSection(
    minRating: Float,
    onRatingChanged: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                value = minRating,
                onValueChange = onRatingChanged,
                valueRange = 0f..10f,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = PremiereRed,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = SliderInactive
                )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .background(SurfaceElevated, RoundedCornerShape(16.dp))
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "⭐ ${minRating.formatToString()}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = TextMuted,
                fontSize = 17.sp
            )
        },
        shape = RoundedCornerShape(18.dp),
        singleLine = true,
        colors = filterFieldColors(),
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = Color.White,
            fontSize = 17.sp
        )
    )
}

@Composable
private fun StyledNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = ""
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = TextMuted,
                style = TextStyle(
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        shape = RoundedCornerShape(18.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = filterFieldColors(),
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun GenreChip(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) PremiereRedDark else SurfaceChip,
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = name,
            color = TextBright,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun filterFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = SurfaceElevated,
    unfocusedContainerColor = SurfaceElevated,
    disabledContainerColor = SurfaceElevated,
    errorContainerColor = SurfaceElevated,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    disabledTextColor = Color.White,
    cursorColor = Color.White,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    focusedPlaceholderColor = TextMuted,
    unfocusedPlaceholderColor = TextMuted
)
