package com.example.bargraph

import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bargraph.ui.theme.BarGraphTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val database = AppDatabase.getDatabase(this)
            val repository = ProductRepository(database.productPurchaseDao())
            val factory = ProductViewModelFactory(repository)
            val viewModel: ProductViewModel = viewModel(factory = factory)


            val weeklyData by viewModel.weeklyPurchases.collectAsState(initial = emptyList())
            LaunchedEffect(key1 = "key1") {
                viewModel.addPurchaseData()
                viewModel.loadWeeklyData()
            }

            BarGraphTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BarGraph(
                        data = weeklyData,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BarGraph(data: List<WeeklyPurchase>, modifier: Modifier = Modifier) {
    val maxQuantity = data.maxOfOrNull { it.quantity } ?: 0
    val dayFormatter = DateTimeFormatter.ofPattern("EEE")

    Canvas(modifier = modifier.fillMaxSize()
        .background(Color(0xFF1C1B2D))
        .padding(16.dp)) {

        // Getting the number of items present in the list
        val totalBars = data.size

        // Added spacing depending on the width of the screen and number of bars (items present in the list)
        val spacing = 0.2f * size.width / totalBars

        /*
            The number of spaces between the bars will be one less than the number of bars so
            we subtract one from the total bars and then multiplying the spacing which we calculated
            earlier with the number of bars minus 1 after that we subtract this value which is number of total space
            taken by the spacing from the total width we have and then Divides the remaining width equally among all bars
            determining the width of each individual bar.
         */
        val barWidth = (size.width - (spacing * (totalBars - 1))) / totalBars
        val graphHeight = size.height * 0.8f
        val maxBarHeight = graphHeight - 100

        // Positioning the text on the top left , done by randomly checking the x and y axis values
        drawContext.canvas.nativeCanvas.apply {
            drawText(
                "Weekly super stats",
                20f,
                160f,
                Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 60f
                    isFakeBoldText = true
                }
            )
        }

        data.forEachIndexed { index, purchase ->
            val barHeight = (purchase.quantity / maxQuantity.toFloat()) * maxBarHeight
            val isToday = purchase.date == LocalDate.now()
            val dayLabel = if (isToday) "Today" else purchase.date.format(dayFormatter)


            Log.d("BarGraph", "Date: ${purchase.date}, Quantity: ${purchase.quantity}, X Position: ${index * (barWidth + spacing)}, Bar Height: $barHeight")

            // Designing the rounder corner bars
            drawRoundRect(
                color = if (isToday) Color(0xFF6A0DAD) else Color(0xFFBB86FC).copy(alpha = 0.7f),
                topLeft = Offset(
                    x = index * (barWidth + spacing),
                    y = size.height - barHeight - 50
                ),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2),
                style = if (isToday) Stroke(width = 4f) else Fill
            )

            // Adding the text at the top of the bar
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "${purchase.quantity}",
                    index * (barWidth + spacing) + barWidth / 2,
                    size.height - barHeight - 70,
                    Paint().apply {
                        color = android.graphics.Color.WHITE
                        textAlign = Paint.Align.CENTER
                        textSize = 30f
                    }
                )
            }

            // Adding the values at x - Axis
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    dayLabel,
                    index * (barWidth + spacing) + barWidth / 2,
                    size.height - 20,
                    Paint().apply {
                        color = if (isToday) android.graphics.Color.WHITE else android.graphics.Color.GRAY
                        textAlign = Paint.Align.CENTER
                        textSize = 25f
                        isFakeBoldText = isToday
                    }
                )
            }
        }


        // Adding Grid lines
        val gridLineCount = 4
        val gridLineSpacing = maxBarHeight / gridLineCount
        for (i in 0..gridLineCount) {
            val y = size.height - (i * gridLineSpacing) - 50
            drawLine(
                color = Color.Gray,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
        }
    }
}


