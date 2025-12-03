package net.typho.town_of_trains.mixin;

import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerMoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.typho.town_of_trains.roles.AbstractRole;
import net.typho.town_of_trains.roles.ModRoles;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerMoodComponent.class)
public class PlayerMoodComponentMixin {
    @Shadow
    @Final
    private PlayerEntity player;

    @Inject(
            method = "eatFood",
            at = @At(
                    value = "FIELD",
                    target = "Ldev/doctor4t/trainmurdermystery/cca/PlayerMoodComponent$EatTask;fulfilled:Z",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void eatFood(CallbackInfo ci) {
        AbstractRole role = ModRoles.INSTANCE.getRole(player);

        if (role != null) {
            role.onTaskCompleted(player, PlayerMoodComponent.Task.EAT, GameWorldComponent.KEY.get(player.getWorld()));
        }
    }

    @Inject(
            method = "drinkCocktail",
            at = @At(
                    value = "FIELD",
                    target = "Ldev/doctor4t/trainmurdermystery/cca/PlayerMoodComponent$DrinkTask;fulfilled:Z",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void drinkCocktail(CallbackInfo ci) {
        AbstractRole role = ModRoles.INSTANCE.getRole(player);

        if (role != null) {
            role.onTaskCompleted(player, PlayerMoodComponent.Task.DRINK, GameWorldComponent.KEY.get(player.getWorld()));
        }
    }
}
