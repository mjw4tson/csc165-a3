package event;

import sage.ai.behaviortrees.BTAction;
import sage.ai.behaviortrees.BTStatus;

public class ReturnHome extends BTAction {
    private NPC npc;
    
    public ReturnHome(NPC npc) {
        this.npc = npc;
    }
    
    @Override
    protected BTStatus update(float paramFloat) {
        npc.returnHome();
        
        return BTStatus.BH_SUCCESS;
    }

}
