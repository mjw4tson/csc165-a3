package event;

import graphicslib3D.Point3D;

import java.util.Random;
import java.util.UUID;

public class NPC {
    private static final float RESTING_HEIGHT = 24.0f;

    private Point3D location;
    private UUID    uuid;

    public NPC() {
        uuid = UUID.randomUUID();
        
        Random rand = new Random();
        location = new Point3D(50 + (rand.nextInt(10) - 20), RESTING_HEIGHT, 75 +(rand.nextInt(10) - 20));
    }

    public UUID getId() {
        return uuid;
    }

    public double getX() {
        return location.getX();
    }

    public double getY() {
        return location.getY();
    }

    public double getZ() {
        return location.getZ();
    }

    public void updateLocation(float elapsedTimeMS) {
        // TODO
    }
}
