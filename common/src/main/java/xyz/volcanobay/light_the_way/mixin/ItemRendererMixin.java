package xyz.volcanobay.light_the_way.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import xyz.volcanobay.light_the_way.LightTheWay;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Final
    @Shadow
    private ItemModelShaper itemModelShaper;

    @WrapOperation(method = "getModel",at  = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemModelShaper;getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;"))
    private BakedModel getModel(ItemModelShaper instance, ItemStack stack, Operation<BakedModel> original) {
        if (stack.is(LightTheWay.FLASHLIGHT.get())) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            boolean enabled = false;
            CompoundTag tag = new CompoundTag();
            if (data != null) {
                enabled = data.copyTag().getBoolean("enabled");
            }
            if (!enabled) {
                return itemModelShaper.getModelManager().getModel(LightTheWay.FLASHLIGHT_OFF);
            }
        }
        return original.call(instance,stack);
    }
}
