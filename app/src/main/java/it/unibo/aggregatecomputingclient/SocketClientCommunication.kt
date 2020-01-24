package it.unibo.aggregatecomputingclient

import android.app.Activity
import communication.Message
import communication.MessageType
import it.unibo.aggregatecomputingclient.devices.Client
import it.unibo.aggregatecomputingclient.devices.Server
import kotlin.concurrent.thread

class SocketClientCommunication(private val client: Client) : ClientCommunication {

    override fun subscribeToServer(server: Server) {
        thread {
            server.tell(Message(client.id, MessageType.Join, Execution.listenPort))
            println("sent join message to ${server.address}")
        }
    }

    override fun clientCallback(message: Message) {
        when (message.type) {
            MessageType.ID -> client.assignId(message.content as Int)
            MessageType.Execute -> client.execute()
            //MessageType.Status -> client.status.add(it)
            MessageType.Result -> (client.context as Activity).runOnUiThread { client.showResult(message.content.toString()) }
            else -> { println(message) }
        }
    }

    override fun listen() = client.physicalDevice.startServer { clientCallback(client.physicalDevice.extractMessage(it)) }
    override fun stop() = client.physicalDevice.stopServer()
}