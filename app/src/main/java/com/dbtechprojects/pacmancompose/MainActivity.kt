package com.dbtechprojects.pacmancompose

import android.os.Bundle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.dbtechprojects.pacmancompose.ui.theme.*
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {

     val gameViewModel: GameViewModel by viewModels()
    @InternalCoroutinesApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PacmanComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val gameStarted = remember {mutableStateOf(false)}
                    val characterYOffset = remember { mutableStateOf(0f)}
                    val characterXOffset = remember { mutableStateOf(0f)}
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
                            gameViewModel = gameViewModel
                        )
                        Controls(
                            gameStarted,
                            characterXOffset = characterXOffset,
                            characterYOffset = characterYOffset,
                            gameViewModel = gameViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameBorder(
    gameStarted: MutableState<Boolean>,
    characterXOffset: MutableState<Float>,
    characterYOffset: MutableState<Float>,
    gameViewModel: GameViewModel
) {
    val characterStartAngle by gameViewModel.characterStartAngle.observeAsState()
    Box(
        modifier = Modifier
            .border(6.dp, color = PacmanRed)
            .padding(6.dp)
    ) {

        val radius = 50f
        val animateFloat = remember { androidx.compose.animation.core.Animatable(0f) }
        LaunchedEffect(animateFloat) {
            animateFloat.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 3000, easing = LinearEasing),
            )
        }
        val infiniteTransition = rememberInfiniteTransition()
        val mouthAnimation by infiniteTransition.animateFloat(
            initialValue = 360F,
            targetValue = 280F,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )


        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.75f)
        ) {
            val height = this.size.height
            val width = this.size.width


            drawArc(
                color = Color.Yellow,
                startAngle = characterStartAngle ?: 30f,
                sweepAngle = if (gameStarted.value) mouthAnimation else 360f * animateFloat.value,
                useCenter = true,
                topLeft = Offset(size.width / 2 + characterXOffset.value, size.height / 2 + characterYOffset.value),
                size = Size(
                    radius * 2,
                    radius * 2
                ),
                style = Fill,

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