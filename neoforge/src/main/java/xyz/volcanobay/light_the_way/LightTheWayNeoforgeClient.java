package xyz.volcanobay.light_the_way;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = LightTheWay.MODID, dist = Dist.CLIENT)
public class LightTheWayNeoforgeClient {
    public LightTheWayNeoforgeClient(IEventBus modEventBus) {
        LightTheWay.clientInit();
    }
}
