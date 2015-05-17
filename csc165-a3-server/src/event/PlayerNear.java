package event;

import games.circuitshooter.NPCController;
import games.circuitshooter.CircuitShooterServer;
import graphicslib3D.Point3D;
import sage.ai.behaviortrees.BTCondition;

public class PlayerNear extends BTCondition {
    private CircuitShooterServer server;
    private NPCController      npcc;
    private NPC                npc;

    public PlayerNear( CircuitShooterServer s, NPCController c, NPC n, boolean toNegate ) {
        super(toNegate);
        server = s;
        npcc = c;
        npc = n;
    }

    protected boolean check() {
        Point3D npcP = new Point3D(npc.getX(), npc.getY(), npc.getZ());
        server.sendCheckForPlayerNear();
        //return npcc.isNearFlag();
        return false;
    }

}
