package net.typho.town_of_trains.mixin.client;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.wathe.block_entity.BeveragePlateBlockEntity;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.typho.town_of_trains.roles.AbstractRole;
import net.typho.town_of_trains.roles.ModRoles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(value = BeveragePlateBlockEntity.class, priority = 1500)
public class BeveragePlateBlockEntityMixin {
    @TargetHandler(
            mixin = "dev.doctor4t.wathe.mixin.client.self.BeveragePlateBlockEntityMixin",
            name = "tickWithoutFearOfCrashing"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/wathe/client/WatheClient;isKiller()Z"
            ),
            remap = false
    )
    private static boolean tickWithoutFearOfCrashing(Operation<Boolean> original, @Local(argsOnly = true) World world, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) BlockState state) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        AbstractRole role = ModRoles.INSTANCE.getRole(player);

        if (role == null) {
            return original.call();
        }

        return role.canSeePoison(player, world, pos, state, GameWorldComponent.KEY.get(player.getWorld()));
    }
}
