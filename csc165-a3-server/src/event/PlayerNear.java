package event;

import games.circuitshooter.CircuitShooterServer;
import graphicslib3D.Point3D;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import sage.ai.behaviortrees.BTCondition;

public class PlayerNear extends BTCondition {
    public static final float NEAR_DISTANCE = 50.0f;
    
    private CircuitShooterServer server;
    private NPC                  npc;

    public PlayerNear(CircuitShooterServer server, NPC npc, boolean toNegate) {
        super(toNegate);
        this.server = server;
        this.npc = npc;
    }

    protected boolean check() {
        Point3D npcLoc = npc.getLocation();
        Iterator<Entry<UUID, Point3D>> itr = server.getPlayerLocations().entrySet().iterator();
        UUID closestPlayer = null;
        float closestDistance = Float.MAX_VALUE;
        
        while (itr.hasNext()) {
            Entry<UUID, Point3D> entry = itr.next();
            Point3D avatarLoc = entry.getValue();
            float distance = distance(npcLoc, avatarLoc);
            
            if (distance < closestDistance) {
                System.out.println("Distance: " + distance);
                
                closestPlayer = entry.getKey();
                closestDistance = distance;
            }
        }
        
        if (closestDistance < NEAR_DISTANCE) {
            npc.chaseAvatar(closestPlayer);
            return true;
        }
        
        return false;
    }
    
    private float distance(Point3D a, Point3D b) {
        return (float)Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getZ() - b.getZ(), 2));
    }
}
