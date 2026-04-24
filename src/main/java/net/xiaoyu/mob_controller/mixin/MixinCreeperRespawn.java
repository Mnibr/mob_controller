package net.xiaoyu.mob_controller.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.xiaoyu.mob_controller.Config;
import net.xiaoyu.mob_controller.util.MobControlledData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 让被控制的苦力怕爆炸后也能进入重生队列。
 * 在 explodeCreeper 开头将苦力怕加入待重生列表，
 * 并主动向控制者发送复活倒计时消息。
 */
@Mixin(Creeper.class)
public abstract class MixinCreeperRespawn {

    @Inject(method = "explodeCreeper", at = @At("HEAD"))
    private void onExplodeCreeper(CallbackInfo ci) {
        Creeper creeper = (Creeper) (Object) this;
        if (MobControlledData.isControlledEntity(creeper) && creeper.level() instanceof ServerLevel serverLevel) {
            // 主动获取控制者并发送复活提醒消息
            Player controller = MobControlledData.getController(creeper, serverLevel);
            if (controller instanceof ServerPlayer serverPlayer) {
                int seconds = Config.RESPAWN_DELAY_TICKS.get() / 20;
                serverPlayer.sendSystemMessage(Component.translatable(
                        "mob_controller.message.respawn_scheduled",
                        creeper.getDisplayName(),
                        seconds
                ));
            }
            // 将苦力怕加入重生队列（内部会保存数据，但可能不发消息）
            MobControlledData.scheduleRespawn(creeper, serverLevel);
        }
    }
}