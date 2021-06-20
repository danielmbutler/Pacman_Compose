package com.dbtechprojects.pacmancompose

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*


class GameViewModel : ViewModel() {

    private val TAG = "GameViewModel"
    private var leftPress: Boolean = false
    private var rightPress: Boolean = false
    private var downPress: Boolean = false
    private var upPress: Boolean = false

// handle presses
    fun leftPress(characterXOffset: MutableState<Float>) {
        leftPress = true
        viewModelScope.launch {
            while(leftPress) {
                //do your network request here
                delay(500)
                if (characterXOffset.value > 340f) characterXOffset.value = -500f
                characterXOffset.value +=50f
                Log.d(TAG, "leftPress: ${characterXOffset.value}")
                
            }
        }
    }

//Cancel presses
    fun releaseLeft(){
        leftPress = false
    }

    fun releaseRight(){
        rightPress = false
    }

    fun releaseUp(){
        upPress = false
    }

    fun releaseDown(){
        downPress = false
    }

}