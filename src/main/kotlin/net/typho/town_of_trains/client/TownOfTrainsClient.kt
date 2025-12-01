package net.typho.town_of_trains.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.typho.town_of_trains.gui.TownConfigScreen
import org.lwjgl.glfw.GLFW

object TownOfTrainsClient : ClientModInitializer {
    val OPEN_CONFIG: KeyBinding = KeyBindingHelper.registerKeyBinding(KeyBinding("key.town_of_trains.config", GLFW.GLFW_KEY_C, "key.categories.town_of_trains"))

    override fun onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            while (OPEN_CONFIG.wasPressed()) {
                client.setScreen(TownConfigScreen())
            }
        }
    }
}
