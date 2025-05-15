package xyz.volcanobay.light_the_way;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.renderer.LightRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class FlashlightItem extends Item {
    public FlashlightItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        boolean enabled = false;
        CompoundTag tag = new CompoundTag();
        if (data != null) {
            enabled = data.copyTag().getBoolean("enabled");
        }
        if (enabled) {
            level.playSound(player, BlockPos.containing((player.getPosition(0))),LightTheWay.FLASHLIGHT_TURN_OFF.get(), SoundSource.PLAYERS);
        } else {
            level.playSound(player, BlockPos.containing((player.getPosition(0))),LightTheWay.FLASHLIGHT_TURN_ON.get(), SoundSource.PLAYERS);
        }
        tag.putBoolean("enabled", !enabled);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return super.use(level, player, hand);
    }
}
