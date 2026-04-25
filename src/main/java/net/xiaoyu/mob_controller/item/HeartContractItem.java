package net.xiaoyu.mob_controller.item;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.xiaoyu.mob_controller.MobController;
import net.xiaoyu.mob_controller.util.MobControlledData;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

import java.util.List;
import java.util.UUID;

/**
 * 心变契约物品。
 *
 * <p>用于由控制者主动解除对目标生物的控制状态。</p>
 */

public class HeartContractItem extends Item {
    /**
     * 构造心变契约物品。
     *
     * @param properties 物品属性
     */
    public HeartContractItem(Properties properties) {
        super(properties);
    }

    /**
     * 对生物使用时尝试解除控制关系。
     *
     * @param stack  手持物品堆
     * @param player 操作玩家
     * @param target 目标实体
     * @param hand   交互手
     * @return 交互结果
     */
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof Mob mob)) {
            return InteractionResult.PASS;
        }

        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!MobControlledData.isControlledEntity(mob)) {
            return InteractionResult.PASS;
        }

        UUID controllerUUID = MobControlledData.getControllerUUID(mob);
        if (controllerUUID == null || !controllerUUID.equals(player.getUUID())) {
            return InteractionResult.FAIL;
        }

        mob.setTarget(null);
        MobControlledData.releaseControl(mob);

        mob.persistenceRequired = false;

        //if (mob instanceof Piglin piglin) {
        //    piglin.setImmuneToZombification(false);
        //} else if (mob instanceof Hoglin hoglin) {
        //    hoglin.setImmuneToZombification(false);
        //}

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("mob_controller.tooltip.heartcontractitem").withStyle(ChatFormatting.AQUA));
    }
}
