package xyz.volcanobay.light_the_way;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.data.AreaLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import foundry.veil.api.client.render.light.renderer.LightRenderer;
import org.joml.*;

import java.lang.Math;

public class Lights {

    private final LightRenderHandle<AreaLightData>[] lights = new LightRenderHandle[4];
    public Lights() {
        LightRenderer lightRenderer = VeilRenderSystem.renderer().getLightRenderer();
        lights[0] = lightRenderer.addLight(new AreaLightData());
        lights[1] = lightRenderer.addLight(new AreaLightData());
        lights[2] = lightRenderer.addLight(new AreaLightData());
        lights[3] = lightRenderer.addLight(new AreaLightData());
    }

    public void update(Vector3f pos, float rotX, float rotY) {
        for (LightRenderHandle<AreaLightData> handle : lights) {
            AreaLightData light = handle.getLightData();
            light.getPosition().set(pos.x,pos.y,pos.z);
            light.getOrientation().set(new Quaternionf().rotateXYZ((float) Math.toRadians(-rotX),(float) Math.toRadians(rotY),0));
            light.setDistance(Constants.distance);
            light.setAngle(0.1f);
            light.setSize(1,1);
            light.setSize(0.5f,0.5f);
        }
        lights[0].getLightData().setSize(0.4f,0.4f);
        lights[1].getLightData().setBrightness(0.5f);
        lights[2].getLightData().setAngle(1f);
        lights[2].getLightData().setDistance(Constants.distance-Constants.distance/6f);
        lights[2].getLightData().setBrightness(0.4f);
        lights[3].getLightData().setBrightness(-0.5f);
        lights[3].getLightData().setSize(0.15f,0.15f);
    }

    public void remove() {
        for (LightRenderHandle<AreaLightData> handle : lights) {
            handle.free();
        }
    }
}
