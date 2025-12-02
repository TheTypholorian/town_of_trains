package net.typho.town_of_trains.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents

@JvmField
var COROUTINE_SCOPE: CoroutineScope? = null

fun handleStandardEvents() {
    ServerLifecycleEvents.SERVER_STARTED.register { server ->
        COROUTINE_SCOPE = CoroutineScope(SupervisorJob() + server.asCoroutineDispatcher())
    }
}