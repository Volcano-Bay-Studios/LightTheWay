package xyz.volcanobay.light_the_way;

import net.fabricmc.api.ClientModInitializer;

public class LightTheWayFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LightTheWay.clientInit();
    }
}
