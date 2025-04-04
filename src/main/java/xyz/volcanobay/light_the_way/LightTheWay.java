package xyz.volcanobay.light_the_way;

import com.mojang.logging.LogUtils;
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
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Mod(LightTheWay.MODID)
public class LightTheWay {
    public static final String MODID = "light_the_way";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final RegistrationProvider<SoundEvent> SOUNDS = RegistrationProvider.get(Registries.SOUND_EVENT, MODID);
    public static final RegistryObject<SoundEvent> FLASHLIGHT_TURN_ON = registerSoundEvent("flashlight_on");
    public static final RegistryObject<SoundEvent> FLASHLIGHT_TURN_OFF = registerSoundEvent("flashlight_off");

    public static final DeferredItem<FlashlightItem> FLASHLIGHT = ITEMS.register("flashlight", () -> new FlashlightItem(new Item.Properties().stacksTo(1)));

    private static final HashMap<UUID, Lights> lightMap = new HashMap<>();

    public static final ModelResourceLocation FLASHLIGHT_OFF = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(MODID, "item/flashlight_off")
            );


    public LightTheWay(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUNDS.register(ResourceLocation.fromNamespaceAndPath(MODID,name), () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID,name)));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void tickClient(RenderLevelStageEvent event) {
            List<UUID> removeLights = new ArrayList<>(lightMap.keySet());
            float partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(true);
            ClientLevel level = Minecraft.getInstance().level;
            for (AbstractClientPlayer player : level.players()) {
                if (player.getMainHandItem().is(FLASHLIGHT) || player.getOffhandItem().is(FLASHLIGHT)) {
                    ItemStack stack;
                    boolean rightHand = true;
                    if (player.getMainHandItem().is(FLASHLIGHT)) {
                        stack = player.getMainHandItem();
                    } else {
                        stack = player.getOffhandItem();
                        rightHand = false;
                    }
                    CustomData data = stack.get(DataComponents.CUSTOM_DATA);
                    boolean enabled = false;
                    CompoundTag tag = new CompoundTag();
                    if (data != null) {
                        enabled = data.copyTag().getBoolean("enabled");
                    }
                    if (enabled) {
                        Lights lights = lightMap.computeIfAbsent(player.getUUID(), (uuid) -> new Lights());

                        Vec3 forward = rotateY(Mth.PI/2f,new Vec3(
                                Math.cos(Math.toRadians(player.getViewYRot(partialTicks))),
                                0,
                                Math.sin(Math.toRadians(player.getViewYRot(partialTicks)))
                        ));
                        Vec3 pos = player.getPosition(partialTicks).add(0, 1.35f, 0).add(
                                rotateY(Mth.PI/2 * (rightHand ? 1 : -1),forward.scale(0.3f)).add(forward.scale(-0.2)));
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
    }

    private static Vec3 rotateY(double angle, Vec3 original) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vec3(
                original.z* sin + original.x * cos,
                original.y,
                original.z * cos - original.x * sin
        );
    }


    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvenstBus {
        @SubscribeEvent
        public static void registerAdditional(ModelEvent.RegisterAdditional event) {

            // An inventory model example
//            event.register(ModelResourceLocation.inventory(
//                    ResourceLocation.fromNamespaceAndPath("examplemod", "item/example_unused_inventory_model")
//            ));

            // A standalone model example
            event.register(FLASHLIGHT_OFF);
        }

        @SubscribeEvent
        public static void buildCreativeModeTabs(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                event.accept(FLASHLIGHT.get());
            }
        }
    }
}
