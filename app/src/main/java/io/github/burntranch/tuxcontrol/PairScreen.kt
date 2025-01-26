package io.github.burntranch.tuxcontrol

import android.annotation.SuppressLint
import android.net.nsd.NsdManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.SystemClock
import android.telephony.NetworkScanRequest
import android.telephony.TelephonyManager
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.github.burntranch.tuxcontrol.backend.NsdDiscover
import io.github.burntranch.tuxcontrol.backend.SERVICE_TYPE
import io.github.burntranch.tuxcontrol.ui.theme.TuxControlTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.InetAddress
import kotlin.system.exitProcess

data class DeviceListState(
    var deviceList: List<Pair<InetAddress, Int>>? = null
)

var nsdManager: NsdManager? = null

@RequiresExtension(extension = Build.VERSION_CODES.TIRAMISU, version = 7)
class DeviceListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DeviceListState())
    val uiState: StateFlow<DeviceListState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val data = getDevices()
            if (data != null) {
                _uiState.update { currentState ->
                    currentState.copy(
                        deviceList = data
                    )
                }
            }
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.TIRAMISU, version = 7)
    suspend fun getDevices(): MutableList<Pair<InetAddress, Int>>? {
        if (nsdManager == null) {
            return null;
        }

        val discover: NsdDiscover = NsdDiscover(nsdManager!!);

        nsdManager!!.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discover.discoveryListener)

        delay(3 * 1000)

        nsdManager!!.stopServiceDiscovery(discover.discoveryListener)

        val hostAddresses: MutableList<Pair<InetAddress, Int>> = mutableListOf()

        for (serviceInfo in discover.serviceInfos) {
            hostAddresses.add(Pair(serviceInfo.hostAddresses[0], serviceInfo.port))
        }

        return hostAddresses
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.TIRAMISU, version = 7)
@Composable
fun PairScreen(modifier: Modifier = Modifier, viewModel: DeviceListViewModel = viewModel()) {
    Column (modifier = modifier) {
        Column (modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            val deviceListState by viewModel.uiState.collectAsStateWithLifecycle()

            if (deviceListState.deviceList != null) {
                for (device in deviceListState.deviceList!!) {
                    Row(modifier = modifier.fillMaxWidth().padding(10.dp)) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_laptop_24),
                            contentDescription = "Device"
                        )
                        device.first.hostAddress?.let { Text(text = it, modifier = modifier.width(200.dp), maxLines = 1, overflow = TextOverflow.Ellipsis) }

                        Spacer(Modifier.weight(1f));

                        Text(
                            text = "Connect",
                            modifier = modifier.clickable(
                                enabled = true,
                                onClick = { exitProcess(1) })
                        )
                    }
                }
            }
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.TIRAMISU, version = 7)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PairScreenApp(modifier: Modifier = Modifier, nsd_manager: NsdManager? = null, nav_controller: NavController? = null) {
    val containerColor = if (isSystemInDarkTheme()) { Color(50, 50, 50) } else { Color(205, 205, 205) }
    val titleColor = if (isSystemInDarkTheme()) { Color(205, 205, 205) } else { Color(50, 50, 50) }

    nsdManager = nsd_manager

    Scaffold (topBar = {
        MediumTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor, titleContentColor = titleColor),
            title = {
                Text("Pairing")
            },
            navigationIcon = {
                if (nav_controller != null) {
                    IconButton(onClick = { nav_controller.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            }
        )
    }, modifier = modifier.fillMaxSize()) {_ ->
        PairScreen(modifier, DeviceListViewModel());
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.TIRAMISU, version = 7)
@Preview(showBackground = true)
@Composable
fun PairScreenPreview() {
    TuxControlTheme {
        PairScreen()
    }
}
