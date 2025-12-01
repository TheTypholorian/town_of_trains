package net.typho.town_of_trains.config

import net.minecraft.text.Text

interface ToText<T> {
    fun toText(value: T): Text

    fun toDesc(value: T): Text?

    class ToString<T> : ToText<T> {
        override fun toText(value: T): Text = Text.literal(value.toString())

        override fun toDesc(value: T): Text? = null
    }

    class HasName<T: net.typho.town_of_trains.HasName> : ToText<T> {
        override fun toText(value: T): Text = value.getName()

        override fun toDesc(value: T): Text? = value.getDesc()
    }
}