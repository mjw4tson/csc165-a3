package games.circuitshooter;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.UUID;

import sage.networking.client.GameConnectionClient;
import engine.objects.Avatar;
import engine.objects.GhostNPC;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

public class CircuitShooterClient extends GameConnectionClient {
    private CircuitShooter game;
    private UUID id;
    private HashMap<UUID, Avatar> ghostAvatars;
    private HashMap<Integer, GhostNPC> ghostNPCs; 
    
    public CircuitShooterClient(InetAddress remAddr, int remPort, ProtocolType pType, CircuitShooter game) throws IOException {
        super(remAddr, remPort, pType);
        
        this.game = game;
        this.id = UUID.randomUUID();
        this.ghostAvatars = new HashMap<UUID, Avatar>();
        this.ghostNPCs = new HashMap<Integer, GhostNPC>();
    }
    
    @Override
    protected void processPacket(Object o) {
        String msg = (String)o;

        System.out.println(msg);
        
        if (msg == null)
        	return;
        
        String msgTokens[] = msg.split(",");
        
        if (msgTokens.length > 0) {
            switch(msgTokens[0]) {
                case "join":
                    processJoin(msgTokens[1]);
                    break;
                case "create":
                	processCreate(UUID.fromString(msgTokens[1]), Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
                	break;
                case "bye":
                    processBye(UUID.fromString(msgTokens[1]));
                    break;
                case "dsfr":
                    processDsfr(UUID.fromString(msgTokens[1]), Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
                    break;
                case "wsds":
                	processWsds(UUID.fromString(msgTokens[1]));
                    break;
                case "move":
                    processMove(UUID.fromString(msgTokens[1]), Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
                    break;
                case "mnpc":
                	processMoveNPC(Integer.parseInt(msgTokens[1]), Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
            }
        }
    }
    
    private void processJoin(String msg) {
    	System.out.println("Processing join: " + msg);
    	
        if ("success".equals(msg)) {
            game.setIsConnected(true);
            sendCreateMessage(game.getPlayerPosition());
            sendWantsDetailsMsg();
        } else {
        	game.setIsConnected(false);
        }
    }
    
    private void processCreate(UUID ghostID, float x, float y, float z) {
    	if (!ghostAvatars.containsKey(ghostID)) {
	    	System.out.println("Processing create: " + ghostID + "\t" + x + "," + y + "," + z);
	    	Avatar ghost = game.addGhostToGame(x, y, z);
	    	ghostAvatars.put(ghostID, ghost);
    	}
    }
    
    private void processBye(UUID ghostID) {
    	System.out.println("Processing bye: " + ghostID);
    	Avatar ghost = ghostAvatars.get(ghostID);
    	
    	if (ghost != null) 
    		game.removeGhostFromGame(ghost);
    }
    
    private void processDsfr(UUID ghostID, float x, float y, float z) {
    	if (!ghostAvatars.containsKey(ghostID)) {
	    	System.out.println("Processing create: " + ghostID + "\t" + x + "," + y + "," + z);
	    	Avatar ghost = game.addGhostToGame(x, y, z);
	    	ghostAvatars.put(ghostID, ghost);
    	}
	}
    
    private void processMove(UUID ghostID, float x, float y, float z) {
    	//System.out.println("Processing move: " + ghostID + "\t" + x + "," + y + "," + z); Commented due to verbosity
    	
    	Avatar ghost = ghostAvatars.get(ghostID);
    	
    	if (ghost != null) {
    		Matrix3D translate = new Matrix3D();
    		translate.translate(x, y, z);
    		ghost.getTriMesh().setLocalTranslation(translate);
    	}
    }
    
    private void processMoveNPC(Integer id, float x, float y, float z) {
    	GhostNPC npc = ghostNPCs.get(id);
    	
    	if (npc != null) {
    		npc.setPosition(new Vector3D(x, y, z));
    	} else {
    		npc = game.addNpcToGame(id, x, y, z);
    		ghostNPCs.put(id, npc);
    	}
    }
    
    private void processWsds(UUID ghostID) {
    	System.out.println("Processing wants details: " + ghostID);
    	sendDetailsForMessage(ghostID, game.getPlayerPosition());
    }
    
    public void sendCreateMessage(Vector3D pos) {
        // Format: create,localID,x,y,z
        try {
            String msg = new String("create," + id.toString());
            msg += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
            
            System.out.println("Sending create message: " + msg);
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendJoinMsg() {
        // Format: join,localID
        try {
            String msg = new String("join," + id.toString());
            
            System.out.println("Sending join message: " + msg);
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendByeMessage() {
    	// Format: bye,localID
        try {
            String msg = new String("bye," + id.toString());
            
            System.out.println("Sending bye message: " + msg);
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendDetailsForMessage(UUID remId, Vector3D pos) {
    	// Format: dsfr,localID,remoteID,x,y,z
        try {
            String msg = new String("dsfr," + id.toString() + "," + remId.toString());
            msg += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
            
            System.out.println("Sending dsfr message: " + msg);
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendMoveMsg(Vector3D pos) {
    	// Format move,localID,x,y,z
        try {
            String msg = new String("move," + id.toString());
            msg += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendWantsDetailsMsg() {
    	// Format wsds,localID
        try {
            String msg = new String("wsds," + id.toString());
            
            System.out.println("Sending wsds: " + msg);
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
