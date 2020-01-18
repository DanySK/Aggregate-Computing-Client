package it.unibo.aggregatecomputingclient.adapters.protelis

import adapters.protelis.ProtelisContext
import devices.Device
import org.protelis.vm.NetworkManager

class HelloContext(private val device: Device, networkManager: NetworkManager) : ProtelisContext(device, networkManager) {
    override fun instance(): ProtelisContext = HelloContext(device, networkManager)

    fun announce(something: String) = println("${device.id} - $something")
}