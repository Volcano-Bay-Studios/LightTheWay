package xyz.volcanobay.light_the_way;


import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(Constants.MOD_ID)
public class LightTheWayNeoforge {

    public LightTheWayNeoforge(IEventBus eventBus) {
        LightTheWay.init();
    }

    @EventBusSubscriber(modid = LightTheWay.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvenstBus {
        @SubscribeEvent
        public static void registerAdditional(ModelEvent.RegisterAdditional event) {

            // An inventory model example
//            event.register(ModelResourceLocation.inventory(
//                    ResourceLocation.fromNamespaceAndPath("examplemod", "item/example_unused_inventory_model")
//            ));

            // A standalone model example
            LightTheWay.FLASHLIGHT_OFF = new ModelResourceLocation(
                    ResourceLocation.fromNamespaceAndPath(LightTheWay.MODID, "item/flashlight_off"),"standalone"
            );
            event.register(LightTheWay.FLASHLIGHT_OFF);
        }

        @SubscribeEvent
        public static void buildCreativeModeTabs(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                event.accept(LightTheWay.FLASHLIGHT.get());
            }
        }
    }
}
