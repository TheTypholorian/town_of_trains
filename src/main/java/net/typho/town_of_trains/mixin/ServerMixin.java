package net.typho.town_of_trains.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.maxhenkel.voicechat.voice.common.Packet;
import de.maxhenkel.voicechat.voice.common.SoundPacket;
import de.maxhenkel.voicechat.voice.server.ClientConnection;
import de.maxhenkel.voicechat.voice.server.Server;
import dev.doctor4t.trainmurdermystery.cca.PlayerMoodComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.typho.town_of_trains.voice.StandardVoiceStuffKt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Server.class)
public abstract class ServerMixin {
    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void storeInstance(MinecraftServer server, CallbackInfo ci) {
        StandardVoiceStuffKt.SERVER_INSTANCE = (Server) (Object) this;
    }

    @WrapOperation(
            method = "sendSoundPacket",
            at = @At(value = "INVOKE", target = "Lde/maxhenkel/voicechat/voice/server/Server;sendPacket(Lde/maxhenkel/voicechat/voice/common/Packet;Lde/maxhenkel/voicechat/voice/server/ClientConnection;)Z")
    )
    private boolean echoIfMidMoodOrNoIfDepressed(Server instance, Packet<?> packet, ClientConnection connection, Operation<Boolean> original) {
        var player = instance.getServer().getPlayerManager().getPlayer(connection.getPlayerUUID());
        if (!PlayerMoodComponent.KEY.isProvidedBy(player) || !PlayerMoodComponent.KEY.get(player).isLowerThanMid() || !(packet instanceof SoundPacket<?>))
            return original.call(instance, packet, connection);
        if (PlayerMoodComponent.KEY.get(player).isLowerThanDepressed()) {
            return false;
        }
        StandardVoiceStuffKt.startEchoing(player, (SoundPacket<?>) packet);
        return false;
    }
}
