package net.typho.town_of_trains

import dev.doctor4t.trainmurdermystery.index.TMMBlocks
import dev.doctor4t.trainmurdermystery.index.TMMProperties
import foundry.veil.api.client.color.Color
import net.typho.vibrancy.api.BlockStateFunction
import net.typho.vibrancy.api.DynamicLightInfo
import org.joml.Vector3f
import java.util.*

object VibrancyCompat {
    fun init() {
        DynamicLightInfo.put(
            TMMBlocks.TRIMMED_LANTERN,
            DynamicLightInfo(
                Optional.of(BlockStateFunction(Color(1f, 0.98f, 0.89f))),
                Optional.of(
                    BlockStateFunction(
                        0f,
                        BlockStateFunction.Entry(
                            mapOf(
                                Pair("lit", true),
                                Pair(TMMProperties.ACTIVE.name, true)
                            ), 8f
                        )
                    )
                ),
                Optional.of(BlockStateFunction(0.75f)),
                Optional.of(BlockStateFunction(Vector3f(0.5f, 0.125f, 0.5f)))
            )
        )
        DynamicLightInfo.put(
            TMMBlocks.WALL_LAMP,
            DynamicLightInfo(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(BlockStateFunction(Vector3f(0.5f, 0.5f, 0.5f)))
            )
                .copy(TMMBlocks.TRIMMED_LANTERN)
        )
        DynamicLightInfo.put(
            TMMBlocks.NEON_TUBE,
            DynamicLightInfo().copy(TMMBlocks.WALL_LAMP)
        )
    }
}