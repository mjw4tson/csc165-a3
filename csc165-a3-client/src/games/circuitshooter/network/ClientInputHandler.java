package games.circuitshooter.network;

import java.util.HashMap;
import java.util.UUID;

import engine.objects.Avatar;
import engine.objects.GhostNPC;
import games.circuitshooter.CircuitShooter;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

public class ClientInputHandler {
	private CircuitShooter game;
	private ClientOutputHandler output;
    private HashMap<UUID, Avatar> ghostAvatars;
    private HashMap<UUID, GhostNPC> ghostNPCs;
    //private HashMap<UUID, TriMesh> ghostHealth; TODO
	
	public ClientInputHandler(CircuitShooter game, ClientOutputHandler output) {
		this.game = game;
		this.output = output;
        this.ghostAvatars = new HashMap<UUID, Avatar>();
        this.ghostNPCs = new HashMap<UUID, GhostNPC>();
        // this.ghostHealth = new HashMap<UUID, TriMesh>(); TODO
	}
	
	protected void processJoin(String msg) {
    	System.out.println("Processing join: " + msg);
    	
        if ("success".equals(msg)) {
            game.setIsConnected(true);
            output.sendCreateMessage(game.getPlayerPosition());
            output.sendWantsDetailsMsg();
        } else {
        	game.setIsConnected(false);
        }
    }
    
    protected void processCreate(UUID ghostID, float x, float y, float z) {
    	if (!ghostAvatars.containsKey(ghostID)) {
	    	System.out.println("Processing create: " + ghostID + "\t" + x + "," + y + "," + z);
	    	Avatar ghost = game.addGhostToGame(ghostID, x, y, z);
	    	ghostAvatars.put(ghostID, ghost);
    	}
    }
    
    protected void processBye(UUID ghostID) {
    	System.out.println("Processing bye: " + ghostID);
    	Avatar ghost = ghostAvatars.get(ghostID);
    	
    	if (ghost != null) 
    		game.removeGhostFromGame(ghost);
    }
    
    protected void processDsfr(UUID ghostID, float x, float y, float z) {
    	if (!ghostAvatars.containsKey(ghostID)) {
	    	System.out.println("Processing create: " + ghostID + "\t" + x + "," + y + "," + z);
	    	Avatar ghost = game.addGhostToGame(ghostID, x, y, z);
	    	ghostAvatars.put(ghostID, ghost);
    	}
	}
    
    protected void processMove(UUID ghostID, float x, float y, float z) {
    	//System.out.println("Processing move: " + ghostID + "\t" + x + "," + y + "," + z); Commented due to verbosity
    	
    	Avatar ghost = ghostAvatars.get(ghostID);
    	
    	if (ghost != null) {
    		Matrix3D translate = new Matrix3D();
    		translate.translate(x, y, z);
    		ghost.getTriMesh().setLocalTranslation(translate);
    	}
    }
    
    protected void processRotate(UUID ghostID, float col0x, float col0z, float col2x, float col2z) {
    	Avatar ghost = ghostAvatars.get(ghostID);
    	
    	if (ghost != null) {
    		Matrix3D rotate = new Matrix3D();
    		rotate.setCol(0, new Vector3D(col0x, 0, col0z));
    		rotate.setCol(2, new Vector3D(col2x, 0, col2z));
    		ghost.getTriMesh().setLocalRotation(rotate);
    	}
    }
    
    protected void processProjectile(UUID ghostID) {
    	Avatar ghost = ghostAvatars.get(ghostID);
    	
    	if (ghost != null) {
    		game.fire(ghost);
    	}
    }
    
    protected void processHit(UUID ghostID, UUID shooterID, boolean isKilled) {
    	Avatar ghost = ghostAvatars.get(ghostID);
    	
    	System.out.println("Hit registered. Shooter: " + shooterID + "\tGhost Avatar ID: " + ghostID + "\tIsKilled: " + isKilled);
    	
    	if (ghost != null) {
    		if (isKilled && shooterID == output.getUUID()) {
    			System.out.println("You killed someone!");
    			ghost.respawn();
    		} else {
    			ghost.setHealth(ghost.getHealth() - 30);
    		}
    	}
    }
    
    protected void processMoveNPC(UUID id, float x, float y, float z) {
    	GhostNPC npc = ghostNPCs.get(id);
    	
    	if (npc != null) {
    		npc.setPosition(new Vector3D(x, y, z));
    	} else {
    		npc = game.addNpcToGame(id, x, y, z);
    		ghostNPCs.put(id, npc);
    	}
    }
    
    protected void processWsds(UUID ghostID) {
    	System.out.println("Processing wants details: " + ghostID);
    	output.sendDetailsForMessage(ghostID, game.getPlayerPosition());
    }
    
}
