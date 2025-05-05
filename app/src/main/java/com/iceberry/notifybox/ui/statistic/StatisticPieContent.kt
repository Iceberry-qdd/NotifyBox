/*
 * Copyright (c) 2025 Iceberry-qdd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iceberry.notifybox.ui.statistic

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.iceberry.notifybox.ui.list.AppIcon
import com.iceberry.notifybox.ui.statistic.chart.PieChart
import com.iceberry.notifybox.ui.theme.COLOR_PALETTE
import com.iceberry.notifybox.ui.theme.NBApplicationTheme

@Composable
fun StatisticPieContent(
    modifier: Modifier = Modifier,
    uiState: PieChartUiState
) {
    LazyColumn(
        modifier = modifier
    ) {
        if (!uiState.isSuccess) {
            item {
                Box(
                    modifier = Modifier.height(250.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "暂无数据",
                        style = MaterialTheme.typography.bodyMedium.merge(color = MaterialTheme.colorScheme.outline)
                    )
                }
            }
            return@LazyColumn
        }

        val pieChartData = uiState.pieChartData
        item {
            val pieData by produceState(initialValue = INIT_PIE_DATA) {
                value = StatisticViewModel.handlePieData(pieChartData)
            }

            PieChart(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .height(250.dp),
                data = pieData,
                onRest = {},
                onRelease = {},
                update = {}
            )
        }

        items(
            count = pieChartData.size,
            key = { i -> pieChartData[i].label }
        ) { i ->
            StatisticItem(
                modifier = Modifier,
                state = rememberStatisticItemState(
                    progress = pieChartData[i].dest,
                    title = pieChartData[i].label,
                    value = "${pieChartData[i].value}",
                    indicatorColor = Color(COLOR_PALETTE[i % COLOR_PALETTE.size]),
                    appIcon = pieChartData[i].appIcon
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticItem(
    modifier: Modifier = Modifier,
    state: StatisticItemState
) {
    var progress by remember { mutableFloatStateOf(state.progress) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .clickable(enabled = true, onClick = {})
                .padding(12.dp),
        ) {
            AppIcon(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(percent = 25))
                    .aspectRatio(1f),
                icon = state.appIcon
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.title,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        val percentText by remember {
                            derivedStateOf {
                                if (state.progress < 0.01) "< 1%" else "%.1f%%".format(state.progress * 100)
                            }
                        }
                        Text(
                            text = percentText,
                            style = MaterialTheme.typography.labelMedium.merge(color = MaterialTheme.colorScheme.outline),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Text(
                        text = state.value,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = { animatedProgress },
                    color = state.indicatorColor,
                    strokeCap = StrokeCap.Round,
                    gapSize = 4.dp,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun StatisticItemLightPreview() {
    NBApplicationTheme {
        StatisticItem(
            state = rememberStatisticItemState(
                progress = 0.357f,
                title = "通知盒子",
                value = "395",
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun StatisticScreenLightPreview() {
    NBApplicationTheme {
        StatisticPieContent(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            uiState = PieChartUiState(
                isSuccess = true,
                pieChartData = listOf(INIT_PIE_CHART_DATA)
            )
        )
    }
}