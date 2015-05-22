package games.circuitshooter;

import sage.ai.behaviortrees.BTCompositeType;
import sage.ai.behaviortrees.BTSequence;
import sage.ai.behaviortrees.BehaviorTree;
import event.ChaseAvatar;
import event.NPC;
import event.PlayerNear;
import event.ReturnHome;
import event.TenSecPassed;

public class NPCController {
    private NPC npc;
    private BehaviorTree bt;
    private CircuitShooterServer server;
    private long startTime;
    private long lastUpdateTime;
    
    public NPCController(CircuitShooterServer server) {
        this.server = server;
        bt = new BehaviorTree(BTCompositeType.SELECTOR);
        npc = new NPC(server);
        
        startTime = System.nanoTime();
        lastUpdateTime = startTime;
        
        setupBehaviorTree();
    }
    
    public void updateNPC(float elapsedTimeMS) {
        npc.updateLocation(elapsedTimeMS);
    }
    
    public NPC getNPC() {
        return npc;
    }
    
    public void setupBehaviorTree() {
        bt.insertAtRoot(new BTSequence(10));
        bt.insertAtRoot(new BTSequence(20));
        bt.insertAtRoot(new BTSequence(30));
        bt.insert(10, new TenSecPassed(this, npc, false));
        bt.insert(10, new ReturnHome(npc));
        bt.insert(20, new PlayerNear(server, npc, false));
        bt.insert(20, new ChaseAvatar(npc));
    }

    public void npcLoop() {
        while (true) {
            long frameStartTime = System.nanoTime();
            float elapsedMilliSecs = (frameStartTime - lastUpdateTime) / (1000000.0f);
            if (elapsedMilliSecs >= 50.0f) {
                lastUpdateTime = frameStartTime;
                updateNPC(50.0f);
                bt.update(elapsedMilliSecs);
                server.sendNPCInfo();
            }

            Thread.yield();
        }
    }
}
