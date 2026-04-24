package net.xiaoyu.mob_controller.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.xiaoyu.mob_controller.util.MobControlledData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WanderingTrader.class)
public abstract class MixinWanderingTrader {

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        WanderingTrader trader = (WanderingTrader) (Object) this;
        // 仅当流浪商人受控且控制者是当前玩家时，才检查潜行
        if (MobControlledData.isControlledEntity(trader) &&
                MobControlledData.getControllerUUID(trader) != null &&
                MobControlledData.getControllerUUID(trader).equals(player.getUUID())) {
            if (player.isShiftKeyDown()) {
                // 玩家潜行，阻止交易界面打开
                cir.setReturnValue(InteractionResult.PASS);
            }
        }
    }
}