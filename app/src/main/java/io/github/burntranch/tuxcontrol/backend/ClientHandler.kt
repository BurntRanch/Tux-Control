package io.github.burntranch.tuxcontrol.backend

import android.content.ContentValues.TAG
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.io.PrintWriter
import java.io.Writer
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.Scanner
import kotlin.concurrent.thread

class ClientHandler {
    private var socket: Socket? = null
    private var socketWriter: OutputStream? = null
    private var socketScanner: Scanner? = null
    private var shouldQuit: Boolean = false;

    private fun connectionLoopThread() = thread(start = true) {
        if (socket == null || socketWriter == null) {
            Log.e(TAG, "SOCKET OR SOCKETWRITER IS NULL!!")
            return@thread
        }

        socketWriter!!.write("Hello, world!".toByteArray())

        socketWriter!!.flush()
    }

    private var loopThread: Thread? = null

    suspend fun StartConnection(address: InetAddress, port: Int) = withContext(Dispatchers.IO) {
        if (loopThread != null) {
            return@withContext
        }

        socket = Socket()
        socket!!.connect(InetSocketAddress(address, port), 5000)

        socketWriter = socket!!.getOutputStream()
//        socketScanner = Scanner(socket!!.getInputStream())

        loopThread = connectionLoopThread()
    }

    suspend fun CloseConnection() {
        if (socket == null) {
            return;
        }

        if (loopThread != null) {
            shouldQuit = true
            withContext(Dispatchers.IO) {
                loopThread!!.join()
            }
        }

        socketWriter = null
        socketScanner = null

        withContext(Dispatchers.IO) {
            socket!!.close()
        }
        socket = null
    }
}