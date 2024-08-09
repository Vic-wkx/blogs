package com.game.snake

import androidx.compose.ui.geometry.Offset

data class SnakeState(
    val body: List<Offset>,
    val direction: Direction
) {
    companion object {
        fun initialize(length: Int, startPosition: Offset, direction: Direction): SnakeState {
            val body = List(length) { index ->
                when (direction) {
                    Direction.UP -> startPosition.copy(y = startPosition.y + index * 10)
                    Direction.DOWN -> startPosition.copy(y = startPosition.y - index * 10)
                    Direction.LEFT -> startPosition.copy(x = startPosition.x + index * 10)
                    Direction.RIGHT -> startPosition.copy(x = startPosition.x - index * 10)
                }
            }
            return SnakeState(body, direction)
        }
    }

    fun move(grow: Boolean = false): SnakeState {
        val head = body.first()
        val newHead = when (direction) {
            Direction.UP -> head.copy(y = head.y - 10)
            Direction.DOWN -> head.copy(y = head.y + 10)
            Direction.LEFT -> head.copy(x = head.x - 10)
            Direction.RIGHT -> head.copy(x = head.x + 10)
        }
        val newBody = if (grow) {
            listOf(newHead) + body
        } else {
            listOf(newHead) + body.dropLast(1)
        }
        return copy(body = newBody)
    }
}