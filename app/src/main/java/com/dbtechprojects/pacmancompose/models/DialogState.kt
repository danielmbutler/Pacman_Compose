package com.dbtechprojects.pacmancompose.models

import androidx.compose.runtime.MutableState

data class DialogState (
        val shouldShow: MutableState<Boolean>,
        val message: MutableState<String>,
        )