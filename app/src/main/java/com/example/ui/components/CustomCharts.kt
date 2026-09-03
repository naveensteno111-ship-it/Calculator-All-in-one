package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.YearlyGrowthItem
import com.example.util.AppFormatters
import kotlin.math.max

data class ChartSlice(
    val label: String,
    val value: Double,
    val color: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DonutPieChart(
    slices: List<ChartSlice>,
    modifier: Modifier = Modifier,
    centerTitle: String? = null,
    centerValue: String? = null
) {
    val total = slices.sumOf { it.value }
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(slices) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(170.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(150.dp)) {
                if (total <= 0) {
                    drawCircle(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        style = Stroke(width = 24.dp.toPx())
                    )
                    return@Canvas
                }

                var startAngle = -90f
                val strokeWidth = 24.dp.toPx()
                val diameter = size.minDimension - strokeWidth
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                val arcSize = Size(diameter, diameter)

                slices.forEach { slice ->
                    val sweepAngle = ((slice.value / total) * 360f).toFloat() * animatedProgress.value
                    if (sweepAngle > 0f) {
                        drawArc(
                            color = slice.color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                    }
                    startAngle += sweepAngle
                }
            }

            // Center Text
            if (centerTitle != null || centerValue != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (centerTitle != null) {
                        Text(
                            text = centerTitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (centerValue != null) {
                        Text(
                            text = centerValue,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Legend
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            slices.forEach { slice ->
                val percentage = if (total > 0) (slice.value / total) * 100.0 else 0.0
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(slice.color)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${slice.label} (${AppFormatters.formatPercentage(percentage, 1)})",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun YearlyGrowthBarChart(
    items: List<YearlyGrowthItem>,
    modifier: Modifier = Modifier,
    investedColor: Color = Color(0xFF3B82F6),
    returnsColor: Color = Color(0xFF10B981)
) {
    if (items.isEmpty()) return

    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(items) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    val maxBalance = items.maxOfOrNull { it.balance } ?: 1.0

    Column(modifier = modifier.fillMaxWidth().padding(top = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Growth Projection",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(investedColor))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Invested", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(returnsColor))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Returns", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Canvas Bars
        Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            val count = items.size
            val barGroupWidth = size.width / count
            val barWidth = (barGroupWidth * 0.65f).coerceAtMost(28.dp.toPx())

            items.forEachIndexed { index, item ->
                val xCenter = (index * barGroupWidth) + (barGroupWidth / 2f)
                val left = xCenter - (barWidth / 2f)

                val investedHeight = ((item.invested / maxBalance) * size.height).toFloat() * animatedProgress.value
                val returnsHeight = ((item.returns / maxBalance) * size.height).toFloat() * animatedProgress.value

                // Draw Invested Bar
                drawRect(
                    color = investedColor,
                    topLeft = Offset(left, size.height - investedHeight),
                    size = Size(barWidth, investedHeight)
                )

                // Draw Stacked Returns Bar
                if (returnsHeight > 0) {
                    drawRect(
                        color = returnsColor,
                        topLeft = Offset(left, size.height - investedHeight - returnsHeight),
                        size = Size(barWidth, returnsHeight)
                    )
                }
            }
        }

        // Year labels row
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val step = max(1, items.size / 5)
            items.forEachIndexed { index, item ->
                if (index == 0 || index == items.size - 1 || index % step == 0) {
                    Text(
                        text = "Y${item.year}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
