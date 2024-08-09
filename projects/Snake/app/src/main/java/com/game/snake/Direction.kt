package com.game.snake

import androidx.compose.ui.geometry.Offset

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    fun isHorizontal() = this == LEFT || this == RIGHT
    fun isVertical() = this == UP || this == DOWN

    fun fromTapPosition(position: Offset, head: Offset): Direction? {
        return when {
            position.x < head.x && isVertical() -> LEFT
            position.x > head.x && isVertical() -> RIGHT
            position.y < head.y && isHorizontal() -> UP
            position.y > head.y && isHorizontal() -> DOWN
            else -> null
        }
    }
}