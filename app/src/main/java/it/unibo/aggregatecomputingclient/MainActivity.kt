package it.unibo.aggregatecomputingclient

import adapters.protelis.ProtelisAdapter
import adapters.protelis.ProtelisNetworkManager
import adapters.protelis.SimpleProtelisContext
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import it.unibo.aggregatecomputingclient.devices.Client
import it.unibo.aggregatecomputingclient.devices.Server
import kotlinx.android.synthetic.main.activity_main.*
import java.net.InetAddress

class MainActivity : AppCompatActivity() {
    private lateinit var client: Client
    private var server: Server? = null
    private lateinit var clientCommunication: ClientCommunication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*client = Client(applicationContext) {
            ProtelisAdapter(it, Execution.moduleName, ::ProtelisNetworkManager, ::SimpleProtelisContext)
        }
        clientCommunication = SocketClientCommunication(client,
            idHandler = { client.assignId(it) },
            executeHandler = { client.execute() }
        )

        clientCommunication.listen()*/
    }

    override fun onDestroy() {
        super.onDestroy()
        clientCommunication.stop()
    }

    @Suppress("UNUSED_PARAMETER")
    fun connect(view: View) {
        server = Server(
            InetAddress.getByName(serverAddress.text.toString()),
            serverPort.text.toString().toInt()
        )
        clientCommunication.subscribeToServer(server!!)
    }
}