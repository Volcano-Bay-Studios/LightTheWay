package xyz.volcanobay.light_the_way;

import com.mojang.logging.LogUtils;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LightTheWay {

    public static final String MODID = "light_the_way";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, MODID);
    public static final RegistrationProvider<SoundEvent> SOUNDS = RegistrationProvider.get(Registries.SOUND_EVENT, MODID);
    public static final RegistryObject<SoundEvent> FLASHLIGHT_TURN_ON = registerSoundEvent("flashlight_on");
    public static final RegistryObject<SoundEvent> FLASHLIGHT_TURN_OFF = registerSoundEvent("flashlight_off");

    public static final RegistryObject<FlashlightItem> FLASHLIGHT = ITEMS.register("flashlight", () -> new FlashlightItem(new Item.Properties().stacksTo(1)));

    private static final HashMap<UUID, Lights> lightMap = new HashMap<>();

    public static ModelResourceLocation FLASHLIGHT_OFF = null;


    public static void init() {
        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage(((stage, levelRenderer, bufferSource, matrixStack, matrix4fc, matrix4fc1, i, deltaTracker, camera, frustum) -> {
            if (stage.equals(VeilRenderLevelStageEvent.Stage.AFTER_WEATHER)) {
                renderEvent(deltaTracker.getGameTimeDeltaPartialTick(false));
            }
        }));

    }

    public static void renderEvent(float partialTicks) {
        List<UUID> removeLights = new ArrayList<>(lightMap.keySet());
        ClientLevel level = Minecraft.getInstance().level;
        for (AbstractClientPlayer player : level.players()) {
            if (player.getMainHandItem().is(FLASHLIGHT.get()) || player.getOffhandItem().is(FLASHLIGHT.get())) {
                ItemStack stack;
                boolean rightHand = true;
                boolean firstPerson = player == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson();
                if (player.getMainHandItem().is(FLASHLIGHT.get())) {
                    stack = player.getMainHandItem();
                } else {
                    stack = player.getOffhandItem();
                    rightHand = false;
                }
                CustomData data = stack.get(DataComponents.CUSTOM_DATA);
                boolean enabled = false;
                if (data != null) {
                    enabled = data.copyTag().getBoolean("enabled");
                }
                if (enabled) {
                    Lights lights = lightMap.computeIfAbsent(player.getUUID(), (uuid) -> new Lights());

                    Vec3 forward = rotateY(Mth.PI / 2f, new Vec3(
                            Math.cos(Math.toRadians(player.getViewYRot(partialTicks))),
                            0,
                            Math.sin(Math.toRadians(player.getViewYRot(partialTicks)))
                    ).scale(firstPerson ? -0.5 : 1));
                    Vec3 pos = player.getPosition(partialTicks).add(0, (firstPerson ? 1.35f : 1f) - (player.isCrouching() ? 0.3f : 0) - (player.isVisuallyCrawling() ? 1 : 0), 0).add(
                            rotateY(Mth.PI / 2 * (rightHand ? -1 : 1) * (firstPerson ? 1 : 0), forward.scale(firstPerson ? 0.3 : 0.8)).add(forward.scale(-0.2)));
                    lights.update(new Vector3f((float) pos.x, (float) pos.y, (float) pos.z), player.getViewXRot(partialTicks), player.getViewYRot(partialTicks));
                    removeLights.remove(player.getUUID());
                }
            }
        }
        for (UUID uuid : removeLights) {
            lightMap.get(uuid).remove();
            lightMap.remove(uuid);
        }
    }

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUNDS.register(ResourceLocation.fromNamespaceAndPath(MODID, name), () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, name)));
    }

    private static Vec3 rotateY(double angle, Vec3 original) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vec3(
                original.z * sin + original.x * cos,
                original.y,
                original.z * cos - original.x * sin
        );
    }
}
