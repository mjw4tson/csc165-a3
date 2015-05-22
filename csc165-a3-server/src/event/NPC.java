package event;

import games.circuitshooter.CircuitShooterServer;
import graphicslib3D.Point3D;

import java.util.Random;
import java.util.UUID;

public class NPC {
    private static final float RESTING_HEIGHT = 24.0f;

    private Point3D              location;
    private Point3D              homeLocation;
    private UUID                 uuid;
    private UUID                 chaseAvatar;
    private MoveMode             moveMode;
    private CircuitShooterServer server;
    
    public enum MoveMode {
        ROAMING, CHASING, RETURNING;
    }

    public NPC(CircuitShooterServer server) {
        uuid = UUID.randomUUID();
        
        this.server = server;
        Random rand = new Random();
        location = new Point3D(50 + (rand.nextInt(10) - 20), RESTING_HEIGHT, 75 +(rand.nextInt(10) - 20));
        homeLocation = (Point3D)location.clone();
        chaseAvatar = null;
        moveMode = MoveMode.ROAMING;
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
    
    public Point3D getLocation() {
        return location;
    }

    public void updateLocation(float elapsedTimeMS) {
        if (moveMode == MoveMode.CHASING)
            System.out.println("MOVEMODE: CHASING");
        
        System.out.println("Mode: " + moveMode);
        if (moveMode == MoveMode.CHASING && chaseAvatar != null) {
            System.out.println("CHASING");
            moveTowardsPoint(server.getPlayerLocations().get(chaseAvatar), elapsedTimeMS);
        } else if (moveMode == MoveMode.RETURNING) {
            System.out.println("RETURNING");
            moveTowardsPoint(homeLocation, elapsedTimeMS);
        }
    }
    
    public void resetMode() {
        moveMode = MoveMode.RETURNING;
        chaseAvatar(null);
    }
    
    public void chaseAvatar(UUID avatar) {
        chaseAvatar = avatar;
        moveMode = MoveMode.CHASING;
    }
    
    public void chase() {
        moveMode = MoveMode.CHASING;
    }

    public void returnHome() {
        moveMode = MoveMode.RETURNING;
    }

    public void roam() {
        moveMode = MoveMode.ROAMING;
    }
    
    public MoveMode getMoveMode() {
        return moveMode;
    }
    
    private void moveTowardsPoint(Point3D target, float elapsedTimeMS) {
        location.setX(target.getX());
        location.setZ(-target.getZ());
    }
}
