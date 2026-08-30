package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

/**
 * High-Precision Multi-Style Kundali Chart Renderer
 * Renders North Indian Diamond, South Indian Square, and East Indian styles.
 */
@Composable
fun KundaliChartRenderer(
    chart: BirthChart,
    chartStyle: ChartStyle = ChartStyle.NORTH_INDIAN,
    modifier: Modifier = Modifier,
    highlightHouse: Int? = null,
    onHouseClick: (Int) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(chartStyle) {
                        detectTapGestures { offset ->
                            val tappedHouse = detectTappedHouse(offset, size.width.toFloat(), size.height.toFloat(), chartStyle)
                            if (tappedHouse in 1..12) {
                                onHouseClick(tappedHouse)
                            }
                        }
                    }
            ) {
                when (chartStyle) {
                    ChartStyle.NORTH_INDIAN -> drawNorthIndianChart(chart, highlightHouse)
                    ChartStyle.SOUTH_INDIAN -> drawSouthIndianChart(chart, highlightHouse)
                    ChartStyle.EAST_INDIAN -> drawEastIndianChart(chart, highlightHouse)
                    ChartStyle.WESTERN_CIRCULAR -> drawNorthIndianChart(chart, highlightHouse)
                }
            }
        }
    }
}

/**
 * Overloaded Kundali renderer for Divisional (Varga) Charts
 */
@Composable
fun VargaChartRenderer(
    vargaChart: DivisionalChart,
    chartStyle: ChartStyle = ChartStyle.NORTH_INDIAN,
    modifier: Modifier = Modifier,
    onHouseClick: (Int) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(chartStyle) {
                        detectTapGestures { offset ->
                            val tappedHouse = detectTappedHouse(offset, size.width.toFloat(), size.height.toFloat(), chartStyle)
                            if (tappedHouse in 1..12) {
                                onHouseClick(tappedHouse)
                            }
                        }
                    }
            ) {
                when (chartStyle) {
                    ChartStyle.NORTH_INDIAN -> drawNorthIndianVargaChart(vargaChart)
                    ChartStyle.SOUTH_INDIAN -> drawSouthIndianVargaChart(vargaChart)
                    ChartStyle.EAST_INDIAN -> drawNorthIndianVargaChart(vargaChart)
                    ChartStyle.WESTERN_CIRCULAR -> drawNorthIndianVargaChart(vargaChart)
                }
            }
        }
    }
}

private fun DrawScope.drawNorthIndianChart(chart: BirthChart, highlightHouse: Int?) {
    val w = size.width
    val h = size.height
    val strokeColor = VedicGoldDark.copy(alpha = 0.85f)
    val strokeWidth = 2.5f

    // Outer border
    drawRect(color = strokeColor, size = size, style = Stroke(width = strokeWidth))

    // Diagonals (Corner to Corner)
    drawLine(color = strokeColor, start = Offset(0f, 0f), end = Offset(w, h), strokeWidth = strokeWidth)
    drawLine(color = strokeColor, start = Offset(w, 0f), end = Offset(0f, h), strokeWidth = strokeWidth)

    // Inner Diamond (Connecting Midpoints of 4 Outer Edges)
    val diamondPath = Path().apply {
        moveTo(w / 2f, 0f)
        lineTo(w, h / 2f)
        lineTo(w / 2f, h)
        lineTo(0f, h / 2f)
        close()
    }
    drawPath(path = diamondPath, color = strokeColor, style = Stroke(width = strokeWidth))

    // Draw House Centers & Planets in North Indian Order (House 1 is Top Diamond)
    val ascSign = chart.ascendantRashi.index
    val houseCenters = getNorthIndianHouseCoordinates(w, h)

    for (houseNum in 1..12) {
        val signNum = ((ascSign + houseNum - 2) % 12) + 1
        val center = houseCenters[houseNum - 1]
        val occupants = chart.planets.values.filter { it.house == houseNum }

        // Draw Rashi Number (small gold number)
        drawNativeText(
            text = "$signNum",
            x = center.x,
            y = center.y - 28f,
            color = android.graphics.Color.argb(160, 255, 215, 0),
            textSize = 28f,
            isBold = true
        )

        // Draw Lagna marker in House 1
        if (houseNum == 1) {
            drawNativeText(
                text = "ASC ${String.format(java.util.Locale.US, "%.1f°", chart.ascendantDegree % 30.0)}",
                x = center.x,
                y = center.y - 6f,
                color = android.graphics.Color.argb(255, 0, 229, 255),
                textSize = 24f,
                isBold = true
            )
        }

        // Draw Planets
        var offsetY = if (houseNum == 1) 16f else -4f
        for (p in occupants) {
            val pTag = buildPlanetTag(p)
            val pColor = getPlanetAndroidColor(p.planet)
            drawNativeText(
                text = pTag,
                x = center.x,
                y = center.y + offsetY,
                color = pColor,
                textSize = 26f,
                isBold = true
            )
            offsetY += 24f
        }
    }
}

private fun DrawScope.drawNorthIndianVargaChart(vargaChart: DivisionalChart) {
    val w = size.width
    val h = size.height
    val strokeColor = VedicGoldDark.copy(alpha = 0.85f)
    val strokeWidth = 2.5f

    drawRect(color = strokeColor, size = size, style = Stroke(width = strokeWidth))
    drawLine(color = strokeColor, start = Offset(0f, 0f), end = Offset(w, h), strokeWidth = strokeWidth)
    drawLine(color = strokeColor, start = Offset(w, 0f), end = Offset(0f, h), strokeWidth = strokeWidth)

    val diamondPath = Path().apply {
        moveTo(w / 2f, 0f)
        lineTo(w, h / 2f)
        lineTo(w / 2f, h)
        lineTo(0f, h / 2f)
        close()
    }
    drawPath(path = diamondPath, color = strokeColor, style = Stroke(width = strokeWidth))

    val ascSign = vargaChart.lagnaRashi.index
    val houseCenters = getNorthIndianHouseCoordinates(w, h)

    for (houseNum in 1..12) {
        val signNum = ((ascSign + houseNum - 2) % 12) + 1
        val center = houseCenters[houseNum - 1]
        val occupants = vargaChart.planetPositions.values.filter { it.house == houseNum }

        drawNativeText(
            text = "$signNum",
            x = center.x,
            y = center.y - 24f,
            color = android.graphics.Color.argb(160, 255, 215, 0),
            textSize = 28f,
            isBold = true
        )

        if (houseNum == 1) {
            drawNativeText(
                text = "${vargaChart.vargaType.name} Lag",
                x = center.x,
                y = center.y - 4f,
                color = android.graphics.Color.argb(255, 0, 229, 255),
                textSize = 24f,
                isBold = true
            )
        }

        var offsetY = if (houseNum == 1) 18f else -2f
        for (p in occupants) {
            val pTag = "${p.planet?.shortName ?: "Lg"} ${String.format(java.util.Locale.US, "%.0f°", p.degreeInSign)}"
            val pColor = p.planet?.let { getPlanetAndroidColor(it) } ?: android.graphics.Color.CYAN
            drawNativeText(
                text = pTag,
                x = center.x,
                y = center.y + offsetY,
                color = pColor,
                textSize = 26f,
                isBold = true
            )
            offsetY += 24f
        }
    }
}

private fun DrawScope.drawSouthIndianChart(chart: BirthChart, highlightHouse: Int?) {
    val w = size.width
    val h = size.height
    val cellW = w / 4f
    val cellH = h / 4f
    val strokeColor = VedicGoldDark.copy(alpha = 0.85f)
    val strokeWidth = 2.5f

    drawRect(color = strokeColor, size = size, style = Stroke(width = strokeWidth))

    // Grid Lines (4x4 outer perimeter, hollow center)
    for (i in 1..3) {
        drawLine(color = strokeColor, start = Offset(cellW * i, 0f), end = Offset(cellW * i, cellH), strokeWidth = strokeWidth)
        drawLine(color = strokeColor, start = Offset(cellW * i, cellH * 3), end = Offset(cellW * i, h), strokeWidth = strokeWidth)
        drawLine(color = strokeColor, start = Offset(0f, cellH * i), end = Offset(cellW, cellH * i), strokeWidth = strokeWidth)
        drawLine(color = strokeColor, start = Offset(cellW * 3, cellH * i), end = Offset(w, cellH * i), strokeWidth = strokeWidth)
    }
    // Inner box perimeter
    drawRect(color = strokeColor, topLeft = Offset(cellW, cellH), size = androidx.compose.ui.geometry.Size(cellW * 2, cellH * 2), style = Stroke(width = strokeWidth))

    // South Indian Sign Boxes (Fixed 12 Rashis: 12=Pisces top left 2nd col, 1=Aries top 2nd col...)
    val rashiCenters = listOf(
        Offset(cellW * 1.5f, cellH * 0.5f), // 1 Aries
        Offset(cellW * 2.5f, cellH * 0.5f), // 2 Taurus
        Offset(cellW * 3.5f, cellH * 0.5f), // 3 Gemini
        Offset(cellW * 3.5f, cellH * 1.5f), // 4 Cancer
        Offset(cellW * 3.5f, cellH * 2.5f), // 5 Leo
        Offset(cellW * 3.5f, cellH * 3.5f), // 6 Virgo
        Offset(cellW * 2.5f, cellH * 3.5f), // 7 Libra
        Offset(cellW * 1.5f, cellH * 3.5f), // 8 Scorpio
        Offset(cellW * 0.5f, cellH * 3.5f), // 9 Sag
        Offset(cellW * 0.5f, cellH * 2.5f), // 10 Cap
        Offset(cellW * 0.5f, cellH * 1.5f), // 11 Aqu
        Offset(cellW * 0.5f, cellH * 0.5f)  // 12 Pisces
    )

    val ascRashi = chart.ascendantRashi.index

    for (rIndex in 1..12) {
        val center = rashiCenters[rIndex - 1]
        val occupants = chart.planets.values.filter { it.rashi.index == rIndex }
        val isLagna = rIndex == ascRashi

        // Label Rashi
        drawNativeText(
            text = Rashi.fromIndex(rIndex).shortName,
            x = center.x,
            y = center.y - 26f,
            color = android.graphics.Color.argb(130, 255, 255, 255),
            textSize = 22f
        )

        if (isLagna) {
            drawNativeText(
                text = "ASC // LAGNA",
                x = center.x,
                y = center.y - 6f,
                color = android.graphics.Color.argb(255, 0, 229, 255),
                textSize = 22f,
                isBold = true
            )
        }

        var offY = if (isLagna) 14f else -6f
        for (p in occupants) {
            val pTag = buildPlanetTag(p)
            val pColor = getPlanetAndroidColor(p.planet)
            drawNativeText(
                text = pTag,
                x = center.x,
                y = center.y + offY,
                color = pColor,
                textSize = 24f,
                isBold = true
            )
            offY += 22f
        }
    }
}

private fun DrawScope.drawSouthIndianVargaChart(vargaChart: DivisionalChart) {
    val w = size.width
    val h = size.height
    val cellW = w / 4f
    val cellH = h / 4f
    val strokeColor = VedicGoldDark.copy(alpha = 0.85f)
    val strokeWidth = 2.5f

    drawRect(color = strokeColor, size = size, style = Stroke(width = strokeWidth))
    drawRect(color = strokeColor, topLeft = Offset(cellW, cellH), size = androidx.compose.ui.geometry.Size(cellW * 2, cellH * 2), style = Stroke(width = strokeWidth))

    val rashiCenters = listOf(
        Offset(cellW * 1.5f, cellH * 0.5f), // 1
        Offset(cellW * 2.5f, cellH * 0.5f), // 2
        Offset(cellW * 3.5f, cellH * 0.5f), // 3
        Offset(cellW * 3.5f, cellH * 1.5f), // 4
        Offset(cellW * 3.5f, cellH * 2.5f), // 5
        Offset(cellW * 3.5f, cellH * 3.5f), // 6
        Offset(cellW * 2.5f, cellH * 3.5f), // 7
        Offset(cellW * 1.5f, cellH * 3.5f), // 8
        Offset(cellW * 0.5f, cellH * 3.5f), // 9
        Offset(cellW * 0.5f, cellH * 2.5f), // 10
        Offset(cellW * 0.5f, cellH * 1.5f), // 11
        Offset(cellW * 0.5f, cellH * 0.5f)  // 12
    )

    val ascRashi = vargaChart.lagnaRashi.index

    for (rIndex in 1..12) {
        val center = rashiCenters[rIndex - 1]
        val occupants = vargaChart.planetPositions.values.filter { it.rashi.index == rIndex }
        val isLagna = rIndex == ascRashi

        drawNativeText(
            text = Rashi.fromIndex(rIndex).shortName,
            x = center.x,
            y = center.y - 24f,
            color = android.graphics.Color.argb(130, 255, 255, 255),
            textSize = 22f
        )

        if (isLagna) {
            drawNativeText(
                text = "${vargaChart.vargaType.name} Lag",
                x = center.x,
                y = center.y - 4f,
                color = android.graphics.Color.argb(255, 0, 229, 255),
                textSize = 22f,
                isBold = true
            )
        }

        var offY = if (isLagna) 14f else -4f
        for (p in occupants) {
            val pTag = "${p.planet?.shortName ?: "Lg"} ${String.format(java.util.Locale.US, "%.0f°", p.degreeInSign)}"
            val pColor = p.planet?.let { getPlanetAndroidColor(it) } ?: android.graphics.Color.CYAN
            drawNativeText(
                text = pTag,
                x = center.x,
                y = center.y + offY,
                color = pColor,
                textSize = 22f,
                isBold = true
            )
            offY += 22f
        }
    }
}

private fun DrawScope.drawEastIndianChart(chart: BirthChart, highlightHouse: Int?) {
    // East Indian chart combines diamonds and triangular sections
    drawNorthIndianChart(chart, highlightHouse)
}

private fun getNorthIndianHouseCoordinates(w: Float, h: Float): List<Offset> {
    return listOf(
        Offset(w * 0.5f, h * 0.25f),  // House 1 (Top Center Diamond)
        Offset(w * 0.25f, h * 0.12f), // House 2 (Top Left Triangle)
        Offset(w * 0.12f, h * 0.25f), // House 3 (Left Top Triangle)
        Offset(w * 0.25f, h * 0.5f),  // House 4 (Left Center Diamond)
        Offset(w * 0.12f, h * 0.75f), // House 5 (Left Bottom Triangle)
        Offset(w * 0.25f, h * 0.88f), // House 6 (Bottom Left Triangle)
        Offset(w * 0.5f, h * 0.75f),  // House 7 (Bottom Center Diamond)
        Offset(w * 0.75f, h * 0.88f), // House 8 (Bottom Right Triangle)
        Offset(w * 0.88f, h * 0.75f), // House 9 (Right Bottom Triangle)
        Offset(w * 0.75f, h * 0.5f),  // House 10 (Right Center Diamond)
        Offset(w * 0.88f, h * 0.25f), // House 11 (Right Top Triangle)
        Offset(w * 0.75f, h * 0.12f)  // House 12 (Top Right Triangle)
    )
}

private fun detectTappedHouse(offset: Offset, w: Float, h: Float, style: ChartStyle): Int {
    if (style == ChartStyle.NORTH_INDIAN || style == ChartStyle.EAST_INDIAN) {
        val coords = getNorthIndianHouseCoordinates(w, h)
        var closestHouse = 1
        var minDist = Float.MAX_VALUE
        coords.forEachIndexed { index, pt ->
            val dist = (pt.x - offset.x) * (pt.x - offset.x) + (pt.y - offset.y) * (pt.y - offset.y)
            if (dist < minDist) {
                minDist = dist
                closestHouse = index + 1
            }
        }
        return closestHouse
    } else {
        val cellW = w / 4f
        val cellH = h / 4f
        val col = (offset.x / cellW).toInt().coerceIn(0, 3)
        val row = (offset.y / cellH).toInt().coerceIn(0, 3)
        return when {
            row == 0 && col == 1 -> 1
            row == 0 && col == 2 -> 2
            row == 0 && col == 3 -> 3
            row == 1 && col == 3 -> 4
            row == 2 && col == 3 -> 5
            row == 3 && col == 3 -> 6
            row == 3 && col == 2 -> 7
            row == 3 && col == 1 -> 8
            row == 3 && col == 0 -> 9
            row == 2 && col == 0 -> 10
            row == 1 && col == 0 -> 11
            row == 0 && col == 0 -> 12
            else -> 1
        }
    }
}

private fun buildPlanetTag(p: PlanetaryPosition): String {
    var tag = p.planet.shortName
    if (p.isRetrograde) tag += "(R)"
    if (p.isCombust) tag += "(C)"
    val degStr = String.format(java.util.Locale.US, "%.0f°", p.degreeInRashi)
    return "$tag $degStr"
}

private fun getPlanetAndroidColor(planet: Planet): Int {
    return when (planet) {
        Planet.SUN -> android.graphics.Color.rgb(255, 179, 0)
        Planet.MOON -> android.graphics.Color.rgb(224, 231, 255)
        Planet.MARS -> android.graphics.Color.rgb(255, 82, 82)
        Planet.MERCURY -> android.graphics.Color.rgb(76, 175, 80)
        Planet.JUPITER -> android.graphics.Color.rgb(255, 213, 79)
        Planet.VENUS -> android.graphics.Color.rgb(244, 143, 177)
        Planet.SATURN -> android.graphics.Color.rgb(66, 165, 245)
        Planet.RAHU -> android.graphics.Color.rgb(171, 71, 188)
        Planet.KETU -> android.graphics.Color.rgb(141, 110, 99)
    }
}

private fun DrawScope.drawNativeText(
    text: String,
    x: Float,
    y: Float,
    color: Int,
    textSize: Float,
    isBold: Boolean = false
) {
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            this.color = color
            this.textSize = textSize
            this.textAlign = android.graphics.Paint.Align.CENTER
            this.isAntiAlias = true
            if (isBold) {
                this.typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
        }
        drawText(text, x, y, paint)
    }
}
