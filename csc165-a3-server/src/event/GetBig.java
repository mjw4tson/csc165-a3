package event;

import sage.ai.behaviortrees.BTStatus;

public class GetBig {
    private NPC npc;

    public GetBig( NPC n ) {
        npc = n;
    }

    protected BTStatus update( float elapsedTime ) {
        return BTStatus.BH_SUCCESS;
    }
}
