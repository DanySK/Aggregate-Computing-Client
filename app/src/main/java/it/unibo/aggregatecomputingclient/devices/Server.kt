package it.unibo.aggregatecomputingclient.devices

import communication.Message
import communication.MessageType
import communication.SocketCommunication
import devices.InternetDevice
import java.net.InetAddress
import java.net.InetSocketAddress

class Server(address: InetAddress, port: Int) : InternetDevice {
    override val address = InetSocketAddress(address, port)
    override val physicalDevice = SocketCommunication(this)
    override val name: String = "Server"

    override val id: Int = -1
    override var status: MutableSet<Message> = mutableSetOf()

    override fun execute() {

    }

    override fun tell(message: Message) = physicalDevice.send(message)
    override fun showResult(result: String) = tell(Message(id, MessageType.Result, result))

}