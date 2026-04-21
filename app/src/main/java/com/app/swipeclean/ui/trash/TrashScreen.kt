package com.app.swipeclean.ui.trash

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.app.swipeclean.ui.components.BrutalButton
import com.app.swipeclean.ui.theme.BrutalBlack
import com.app.swipeclean.ui.theme.BrutalCream
import com.app.swipeclean.ui.theme.BrutalRed

@Composable
fun TrashScreen(
    onBack: () -> Unit,
    vm: TrashViewModel = hiltViewModel()
) {

    val state by vm.state.collectAsStateWithLifecycle()
    Column (
        modifier = Modifier.fillMaxSize().background(BrutalCream).padding(top = 52.dp, start = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        //Header row
        Row (
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text("TRASH", style = MaterialTheme.typography.displayLarge)
            Box(
                Modifier.background(BrutalRed).border(2.dp, BrutalBlack)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("${state.entries.size} ITEMS",
                    style = MaterialTheme.typography.bodyMedium, color = Color.White)
            }
        }

        Text("AUTO-DELETES IN 30 DAYS",
            style = MaterialTheme.typography.bodyMedium,
            color = BrutalBlack.copy(alpha = 0.4f))
        HorizontalDivider(thickness = 2.dp, color = BrutalBlack)

        //3 column thumbnail grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(state.entries, key = { it.uri }) { entry ->
                Box (
                    modifier = Modifier.aspectRatio(1f).border(2.dp, BrutalBlack)
                ) {
                    AsyncImage(
                        model = entry.uri,
                        contentDescription = entry.displayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    //Red X Badge
                    Box(
                        Modifier.align(Alignment.TopEnd).padding(2.dp).size(16.dp).background(BrutalRed)
                            .border(1.dp, Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✕", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // Action buttons
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BrutalButton("RESTORE ALL", { vm.restoreAll() },
                Modifier.weight(1f), Color.White, BrutalBlack)
            BrutalButton("DELETE NOW", { vm.deleteAll() },
                Modifier.weight(1f), BrutalRed, Color.White)
        }
    }
}