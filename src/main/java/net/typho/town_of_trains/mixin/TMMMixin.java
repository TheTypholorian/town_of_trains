package net.typho.town_of_trains.mixin;

import dev.doctor4t.trainmurdermystery.TMM;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TMM.class)
public class TMMMixin {
    @Inject(
            method = "isSupporter",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void isSupporter(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            cir.setReturnValue(true);
        }
    }
}
