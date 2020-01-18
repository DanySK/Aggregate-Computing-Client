package it.unibo.aggregatecomputingclient.devices

import adapters.Adapter
import android.content.Context
import android.net.wifi.WifiManager
import android.widget.Toast
import communication.Message
import communication.SocketCommunication
import devices.InternetDevice
import devices.VirtualDevice
import it.unibo.aggregatecomputingclient.Execution
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.SocketAddress

class Client(private val context: Context, private val onResult: (String) -> Unit) : InternetDevice {
    override var id: Int = -1

    private lateinit var virtualDevice: VirtualDevice

    fun assignId(value: Int) {
        println("id set to $value")
        virtualDevice = VirtualDevice(value)
        id = value
    }

    private fun getLocalIpAddress(): InetAddress {
        val ip = (context.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo.ipAddress
        return InetAddress.getByName(
            String.format(
                "%d.%d.%d.%d", ip and 0xff, ip shr 8 and 0xff, ip shr 16 and 0xff, ip shr 24 and 0xff
            ))
    }

    override val address: SocketAddress = InetSocketAddress(getLocalIpAddress(), Execution.listenPort)

    override val physicalDevice = SocketCommunication(this)
    override var status: MutableSet<Message>
        get() = virtualDevice.status
        set(value) { virtualDevice.status = value }

    override fun execute() = virtualDevice.execute()
    override fun tell(message: Message) = virtualDevice.tell(message)

    override fun showResult(result: String) {
        onResult(result)
    }
}