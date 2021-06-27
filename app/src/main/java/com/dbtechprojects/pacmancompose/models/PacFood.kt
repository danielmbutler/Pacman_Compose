package com.dbtechprojects.pacmancompose.models

import android.util.Log
import com.dbtechprojects.pacmancompose.utils.GameConstants
import kotlin.random.Random


data class PacFood(
    val foodList: ArrayList<PacFoodModel> = ArrayList(),
) {
    init {
        initPacFoodList()
    }

    private fun initPacFoodList() {
        foodList.clear()



        for (i in 0 until GameConstants.FOOD_COUNTER) {
            val food = PacFoodModel(
                xPos = Random.nextInt(85, 850),
                yPos = Random.nextInt(85, 1200),
                size = 0.5f
            )
            Log.w("food", "${food.xPos}")
            foodList.add(food)

        }
    }

    fun initRedraw(){
        initPacFoodList()
    }
}



data class PacFoodModel(
    var xPos: Int,
    var yPos: Int,
    var size: Float
)
