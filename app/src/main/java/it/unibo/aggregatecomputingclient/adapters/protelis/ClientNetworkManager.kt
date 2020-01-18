package it.unibo.aggregatecomputingclient.adapters.protelis

import adapters.protelis.IntUID
import communication.Message
import communication.MessageType
import it.unibo.aggregatecomputingclient.devices.Client
import it.unibo.aggregatecomputingclient.devices.Server
import org.protelis.lang.datatype.DeviceUID
import org.protelis.vm.CodePath
import org.protelis.vm.NetworkManager

class ClientNetworkManager(private val client: Client, private val server: Server) : NetworkManager {
    /**
     * Tell the server to tell to my neighbours my status
     */
    override fun shareState(toSend: Map<CodePath, Any>) {
        if (toSend.isNotEmpty()) {
            server.tell(
                Message(
                    client.id,
                    MessageType.SendToNeighbours,
                    Message(client.id, MessageType.Status, toSend)
                )
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getNeighborState(): Map<DeviceUID, Map<CodePath, Any>> {
        return client.status
            .map { (IntUID(it.senderUid) as DeviceUID) to (it.content as Map<CodePath, Any>) }
            .toMap()
            .apply { client.status.clear() }
    }
}