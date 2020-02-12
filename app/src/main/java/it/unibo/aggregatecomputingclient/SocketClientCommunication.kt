package it.unibo.aggregatecomputingclient

import communication.Message
import communication.MessageType
import devices.implementations.RemoteDevice
import java.net.SocketAddress
import kotlin.concurrent.thread

/**
 * Mean of communication between this Client and the Server
 * onID: What to do when the Server tells this Client its ID
 * onMessage: What to do when this Client receives a message
 */
class SocketClientCommunication(localAddress: SocketAddress,
                                private val onID: (Int) -> Unit,
                                private val onMessage: (Message) -> Unit) : ClientCommunication {

    private val listener = RemoteDevice(-1, localAddress)

    override fun subscribeToServer(server: RemoteDevice) {
        thread {
            server.tell(Message(-1, MessageType.Join, port))
            println("sent join message to ${server.address}")
        }
    }

    override fun listen() = listener.physicalDevice.startServer {
        val message = listener.physicalDevice.extractMessage(it)
        when (message.type) {
            MessageType.ID -> onID(message.content as Int)
            else -> onMessage(message)
        }
    }

    override fun stop() = listener.physicalDevice.stopServer()
}