package net.typho.town_of_trains.cca

import dev.doctor4t.trainmurdermystery.entity.PlayerBodyEntity
import dev.doctor4t.trainmurdermystery.game.GameConstants
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import org.ladysnake.cca.api.v3.component.ComponentV3
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent
import java.util.*

class PlayerBodyInfoComponent(var holder: PlayerBodyEntity) : ComponentV3, ServerTickingComponent, AutoSyncedComponent {
    var suspects = LinkedList<UUID>()
    var reason: Identifier = GameConstants.DeathReasons.GENERIC

    override fun serverTick() {
        val newSuspects = holder.world.getEntitiesByClass(
            PlayerEntity::class.java,
            Box.from(holder.pos).expand(2.0)
        ) { player -> !player.isSpectator && !suspects.contains(player.uuid) }

        if (!newSuspects.isEmpty()) {
            for (player in newSuspects) {
                suspects.add(player.uuid)
            }

            sync()
        }
    }

    fun sync() {
        ModComponents.PLAYER_BODY_INFO.sync(holder)
    }

    override fun readFromNbt(nbt: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
        suspects.clear()
        val list = nbt.getList("Suspects", NbtElement.STRING_TYPE.toInt())

        if (list != null) {
            for (uuid in list) {
                suspects.add(UUID.fromString(uuid.asString()))
            }
        }

        reason = Identifier.of(nbt.getString("Reason"))
    }

    override fun writeToNbt(nbt: NbtCompound, lookup: RegistryWrapper.WrapperLookup) {
        val list = NbtList()

        for (uuid in suspects) {
            list.add(NbtString.of(uuid.toString()))
        }

        nbt.put("Suspects", list)
        nbt.put("Reason", NbtString.of(reason.toString()))
    }

    companion object {
        fun PlayerBodyEntity.getBodyInfo(): PlayerBodyInfoComponent = ModComponents.PLAYER_BODY_INFO.get(this)
    }
}