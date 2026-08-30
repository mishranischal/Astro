package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

/**
 * Renders North Indian Diamond Chart (12 Bhavas) with authentic styling.
 */
@Composable
fun NorthIndianChartCanvas(
    chart: BirthChart,
    selectedVargaPositions: Map<Planet, VargaPosition>? = null,
    vargaLagnaRashi: Rashi? = null,
    modifier: Modifier = Modifier,
    onHouseClick: ((Int) -> Unit)? = null
) {
    val lagnaRashi = vargaLagnaRashi ?: chart.ascendantRashi
    val goldColor = VedicGold
    val gridColor = Color(0xFFD4AF37)
    val bgColor = CosmicCardSurface
    val textPrimary = TextWhitePrimary
    val textSecondary = TextSilverSecondary
    val saffronColor = VedicSaffron

    // Map planets to houses 1..12
    val housePlanetsMap = (1..12).associateWith { mutableListOf<String>() }

    if (selectedVargaPositions != null) {
        for ((planet, vPos) in selectedVargaPositions) {
            val shortText = buildString {
                append(planet.shortName)
                if (chart.planets[planet]?.isRetrograde == true) append("(R)")
                if (chart.planets[planet]?.isCombust == true) append("*")
            }
            housePlanetsMap[vPos.house]?.add(shortText)
        }
    } else {
        for ((planet, pos) in chart.planets) {
            val shortText = buildString {
                append(planet.shortName)
                if (pos.isRetrograde) append("(R)")
                if (pos.isCombust) append("*")
            }
            housePlanetsMap[pos.house]?.add(shortText)
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Draw Outer Square
            drawRect(
                color = gridColor,
                topLeft = Offset(0f, 0f),
                size = size,
                style = Stroke(width = 3.dp.toPx())
            )

            // 2. Draw Diagonal Crosses (X)
            drawLine(gridColor, Offset(0f, 0f), Offset(w, h), strokeWidth = 2.dp.toPx())
            drawLine(gridColor, Offset(w, 0f), Offset(0f, h), strokeWidth = 2.dp.toPx())

            // 3. Draw Inner Diamond (Rhombus)
            val diamondPath = Path().apply {
                moveTo(w / 2f, 0f)
                lineTo(w, h / 2f)
                lineTo(w / 2f, h)
                lineTo(0f, h / 2f)
                close()
            }
            drawPath(diamondPath, color = gridColor, style = Stroke(width = 2.dp.toPx()))

            // House Centers for Text Placement (North Indian fixed house positions)
            val houseCenters = listOf(
                Offset(w * 0.50f, h * 0.22f), // House 1 (Top Center Diamond)
                Offset(w * 0.25f, h * 0.12f), // House 2 (Top Left Triangle)
                Offset(w * 0.12f, h * 0.25f), // House 3 (Left Top Triangle)
                Offset(w * 0.25f, h * 0.50f), // House 4 (Left Center Diamond)
                Offset(w * 0.12f, h * 0.75f), // House 5 (Left Bottom Triangle)
                Offset(w * 0.25f, h * 0.88f), // House 6 (Bottom Left Triangle)
                Offset(w * 0.50f, h * 0.78f), // House 7 (Bottom Center Diamond)
                Offset(w * 0.75f, h * 0.88f), // House 8 (Bottom Right Triangle)
                Offset(w * 0.88f, h * 0.75f), // House 9 (Right Bottom Triangle)
                Offset(w * 0.75f, h * 0.50f), // House 10 (Right Center Diamond)
                Offset(w * 0.88f, h * 0.25f), // House 11 (Right Top Triangle)
                Offset(w * 0.75f, h * 0.12f)  // House 12 (Top Right Triangle)
            )

            // Draw Rashi Numbers & Grahas for each house
            for (hIdx in 1..12) {
                val center = houseCenters[hIdx - 1]
                val signIndex = ((lagnaRashi.index + hIdx - 2) % 12) + 1
                val rashi = Rashi.fromIndex(signIndex)

                // Sign Number Tag
                val paintSign = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#F59E0B")
                    textSize = 28f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                }
                drawContext.canvas.nativeCanvas.drawText(
                    "${rashi.index}",
                    center.x,
                    center.y - 18f,
                    paintSign
                )

                // Ascendant / Lagna label in House 1
                if (hIdx == 1) {
                    val paintAsc = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#FFD700")
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    drawContext.canvas.nativeCanvas.drawText("ASC", center.x, center.y - 42f, paintAsc)
                }

                // Planets in this House
                val occupants = housePlanetsMap[hIdx] ?: emptyList()
                if (occupants.isNotEmpty()) {
                    val paintPlanet = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#FFFFFF")
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    val planetLines = occupants.chunked(2)
                    planetLines.forEachIndexed { lineIdx, chunk ->
                        drawContext.canvas.nativeCanvas.drawText(
                            chunk.joinToString(" "),
                            center.x,
                            center.y + 12f + (lineIdx * 24f),
                            paintPlanet
                        )
                    }
                }
            }
        }
    }
}

/**
 * Renders South Indian Square Chart (Fixed Zodiac) with authentic styling.
 */
@Composable
fun SouthIndianChartCanvas(
    chart: BirthChart,
    selectedVargaPositions: Map<Planet, VargaPosition>? = null,
    vargaLagnaRashi: Rashi? = null,
    modifier: Modifier = Modifier
) {
    val lagnaRashi = vargaLagnaRashi ?: chart.ascendantRashi
    val gridColor = Color(0xFFD4AF37)
    val bgColor = CosmicCardSurface

    // Fixed South Indian Rashi layout 4x4 perimeter (Indices 1 to 12)
    // Row 0: Pisces(12), Aries(1), Taurus(2), Gemini(3)
    // Row 1: Aquarius(11), [Center Box], Cancer(4)
    // Row 2: Capricorn(10), [Center Box], Leo(5)
    // Row 3: Sagittarius(9), Scorpio(8), Libra(7), Virgo(6)

    // Map Rashi to Planets occupying it
    val rashiPlanetsMap = (1..12).associateWith { mutableListOf<String>() }
    if (selectedVargaPositions != null) {
        for ((planet, vPos) in selectedVargaPositions) {
            val shortText = buildString {
                append(planet.shortName)
                if (chart.planets[planet]?.isRetrograde == true) append("(R)")
                if (chart.planets[planet]?.isCombust == true) append("*")
            }
            rashiPlanetsMap[vPos.rashi.index]?.add(shortText)
        }
    } else {
        for ((planet, pos) in chart.planets) {
            val shortText = buildString {
                append(planet.shortName)
                if (pos.isRetrograde) append("(R)")
                if (pos.isCombust) append("*")
            }
            rashiPlanetsMap[pos.rashi.index]?.add(shortText)
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cellW = w / 4f
            val cellH = h / 4f

            // Draw outer border
            drawRect(color = gridColor, topLeft = Offset(0f, 0f), size = size, style = Stroke(3.dp.toPx()))

            // Vertical 4 columns
            for (c in 1..3) {
                drawLine(gridColor, Offset(c * cellW, 0f), Offset(c * cellW, h), 2.dp.toPx())
            }
            // Horizontal 4 rows
            for (r in 1..3) {
                drawLine(gridColor, Offset(0f, r * cellH), Offset(w, r * cellH), 2.dp.toPx())
            }

            // Clear Center 2x2 area
            drawRect(
                color = CosmicMidnightSurface,
                topLeft = Offset(cellW, cellH),
                size = androidx.compose.ui.geometry.Size(cellW * 2, cellH * 2)
            )
            drawRect(
                color = gridColor,
                topLeft = Offset(cellW, cellH),
                size = androidx.compose.ui.geometry.Size(cellW * 2, cellH * 2),
                style = Stroke(2.dp.toPx())
            )

            // Center Label
            val paintCenter = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#FFD700")
                textSize = 34f
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = true
            }
            drawContext.canvas.nativeCanvas.drawText("JANMA KUNDALI", w / 2f, h / 2f - 10f, paintCenter)

            val paintSub = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#94A3B8")
                textSize = 22f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            drawContext.canvas.nativeCanvas.drawText("Lagna: ${lagnaRashi.englishName}", w / 2f, h / 2f + 25f, paintSub)

            // Rashi Cell placement coordinates in (col, row)
            val rashiCells = mapOf(
                12 to Pair(0, 0), // Pisces
                1 to Pair(1, 0),  // Aries
                2 to Pair(2, 0),  // Taurus
                3 to Pair(3, 0),  // Gemini
                4 to Pair(3, 1),  // Cancer
                5 to Pair(3, 2),  // Leo
                6 to Pair(3, 3),  // Virgo
                7 to Pair(2, 3),  // Libra
                8 to Pair(1, 3),  // Scorpio
                9 to Pair(0, 3),  // Sagittarius
                10 to Pair(0, 2), // Capricorn
                11 to Pair(0, 1)  // Aquarius
            )

            for ((rIndex, cell) in rashiCells) {
                val rashi = Rashi.fromIndex(rIndex)
                val cX = cell.first * cellW + (cellW / 2f)
                val cY = cell.second * cellH + (cellH / 2f)

                // If Lagna is in this sign, draw ASC indicator & diagonal slash
                if (rashi == lagnaRashi) {
                    val pAsc = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#EF4444")
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.RIGHT
                        isFakeBoldText = true
                    }
                    drawContext.canvas.nativeCanvas.drawText("ASC", (cell.first + 1) * cellW - 10f, cell.second * cellH + 28f, pAsc)

                    drawLine(
                        color = Color(0xFFEF4444),
                        start = Offset(cell.first * cellW, cell.second * cellH),
                        end = Offset((cell.first + 1) * cellW, (cell.second + 1) * cellH),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }

                // Sign Name Header
                val pSign = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#F59E0B")
                    textSize = 20f
                    textAlign = android.graphics.Paint.Align.LEFT
                    isFakeBoldText = true
                }
                drawContext.canvas.nativeCanvas.drawText(rashi.shortName, cell.first * cellW + 10f, cell.second * cellH + 24f, pSign)

                // Planets in Sign
                val occupants = rashiPlanetsMap[rIndex] ?: emptyList()
                if (occupants.isNotEmpty()) {
                    val pGraha = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#FFFFFF")
                        textSize = 22f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    occupants.forEachIndexed { idx, str ->
                        drawContext.canvas.nativeCanvas.drawText(
                            str,
                            cX,
                            cY - 5f + (idx * 22f),
                            pGraha
                        )
                    }
                }
            }
        }
    }
}
