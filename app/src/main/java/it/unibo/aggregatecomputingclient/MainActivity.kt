package it.unibo.aggregatecomputingclient

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import it.unibo.aggregatecomputingclient.devices.Client
import it.unibo.aggregatecomputingclient.devices.Server
import kotlinx.android.synthetic.main.activity_main.*
import java.net.InetAddress

class MainActivity : AppCompatActivity() {
    private lateinit var client: Client
    private var server: Server? = null
    private var listener: ClientCommunication? = null

    private var resultStrings = mutableListOf<String>("1","2")

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

    @Suppress("UNUSED_PARAMETER")
    fun connect(view: View) {
        server = Server(
            InetAddress.getByName(serverAddress.text.toString()),
            serverPort.text.toString().toInt()
        )

        client = Client(applicationContext) {
            //add the result to the view
            resultStrings.add(it)
            resultView.adapter!!.notifyDataSetChanged()
        }

        if (listener == null) {
            listener = SocketClientCommunication(client)
            listener!!.listen()
            listener!!.subscribeToServer(server!!)
        }
    }
}