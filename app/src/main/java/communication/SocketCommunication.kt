package communication

import android.app.Activity
import android.content.Context
import android.net.wifi.WifiManager
import android.widget.Toast
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.Channels
import java.nio.channels.CompletionHandler
import kotlin.concurrent.thread

class SocketCommunication(private val context: Context, private val port: Int,
                          private val idHandler: (Int) -> Unit) : Communication {

    var listener: AsynchronousServerSocketChannel? = null

    private fun getLocalIpAddress(): InetAddress? {
        val ip = (context.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo.ipAddress
        return InetAddress.getByName(
            String.format(
                "%d.%d.%d.%d", ip and 0xff, ip shr 8 and 0xff, ip shr 16 and 0xff, ip shr 24 and 0xff
            ))
    }

    override fun AsynchronousSocketChannel.extractMessage() : Message =
        ObjectInputStream(Channels.newInputStream(this)).use {
            return it.readObject() as Message
        }

    override fun listen() {
        thread {
            listener = AsynchronousServerSocketChannel.open()
            listener!!.bind(InetSocketAddress(getLocalIpAddress(), port))
            println("channel started at ${listener!!.localAddress}")
            listener!!.accept<Any>(null, object : CompletionHandler<AsynchronousSocketChannel, Any> {
                override fun completed(clientChannel: AsynchronousSocketChannel?, attachment: Any?) {
                    if (clientChannel?.isOpen == true) {
                        clientChannel.extractMessage().handle()
                    }

                    if (listener!!.isOpen) {
                        listener!!.accept<Any>(null, this)
                    }
                }
                override fun failed(exc: Throwable, attachment: Any?) {
                    exc.printStackTrace()
                }
            })
        }
    }

    override fun close() {
        listener?.close()
    }

    override fun subscribeToServer(serverAddress: InetSocketAddress) {
        thread {
            var channel: AsynchronousSocketChannel? = null
            var output: ObjectOutputStream? = null
            try {
                channel = AsynchronousSocketChannel.open()
                val future = channel!!.connect(serverAddress)
                future.get()

                output = ObjectOutputStream(Channels.newOutputStream(channel))
                output.writeObject(Message(0, MessageType.Join, port))
            } catch (e: Exception) {
                (context as Activity).runOnUiThread {
                    Toast.makeText(
                        context,
                        "Can't reach $serverAddress",
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
            } finally {
                channel?.close()
                output?.close()
            }
        }
    }

    override fun Message.handle() {
        when(this.type) {
            MessageType.ID -> idHandler(this.content as Int)
            else -> {}
        }
    }
}