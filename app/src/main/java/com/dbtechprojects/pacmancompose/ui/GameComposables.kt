package com.dbtechprojects.pacmancompose.ui

import android.content.res.Resources
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import com.dbtechprojects.pacmancompose.GameViewModel
import com.dbtechprojects.pacmancompose.models.DialogState
import com.dbtechprojects.pacmancompose.models.EnemyMovementModel
import com.dbtechprojects.pacmancompose.models.GameStatsModel
import com.dbtechprojects.pacmancompose.models.PacFood
import com.dbtechprojects.pacmancompose.ui.theme.*
import com.dbtechprojects.pacmancompose.utils.GameConstants

@Composable
fun FullScreenDialog(showDialog: MutableState<Boolean> , text: String) {
    if (showDialog.value) {
        Dialog(
            properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ), onDismissRequest = {}
        ) {
            Surface(
                modifier = Modifier.wrapContentSize(),
                shape = RoundedCornerShape(16.dp),
                color = PacmanYellow
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                    ) {

                        Column(
                            Modifier.padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = text,
                                color = PacmanPink,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = HeaderFont,
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                softWrap = false

                            )
                            Button(
                                onClick = { showDialog.value = false },
                                colors = ButtonDefaults.buttonColors(backgroundColor = PacmanPink)
                            ) {
                                Text(
                                    text = "Close",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameBorder(
    gameViewModel: GameViewModel,
    pacFoodState: PacFood,
    enemyMovementModel: EnemyMovementModel,
    resources: Resources,
    gameOverDialogState: DialogState,
    gameStatsModel: GameStatsModel,
    redEnemyDrawable: Int,
    orangeEnemyDrawable: Int
) {
    val characterStartAngle by gameViewModel.characterStartAngle.observeAsState()

    FullScreenDialog(showDialog = gameOverDialogState.shouldShow, gameOverDialogState.message.value)

    Box(
        modifier = Modifier
            .border(6.dp, color = PacmanRed)
            .padding(6.dp)
    ) {
        // used for character and food to draw circle
        val radius = 50f

        // animate drawing the pac character at the start of the game
        val animateCharacterSweepAngle = remember { Animatable(0f) }
        LaunchedEffect(animateCharacterSweepAngle) {
            animateCharacterSweepAngle.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 3000, easing = LinearEasing),
            )
        }


        // animate continued opening and closing of mouth whilst game is running
        val infiniteTransition = rememberInfiniteTransition()
        val mouthAnimation by infiniteTransition.animateFloat(
            initialValue = 360F,
            targetValue = 280F,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        // define enemy values (more to be added)

        //Red Enemy
        enemyMovementModel.redEnemyMovement.value = enemyMovement(
            duration = GameConstants.RED_ENEMY_SPEED,
            gameStats = gameStatsModel,
            initialXOffset = 90f
        )

        // Orange Enemy
        enemyMovementModel.orangeEnemyMovement.value = enemyMovement(
            duration = GameConstants.ORANGE_ENEMY_SPEED,
            gameStats = gameStatsModel,
            initialXOffset = 70f
        )


        // canvas to draw game
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.75f)
        ) {
            val height = this.size.height
            val width = this.size.width
            Log.d("canvas", "width: $width, height: $height ")

            // character
            drawArc(
                color = Color.Yellow,
                startAngle = characterStartAngle ?: 30f,
                sweepAngle = if (gameStatsModel.isGameStarted.value) mouthAnimation else 360f * animateCharacterSweepAngle.value,
                useCenter = true,
                topLeft = Offset(
                    size.width / 2 - 90f + gameStatsModel.CharacterXOffset.value,
                    size.height - 155f + gameStatsModel.CharacterYOffset.value
                ),
                size = Size(
                    radius * 2,
                    radius * 2
                ),
                style = Fill,

                )

            // food
            for (i in pacFoodState.foodList) {
                drawArc(
                    color = Color.Yellow,
                    startAngle = characterStartAngle ?: 30f,
                    sweepAngle = 360f,
                    useCenter = true,
                    topLeft = Offset(i.xPos.toFloat(), i.yPos.toFloat()),
                    size = Size(
                        radius * i.size,
                        radius * i.size
                    ),
                    style = Fill,

                    )
            }

            // Bonus Food
            for(i in pacFoodState.bonusFoodList){
                drawArc(
                    color = Color.Magenta,
                    startAngle = characterStartAngle ?: 30f,
                    sweepAngle = 360f,
                    useCenter = true,
                    topLeft = Offset(i.xPos.toFloat(), i.yPos.toFloat()),
                    size = Size(
                        radius * i.size,
                        radius * i.size
                    ),
                    style = Fill,
                )
            }

            // Enemy
            drawImage(
                image = ImageBitmap.imageResource(res = resources, redEnemyDrawable),
                topLeft = enemyMovementModel.redEnemyMovement.value

            )
            drawImage(
                image = ImageBitmap.imageResource(res = resources, orangeEnemyDrawable),
                topLeft = enemyMovementModel.orangeEnemyMovement.value

            )

            // Maze
            val borderPath = Path()

            // barriers
            val barrierPath = Path()

            borderPath.apply {
                // border
                lineTo(size.width, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                lineTo(0f, 0f)

                // second border
                moveTo(50f, 50f)
                lineTo(size.width - 50f, 50f)
                lineTo(size.width - 50f, size.height - 50f)
                lineTo(50f, size.height - 50f)
                lineTo(50f, 50f)

                // enemy box
                moveTo(size.width / 2 + 90f, size.height / 2 + 90f)
                lineTo(size.width / 2 + 90f, size.height / 2 + 180f)
                lineTo(size.width / 2 - 120f, size.height / 2 + 180f)
                lineTo(size.width / 2 - 120f, size.height / 2 + 90f)


            }

            barrierPath.apply {
                /*
                     barriers
                  __________
                 |___    ___|
                     |__|
                 */
                //left  top corner barrier
                moveTo(size.width / 4 + 60f, size.height / 4)
                lineTo(size.width / 4 - 20f, size.height / 4) // bottom
                lineTo(size.width / 4 - 20f, size.height / 4 - 60f) // left
                lineTo(size.width / 4 - 90f, size.height / 4 - 60f) // left angle
                lineTo(size.width / 4 - 90f, size.height / 4 - 120f) // left upward line to top
                lineTo(size.width / 4 + 120f, size.height / 4 - 120f) // top line
                lineTo(size.width / 4 + 120f, size.height / 4 - 60f) // line down to right
                lineTo(size.width / 4 + 50f, size.height / 4 - 60f) // line right to center
                lineTo(size.width / 4 + 50f, size.height / 4) // bottom line

                // right  top corner barrier
                moveTo(size.width / 1.5f + 60f, size.height / 4)
                lineTo(size.width / 1.5f - 20f, size.height / 4) // bottom
                lineTo(size.width / 1.5f - 20f, size.height / 4 - 60f) // left
                lineTo(size.width / 1.5f - 90f, size.height / 4 - 60f) // left angle
                lineTo(size.width / 1.5f - 90f, size.height / 4 - 120f) // left upward line to top
                lineTo(size.width / 1.5f + 120f, size.height / 4 - 120f) // top line
                lineTo(size.width / 1.5f + 120f, size.height / 4 - 60f) // line down to right
                lineTo(size.width / 1.5f + 50f, size.height / 4 - 60f) // line right to center
                lineTo(size.width / 1.5f + 50f, size.height / 4) // bottom line

                // right bottom corner barrier
                moveTo(size.width / 1.5f + 60f, size.height / 1.15f)
                lineTo(size.width / 1.5f - 20f, size.height / 1.15f) // bottom
                lineTo(size.width / 1.5f - 20f, size.height / 1.15f - 60f) // left
                lineTo(size.width / 1.5f - 90f, size.height / 1.15f - 60f) // left angle
                lineTo(
                    size.width / 1.5f - 90f,
                    size.height / 1.15f - 120f
                ) // left upward line to top
                lineTo(size.width / 1.5f + 120f, size.height / 1.15f - 120f) // top line
                lineTo(size.width / 1.5f + 120f, size.height / 1.15f - 60f) // line down to right
                lineTo(size.width / 1.5f + 50f, size.height / 1.15f - 60f) // line right to center
                lineTo(size.width / 1.5f + 50f, size.height / 1.15f) // bottom line

                //left  bottom corner barrier
                moveTo(size.width / 4 + 60f, size.height / 1.15f)
                lineTo(size.width / 4 - 20f, size.height / 1.15f) // bottom
                lineTo(size.width / 4 - 20f, size.height / 1.15f - 60f) // left
                lineTo(size.width / 4 - 90f, size.height / 1.15f - 60f) // left angle
                lineTo(size.width / 4 - 90f, size.height / 1.15f - 120f) // left upward line to top
                lineTo(size.width / 4 + 120f, size.height / 1.15f - 120f) // top line
                lineTo(size.width / 4 + 120f, size.height / 1.15f - 60f) // line down to right
                lineTo(size.width / 4 + 50f, size.height / 1.15f - 60f) // line right to center
                lineTo(size.width / 4 + 50f, size.height / 1.15f) // bottom line

            }
            drawPath(
                path = borderPath,
                color = PacmanMazeColor,
                style = Stroke(
                    width = 6.dp.toPx(),
                ),

                )

            drawPath(
                path = barrierPath,
                color = PacmanMazeColor,
                style = Fill,
            )


        }
    }
}




@ExperimentalFoundationApi
@Composable
fun Controls(
    gameStatsModel: GameStatsModel,
    gameViewModel: GameViewModel,
    upButtonDrawable: Int,
    downButtonDrawable: Int,
    leftButtonDrawable: Int,
    rightButtonDrawable: Int
) {


    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 6.dp)
    ) {
        // controls
        ConstraintLayout(modifier = Modifier.fillMaxSize(0.5f)) {
            // Create references for the composables to constrain
            val (upArrow, leftArrow, rightArrow, downArrow) = createRefs()
            Image(painter = painterResource(id = upButtonDrawable),
                contentDescription = "Up Arrow",
                Modifier
                    .constrainAs(upArrow) {
                        bottom.linkTo(leftArrow.top)
                        start.linkTo(leftArrow.end)
                    }
                    .size(30.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                if (gameStatsModel.isGameStarted.value) {
                                    gameViewModel.upPress(
                                        characterYOffset = gameStatsModel.CharacterYOffset,
                                        characterXOffset = gameStatsModel.CharacterXOffset
                                    )
                                    tryAwaitRelease()
                                    gameViewModel.releaseUp()
                                }
                            }
                        )
                    }
            )

            Image(painter = painterResource(id = leftButtonDrawable),
                contentDescription = "Left Arrow",
                Modifier
                    .constrainAs(leftArrow) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
                    .size(30.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                if (gameStatsModel.isGameStarted.value) {
                                    gameViewModel.leftPress(
                                        characterYOffset = gameStatsModel.CharacterYOffset,
                                        characterXOffset = gameStatsModel.CharacterXOffset
                                    )
                                    tryAwaitRelease()
                                    gameViewModel.releaseLeft()
                                }
                            }
                        )
                    }
            )
            Image(painter = painterResource(id = rightButtonDrawable),
                contentDescription = "Right Arrow",
                Modifier
                    .constrainAs(rightArrow) {
                        start.linkTo(upArrow.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .size(30.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                if (gameStatsModel.isGameStarted.value) {
                                    gameViewModel.rightPress(
                                        characterYOffset = gameStatsModel.CharacterYOffset,
                                        characterXOffset = gameStatsModel.CharacterXOffset
                                    )
                                    tryAwaitRelease()
                                    gameViewModel.releaseRight()
                                }
                            }
                        )
                    }

            )
            Image(painter = painterResource(id = downButtonDrawable),
                contentDescription = "Down Arrow",
                Modifier
                    .constrainAs(downArrow) {
                        top.linkTo(rightArrow.bottom)
                        start.linkTo(leftArrow.end)
                    }
                    .size(30.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                if (gameStatsModel.isGameStarted.value) {
                                    gameViewModel.downPress(
                                        characterYOffset = gameStatsModel.CharacterYOffset,
                                        characterXOffset = gameStatsModel.CharacterXOffset
                                    )
                                    tryAwaitRelease()
                                    gameViewModel.releaseDown()
                                }
                            }
                        )
                    }
            )
        }

        // start/stop button
        Button(
            onClick = { gameStatsModel.isGameStarted.value = !gameStatsModel.isGameStarted.value },
            Modifier
                .clip(CircleShape)
                .align(Alignment.CenterVertically),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            Text(
                text = if (gameStatsModel.isGameStarted.value) "Stop" else "Start",
                color = PacmanWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }

}

/*
    function to provide an Offset to set enemy position, enemies position is animated to the
    position of the character if the game has started
    */

@Composable
fun enemyMovement(duration: Int, gameStats: GameStatsModel, initialXOffset: Float): Offset {
    // X Axis
    val enemyMovementXAxis by animateFloatAsState(
        targetValue = if (gameStats.isGameStarted.value) {
            958.0f / 2 - 90f + gameStats.CharacterXOffset.value
        } else {
            958.0f / 2 - initialXOffset // create spacing between enemies in box
        },
        animationSpec = tween(duration, easing = LinearEasing),
        finishedListener = {

        }

    )

    // y Axis
    val enemyMovementYAxis by animateFloatAsState(
        targetValue = if (gameStats.isGameStarted.value) {
            1290.0f - 155f + gameStats.CharacterYOffset.value
        } else {
            1290.0f / 2 + 60f
        },
        animationSpec = tween(duration, easing = LinearEasing),
        finishedListener = {
        }
    )

    return Offset(enemyMovementXAxis, enemyMovementYAxis)


}