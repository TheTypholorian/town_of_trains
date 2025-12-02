package net.typho.town_of_trains.mixin;

import dev.doctor4t.trainmurdermystery.api.Role;
import net.typho.town_of_trains.roles.AbstractRole;
import net.typho.town_of_trains.roles.RoleAttacher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Role.class)
public class RoleMixin implements RoleAttacher {
    @Unique
    private AbstractRole role;

    @Override
    public @Nullable AbstractRole town_of_trains$getRole() {
        return role;
    }

    @Override
    public void town_of_trains$setRole(@NotNull AbstractRole role) {
        this.role = role;
    }
}
