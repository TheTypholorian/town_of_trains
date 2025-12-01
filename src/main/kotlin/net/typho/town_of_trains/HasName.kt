package net.typho.town_of_trains

import net.minecraft.text.Text

interface HasName {
    fun getName(): Text

    fun getDesc(): Text?
}