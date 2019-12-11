package it.unibo.aggregatecomputingclient

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import communication.Communication
import communication.SocketCommunication
import kotlinx.android.synthetic.main.activity_main.*
import java.net.InetAddress
import java.net.InetSocketAddress

class MainActivity : AppCompatActivity() {
    private val port = 20000
    private lateinit var communication: Communication
    private var id: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        communication = SocketCommunication(applicationContext, port)
            { id = it; println("id set to $id") }

        communication.listen()
    }

    override fun onDestroy() {
        super.onDestroy()
        communication.close()
    }

    fun connect(view: View) {
        val address = InetSocketAddress(
            InetAddress.getByName(serverAddress.text.toString()),
            serverPort.text.toString().toInt()
        )
        communication.subscribeToServer(address)
    }
}
