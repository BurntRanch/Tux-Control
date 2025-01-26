package io.github.burntranch.tuxcontrol.backend

import android.content.ContentValues.TAG
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.Executor

enum class DiscoverStatus {
    IDLE,
    STARTED,
    STOPPED
}

internal class DirectExecutor : Executor {
    override fun execute(r: Runnable) {
        r.run()
    }
}

const val SERVICE_TYPE = "_tuxcontrol._tcp."

class NsdDiscover constructor(nsdManager: NsdManager) {
    var status: DiscoverStatus = DiscoverStatus.IDLE;

    var serviceInfos: MutableList<NsdServiceInfo> = mutableListOf()

    val serviceInfoCallback = @RequiresExtension(
        extension = Build.VERSION_CODES.TIRAMISU,
        version = 7
    )
    object : NsdManager.ServiceInfoCallback {
        override fun onServiceLost() {
            Log.e(TAG, "service lost")
        }

        override fun onServiceInfoCallbackUnregistered() {
            Log.d(TAG, "service info callback unregistered")
        }

        override fun onServiceInfoCallbackRegistrationFailed(errorCode: Int) {
            Log.e(TAG, "callback registration failed: $errorCode")
        }

        override fun onServiceUpdated(serviceInfo: NsdServiceInfo) {
            /* Screw you google for making mHostname private with literally NO WAY to access it. Shame on you. */
            val hostname: String = serviceInfo.toString().substringAfter("hostname: ").substringBefore(' ')

            if (serviceInfos.find { info -> info.toString().substringAfter("hostname: ").substringBefore(' ') == hostname } != null) {
                return;
            }

            serviceInfos.add(serviceInfo)
        }
    }

    val discoveryListener = object : NsdManager.DiscoveryListener {
        // Called as soon as service discovery begins.
        override fun onDiscoveryStarted(regType: String) {
            Log.d(TAG, "Service discovery started")
            serviceInfos = mutableListOf()
            status = DiscoverStatus.STARTED
        }

        @RequiresExtension(extension = Build.VERSION_CODES.TIRAMISU, version = 7)
        override fun onServiceFound(service: NsdServiceInfo) {
            // A service was found! Do something with it.
            Log.d(TAG, "Service discovery success! $service")
            when {
                service.serviceType != SERVICE_TYPE -> // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: ${service.serviceType}")
//                service.serviceName == mServiceName ->
//                    Log.d(TAG, "Same machine: $mServiceName")
                service.serviceName.contains("TuxControl") -> nsdManager.registerServiceInfoCallback(service, DirectExecutor(), serviceInfoCallback);
            }
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            Log.e(TAG, "service lost: $service")
            status = DiscoverStatus.IDLE
        }

        override fun onDiscoveryStopped(serviceType: String) {
            Log.i(TAG, "Discovery stopped: $serviceType")
            status = DiscoverStatus.STOPPED
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }
    }
}