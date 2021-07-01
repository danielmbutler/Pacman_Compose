package com.dbtechprojects.pacmancompose.models

import androidx.compose.runtime.MutableState

data class GameStatsModel(
    val CharacterXOffset: MutableState<Float>,
    val CharacterYOffset: MutableState<Float>,
    val isGameStarted: MutableState<Boolean>,
    val isReverseMode: MutableState<Boolean>

)
