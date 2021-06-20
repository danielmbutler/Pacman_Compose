package com.dbtechprojects.pacmancompose

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*


class GameViewModel : ViewModel() {

    private val TAG = "GameViewModel"
    private var canvasHeight = 0f
    private var canvasWidth = 0f
    private var leftPress: Boolean = false
    private var rightPress: Boolean = false
    private var downPress: Boolean = false
    private var upPress: Boolean = false
    private var incrementValue = 75f

    /* handles the direction the character is facing
         start angle
         left = 25f
         right = 200f
         bottom = 100f
         top = 280f
          */
    private var _characterStartAngle = MutableLiveData<Float>(25f)
    val characterStartAngle: LiveData<Float>
        get() = _characterStartAngle

    // handle presses
    fun rightPress(characterXOffset: MutableState<Float>) {
        rightPress = true
        _characterStartAngle.postValue(25f)
        viewModelScope.launch {
            while (rightPress) {
                delay(500)
                if (characterXOffset.value > 340f) characterXOffset.value = -500f
                characterXOffset.value += incrementValue
                Log.d(TAG, "rightpress: ${characterXOffset.value}")

            }
        }
    }

    fun leftPress(characterXOffset: MutableState<Float>) {
        leftPress = true
        _characterStartAngle.postValue(200f)
        viewModelScope.launch {
            while (leftPress) {
                delay(500)
                if (characterXOffset.value < -400f) characterXOffset.value = +430f
                characterXOffset.value -= incrementValue
                Log.d(TAG, "leftPress: ${characterXOffset.value}")

            }
        }
    }

    fun upPress(characterYOffset: MutableState<Float>) {
        upPress = true
        _characterStartAngle.postValue(280f)
        viewModelScope.launch {
            while (upPress) {
                delay(500)
                if (characterYOffset.value <= -600f) characterYOffset.value += 0f else characterYOffset.value -= incrementValue
                Log.d(TAG, "UpPress: ${characterYOffset.value}")

            }
        }
    }

    fun downPress(characterYOffset: MutableState<Float>) {
        downPress = true
        _characterStartAngle.postValue(100f)
        viewModelScope.launch {
            while (downPress) {
                delay(500)
                if (characterYOffset.value > 500f) characterYOffset.value += 0f else characterYOffset.value += incrementValue
                Log.d(TAG, "leftPress: ${characterYOffset.value}")

            }
        }
    }

//Cancel presses

    fun releaseLeft() {
        leftPress = false
    }

    fun releaseRight() {
        rightPress = false
    }

    fun releaseUp() {
        upPress = false
    }

    fun releaseDown() {
        downPress = false
    }


}