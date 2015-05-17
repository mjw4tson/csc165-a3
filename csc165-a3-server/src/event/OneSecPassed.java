package event;

import games.treasurehunt2015.NPCController;
import sage.ai.behaviortrees.BTCondition;

public class OneSecPassed extends BTCondition {
    private NPCController npcc;
    private NPC npc;
    private long lastUpdateTime;

    public OneSecPassed( NPCController c, NPC n, boolean toNegate ) {
        super(toNegate);
        npcc = c;
        npc = n;
        lastUpdateTime = System.nanoTime();
    }

    protected boolean check() {
        float elapsedMilliSecs = (System.nanoTime() - lastUpdateTime) / (1000000.0f);
        if (elapsedMilliSecs >= 500.0f) {
            lastUpdateTime = System.nanoTime();
            //npcc.setNearFlag(false);
            return true;
        } else
            return false;
    }
}