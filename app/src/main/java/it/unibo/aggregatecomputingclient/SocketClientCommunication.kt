package it.unibo.aggregatecomputingclient

import communication.Message
import communication.MessageType
import it.unibo.aggregatecomputingclient.devices.Client
import it.unibo.aggregatecomputingclient.devices.Server
import kotlin.concurrent.thread

class SocketClientCommunication(private val client: Client,
                                private val idHandler: (Int) -> Unit,
                                private val executeHandler: () -> Unit) : ClientCommunication {

    override fun subscribeToServer(server: Server) {
        thread {
            server.tell(Message(client.id, MessageType.Join, Execution.listenPort))
        }
    }

    override fun listen() = client.communication.startServer { client.communication.extractMessage(it).handle() }
    override fun stop() = client.communication.stopServer()

    private fun Message.handle() {
        when(this.type) {
            MessageType.ID -> idHandler(this.content as Int)
            MessageType.Execute -> executeHandler()
            else -> {}
        }
    }
}