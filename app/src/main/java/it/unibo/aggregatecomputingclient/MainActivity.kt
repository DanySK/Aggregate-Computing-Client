package it.unibo.aggregatecomputingclient

import adapters.scafi.AbstractAggregateProgram
import adapters.scafi.ScafiAdapter
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import communication.Message
import communication.MessageType
import devices.implementations.RemoteDevice
import devices.implementations.VirtualDevice
import kotlinx.android.synthetic.main.activity_main.*
import server.Execution
import server.Support
import utils.FromKotlin.*
import java.net.InetAddress
import java.net.InetSocketAddress
import kotlin.concurrent.thread

const val port = 20000

class MainActivity : AppCompatActivity() {
    //A Client works exactly as a VirtualDevice
    private lateinit var client: VirtualDevice
    private lateinit var server: RemoteDevice
    private var listener: ClientCommunication? = null

    private var resultStrings = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultView.layoutManager = LinearLayoutManager(this)
        resultView.adapter = ResultsAdapter(resultStrings)
    }

    override fun onDestroy() {
        super.onDestroy()
        listener?.stop()
    }

    private fun getLocalIpAddress(): InetAddress {
        val ip = (getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo.ipAddress
        return InetAddress.getByName(
            String.format(
                "%d.%d.%d.%d", ip and 0xff, ip shr 8 and 0xff, ip shr 16 and 0xff, ip shr 24 and 0xff
            ))
    }

    @Suppress("UNUSED_PARAMETER")
    fun connect(view: View) {
        val serverAddress = InetSocketAddress(
            InetAddress.getByName(serverAddress.text.toString()), serverPort.text.toString().toInt()
        )
        thread {
            server = RemoteDevice(Support.id, serverAddress, Support.name)

            Execution.adapter = { ScafiAdapter(it, Program(), server) }

            if (listener == null) {
                listener = SocketClientCommunication(
                    InetSocketAddress(getLocalIpAddress(), port),
                    onID = { id ->
                        client = VirtualDevice(id, "Me", Execution.adapter, { result ->
                            resultStrings.add(result.toString())
                            runOnUiThread { resultView.adapter!!.notifyDataSetChanged() }
                        })

                        runOnUiThread { btnGoLightweight.isEnabled = true }
                    },
                    onMessage = { client.tell(it) }
                )
                listener!!.listen()
                listener!!.subscribeToServer(server)
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun goLightWeight(view: View) {
        thread { server.tell(Message(client.id, MessageType.GoLightWeight, true)) }

        runOnUiThread {
            btnGoLightweight.isEnabled = false
            btnLeaveLightweight.isEnabled = true
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun leaveLightWeight(view: View) {
        thread { server.tell(Message(client.id, MessageType.LeaveLightWeight)) }

        runOnUiThread {
            btnGoLightweight.isEnabled = true
            btnLeaveLightweight.isEnabled = false
        }
    }
}

class Program : AbstractAggregateProgram() {
    //override fun main(): Any = this.mid()

    private fun isMe(): Boolean = nbr(def0 { mid() }) == mid()
    override fun main(): Any = foldhood(
        def0 { listOf<Int>() },
        def2 { l1, l2 -> l1 + l2 },
        def0 { mux(isMe(), listOf<Int>(), listOf(nbr(def0 { mid() as Int }))) }
    )
}