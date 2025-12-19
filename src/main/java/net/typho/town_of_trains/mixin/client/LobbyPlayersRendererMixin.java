package net.typho.town_of_trains.mixin.client;

import dev.doctor4t.wathe.client.gui.LobbyPlayersRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LobbyPlayersRenderer.class)
public class LobbyPlayersRendererMixin {
    @Inject(
            method = "renderHud",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
                    ordinal = 1
            ),
            cancellable = true
    )
    private static void renderCredits(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, CallbackInfo ci) {
        ci.cancel();
    }
}
