package event;

import games.circuitshooter.NPCController;
import sage.ai.behaviortrees.BTCondition;

public class TenSecPassed extends BTCondition {
    private NPC npc;
    private long lastUpdateTime;

    public TenSecPassed( NPCController ctrl, NPC npc, boolean toNegate ) {
        super(toNegate);
        
        this.npc = npc;
        lastUpdateTime = System.nanoTime();
    }

    protected boolean check() {
        float elapsedMilliSecs = (System.nanoTime() - lastUpdateTime) / (1000000.0f);

        if (elapsedMilliSecs >= 10000.0f) {
            lastUpdateTime = System.nanoTime();
            npc.resetMode();
        
            return true;
        } else {
            return false;
        }
    }
}