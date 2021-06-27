package com.dbtechprojects.pacmancompose

import android.os.Bundle
import android.util.Log
import android.util.Range
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dbtechprojects.pacmancompose.models.DialogState
import com.dbtechprojects.pacmancompose.models.EnemyMovementModel
import com.dbtechprojects.pacmancompose.models.GameStatsModel
import com.dbtechprojects.pacmancompose.models.PacFood
import com.dbtechprojects.pacmancompose.ui.Controls
import com.dbtechprojects.pacmancompose.ui.GameBorder
import com.dbtechprojects.pacmancompose.ui.theme.HeaderFont
import com.dbtechprojects.pacmancompose.ui.theme.PacmanBackground
import com.dbtechprojects.pacmancompose.ui.theme.PacmanComposeTheme
import com.dbtechprojects.pacmancompose.ui.theme.PacmanYellow
import com.dbtechprojects.pacmancompose.utils.GameConstants
import kotlinx.coroutines.InternalCoroutinesApi

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()
    private lateinit var gameStarted: MutableState<Boolean>
    private lateinit var characterYOffset: MutableState<Float>
    private lateinit var characterXOffset: MutableState<Float>
    private lateinit var enemyMovementModel: MutableState<EnemyMovementModel>
    private lateinit var gameOverDialogState: DialogState
    private lateinit var pacFoodState: PacFood
    private lateinit var gameStatsModel: GameStatsModel
    private lateinit var foodCounter: MutableState<Int>

    @InternalCoroutinesApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            gameStarted = remember { mutableStateOf(false) }
            characterYOffset = remember { mutableStateOf(0f) }
            characterXOffset = remember { mutableStateOf(0f) }
            gameStatsModel = GameStatsModel(characterXOffset, characterYOffset, gameStarted)
            enemyMovementModel = remember { mutableStateOf(EnemyMovementModel()) }
            gameOverDialogState = remember {
                DialogState(
                    shouldShow = mutableStateOf(false),
                    mutableStateOf("")
                )
            }
            foodCounter = remember { mutableStateOf(100) }
            pacFoodState = remember { PacFood() }
            PacmanComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreenContent()
                }
            }
        }
        gameLoop(
            gameStatsModel = gameStatsModel,
            pacFoodState = pacFoodState,
            enemyMovementModel = enemyMovementModel.value,
            foodCounter = foodCounter
        )
    }

    @ExperimentalFoundationApi
    @Composable
    private fun MainScreenContent() {
        Column(
            modifier = Modifier
                .background(color = PacmanBackground)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Pacman",
                fontSize = 36.sp,
                fontFamily = HeaderFont,
                color = PacmanYellow,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(6.dp)
            )
            GameBorder(
                gameViewModel = gameViewModel,
                gameStatsModel = gameStatsModel,
                pacFoodState = pacFoodState,
                resources = resources,
                enemyMovementModel = enemyMovementModel.value,
                gameOverDialogState = gameOverDialogState,
                redEnemyDrawable = R.drawable.ghost_red,
                orangeEnemyDrawable = R.drawable.ghost_orange

            )
            Controls(
                gameStatsModel = gameStatsModel,
                gameViewModel = gameViewModel,
                upButtonDrawable = R.drawable.arrow_up,
                leftButtonDrawable = R.drawable.arrow_left,
                downButtonDrawable = R.drawable.arrow_down,
                rightButtonDrawable = R.drawable.arrow_right
            )
        }
    }

    private fun gameLoop(
        foodCounter: MutableState<Int>,
        pacFoodState: PacFood,
        enemyMovementModel: EnemyMovementModel,
        gameStatsModel: GameStatsModel,
    ) {

        if (gameStatsModel.isGameStarted.value) {
            // Collision Check
            val characterX = 958.0f / 2 - 90f + gameStatsModel.CharacterXOffset.value
            val characterY = 1290.0f - 155f + gameStatsModel.CharacterYOffset.value
            pacFoodState.foodList.forEach { foodModel ->
                if (
                    Range.create(characterX, characterX + 100).contains(foodModel.xPos.toFloat()) &&
                    Range.create(characterY, characterY + 100).contains(foodModel.yPos.toFloat())
                ) {
                    // redraw outside box with 0 size and increment score by 1
                    foodModel.xPos = 1000
                    foodModel.yPos = 2000
                    foodCounter.value -= 1
                    Log.d("pacfood", "onCreate: collision ")
                    Log.d("pacfood", "onCreate: collision ")
                }
            }

            // enemy collision detection
            Log.d(
                "enemyMovement", "" +
                        "Orange : x: ${enemyMovementModel.orangeEnemyMovement.value.x} " +
                        "y: ${enemyMovementModel.orangeEnemyMovement.value.y} Red: x:" +
                        " ${enemyMovementModel.redEnemyMovement.value.x} y: " +
                        "${enemyMovementModel.redEnemyMovement.value.y} character current position : x:" +
                        " $characterX y : $characterY"
            )

            if (
            // if enemy is within 100f of character then a collision has occurred and the game should stop
                Range.create(characterX, characterX + 25).contains(
                    enemyMovementModel.redEnemyMovement.value.x
                ) &&
                Range.create(characterY, characterY + 25).contains(
                    enemyMovementModel.redEnemyMovement.value.y
                ) ||
                Range.create(characterX, characterX + 25).contains(
                    enemyMovementModel.orangeEnemyMovement.value.x
                ) &&
                Range.create(characterY, characterY + 25).contains(
                    enemyMovementModel.orangeEnemyMovement.value.y
                )

            ) {
                // gameover, stop game and show dialog
                resetGame("GAME OVER")

            }

            // win logic
            Log.d("food counter", "counter: ${foodCounter.value} ")
            if (foodCounter.value == 0) {
                resetGame("YOU WON !")
            }


        }
    }

    private fun resetGame(message: String) {
        gameStarted.value = false
        gameOverDialogState.shouldShow.value = true
        gameOverDialogState.message.value = message
        foodCounter.value = GameConstants.FOOD_COUNTER // reset counter
        pacFoodState.initRedraw()
        // reset character position
        characterXOffset.value = 0f
        characterYOffset.value = 0f

    }

}