package event;

import sage.ai.behaviortrees.BTAction;
import sage.ai.behaviortrees.BTStatus;

public class GetSmall extends BTAction
 {
    private NPC npc;

    public GetSmall( NPC n ) {
        npc = n;
    }

    protected BTStatus update( float elapsedTime ) {
        npc.getSmall();
        return BTStatus.BH_SUCCESS;
    }
}
