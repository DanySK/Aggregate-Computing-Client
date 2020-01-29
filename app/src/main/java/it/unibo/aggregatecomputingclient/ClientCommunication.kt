package it.unibo.aggregatecomputingclient

import devices.implementations.RemoteDevice

interface ClientCommunication {
    /**
     * Starts a listener for Server Messages
     */
    fun listen()
    fun stop()

    /**
     * Tell the Server this Device wants to join the net
     */
    fun subscribeToServer(server: RemoteDevice)
}