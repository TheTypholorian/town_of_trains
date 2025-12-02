package net.typho.town_of_trains.mixin.client;

import dev.doctor4t.trainmurdermystery.client.gui.RoleAnnouncementTexts;
import dev.doctor4t.trainmurdermystery.client.gui.RoundTextRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.typho.town_of_trains.roles.AbstractRole;
import net.typho.town_of_trains.roles.ModRoles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(RoundTextRenderer.class)
public class RoundTextRendererMixin {
    @Shadow
    private static RoleAnnouncementTexts.RoleAnnouncementText role;

    @Inject(
            method = "renderHud",
            at = @At("HEAD")
    )
    private static void renderHud(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, CallbackInfo ci) {
        AbstractRole role = ModRoles.INSTANCE.getRole(Objects.requireNonNull(MinecraftClient.getInstance().player));

        if (role != null) {
            RoundTextRendererMixin.role = role.getAnnouncementText();
        }
    }
}
