package net.typho.town_of_trains.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.world.dimension.DimensionType;
import net.typho.town_of_trains.client.VibrancyCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
    @WrapOperation(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/LightmapTextureManager;getBrightness(Lnet/minecraft/world/dimension/DimensionType;I)F",
                    ordinal = 1
            )
    )
    private float update(DimensionType type, int lightLevel, Operation<Float> original) {
        float b = original.call(type, lightLevel);

        if (VibrancyCompat.Companion.isRaytracingEnabled()) {
            b /= 2;
        }

        return b;
    }
}
