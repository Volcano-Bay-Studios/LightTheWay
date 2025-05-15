package xyz.volcanobay.light_the_way.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.DaylightDetectorBlock;
import net.minecraft.world.level.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.volcanobay.light_the_way.LightTheWay;

@Mixin(DaylightDetectorBlock.class)
public abstract class DaylightDetectorBlockMixin {
    @Shadow
    private static void updateSignalStrength(BlockState pState, Level pLevel, BlockPos pPos) {}

    @WrapOperation(method = "updateSignalStrength", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(III)I"))
    private static int updateSignalStrength(int pValue, int pMin, int pMax, Operation<Integer> original, @Local(argsOnly = true) BlockPos blockPos, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockState blockState) {
        boolean inverted = (Boolean)blockState.getValue(DaylightDetectorBlock.INVERTED);

        for (Player player : level.getEntitiesOfClass(Player.class, new AABB(blockPos).inflate(64))) {
            if (player.getMainHandItem().is(LightTheWay.FLASHLIGHT.get()) || player.getOffhandItem().is(LightTheWay.FLASHLIGHT.get())) {
                ItemStack stack;
                if (player.getMainHandItem().is(LightTheWay.FLASHLIGHT.get())) {
                    stack = player.getMainHandItem();
                } else {
                    stack = player.getOffhandItem();
                }
                CustomData data = stack.get(DataComponents.CUSTOM_DATA);
                boolean enabled = false;
                if (data != null) {
                    enabled = data.copyTag().getBoolean("enabled");
                }
                if (enabled) {
                    Vec3 forward = player.getForward().normalize();
                    Vec3 view = blockPos.getCenter().subtract(player.getEyePosition(1f));
                    Vec3 pos = view.normalize();
                    float rotation = (float) forward.distanceTo(pos);
                    return (int) Math.floor(Mth.clamp(Mth.clamp(Mth.map(rotation, 1, 0, 0, 15),0,15) * (inverted ? -1 : 1) + pValue,0,15) / (Math.max(1,view.length()/8f)));
                }
            }
        }
        return Mth.clamp(pValue,pMin,pMax);
    }

    @Inject(method = "tickEntity", at = @At("HEAD"), cancellable = true)
    private static void tickEntity(Level p_153113_, BlockPos p_153114_, BlockState p_153115_, DaylightDetectorBlockEntity p_153116_, CallbackInfo ci) {
        updateSignalStrength(p_153115_, p_153113_, p_153114_);
        ci.cancel();
    }

}
