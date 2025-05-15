package xyz.volcanobay.light_the_way;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class LightTheWayFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        LightTheWay.init();
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries ->
                entries.accept(LightTheWay.FLASHLIGHT.get()
                ));
    }
}
