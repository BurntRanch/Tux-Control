package io.github.burntranch.tuxcontrol

import android.content.ContentValues.TAG
import android.net.nsd.NsdManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.burntranch.tuxcontrol.StartScreen
import io.github.burntranch.tuxcontrol.ui.theme.TuxControlTheme
import java.net.InetAddress

enum class TuxControlScreen() {
    Start,
    Pair,
    Device
}

var selectedDevice: Pair<InetAddress, Int>? = null

class MainActivity : ComponentActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.TIRAMISU, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nsdManager = this.getSystemService(NSD_SERVICE) as NsdManager

        enableEdgeToEdge()
        setContent {
            TuxControlTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = TuxControlScreen.Start.name, modifier = Modifier) {
                    composable(route = TuxControlScreen.Start.name) {
                        StartScreenApp(
                            modifier = Modifier,
                            onStartButtonClick = { navController.navigate(TuxControlScreen.Pair.name); }
                        )
                    }
                    composable(route = TuxControlScreen.Pair.name) {
                        PairScreenApp(
                            modifier = Modifier,
                            navController = navController,
                            nsdManager = nsdManager
                        )
                    }
                    composable(route = TuxControlScreen.Device.name) {
                        if (selectedDevice == null) {
                            Log.e(TAG, "Tried going into the Device route with no device selected!")
                        }
                        DeviceScreenApp(
                            modifier = Modifier,
                            device = selectedDevice!!
                        )
                    }
                }
            }
        }
    }
}