package com.game.snake

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.game.snake.ui.theme.SnakeTheme
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    private var snakeState by mutableStateOf(SnakeState.initialize(10, Offset(50f, 50f), Direction.RIGHT))
    private var food by mutableStateOf(generateFood(snakeState.body))
    private var score by mutableStateOf(0)
    private var isGameRunning by mutableStateOf(false)
    private var isGameOver by mutableStateOf(false)
    private var screenWidth = 0
    private var screenHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels
        // Generate food after screen dimensions are set
        food = generateFood(snakeState.body)
        setContent {
            SnakeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (!isGameRunning) {
                            StartButton(onStart = { startGame() })
                        } else {
                            GameBoard(
                                snake = snakeState,
                                food = food,
                                onTap = { position ->
                                    val head = snakeState.body.first()
                                    val direction = snakeState.direction
                                    Log.d("~~~", "head: $head, direction: $direction, position: $position, thread: ${Thread.currentThread().name}")
                                    val newDirection = direction.fromTapPosition(position, head)
                                    newDirection?.let { snakeState = snakeState.copy(direction = it) }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                            Score(score = score, modifier = Modifier.align(Alignment.TopCenter))
                            if (isGameOver) {
                                StartButton(onStart = { startGame() })
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startGame() {
        isGameRunning = true
        isGameOver = false
        val centerX = screenWidth / 20 * 10f
        val centerY = screenHeight / 20 * 10f
        snakeState = SnakeState.initialize(10, Offset(centerX, centerY), Direction.RIGHT)
        food = generateFood(snakeState.body)
        score = 0
        startGameLoop()
    }

    private fun startGameLoop() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (!isGameRunning) return
                val grow = snakeState.body.first() == food
                snakeState = snakeState.move(grow)
                if (checkCollision(snakeState)) {
                    isGameOver = true
                    return
                }
                if (grow) {
                    score += 10
                    food = generateFood(snakeState.body)
                }
                handler.postDelayed(this, 200)
            }
        }
        handler.post(runnable)
    }

    private fun checkCollision(snake: SnakeState): Boolean {
        val head = snake.body.first()
        // Check wall collision
        if (head.x < 0 || head.x >= screenWidth || head.y < 0 || head.y >= screenHeight) return true
        // Check self collision
        if (snake.body.drop(1).contains(head)) return true
        return false
    }

    private fun generateFood(snake: List<Offset>): Offset {
        val random = Random(System.currentTimeMillis())
        var food: Offset
        Log.d("~~~", "screenWidth: $screenWidth, screenHeight: $screenHeight")
        if (screenWidth == 0) return Offset(100f, 100f)
        do {
            food = Offset(
                x = random.nextInt(screenWidth / 10) * 10f,
                y = random.nextInt(screenHeight / 10) * 10f
            )
        } while (snake.contains(food))
        Log.d("~~~", "food: $food, snake: $snake")
        return food
    }
}