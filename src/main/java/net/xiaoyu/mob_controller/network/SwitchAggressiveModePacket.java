package net.xiaoyu.mob_controller.network;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.xiaoyu.mob_controller.registry.ModItems;
import net.xiaoyu.mob_controller.util.MobControlUtil;
import net.xiaoyu.mob_controller.util.MobControlledData;

import java.util.function.Supplier;

public record SwitchAggressiveModePacket(boolean aggressive) {
    public SwitchAggressiveModePacket(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(aggressive);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!player.getMainHandItem().is(ModItems.AGGRESSIVE_SWITCH_ITEM.get())) return;

            int affectedCount = MobControlledData.setAggressiveModeForAll(player, 32, aggressive);
            if (affectedCount == 0) {
                player.displayClientMessage(Component.translatable("mob_controller.message.no_controlled_mobs_nearby")
                        .withStyle(ChatFormatting.RED), true);
                return;
            }

            String modeKey = aggressive ? "mob_controller.mode.aggressive" : "mob_controller.mode.protective";
            MobControlUtil.showMessageToPlayer(player, Component.literal("[" + affectedCount + "]"), modeKey, new Object[]{}, ChatFormatting.GOLD);
        });
        ctx.get().setPacketHandled(true);
    }
}