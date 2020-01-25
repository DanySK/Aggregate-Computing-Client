package it.unibo.aggregatecomputingclient

import adapters.scafi.AbstractAggregateProgram
import adapters.scafi.ScafiAdapter
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import devices.server.RemoteDevice
import devices.server.VirtualDevice
import kotlinx.android.synthetic.main.activity_main.*
import server.Support
import java.net.InetAddress
import java.net.InetSocketAddress
import kotlin.concurrent.thread

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
        }

        val program = object : AbstractAggregateProgram() {
            override fun main(): Any = mid()
        }

        if (listener == null) {
            val address = InetSocketAddress(getLocalIpAddress(), Execution.listenPort)

            listener = SocketClientCommunication(
                address,
                onID = { id ->
                    client = VirtualDevice(id, "Me", { ScafiAdapter(it, program, server) }, { result ->
                        resultStrings.add(result)
                        runOnUiThread { resultView.adapter!!.notifyDataSetChanged() }
                    })
                },
                onMessage = { client.tell(it) }
            )
            listener!!.listen()
            listener!!.subscribeToServer(server)
        }
    }
}