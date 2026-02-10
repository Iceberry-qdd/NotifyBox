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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iceberry.notifybox.ui.list.AppIcon
import com.iceberry.notifybox.ui.list.NotifyVO
import com.iceberry.notifybox.ui.statistic.chart.BarChart
import com.iceberry.notifybox.ui.statistic.chart.BarChartUiState
import com.iceberry.notifybox.ui.statistic.chart.INIT_BAR_CHART_DATA
import com.iceberry.notifybox.ui.statistic.chart.INIT_PIE_CHART_DATA
import com.iceberry.notifybox.ui.statistic.chart.PieChart
import com.iceberry.notifybox.ui.statistic.chart.PieChartUiState
import com.iceberry.notifybox.ui.statistic.chart.TimeRangeFilter
import com.iceberry.notifybox.ui.statistic.chart.handleBarData
import com.iceberry.notifybox.ui.statistic.chart.handlePieData
import com.iceberry.notifybox.ui.theme.COLOR_PALETTE
import com.iceberry.notifybox.ui.theme.NBApplicationTheme
import com.iceberry.notifybox.ui.theme.convertToRGB

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticContent(
    modifier: Modifier = Modifier,
    uiState: StatisticUiState,
    lazyListState: LazyListState,
    onToggleFilter: (TimeRangeFilter) -> Unit,
    onNavToDetails: (NotifyVO) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val mbsViewModel: MBSViewModel = hiltViewModel()

    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        item {
            DateFilterChips(
                modifier = Modifier
                    .fillMaxWidth(),
                selectedChip = uiState.selectedFilter,
                onToggle = onToggleFilter
            )
        }

        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight()
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            return@LazyColumn
        }

        if (uiState.pieChartUiState.pieChartData.isEmpty() || uiState.barChartUiState.barChartData.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight(),
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

        if (!uiState.pieChartUiState.isSuccess || !uiState.barChartUiState.isSuccess) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "数据加载失败",
                        style = MaterialTheme.typography.bodyMedium.merge(color = MaterialTheme.colorScheme.outline)
                    )
                }
            }
            return@LazyColumn
        }

        item {
            val colors = listOf(convertToRGB(MaterialTheme.colorScheme.primary))
            val pieData = handleBarData(uiState.barChartUiState.barChartData, colors)

            BarChart(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .height(150.dp),
                data = pieData,
                dimension = uiState.selectedFilter.dimension,
                onRest = {},
                onRelease = {},
                update = {}
            )
        }

        val pieChartData = uiState.pieChartUiState.pieChartData
        item {
            val pieData = handlePieData(pieChartData)

            PieChart(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .height(250.dp),
                data = pieData,
                onClickEntry = {
                    showBottomSheet = true
                    mbsViewModel.setPagerParams(pieChartData[it].packageName, uiState.selectedFilter)
                },
                onRest = {},
                onRelease = {},
                update = {}
            )
        }

        items(
            count = pieChartData.size,
            key = { i -> pieChartData[i].packageName }
        ) { i ->
            StatisticItem(
                modifier = Modifier,
                state = rememberStatisticItemState(
                    progress = pieChartData[i].dest,
                    title = pieChartData[i].label?: pieChartData[i].packageName,
                    value = "${pieChartData[i].value.toLong()}",
                    indicatorColor = Color(COLOR_PALETTE[i % COLOR_PALETTE.size]),
                    appIcon = pieChartData[i].appIcon,
                ),
                onClick = {
                    showBottomSheet = true
                    mbsViewModel.setPagerParams(pieChartData[i].packageName, uiState.selectedFilter)
                }
            )
        }
    }

    if (showBottomSheet) {
        val mbsState: StatisticMBSState = rememberStatisticMBSState(
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = false
            )
        )

        StatisticModelBottomSheet(
            modifier = Modifier.fillMaxWidth(),
            state = mbsState,
            onDismissRequest = { showBottomSheet = false },
            onClickItem = {
                showBottomSheet = false
                onNavToDetails(it)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticItem(
    modifier: Modifier = Modifier,
    state: StatisticItemState,
    onClick: () -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .clickable(enabled = true, onClickLabel = "查看该组全部条目", role = Role.ValuePicker, onClick = onClick)
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

                        val percentText = if (state.progress < 0.01) "< 1%" else "%.1f%%".format(state.progress * 100)
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

@Composable
private fun DateFilterChips(
    modifier: Modifier = Modifier,
    selectedChip: TimeRangeFilter,
    onToggle: (TimeRangeFilter) -> Unit
) {
    FlowRow(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalArrangement = Arrangement.Center
    ) {
        TimeRangeFilter.entries.filterNot { it == TimeRangeFilter.CUSTOM }.forEach {
            val selected = selectedChip == it
            val isCustomFilter by remember {
                derivedStateOf { it == TimeRangeFilter.CUSTOM }
            }
            FilterChip(
                selected = selected,
                onClick = { onToggle(it) },
                label = {
                    Text(
                        text = it.label,
                        maxLines = 1,
                        style = if (selected) {
                            MaterialTheme.typography.bodyMedium.merge(color = MaterialTheme.colorScheme.primary)
                        } else {
                            MaterialTheme.typography.bodyMedium
                        }
                    )
                },
                leadingIcon = {
                    if (selected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "已选中${it.label}",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                trailingIcon = {
                    if (isCustomFilter) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "清除筛选条件",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                shape = RoundedCornerShape(100),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = false,
                    selected = selected,
                    borderWidth = 0.dp
                ),
                colors = if (selected) {
                    FilterChipDefaults.filterChipColors()
                } else {
                    FilterChipDefaults.filterChipColors().copy(containerColor = MaterialTheme.colorScheme.surface)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun StatisticItemPreview() {
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
private fun StatisticScreenPreview() {
    NBApplicationTheme {
        StatisticContent(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            uiState = StatisticUiState(
                isLoading = false,
                pieChartUiState = PieChartUiState(
                    isSuccess = true,
                    pieChartData = listOf(INIT_PIE_CHART_DATA)
                ),
                barChartUiState = BarChartUiState(
                    isSuccess = true,
                    barChartData = listOf(INIT_BAR_CHART_DATA)
                )
            ),
            lazyListState = rememberLazyListState(),
            onToggleFilter = {},
            onNavToDetails = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun DateFilterChipsPreview() {
    NBApplicationTheme {
        DateFilterChips(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.surface),
            selectedChip = TimeRangeFilter.ALL,
            onToggle = {}
        )
    }
}