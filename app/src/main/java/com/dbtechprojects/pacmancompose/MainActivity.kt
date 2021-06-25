package com.dbtechprojects.pacmancompose

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.Range
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.dbtechprojects.pacmancompose.models.GameStatsModel
import com.dbtechprojects.pacmancompose.models.PacFood
import com.dbtechprojects.pacmancompose.ui.theme.*
import com.dbtechprojects.pacmancompose.utils.GameHelpers
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    @InternalCoroutinesApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PacmanComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val gameStarted = remember { mutableStateOf(false) }
                    val characterYOffset = remember { mutableStateOf(0f) }
                    val characterXOffset = remember { mutableStateOf(0f) }
                    val pacFoodState = remember { PacFood() }
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
                            gameStarted = gameStarted,
                            characterXOffset = characterXOffset,
                            characterYOffset = characterYOffset,
                            gameViewModel = gameViewModel,
                            pacFoodState = pacFoodState,
                            resources = resources,
                        )
                        Controls(
                            gameStarted,
                            characterXOffset = characterXOffset,
                            characterYOffset = characterYOffset,
                            gameViewModel = gameViewModel
                        )
                    }

                    gameLoop(
                        gameStarted = gameStarted,
                        characterXOffset = characterXOffset,
                        characterYOffset = characterYOffset,
                        pacFoodState = pacFoodState,
                    )
                }
            }
        }
    }

    private fun gameLoop(
        gameStarted: MutableState<Boolean>,
        characterXOffset: MutableState<Float>,
        characterYOffset: MutableState<Float>,
        pacFoodState: PacFood
    ) {
        if (gameStarted.value) {
            // Collision Check
            val characterX = 958.0f / 2 - 90f + characterXOffset.value
            val characterY = 1290.0f - 155f + characterYOffset.value
            pacFoodState.foodList.forEach { foodModel ->
                Log.d(
                    "pacfood", "foodModel y: ${foodModel.yPos.toFloat()}, " +
                            "characterOffset y: ${1290.0f / 2 + characterYOffset.value}, " +
                            "foodModel x: ${foodModel.xPos.toFloat()}" +
                            "characterOffset x: ${958.0f / 2 + characterXOffset.value}"
                )
                if (
                    Range.create(characterX, characterX + 100).contains(foodModel.xPos.toFloat()) &&
                    Range.create(characterY, characterY + 100).contains(foodModel.yPos.toFloat())
                ) {
                    // redraw outside box with 0 size and increment score by 1
                    foodModel.xPos = 1000
                    foodModel.yPos = 2000
                    Log.d("pacfood", "onCreate: collision ")
                    Log.d("pacfood", "onCreate: collision ")
                }
            }

            // enemy detection

        }
    }
}

@Composable
fun GameBorder(
    gameStarted: MutableState<Boolean>,
    characterXOffset: MutableState<Float>,
    characterYOffset: MutableState<Float>,
    gameViewModel: GameViewModel,
    pacFoodState: PacFood,
    resources: Resources
) {
    val characterStartAngle by gameViewModel.characterStartAngle.observeAsState()

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

        val redEnemyMovement = GameHelpers.enemyMovement(
            duration = 3000,
            gamestats = GameStatsModel(characterXOffset, characterYOffset, gameStarted)
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
                sweepAngle = if (gameStarted.value) mouthAnimation else 360f * animateCharacterSweepAngle.value,
                useCenter = true,
                topLeft = Offset(
                    size.width / 2 - 90f + characterXOffset.value,
                    size.height - 155f + characterYOffset.value
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

            // Enemy
            drawImage(
                image = ImageBitmap.imageResource(res = resources, R.drawable.ghost_red),
                topLeft = redEnemyMovement

            )

            // Maze
            val path = Path()

            path.apply {
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
                /*
                     barriers
                  __________
                 |___    ___|
                     |__|
                 */
                //left  top corner barrier
                moveTo(size.width /4 + 60f, size.height/4)
                lineTo(size.width /4 -20f, size.height / 4 ) // bottom
                lineTo(size.width /4 -20f, size.height / 4 -60f ) // left
                lineTo(size.width /4 -90f, size.height / 4 -60f ) // left angle
                lineTo(size.width /4 -90f, size.height / 4 -120f ) // left upward line to top
                lineTo(size.width /4 + 120f , size.height / 4 -120f ) // top line
                lineTo(size.width /4 + 120f , size.height / 4 -60f ) // line down to right
                lineTo(size.width /4 + 60f  , size.height / 4 -60f ) // line right to center
                lineTo(size.width /4 + 60f, size.height/4) // bottom line



            }
            drawPath(
                path = path,
                color = PacmanMazeColor,
                style = Stroke(
                    width = 6.dp.toPx(),
                )
            )


        }
    }
}


@ExperimentalFoundationApi
@Composable
fun Controls(
    gameStarted: MutableState<Boolean>,
    characterYOffset: MutableState<Float>,
    characterXOffset: MutableState<Float>,
    gameViewModel: GameViewModel
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
            Image(painter = painterResource(id = R.drawable.arrow_up),
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
                                if (gameStarted.value) {
                                    gameViewModel.upPress(characterYOffset)
                                    tryAwaitRelease()
                                    gameViewModel.releaseUp()
                                }
                            }
                        )
                    }
            )

            Image(painter = painterResource(id = R.drawable.arrow_left),
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
                                if (gameStarted.value) {
                                    gameViewModel.leftPress(characterXOffset)
                                    tryAwaitRelease()
                                    gameViewModel.releaseLeft()
                                }
                            }
                        )
                    }
            )
            Image(painter = painterResource(id = R.drawable.arrow_right),
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
                                if (gameStarted.value) {
                                    gameViewModel.rightPress(characterXOffset)
                                    tryAwaitRelease()
                                    gameViewModel.releaseRight()
                                }
                            }
                        )
                    }

            )
            Image(painter = painterResource(id = R.drawable.arrow_down),
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
                                if (gameStarted.value) {
                                    gameViewModel.downPress(characterYOffset)
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
            onClick = { gameStarted.value = !gameStarted.value },
            Modifier
                .clip(CircleShape)
                .align(Alignment.CenterVertically),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            Text(
                text = if (gameStarted.value) "Stop" else "Start",
                color = PacmanWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }

}