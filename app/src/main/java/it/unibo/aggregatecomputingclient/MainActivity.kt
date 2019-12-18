package it.unibo.aggregatecomputingclient

import adapters.protelis.ProtelisAdapter
import adapters.protelis.ProtelisNetworkManager
import adapters.protelis.SimpleProtelisContext
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import communication.MessageType
import it.unibo.aggregatecomputingclient.adapters.protelis.ClientNetworkManager
import it.unibo.aggregatecomputingclient.adapters.protelis.HelloContext
import it.unibo.aggregatecomputingclient.devices.Client
import it.unibo.aggregatecomputingclient.devices.Server
import kotlinx.android.synthetic.main.activity_main.*
import java.net.InetAddress

class MainActivity : AppCompatActivity() {
    private lateinit var client: Client
    private var server: Server? = null
    private var listener: ClientCommunication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onDestroy() {
        super.onDestroy()
        listener?.stop()
    }

    @Suppress("UNUSED_PARAMETER")
    fun connect(view: View) {
        server = Server(
            InetAddress.getByName(serverAddress.text.toString()),
            serverPort.text.toString().toInt()
        )

        client = Client(applicationContext) {
            ProtelisAdapter(it, resources.getResourceName(R.raw.hello), ::HelloContext) { c ->
                ClientNetworkManager(
                    c as Client,
                    server!!
                )
            }
        }

        if (listener == null) {
            listener = SocketClientCommunication(client) {
                when (it.type) {
                    MessageType.ID -> client.assignId(it.content as Int)
                    MessageType.Execute -> client.execute()
                    MessageType.Status -> client.status.add(it)
                    else -> {
                    }
                }
            }

            listener!!.listen()

            listener!!.subscribeToServer(server!!)
        }
    }
}