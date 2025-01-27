package io.github.burntranch.tuxcontrol

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.burntranch.tuxcontrol.backend.ClientHandler
import io.github.burntranch.tuxcontrol.ui.theme.TuxControlTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.InetAddress

private var clientHandler: ClientHandler = ClientHandler()

@Composable
fun DeviceScreen(modifier: Modifier = Modifier, deviceName: String = "Unknown") {
    Column (modifier = modifier.fillMaxWidth()) {
        Text(text = "Tux Control ($deviceName)", modifier = modifier.align(Alignment.CenterHorizontally))
        Column (modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Nothing here..", modifier = modifier)
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DeviceScreenApp(modifier: Modifier = Modifier, device: Pair<InetAddress, Int>) {
    val coroutineScope = rememberCoroutineScope()

    coroutineScope.launch {
        clientHandler.StartConnection(device.first, device.second)
    }

    Scaffold (modifier = modifier.fillMaxSize()) {innerPadding ->
        DeviceScreen(modifier.padding(innerPadding), "device");
    }
}

@Preview(showBackground = true)
@Composable
fun DeviceScreenPreview() {
    TuxControlTheme {
        DeviceScreen()
    }
}
