package games.circuitshooter;

import java.util.ArrayList;
import java.util.List;

import sage.ai.behaviortrees.BTCompositeType;
import sage.ai.behaviortrees.BehaviorTree;
import event.NPC;

public class NPCController {
    private ArrayList<NPC> npcs;
    private BehaviorTree bt;
    private CircuitShooterServer server;
    private long startTime;
    private long lastUpdateTime;
    
    public NPCController(CircuitShooterServer server) {
        npcs = new ArrayList<NPC>();
        bt = new BehaviorTree(BTCompositeType.SELECTOR);
        this.server = server;
        
        startTime = System.nanoTime();
        lastUpdateTime = startTime;
        
        setupBehaviorTree();
    }
    
    public void updateNPCs() {
        for (NPC npc : npcs) {
            npc.updateLocation();
        }
    }
    
    public int getNumNPCs() {
        return npcs.size();
    }
    
    public List<NPC> getNPCs() {
        return npcs;
    }
    
    public void spawnNpcs(int num) {
        for (int i = 0; i < num; i++) {
            npcs.add(new NPC());
        }
    }
    
    public void setupBehaviorTree() {
//        bt.insertAtRoot(new BTSequence(10));
//        bt.insertAtRoot(new BTSequence(20));
//        bt.insert(10, new OneSecPassed(this, npc, false));
//        bt.insert(10, new GetSmall(npc));
//        bt.insert(20, new PlayerNear(server, this, npc, false));
//        bt.insert(20, new GetBig(npc));
    }
    
    public void npcLoop() {
        while (true) {
            long frameStartTime = System.nanoTime();
            float elapsedMilliSecs = (frameStartTime - lastUpdateTime) / (1000000.0f);
            if (elapsedMilliSecs >= 50.0f) {
                lastUpdateTime = frameStartTime;
                
                for (NPC npc : npcs)
                    npc.updateLocation();
                
                server.sendNPCInfo();
                bt.update(elapsedMilliSecs);
            }
            Thread.yield();
        }
    }
}
