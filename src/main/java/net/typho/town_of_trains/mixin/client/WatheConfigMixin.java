package net.typho.town_of_trains.mixin.client;

import dev.doctor4t.wathe.WatheConfig;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WatheConfig.class)
public abstract class WatheConfigMixin {
    @Inject(
            method = "writeChanges",
            at = @At("HEAD"),
            cancellable = true
    )
    private void stopIfItClientIsNull(String modid, CallbackInfo ci) {
        if (MinecraftClient.getInstance() == null)
            ci.cancel();
    }
}
