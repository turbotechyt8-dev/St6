package com.strengthtracker.ui.today

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.strengthtracker.repository
import com.strengthtracker.ui.theme.CharcoalBorder
import com.strengthtracker.ui.theme.CharcoalRaised
import com.strengthtracker.ui.theme.ElectricBlue
import com.strengthtracker.ui.theme.ElectricBlueSoft
import com.strengthtracker.ui.theme.TextSecondary

@Composable
fun TodayScreen(onOpenSession: (Long) -> Unit) {
    val repo = LocalContext.current.repository()
    val vm: TodayViewModel = viewModel { TodayViewModel(repo) }
    val state by vm.state.collectAsState()

    if (!state.loaded) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Today", style = MaterialTheme.typography.headlineMedium)
        WeekStrip(state.week)
        TodayCard(state = state, onAction = { vm.openToday(onOpenSession) })
    }
}

@Composable
private fun WeekStrip(week: List<DayChip>) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        week.forEach { chip ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = chip.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (chip.isToday) ElectricBlue else TextSecondary
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(chip.status.dotColor())
                        .border(1.dp, CharcoalBorder, CircleShape)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = chip.status.label(),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun TodayCard(state: TodayUiState, onAction: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CharcoalRaised)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.isRest) {
                Text("Rest day", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Nothing is planned today. Recovery is part of the program.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            } else {
                Text(state.title, style = MaterialTheme.typography.titleLarge)
                state.exerciseNames.forEach { name ->
                    Text(name, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(Modifier.height(4.dp))
                Button(onClick = onAction, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        when {
                            state.sessionFinished -> "View summary"
                            state.sessionStarted -> "Continue workout"
                            else -> "Start workout"
                        }
                    )
                }
            }
        }
    }
}

private fun DayStatus.dotColor(): Color = when (this) {
    DayStatus.DONE -> ElectricBlue
    DayStatus.IN_PROGRESS -> ElectricBlueSoft
    DayStatus.PLANNED -> Color.Transparent
    DayStatus.MISSED -> Color.Transparent
    DayStatus.REST -> Color.Transparent
}

private fun DayStatus.label(): String = when (this) {
    DayStatus.DONE -> "Done"
    DayStatus.IN_PROGRESS -> "Active"
    DayStatus.PLANNED -> "Planned"
    DayStatus.MISSED -> "Missed"
    DayStatus.REST -> "Rest"
}
