package communication

import communication.Message
import java.net.InetSocketAddress
import java.nio.channels.AsynchronousSocketChannel

interface Communication {
    fun AsynchronousSocketChannel.extractMessage() : Message

    /**
     * Starts a listener to be able to receive Messages from the Server
     */
    fun listen()
    fun close()

    fun subscribeToServer(serverAddress: InetSocketAddress)

    fun Message.handle()
}