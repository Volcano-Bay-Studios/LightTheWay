package xyz.volcanobay.light_the_way;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.AreaLight;
import foundry.veil.api.client.render.light.renderer.LightRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;

public class Lights {
    private static LightRenderer renderer = VeilRenderSystem.renderer().getLightRenderer();

    private final AreaLight[] lights = new AreaLight[4];
    public Lights() {
        lights[0] = new AreaLight();
        lights[1] = new AreaLight();
        lights[2] = new AreaLight();
        lights[3] = new AreaLight();
        for (AreaLight light : lights) {
            renderer.addLight(light);
        }
    }

    public void update(Vector3f pos, float rotX, float rotY) {
        for (AreaLight light : lights) {
            light.setPosition(new Vector3d(pos.x,pos.y,pos.z));
            light.setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(-rotX),(float) Math.toRadians(rotY),0));
            light.setDistance(Constants.distance);
            light.setAngle(0.1f);
            light.setSize(1,1);
            light.setSize(0.5f,0.5f);
        }
        lights[0].setSize(0.4f,0.4f);
        lights[1].setBrightness(0.5f);
        lights[2].setAngle(1f);
        lights[2].setDistance(Constants.distance-Constants.distance/6f);
        lights[2].setBrightness(0.4f);
        lights[3].setBrightness(-0.5f);
        lights[3].setSize(0.15f,0.15f);
    }

    public void remove() {
        for (AreaLight light : lights) {
            renderer.removeLight(light);
        }
    }
}
