package com.game.snake

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun StartButton(onStart: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = onStart) {
            Text(text = "Start Game")
        }
    }
}

@Composable
fun Score(score: Int, modifier: Modifier = Modifier) {
    Text(text = "Score: $score", color = Color.White, modifier = modifier)
}