package it.unibo.aggregatecomputingclient

import communication.Message
import communication.MessageType
import it.unibo.aggregatecomputingclient.devices.Client
import it.unibo.aggregatecomputingclient.devices.Server
import kotlin.concurrent.thread

class SocketClientCommunication(private val client: Client,
                                private val handler: (Message) -> Unit) : ClientCommunication {

    override fun subscribeToServer(server: Server) {
        thread {
            server.tell(Message(client.id, MessageType.Join, Execution.listenPort))
            println("sent join message to ${server.address}")
        }
    }

    override fun listen() = client.physicalDevice.startServer { handler(client.physicalDevice.extractMessage(it)) }
    override fun stop() = client.physicalDevice.stopServer()
}