package com.game.snake

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun GameBoard(snake: SnakeState, food: Offset, onTap: ((Offset) -> Unit)?, modifier: Modifier = Modifier) {
    Box(modifier = modifier
        .background(Color.Black)
        .pointerInput(Unit) {
            detectTapGestures(onTap = onTap)
        }
    ) {
        // Draw Snake
        snake.body.forEach { segment ->
            Box(
                modifier = Modifier
                    .offset(segment.x.dp, segment.y.dp)
                    .size(10.dp)
                    .background(Color.Green)
            )
        }
        // Draw Food
        Box(
            modifier = Modifier
                .offset(food.x.dp, food.y.dp)
                .size(10.dp)
                .background(Color.Red)
        )
    }
}