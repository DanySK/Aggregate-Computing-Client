package it.unibo.aggregatecomputingclient

import adapters.scafi.AbstractAggregateProgram
import adapters.scafi.ScafiAdapter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import communication.MessageType
import devices.VirtualDevice
import it.unibo.aggregatecomputingclient.devices.Client
import it.unibo.aggregatecomputingclient.devices.Server
import kotlinx.android.synthetic.main.activity_main.*
import server.Support
import server.Topology
import java.net.InetAddress
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var client: Client
    private var server: Server? = null
    private var listener: ClientCommunication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        thread {
//            repeat(5) {
//                val d = Support.devices.createAndAddDevice { id ->
//                    VirtualDevice(id).apply {
//                        adapter = ScafiAdapter(this, object : AbstractAggregateProgram() {
//                            override fun main(): Any = mid()
//                        })
//                    }
//                }
//            }
//            Support.devices.finalize(Topology.Line)
//            Support.execute()
//        }
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
            resultText.text = "$it\n" + resultText.text.toString()
        } /*{
            ProtelisAdapter(it, "module hello\n\n1", ::HelloContext) { c ->
                ClientNetworkManager(
                    c as Client,
                    server!!
                )
            }
        }*/

        if (listener == null) {
            listener = SocketClientCommunication(client) {
                when (it.type) {
                    MessageType.ID -> client.assignId(it.content as Int)
                    //MessageType.Execute -> client.execute()
                    //MessageType.Status -> client.status.add(it)
                    MessageType.Result -> runOnUiThread { client.showResult(it.content.toString()) }
                    else -> { println(it) }
                }
            }

            listener!!.listen()

            listener!!.subscribeToServer(server!!)
        }
    }
}