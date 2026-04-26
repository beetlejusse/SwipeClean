package com.app.swipeclean.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.swipeclean.data.local.DailyFreed
import com.app.swipeclean.ui.components.BrutalCard
import com.app.swipeclean.ui.components.BrutalStatCard
import com.app.swipeclean.ui.components.StreakBanner
import com.app.swipeclean.ui.theme.*

@Composable
fun StatScreen(onBack: () -> Unit, vm: StatsViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    LazyColumn (
        modifier = Modifier.fillMaxSize().background(BrutalCream)
            .padding(top = 52.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("STATS", style = MaterialTheme.typography.displayLarge)
            HorizontalDivider(thickness = 2.dp, color = BrutalBlack)
        }
        item {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BrutalStatCard(
                    value = "${state.totalDeleted}",
                    label = "TOTAL DELETED",
                    modifier = Modifier.weight(1f)
                )
                BrutalStatCard(
                    "%.1f GB".format(state.totalFreedMb / 1024f),
                    label = "STORAGE FREED",
                    background = BrutalYellow,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item { StreakBanner(state.streak) }

        //Bar Chart = 30-day freed storage
        item {
            Text("30-DAY HISTORY", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            FreedStorageChart(data = state.dailyFreed)
        }

        // Session history list
        items(state.session , key = { it.id }) { session ->
            BrutalCard(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(session.date, style = MaterialTheme.typography.bodyMedium)
                    Text("${session.photosDeleted} deleted • "+
                            "%.1f MB freed".format(session.bytesFreed/1_048_576f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrutalBlack.copy(alpha = 0.6f))
                }
            }
        }
        item { Spacer(Modifier.height(32.dp)) }
    }
}

// Simple bar chart: each bar is a Box with height proportional to value
@Composable
fun FreedStorageChart(data: List<DailyFreed>) {
    if (data.isEmpty()) return
    val maxVal = data.maxOf { it.total }.coerceAtLeast(1L)
    Row(
        modifier = Modifier.fillMaxWidth().height(80.dp)
            .border(2.dp, BrutalBlack).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        data.take(30).forEach { day ->
            val fraction = day.total.toFloat() / maxVal
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(fraction.coerceAtLeast(0.02f))
                    .background(BrutalYellow)
                    .border(1.dp, BrutalBlack)
            )
        }
    }
}