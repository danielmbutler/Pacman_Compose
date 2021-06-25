package com.dbtechprojects.pacmancompose.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import com.dbtechprojects.pacmancompose.models.GameStatsModel

object GameHelpers {


    @Composable
    fun enemyMovement(duration: Int, gamestats: GameStatsModel): Offset {
        // X Axis
        val enemyMovementXAxis by animateFloatAsState(
            targetValue = if (gamestats.isGameStarted.value) {
                958.0f / 2 - 90f + gamestats.CharacterXOffset.value
            } else {
                958.0f / 2 - 90f
            },
            animationSpec = tween(duration, easing = LinearEasing),
            finishedListener = {

            }

        )

        // y Axis

        val enemyMovementYAxis by animateFloatAsState(
            targetValue = if (gamestats.isGameStarted.value) {
                1290.0f - 155f + gamestats.CharacterYOffset.value
            } else {
                1290.0f / 2 + 60f
            },
            animationSpec = tween(duration, easing = LinearEasing),
            finishedListener = {
            }
        )

        return Offset(enemyMovementXAxis, enemyMovementYAxis)


    }
}