package net.typho.town_of_trains.voice

import de.maxhenkel.voicechat.voice.common.SoundPacket
import de.maxhenkel.voicechat.voice.server.Server
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.server.network.ServerPlayerEntity
import net.typho.town_of_trains.event.COROUTINE_SCOPE

@JvmField
var SERVER_INSTANCE: Server? = null

fun startEchoing(player: ServerPlayerEntity, packet: SoundPacket<*>) {
    COROUTINE_SCOPE?.launch {
        for (i in 8 downTo 2 step 2) {
            delay(500)
            (packet as AmplifierAttacher).`town_of_trains$setAmplifier`(i / 10.0F)
            SERVER_INSTANCE?.sendPacket(packet, SERVER_INSTANCE?.getConnection(player.uuid))
        }
    }
}