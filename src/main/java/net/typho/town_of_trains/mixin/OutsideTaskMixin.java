package net.typho.town_of_trains.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.trainmurdermystery.cca.PlayerMoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.typho.town_of_trains.roles.AbstractRole;
import net.typho.town_of_trains.roles.ModRoles;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerMoodComponent.OutsideTask.class)
public class OutsideTaskMixin {
    @Shadow
    private int timer;

    @Inject(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Ldev/doctor4t/trainmurdermystery/cca/PlayerMoodComponent$OutsideTask;timer:I",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void tick(CallbackInfo ci, @Local(argsOnly = true) PlayerEntity player) {
        if (timer == 1) {
            AbstractRole role = ModRoles.INSTANCE.getRole(player);

            if (role != null) {
                role.onTaskCompleted(player, PlayerMoodComponent.Task.OUTSIDE);
            }
        }
    }
}
