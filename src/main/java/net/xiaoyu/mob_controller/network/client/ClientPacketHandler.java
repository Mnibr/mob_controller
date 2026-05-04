package net.xiaoyu.mob_controller.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.xiaoyu.mob_controller.capability.MobControlCapabilityProvider;
import net.xiaoyu.mob_controller.network.MobControlCapabilitySyncPacket;
import net.xiaoyu.mob_controller.network.SyncLegionColorPacket;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ClientPacketHandler {

    private static final Map<UUID, Integer> LEGION_COLORS = new ConcurrentHashMap<>();

    public static void handleMobControlCapabilitySync(Supplier<NetworkEvent.Context> ctx, MobControlCapabilitySyncPacket packet) {
        if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) return;
        ctx.get().setPacketHandled(true);

        Player player = Minecraft.getInstance().player;
        if (player != null && player.level().getEntity(packet.entityId()) instanceof Mob mob) {
            mob.getCapability(MobControlCapabilityProvider.MOB_CONTROL_CAPABILITY)
                    .ifPresent(cap -> cap.deserializeNBT(packet.entityCap()));
        }
    }

    public static void handlePlaySound(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.05F, 1.0F);
        });
        ctx.get().setPacketHandled(true);
    }

    public static void handleSyncLegionColor(SyncLegionColorPacket packet) {
        LEGION_COLORS.put(packet.playerUUID(), packet.colorRGB());
    }

    public static int getLegionColorRGB(UUID controllerUUID) {
        return LEGION_COLORS.getOrDefault(controllerUUID, -1);
    }

    public static void removePlayerColor(UUID playerUUID) {
        LEGION_COLORS.remove(playerUUID);
    }
}