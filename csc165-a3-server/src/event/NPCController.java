package event;

import games.treasurehunt2015.TreasureHuntServer;

import java.util.Random;

import sage.ai.behaviortrees.BTCompositeType;
import sage.ai.behaviortrees.BTSequence;
import sage.ai.behaviortrees.BehaviorTree;

public class NPCController {

    BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
    private long startTime;
    private long lastUpdateTime;
    private NPC  npc;
    private boolean nearFlag = false;
    private TreasureHuntServer server;
    
    public NPCController(TreasureHuntServer serv){
        this.server = serv;
    }

    public void startNPControl() {
        startTime = System.nanoTime();
        lastUpdateTime = startTime;
        setupNPC();
        setupBehaviorTree();
        npcLoop();
    }

    public void setupNPC() {
        Random rn = new Random();
        npc = new NPC();
        npc.randomizeLocation(rn.nextInt(100), rn.nextInt(100));
    }

    public void npcLoop() {
        while (true) {
            long frameStartTime = System.nanoTime();
            float elapsedMilliSecs = (frameStartTime - lastUpdateTime) / (1000000.0f);
            if ( elapsedMilliSecs >= 50.0f ) {
                lastUpdateTime = frameStartTime;
                npc.updateLocation();
                server.sendNPCinfo();
                bt.update(elapsedMilliSecs);
            }
            Thread.yield();
        }
    }

    public void setupBehaviorTree() {
        bt.insertAtRoot(new BTSequence(10));
        bt.insertAtRoot(new BTSequence(20));
        bt.insert(10, new OneSecPassed(this, npc, false));
        bt.insert(10, new GetSmall(npc));
        bt.insert(20, new PlayerNear(server, this, npc, false));
        bt.insert(20, new GetBig(npc));
    }

    public boolean isNearFlag() {
        return nearFlag;
    }

    public void setNearFlag( boolean nearFlag ) {
        this.nearFlag = nearFlag;
    }

}
