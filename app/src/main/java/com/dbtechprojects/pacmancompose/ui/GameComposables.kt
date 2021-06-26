package com.dbtechprojects.pacmancompose.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dbtechprojects.pacmancompose.ui.theme.HeaderFont
import com.dbtechprojects.pacmancompose.ui.theme.PacmanPink
import com.dbtechprojects.pacmancompose.ui.theme.PacmanYellow

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