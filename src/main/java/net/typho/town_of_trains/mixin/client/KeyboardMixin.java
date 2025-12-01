package net.typho.town_of_trains.mixin.client;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.typho.town_of_trains.config.TownOfTrainsConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Keyboard.class, priority = 1500)
public class KeyboardMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @TargetHandler(
            mixin = "dev.doctor4t.trainmurdermystery.mixin.client.restrictions.KeyboardMixin",
            name = "tmm$disableF3Keybinds"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/trainmurdermystery/client/TMMClient;isPlayerAliveAndInSurvival()Z"
            ),
            remap = false
    )
    private boolean disableF3Keybinds(Operation<Boolean> original) {
        return !TownOfTrainsConfig.INSTANCE.isDebugScreenEnabled(client.player);
    }
}
