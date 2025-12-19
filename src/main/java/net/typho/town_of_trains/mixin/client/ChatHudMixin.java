package net.typho.town_of_trains.mixin.client;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.typho.town_of_trains.config.TownOfTrainsConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ChatHud.class, priority = 1500)
public class ChatHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @TargetHandler(
            mixin = "dev.doctor4t.wathe.mixin.client.restrictions.ChatHudMixin",
            name = "wathe$disableChatRender"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/wathe/client/WatheClient;isPlayerAliveAndInSurvival()Z"
            ),
            remap = false
    )
    private boolean disableChatRender(Operation<Boolean> original) {
        return !TownOfTrainsConfig.INSTANCE.isChatEnabled(client.player);
    }
}
