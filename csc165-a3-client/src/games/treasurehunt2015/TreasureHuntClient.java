package games.treasurehunt2015;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.UUID;

import sage.networking.client.GameConnectionClient;
import engine.objects.Avatar;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

public class TreasureHuntClient extends GameConnectionClient {
    private TreasureHunt game;
    private UUID id;
    private HashMap<UUID,Avatar> ghostAvatars;
    
    public TreasureHuntClient(InetAddress remAddr, int remPort, ProtocolType pType, TreasureHunt game) throws IOException {
        super(remAddr, remPort, pType);
        
        this.game = game;
        this.id = UUID.randomUUID();
        this.ghostAvatars = new HashMap<UUID,Avatar>();
    }
    
    @Override
    protected void processPacket(Object o) {
        String msg = (String)o;
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
                    break;

                case "move":
                    processMove(UUID.fromString(msgTokens[1]), Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
                    break;
            }
        }
    }
    
    private void processJoin(String msg) {
    	System.out.println("Processing join: " + msg);
    	
        if ("success".equals(msg)) {
            game.setIsConnected(true);
            sendCreateMessage(game.getPlayerPosition());
        } else {
        	game.setIsConnected(false);
        }
    }
    
    private void processCreate(UUID ghostID, float x, float y, float z) {
    	System.out.println("Processing create: " + ghostID + "\t" + x + "," + y + "," + z);
    	Avatar ghost = game.addGhostToGame(x, y, z);
    	ghostAvatars.put(ghostID, ghost);
    }
    
    private void processBye(UUID ghostID) {
    	System.out.println("Processing bye: " + ghostID);
    	Avatar ghost = ghostAvatars.get(ghostID);
    	
    	if (ghost != null) 
    		game.removeGhostFromGame(ghost);
    }
    
    private void processDsfr(UUID ghostID, float x, float y, float z) {
    	System.out.println("Processing dsfr: " + ghostID + "\t" + x + "," + y + "," + z);
        //game.createGhostAvatar(ghostID, x, y, z);
    }
    
    private void processMove(UUID ghostID, float x, float y, float z) {
    	System.out.println("Processing move: " + ghostID + "\t" + x + "," + y + "," + z);
    	
    	Avatar ghost = ghostAvatars.get(ghostID);
    	
    	if (ghost != null) {
    		Matrix3D translate = new Matrix3D();
    		translate.translate(x, y, z);
    		ghost.setLocalTranslation(translate);
    	}
    }
    
    public void sendCreateMessage(Vector3D pos) {
        // format: create,localId,x,y,z
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
        // format: join,localId
        try {
            String msg = new String("join," + id.toString());
            
            System.out.println("Sending join message: " + msg);
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendByeMessage() {
        try {
            String msg = new String("bye," + id.toString());
            
            System.out.println("Sending bye message: " + msg);
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendDetailsForMessage(UUID remId, Vector3D pos) {
        try {
            String msg = new String("dsfr," + id.toString());
            msg += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
            
            System.out.println("Sending DSFR message: " + msg);
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendMoveMsg(Vector3D pos) {
        try {
            String msg = new String("move," + id.toString());
            msg += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
