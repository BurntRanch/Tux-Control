package io.github.burntranch.tuxcontrol

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.burntranch.tuxcontrol.ui.theme.TuxControlTheme

@Composable
fun StartScreen(modifier: Modifier = Modifier, onStartButtonClick: () -> Unit = {}) {
    Column (modifier = modifier.fillMaxWidth()) {
        Text(text = "Tux Control", modifier = modifier.align(Alignment.CenterHorizontally))

        Column (modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Control your Linux PC remotely!", modifier = modifier)
            Button(onClick = { onStartButtonClick() }, modifier = modifier.align(Alignment.CenterHorizontally)) {
                Text("Get Started")
            }
        }
    }
}

@Composable
fun StartScreenApp(modifier: Modifier = Modifier, onStartButtonClick: () -> Unit = {}) {
    Scaffold (modifier = modifier.fillMaxSize()) {innerPadding ->
        StartScreen(modifier.padding(innerPadding), onStartButtonClick);
    }
}

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    TuxControlTheme {
        StartScreen()
    }
}
