package net.typho.town_of_trains.roles

import dev.doctor4t.trainmurdermystery.api.Role
import dev.doctor4t.trainmurdermystery.item.KeyItem
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier

open class TicketInspectorRole(id: Identifier, type: RoleType, role: Role) : CivilianRole(id, type, role) {
    override fun getNameTag(withRole: PlayerEntity, lookTarget: PlayerEntity, original: Text): Text {
        var room: Text = Text.translatable(getKey().toTranslationKey("role", "room.unknown"))

        for (stack in lookTarget.inventory.main) {
            if (stack.item is KeyItem) {
                val lore = stack.get(DataComponentTypes.LORE)

                if (lore != null) {
                    room = lore.lines.first()
                    break
                }
            }
        }

        return Text.translatable(getKey().toTranslationKey("role", "room"), original, room)
    }
}