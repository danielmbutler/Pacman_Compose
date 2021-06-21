package com.dbtechprojects.pacmancompose.models

import android.util.Log
import kotlin.random.Random


data class PacFood(
    val foodList: ArrayList<PacFoodModel> = ArrayList(),
) {
    init {
        initPacFoodList()
    }

    private fun initPacFoodList() {
        foodList.clear()

        val foodCount = 100

        for (i in 0 until foodCount) {
            val food = PacFoodModel(
                xPos = Random.nextInt(55, 850), // should not be hard coded
                yPos = Random.nextInt(55, 1200),
                size = 0.5f
            )
            Log.w("food", "${food.xPos}")
            foodList.add(food)

        }
    }
}

data class PacFoodModel(
    var xPos: Int,
    var yPos: Int,
    var size: Float
) {

}
